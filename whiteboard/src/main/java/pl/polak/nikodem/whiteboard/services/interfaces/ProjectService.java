package pl.polak.nikodem.whiteboard.services.interfaces;

import pl.polak.nikodem.whiteboard.dtos.project.ProjectContentResponse;
import pl.polak.nikodem.whiteboard.dtos.project.ProjectMemberResponse;
import pl.polak.nikodem.whiteboard.dtos.project.SimpleProjectResponse;
import pl.polak.nikodem.whiteboard.exceptions.ProjectNotFoundException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotFoundException;

import java.util.List;

public interface ProjectService {
    List<SimpleProjectResponse> getUserProjects(String email) throws UserNotFoundException;
    ProjectContentResponse getProjectContent(Long id) throws ProjectNotFoundException;
    List<ProjectMemberResponse> getProjectMembers(Long id) throws ProjectNotFoundException;
    List<ProjectMemberResponse> getProjectOwners(Long id) throws ProjectNotFoundException;
    void createNewProject(Long ownerId, String projectName) throws UserNotFoundException;
    void addProjectMembers(Long projectId, List<String> emails) throws UserNotFoundException, ProjectNotFoundException;
}
