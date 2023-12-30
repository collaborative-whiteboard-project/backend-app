package pl.polak.nikodem.whiteboard.services.interfaces;

import pl.polak.nikodem.whiteboard.dtos.project.ProjectContentResponse;
import pl.polak.nikodem.whiteboard.dtos.project.ProjectMember;
import pl.polak.nikodem.whiteboard.dtos.project.ProjectMemberResponse;
import pl.polak.nikodem.whiteboard.dtos.project.SimpleProjectResponse;
import pl.polak.nikodem.whiteboard.exceptions.InsufficientProjectMemberRoleException;
import pl.polak.nikodem.whiteboard.exceptions.ProjectNotFoundException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotAProjectMemberException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotFoundException;

import java.util.List;

public interface ProjectService {
    List<SimpleProjectResponse> getUserProjects(String email) throws UserNotFoundException;
    ProjectContentResponse getProjectContent(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException;
    List<ProjectMemberResponse> getProjectMembers(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException;
    List<ProjectMemberResponse> getProjectOwners(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException;
    void createNewProject(String ownerEmail, String projectName) throws UserNotFoundException;
    void addProjectMembers(Long projectId, String userEmail, List<ProjectMember> members) throws UserNotFoundException, ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException;
    void deleteProjectMembers(Long projectId, String userEmail, List<String> members) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException;
}
