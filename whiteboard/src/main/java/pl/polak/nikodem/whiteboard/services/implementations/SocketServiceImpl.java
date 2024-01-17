package pl.polak.nikodem.whiteboard.services.implementations;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.polak.nikodem.whiteboard.dtos.project.ProjectContentResponse;
import pl.polak.nikodem.whiteboard.dtos.socket.CreateWhiteboardElementData;
import pl.polak.nikodem.whiteboard.dtos.socket.DeleteWhiteboardElementData;
import pl.polak.nikodem.whiteboard.dtos.socket.UpdateWhiteboardElementData;
import pl.polak.nikodem.whiteboard.dtos.socket.WhiteboardOperationData;
import pl.polak.nikodem.whiteboard.entities.Project;
import pl.polak.nikodem.whiteboard.exceptions.InsufficientProjectMemberRoleException;
import pl.polak.nikodem.whiteboard.exceptions.ProjectNotFoundException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotAProjectMemberException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotAuthenticatedException;
import pl.polak.nikodem.whiteboard.models.WhiteboardElement;
import pl.polak.nikodem.whiteboard.services.interfaces.JwtService;
import pl.polak.nikodem.whiteboard.services.interfaces.ProjectService;
import pl.polak.nikodem.whiteboard.services.interfaces.SocketService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocketServiceImpl implements SocketService {
    private final JwtService jwtService;
    private final ProjectService projectService;
    private final SocketIOServer socketIOServer;
    private final Map<String, List<WhiteboardOperationData>> projectsChanges = new ConcurrentHashMap<>();
    private final Map<String, ProjectContentResponse> cachedProjectsContent = new ConcurrentHashMap<>();

    @Override
    public void joinRoom(SocketIOClient client, String jwtToken, String projectId) throws UserNotAuthenticatedException, ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException {
        if (!jwtService.isTokenExpired(jwtToken)) {
            throw new UserNotAuthenticatedException("User's token expired");
        }

        String email = jwtService.extractUsername(jwtToken);

        if (projectService.isUserIsProjectMember(Long.parseLong(projectId), email)) {
            client.joinRoom(projectId);
            projectsChanges.putIfAbsent(projectId, Collections.synchronizedList(new ArrayList<>()));
            cachedProjectsContent.putIfAbsent(projectId, projectService.getProjectContent(Long.parseLong(projectId), email));
            sendUserProjectContent(client, projectId);
            log.debug("User: " + email + ": joined room: " + projectId);
        }
    }

    @Override
    public void leaveRoom(SocketIOClient client, String jwtToken, String projectId) throws UserNotAuthenticatedException, ProjectNotFoundException {
        if (!jwtService.isTokenExpired(jwtToken)) {
            throw new UserNotAuthenticatedException("User's token expired");
        }

        String email = jwtService.extractUsername(jwtToken);
        client.leaveRoom(projectId);

        log.debug("User: " + email + ": left room: " + projectId);

        if (getProjectUsers(projectId).isEmpty()) {
            saveProjectChangesToDatabase(projectId);
        }
    }

    @Override
    public void disconnectUser(SocketIOClient client) {
        Set<String> userRooms = client.getAllRooms();
        userRooms.forEach(room -> {
            if (getProjectUsers(room).isEmpty()) {
                try {
                    saveProjectChangesToDatabase(room);
                    cachedProjectsContent.remove(room);
                } catch (ProjectNotFoundException e) {
                    this.sendErrorMessage(client, e.getMessage());
                }
            }
            client.leaveRoom(room);
        });
    }

    @Override
    public void editWhiteboard(SocketIOClient client, WhiteboardOperationData data) {
        if (data instanceof CreateWhiteboardElementData createData) {
            createElement(client, createData);
        } else if (data instanceof UpdateWhiteboardElementData updateData) {
            updateElement(client, updateData);
        } else if (data instanceof DeleteWhiteboardElementData deleteData) {
            deleteElement(client, deleteData);
        }
    }

    @Override
    public void sendErrorMessage(SocketIOClient client, String errorMessage) {
        client.sendEvent("error", errorMessage);
    }

    private void createElement(SocketIOClient client, CreateWhiteboardElementData data) {
        ProjectContentResponse projectContent = cachedProjectsContent.get(data.getProjectId());
        if (projectContent.getElements()
                          .stream()
                          .noneMatch(element -> element.getId().equals(data.getElement().getId()))) {
            projectsChanges.get(data.getProjectId()).add(data);
            socketIOServer.getRoomOperations(data.getProjectId()).getClients().forEach(user -> {
                user.sendEvent("createElement", data);
            });
        } else {
            client.sendEvent("removeElement", DeleteWhiteboardElementData.builder()
                                                                         .id(data.getElement().getId())
                                                                         .projectId(data.getProjectId())
                                                                         .timestamp(data.getTimestamp())
                                                                         .build());
        }
    }

    private void updateElement(SocketIOClient client, UpdateWhiteboardElementData data) {
        ProjectContentResponse projectContent = cachedProjectsContent.get(data.getProjectId());
        if (projectContent.getElements().stream().anyMatch(element -> element.getId().equals(data.getId()))) {
            projectsChanges.get(data.getProjectId()).add(data);
            socketIOServer.getRoomOperations(data.getProjectId()).getClients().forEach(user -> {
                user.sendEvent("createElement", data);
            });
        } else {
            client.sendEvent("removeElement", DeleteWhiteboardElementData.builder()
                                                                         .id(data.getId())
                                                                         .projectId(data.getProjectId())
                                                                         .timestamp(data.getTimestamp())
                                                                         .build());
        }
    }

    private void deleteElement(SocketIOClient client, DeleteWhiteboardElementData data) {
        ProjectContentResponse projectContent = cachedProjectsContent.get(data.getProjectId());
        if (projectContent.getElements().stream().anyMatch(element -> element.getId().equals(data.getId()))) {
            projectsChanges.get(data.getProjectId()).add(data);
            socketIOServer.getRoomOperations(data.getProjectId()).getClients().forEach(user -> {
                user.sendEvent("deleteElement", data);
            });
        }
    }


    private void sendUserProjectContent(SocketIOClient client, String projectId) {
        ProjectContentResponse projectContent = cachedProjectsContent.get(projectId);
        client.sendEvent("projectContent", projectContent);
        sendUserLatestChanges(client, projectId, projectContent.getModifiedAt());
    }

    private void sendUserLatestChanges(SocketIOClient client, String projectId, LocalDateTime modifiedAt) {
        List<WhiteboardOperationData> changes = projectsChanges.get(projectId);

        client.sendEvent("latestChanges", changes.stream()
                                                 .filter(change -> change.getTimestamp().isAfter(modifiedAt))
                                                 .toList());
    }

    private void saveProjectChangesToDatabase(String projectId) throws ProjectNotFoundException {
        Project project = projectService.getProjectById(Long.parseLong(projectId))
                                        .orElseThrow(() -> new ProjectNotFoundException("Project with id not found"));

        List<WhiteboardElement> elements = project.getWhiteboardElementsJSON();
        List<WhiteboardOperationData> changes = projectsChanges.get(projectId);
        changes.forEach(change -> {
            if (change instanceof CreateWhiteboardElementData createElementData) {
                elements.add(createElementData.getElement());
            }

            if (change instanceof UpdateWhiteboardElementData updateElementData) {
                elements.stream()
                        .filter(element -> element.getId().equals(updateElementData.getId()))
                        .findFirst()
                        .ifPresent(element -> element.updateProperty(updateElementData.getPropertyName(), updateElementData.getPropertyValue()));
            }

            if (change instanceof DeleteWhiteboardElementData deleteElementData) {
                elements.removeIf(element -> element.getId().equals(deleteElementData.getId()));
            }
        });
        projectsChanges.remove(projectId);
    }

    private Collection<SocketIOClient> getProjectUsers(String room) {
        return socketIOServer.getRoomOperations(room).getClients();
    }
}
