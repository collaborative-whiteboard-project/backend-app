package pl.polak.nikodem.whiteboard.services.interfaces;

import pl.polak.nikodem.whiteboard.dtos.project.*;
import pl.polak.nikodem.whiteboard.dtos.socket.WhiteboardOperationData;
import pl.polak.nikodem.whiteboard.entities.Project;
import pl.polak.nikodem.whiteboard.enums.ProjectMemberRole;
import pl.polak.nikodem.whiteboard.exceptions.InsufficientProjectMemberRoleException;
import pl.polak.nikodem.whiteboard.exceptions.ProjectNotFoundException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotAProjectMemberException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotFoundException;
import pl.polak.nikodem.whiteboard.models.WhiteboardElement;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    void save(Project project);
    List<SimpleProjectResponse> getAllProjects();
    Optional<Project> getProjectById(Long id);
    ProjectResponse getProjectById(Long id, String email) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException;
    List<SimpleProjectResponse> getUserProjects(String email) throws UserNotFoundException;
    ProjectContentResponse getProjectContent(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException;
    List<ProjectMemberResponse> getProjectMembers(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException;
    List<ProjectMemberResponse> getProjectOwners(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException;
    void createNewProject(String ownerEmail, String projectName) throws UserNotFoundException;
    void addProjectMembers(Long projectId, String userEmail, List<ProjectMember> members) throws UserNotFoundException, ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException;
    void deleteProjectMembers(Long projectId, String userEmail, List<String> members) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException;
    void deleteProject(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException;
    boolean isUserProjectMember(Long projectId, String userEmail) throws ProjectNotFoundException;
    void changeProjectData(Long projectId, ChangeProjectDataRequest request) throws ProjectNotFoundException;
    ProjectMemberRole getMemberRole(Long projectId, String email) throws ProjectNotFoundException, UserNotFoundException;
    List<WhiteboardElement> saveProjectChangesToDatabase(String projectId, List<WhiteboardOperationData> projectChanges) throws ProjectNotFoundException;
}
