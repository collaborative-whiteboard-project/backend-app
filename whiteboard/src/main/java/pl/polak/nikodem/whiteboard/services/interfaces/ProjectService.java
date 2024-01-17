package pl.polak.nikodem.whiteboard.services.interfaces;

import pl.polak.nikodem.whiteboard.dtos.project.*;
import pl.polak.nikodem.whiteboard.entities.Project;
import pl.polak.nikodem.whiteboard.exceptions.InsufficientProjectMemberRoleException;
import pl.polak.nikodem.whiteboard.exceptions.ProjectNotFoundException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotAProjectMemberException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    Optional<Project> getProjectById(Long id);
    ProjectResponse getProjectById(Long id, String email) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException;
    List<SimpleProjectResponse> getUserProjects(String email) throws UserNotFoundException;
    ProjectContentResponse getProjectContent(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException;
    List<ProjectMemberResponse> getProjectMembers(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException;
    List<ProjectMemberResponse> getProjectOwners(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException;
    void createNewProject(String ownerEmail, String projectName) throws UserNotFoundException;
    void addProjectMembers(Long projectId, String userEmail, List<ProjectMember> members) throws UserNotFoundException, ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException;
    void deleteProjectMembers(Long projectId, String userEmail, List<String> members) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException;
    void deleteProject(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException;
    boolean isUserIsProjectMember(Long projectId, String userEmail) throws ProjectNotFoundException;
    void changeProjectData(Long projectId, ChangeProjectDataRequest request) throws ProjectNotFoundException;
}
