package pl.polak.nikodem.whiteboard.services.implementations;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.polak.nikodem.whiteboard.dtos.project.ProjectContentResponse;
import pl.polak.nikodem.whiteboard.dtos.project.ProjectMember;
import pl.polak.nikodem.whiteboard.dtos.socket.CreateWhiteboardElementData;
import pl.polak.nikodem.whiteboard.dtos.socket.DeleteWhiteboardElementData;
import pl.polak.nikodem.whiteboard.dtos.socket.UpdateWhiteboardElementData;
import pl.polak.nikodem.whiteboard.dtos.socket.WhiteboardOperationData;
import pl.polak.nikodem.whiteboard.enums.ProjectMemberRole;
import pl.polak.nikodem.whiteboard.exceptions.*;
import pl.polak.nikodem.whiteboard.services.interfaces.JwtService;
import pl.polak.nikodem.whiteboard.services.interfaces.ProjectService;
import pl.polak.nikodem.whiteboard.services.interfaces.SocketService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocketServiceImpl implements SocketService {
    private final JwtService jwtService;
    private final ProjectService projectService;
    private final SocketIOServer socketIOServer;
    private final Map<String, List<WhiteboardOperationData>> projectsChanges = new ConcurrentHashMap<>();
    private final Map<String, Map<SocketIOClient, String>> clientsEmail = new ConcurrentHashMap<>();
    private final Map<String, ProjectContentResponse> cachedProjectsContent = new ConcurrentHashMap<>();
    private final ReentrantLock processWhiteboardOperationMutex = new ReentrantLock(true);

    @Override
    public void joinRoom(SocketIOClient client, String jwtToken, String projectId) throws UserNotAuthenticatedException, ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException {
        if (jwtService.isTokenExpired(jwtToken)) {
            throw new UserNotAuthenticatedException("User's token expired");
        }

        String email = jwtService.extractUsername(jwtToken);

        if (projectService.isUserProjectMember(Long.parseLong(projectId), email)) {
            client.joinRoom(projectId);
            projectsChanges.putIfAbsent(projectId, Collections.synchronizedList(new ArrayList<>()));
            cachedProjectsContent.putIfAbsent(projectId, projectService.getProjectContent(Long.parseLong(projectId), email));
            this.clientsEmail.putIfAbsent(projectId, new ConcurrentHashMap<>());;
            this.clientsEmail.get(projectId).putIfAbsent(client, email);
            sendUserProjectContent(client, projectId);
            sendConnectedUsersList(projectId);
            log.debug("User: " + email + ": joined room: " + projectId);
        }
    }

    @Override
    public void leaveRoom(SocketIOClient client, String jwtToken, String projectId) throws UserNotAuthenticatedException, ProjectNotFoundException {
        if (jwtService.isTokenExpired(jwtToken)) {
            throw new UserNotAuthenticatedException("User's token expired");
        }

        var x = socketIOServer.getRoomOperations(projectId).getClients();

        String email = jwtService.extractUsername(jwtToken);
        client.leaveRoom(projectId);
        sendConnectedUsersList(projectId);
        log.debug("User: " + email + ": left room: " + projectId);

        if (getProjectUsers(projectId).isEmpty()) {
            this.projectService.saveProjectChangesToDatabase(projectId, this.projectsChanges.get(projectId));
            projectsChanges.remove(projectId);
            cachedProjectsContent.remove(projectId);
            clientsEmail.remove(projectId);
        }
    }

    @Override
    public void disconnectUser(SocketIOClient client) {
        Set<String> userRooms = client.getAllRooms();
        userRooms.forEach(room -> {
            if (getProjectUsers(room).isEmpty()) {
                try {
                    this.projectService.saveProjectChangesToDatabase(room, this.projectsChanges.get(room));
                    projectsChanges.remove(room);
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
        String elementId = data.getElement().getId();
        String projectId = data.getProjectId();
        try {
            processWhiteboardOperationMutex.lock();
            if (!doesElementExists(elementId, projectContent, projectId)) {
                projectsChanges.get(projectId).add(data);
                socketIOServer.getRoomOperations(projectId).getClients().forEach(user -> {
                    user.sendEvent("createElement", data);
                });
            } else {
                client.sendEvent("removeElement", DeleteWhiteboardElementData.builder()
                                                                             .id(data.getElement().getId())
                                                                             .projectId(data.getProjectId())
                                                                             .timestamp(data.getTimestamp())
                                                                             .build());
            }
        } finally {
            processWhiteboardOperationMutex.unlock();
        }
    }


    private boolean isElementInProjectContent(String elementId, ProjectContentResponse projectContent) {
        return projectContent.getElements().stream().anyMatch(element -> element.getId().equals(elementId));
    }

    private boolean isElementInProjectChanges(String elementId, String projectId) {
        boolean created = projectsChanges.get(projectId)
                                         .stream()
                                         .filter(operation -> operation instanceof CreateWhiteboardElementData)
                                         .anyMatch(operation -> ((CreateWhiteboardElementData) operation).getElement()
                                                                                                         .getId()
                                                                                                         .equals(elementId));
        boolean deleted = projectsChanges.get(projectId)
                                         .stream()
                                         .filter(operation -> operation instanceof DeleteWhiteboardElementData)
                                         .anyMatch(operation -> ((DeleteWhiteboardElementData) operation).getId()
                                                                                                         .equals(elementId));
        return created && !deleted;
    }

    private boolean doesElementExists(String elementId, ProjectContentResponse projectContent, String projectId) {
        return isElementInProjectContent(elementId, projectContent) || isElementInProjectChanges(elementId, projectId);
    }

    private void updateElement(SocketIOClient client, UpdateWhiteboardElementData data) {
        ProjectContentResponse projectContent = cachedProjectsContent.get(data.getProjectId());
        String elementId = data.getId();
        String projectId = data.getProjectId();
        try {
            processWhiteboardOperationMutex.lock();
            if (doesElementExists(elementId, projectContent, projectId)) {
                projectsChanges.get(projectId).add(data);
                socketIOServer.getRoomOperations(projectId).getClients().forEach(user -> {
                    user.sendEvent("updateElement", data);
                });
            } else {
                client.sendEvent("deleteElement", DeleteWhiteboardElementData.builder()
                                                                             .id(data.getId())
                                                                             .projectId(data.getProjectId())
                                                                             .timestamp(data.getTimestamp())
                                                                             .build());
            }
        } finally {
            processWhiteboardOperationMutex.unlock();
        }
    }

    private void deleteElement(SocketIOClient client, DeleteWhiteboardElementData data) {
        ProjectContentResponse projectContent = cachedProjectsContent.get(data.getProjectId());
        String elementId = data.getId();
        String projectId = data.getProjectId();
        try {
            processWhiteboardOperationMutex.lock();
            if (doesElementExists(elementId, projectContent, projectId)) {
                projectsChanges.get(projectId).add(data);
                socketIOServer.getRoomOperations(projectId).getClients().forEach(user -> {
                    user.sendEvent("deleteElement", data);
                });
            }
        } finally {
            processWhiteboardOperationMutex.unlock();
        }
    }


    private void sendUserProjectContent(SocketIOClient client, String projectId) {
        ProjectContentResponse projectContent = cachedProjectsContent.get(projectId);
        projectContent.setProjectId(projectId);
        client.sendEvent("projectContent", projectContent);
        sendUserLatestChanges(client, projectId, projectContent.getModifiedAt());
    }

    private void sendUserLatestChanges(SocketIOClient client, String projectId, LocalDateTime modifiedAt) {
        List<WhiteboardOperationData> changes = projectsChanges.get(projectId);

        client.sendEvent("latestChanges", changes.stream()
                                                 .filter(change -> change.getTimestamp().isAfter(modifiedAt))
                                                 .toList());
    }

    private Collection<SocketIOClient> getProjectUsers(String room) {
        return socketIOServer.getRoomOperations(room).getClients();
    }

    void sendConnectedUsersList(String projectId) {
        Map<SocketIOClient, String> members = this.clientsEmail.get(projectId);
        List<ProjectMember> memberList = this.socketIOServer.getRoomOperations(projectId).getClients().stream().map(client -> {
            String email = members.get(client);
            if (email != null) {
                try {
                    ProjectMemberRole role = projectService.getMemberRole(Long.parseLong(projectId), email);
                    return new ProjectMember(email, role);
                } catch (Exception e) {
                    //todo
                }
            }
            return null;
        }).filter(Objects::nonNull).toList();

        this.socketIOServer.getRoomOperations(projectId).getClients().forEach(client -> client.sendEvent("projectMembers", memberList));
    }
}
