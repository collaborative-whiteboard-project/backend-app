package pl.polak.nikodem.whiteboard.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.polak.nikodem.whiteboard.dtos.project.*;
import pl.polak.nikodem.whiteboard.exceptions.*;
import pl.polak.nikodem.whiteboard.services.interfaces.ProjectService;
import pl.polak.nikodem.whiteboard.services.interfaces.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final UserService userService;

    @GetMapping("/all")
    public List<SimpleProjectResponse> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/user/all")
    public List<SimpleProjectResponse> getUserProjects() throws UserNotAuthenticatedException, UserNotFoundException {
        String email = userService.getAuthenticatedUserEmail();
        return projectService.getUserProjects(email);
    }

    @GetMapping("/content/{id}")
    public ProjectContentResponse getProjectContent(@PathVariable Long id) throws ProjectNotFoundException, UserNotAuthenticatedException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException {
        String email = userService.getAuthenticatedUserEmail();
        return projectService.getProjectContent(id, email);
    }

    @GetMapping("/{id}/members")
    public List<ProjectMemberResponse> getProjectMembers(@PathVariable Long id) throws UserNotAuthenticatedException, ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException {
        String email = userService.getAuthenticatedUserEmail();
        return projectService.getProjectMembers(id, email);
    }

    @GetMapping("/{id}/owners")
    public List<ProjectMemberResponse> getProjectOwners(@PathVariable Long id) throws UserNotAuthenticatedException, ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException {
        String email = userService.getAuthenticatedUserEmail();
        return projectService.getProjectOwners(id, email);
    }

    @GetMapping("{id}")
    public ProjectResponse getProjectById(@PathVariable Long id) throws ProjectNotFoundException, UserNotAuthenticatedException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException {
        String email = userService.getAuthenticatedUserEmail();
        return projectService.getProjectById(id, email);
    }

    @PostMapping("/create")
    public void createProject(@RequestBody @Valid CreateProjectRequest createProjectRequest) throws UserNotAuthenticatedException, UserNotFoundException {
        String email = userService.getAuthenticatedUserEmail();
        projectService.createNewProject(email, createProjectRequest.getProjectName());
    }

    @PostMapping("/{id}/members/add")
    public void addProjectMembers(@PathVariable Long id, @RequestBody @Valid AddProjectMembersRequest request) throws UserNotAuthenticatedException, UserNotFoundException, ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException {
        String email = userService.getAuthenticatedUserEmail();
        projectService.addProjectMembers(id, email, request.getMembers());
    }

    @PatchMapping("/{id}/change/data")
    public void changeProjectData(@PathVariable Long id, @RequestBody @Valid ChangeProjectDataRequest request) throws ProjectNotFoundException {
        this.projectService.changeProjectData(id, request);
    }

    @DeleteMapping("/{id}/members/delete")
    public void deleteProjectMembers(@PathVariable Long id, @RequestBody @Valid DeleteProjectMembersRequest request) throws UserNotAuthenticatedException, ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException {
        String email = userService.getAuthenticatedUserEmail();
        projectService.deleteProjectMembers(id, email, request.getEmails());
    }

    @DeleteMapping("/{id}/delete")
    public void deleteProject(@PathVariable Long id) throws UserNotAuthenticatedException, ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException {
        String email = userService.getAuthenticatedUserEmail();
        projectService.deleteProject(id, email);
    }
}
