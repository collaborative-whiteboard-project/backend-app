package pl.polak.nikodem.whiteboard.services.implementations;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.polak.nikodem.whiteboard.dtos.project.*;
import pl.polak.nikodem.whiteboard.dtos.socket.CreateWhiteboardElementData;
import pl.polak.nikodem.whiteboard.dtos.socket.DeleteWhiteboardElementData;
import pl.polak.nikodem.whiteboard.dtos.socket.UpdateWhiteboardElementData;
import pl.polak.nikodem.whiteboard.dtos.socket.WhiteboardOperationData;
import pl.polak.nikodem.whiteboard.entities.Project;
import pl.polak.nikodem.whiteboard.entities.User;
import pl.polak.nikodem.whiteboard.entities.UserProject;
import pl.polak.nikodem.whiteboard.enums.ProjectMemberRole;
import pl.polak.nikodem.whiteboard.enums.UserRole;
import pl.polak.nikodem.whiteboard.exceptions.InsufficientProjectMemberRoleException;
import pl.polak.nikodem.whiteboard.exceptions.ProjectNotFoundException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotAProjectMemberException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotFoundException;
import pl.polak.nikodem.whiteboard.models.WhiteboardElement;
import pl.polak.nikodem.whiteboard.repositories.ProjectRepository;
import pl.polak.nikodem.whiteboard.repositories.UserProjectRepository;
import pl.polak.nikodem.whiteboard.repositories.UserRepository;
import pl.polak.nikodem.whiteboard.services.interfaces.ProjectService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;

    @Override
    public void save(Project project) {
        this.projectRepository.save(project);
    }

    @Override
    @Transactional
    public List<SimpleProjectResponse> getAllProjects() {
        return this.projectRepository.findAll().stream().map(project -> {
            List<ProjectMember> members = getProjectMembers(project);

            return SimpleProjectResponse.builder()
                                        .id(project.getId())
                                        .name(project.getProjectName())
                                        .modifiedAt(project.getModifiedAt().toLocalDateTime())
                                        .members(members)
                                        .build();
        }).toList();
    }

    private List<ProjectMember> getProjectMembers(Project project) {
        return project.getMembers().stream().map(userProject -> {
            return ProjectMember.builder()
                                .memberEmail(userProject.getUser().getEmail())
                                .memberRole(userProject.getMemberRole())
                                .build();
        }).toList();
    }

    @Override
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public ProjectResponse getProjectById(Long id, String email) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException {
        Project project = getProjectForAuthenticatedUser(id, email, ProjectMemberRole.VIEWER);
        List<ProjectMember> members = project.getMembers().stream().map(userProject -> {
            User user = userProject.getUser();
            return ProjectMember.builder().memberEmail(user.getEmail()).memberRole(userProject.getMemberRole()).build();
        }).toList();
        return ProjectResponse.builder()
                              .id(project.getId())
                              .name(project.getProjectName())
                              .members(members)
                              .modifiedAt(project.getModifiedAt().toLocalDateTime())
                              .build();
    }

    @Override
    @Transactional
    public List<SimpleProjectResponse> getUserProjects(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email)
                                  .orElseThrow(() -> new UserNotFoundException("User with email not found"));
        List<Project> projects = user.getProjects().stream().map(UserProject::getProject).toList();

        return projects.stream()
                       .map(project -> SimpleProjectResponse.builder()
                                                            .id(project.getId())
                                                            .name(project.getProjectName())
                                                            .modifiedAt(project.getModifiedAt().toLocalDateTime())
                                                            .members(project.getMembers()
                                                                            .stream()
                                                                            .map(userProject -> ProjectMember.builder()
                                                                                                             .memberEmail(userProject.getUser()
                                                                                                                                     .getEmail())
                                                                                                             .memberRole(userProject.getMemberRole())
                                                                                                             .build())
                                                                            .toList())
                                                            .build())
                       .toList();

    }


    @Override
    @Transactional
    public ProjectContentResponse getProjectContent(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException {
        Project project = getProjectForAuthenticatedUser(id, userEmail, ProjectMemberRole.VIEWER);

        return ProjectContentResponse.builder()
                                     .elements(project.getWhiteboardElementsJSON())
                                     .modifiedAt(project.getModifiedAt().toLocalDateTime())
                                     .build();
    }

    @Override
    @Transactional
    public List<ProjectMemberResponse> getProjectMembers(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException {
        Project project = getProjectForAuthenticatedUser(id, userEmail, ProjectMemberRole.VIEWER);

        return project.getMembers()
                      .stream()
                      .map(userProject -> ProjectMemberResponse.builder()
                                                               .id(userProject.getUser().getId())
                                                               .email(userProject.getUser().getEmail())
                                                               .memberRole(userProject.getMemberRole().name())
                                                               .build())
                      .toList();
    }

    @Override
    @Transactional
    public List<ProjectMemberResponse> getProjectOwners(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException {
        Project project = getProjectForAuthenticatedUser(id, userEmail, ProjectMemberRole.VIEWER);

        return project.getMembers()
                      .stream()
                      .filter(userProject -> userProject.getMemberRole().equals(ProjectMemberRole.OWNER))
                      .map(userProject -> ProjectMemberResponse.builder()
                                                               .id(userProject.getUser().getId())
                                                               .email(userProject.getUser().getEmail())
                                                               .memberRole(userProject.getMemberRole().name())
                                                               .build())
                      .toList();
    }

    @Override
    @Transactional
    public void createNewProject(String ownerEmail, String projectName) throws UserNotFoundException {
        User owner = userRepository.findByEmail(ownerEmail)
                                   .orElseThrow(() -> new UserNotFoundException("User with id not found"));
        Project newProject = projectRepository.save(Project.builder()
                                                           .projectName(projectName)
                                                           .whiteboardElementsJSON(new ArrayList<>())
                                                           .build());
        UserProject userProject = UserProject.builder()
                                             .project(newProject)
                                             .user(owner)
                                             .memberRole(ProjectMemberRole.OWNER)
                                             .build();
        userProjectRepository.save(userProject);
    }

    @Override
    @Transactional
    public void addProjectMembers(Long projectId, String userEmail, List<ProjectMember> members) throws UserNotFoundException, ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException {
        Project project = getProjectForAuthenticatedUser(projectId, userEmail, ProjectMemberRole.OWNER);

        for (ProjectMember member : members) {
            User user = userRepository.findByEmail(member.getMemberEmail())
                                      .orElseThrow(() -> new UserNotFoundException("User with email not found"));

            userProjectRepository.save(UserProject.builder()
                                                  .user(user)
                                                  .project(project)
                                                  .memberRole(member.getMemberRole())
                                                  .build());
        }
    }

    @Override
    @Transactional
    public void deleteProjectMembers(Long projectId, String userEmail, List<String> members) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException {
        Project project = getProjectForAuthenticatedUser(projectId, userEmail, ProjectMemberRole.OWNER);
        List<UserProject> userProjectList = project.getMembers();
        userProjectList.stream().filter(userProject -> {
            User member = userProject.getUser();
            return members.contains(member.getEmail());
        }).forEach(userProjectRepository::delete);

        if (project.getMembers().isEmpty()){
            this.projectRepository.delete(project);
        }
    }

    @Override
    @Transactional
    public void deleteProject(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException {
        Project project = getProjectForAuthenticatedUser(id, userEmail, ProjectMemberRole.OWNER);
        projectRepository.delete(project);
    }

    @Override
    @Transactional
    public boolean isUserProjectMember(Long projectId, String userEmail) throws ProjectNotFoundException {
        Project project = projectRepository.findById(projectId)
                                           .orElseThrow(() -> new ProjectNotFoundException("Project with id not found"));
        List<UserProject> projectMembers = project.getMembers();
        return projectMembers.stream().anyMatch(member -> member.getUser().getEmail().equals(userEmail));
    }

    @Override
    @Transactional
    public void changeProjectData(Long projectId, ChangeProjectDataRequest request) throws ProjectNotFoundException {
        Project project = projectRepository.findById(projectId)
                                           .orElseThrow(() -> new ProjectNotFoundException("Project with id not found"));
        List<UserProject> currentMembersList = project.getMembers();
        List<ProjectMember> modifiedMembersList = request.getMembers();

        modifiedMembersList.forEach(projectMember -> {
            Optional<UserProject> member = findUserInUserProjectListByEmail(currentMembersList, projectMember.getMemberEmail());
            if (member.isEmpty()) {
                Optional<User> newMember = userRepository.findByEmail(projectMember.getMemberEmail());
                saveNewMember(newMember, project, projectMember.getMemberRole());
                return;
            }
            updateMemberRole(member.get(), projectMember.getMemberRole());
        });

        currentMembersList.forEach(projectMember -> {
            Optional<ProjectMember> member = findUserInProjectMemberListByEmail(modifiedMembersList, projectMember.getUser()
                                                                                                                  .getEmail());
            if (member.isEmpty()) {
                userProjectRepository.delete(projectMember);
            }
        });
        project.setProjectName(request.getProjectName());
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public ProjectMemberRole getMemberRole(Long projectId, String email) throws ProjectNotFoundException, UserNotFoundException {
        Project project = projectRepository.findById(projectId)
                                           .orElseThrow(() -> new ProjectNotFoundException("Project with id not found"));

        List<UserProject> members = project.getMembers();
        Optional<UserProject> member = members.stream().filter(projectMember -> {
            return projectMember.getUser().getEmail().equals(email);
        }).findAny();

        if (member.isPresent()) {
            return member.get().getMemberRole();
        }
        throw new UserNotFoundException("Member with email not found");
    }

    public List<WhiteboardElement> saveProjectChangesToDatabase(String projectId, List<WhiteboardOperationData> projectChanges) throws ProjectNotFoundException {
        Project project = this.getProjectById(Long.parseLong(projectId))
                                        .orElseThrow(() -> new ProjectNotFoundException("Project with id not found"));

        List<WhiteboardElement> elements = project.getWhiteboardElementsJSON();
        projectChanges.forEach(change -> {
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
        project.setWhiteboardElementsJSON(elements);
        return this.projectRepository.save(project).getWhiteboardElementsJSON();
    }

    private Optional<UserProject> findUserInUserProjectListByEmail(List<UserProject> userProjectList, String email) {
        return userProjectList.stream()
                              .filter(userProject -> Objects.equals(userProject.getUser().getEmail(), email))
                              .findAny();
    }

    private Optional<ProjectMember> findUserInProjectMemberListByEmail(List<ProjectMember> projectMemberList, String email) {
        return projectMemberList.stream()
                                .filter(projectMember -> Objects.equals(projectMember.getMemberEmail(), email))
                                .findFirst();
    }


    private void saveNewMember(Optional<User> user, Project project, ProjectMemberRole role) {
        if (user.isPresent()) {
            UserProject newUserProject = UserProject.builder()
                                                    .user(user.get())
                                                    .project(project)
                                                    .memberRole(role)
                                                    .build();
            userProjectRepository.save(newUserProject);
        }
    }

    private void updateMemberRole(UserProject userProject, ProjectMemberRole newRole) {
        userProject.setMemberRole(newRole);
    }

    private Project getProjectForAuthenticatedUser(Long id, String userEmail, ProjectMemberRole requiredUserRole) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException {
        Project project = projectRepository.findById(id)
                                           .orElseThrow(() -> new ProjectNotFoundException("Project with id not found"));
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException("User with email not found"));

        if (user.getRole().equals(UserRole.ADMIN)) {
            return project;
        }

        List<UserProject> projectMembers = project.getMembers();

        UserProject member = projectMembers.stream()
                                         .filter(userProject -> userProject.getUser().getEmail().equals(userEmail))
                                         .findAny()
                                         .orElseThrow(() -> new UserNotAProjectMemberException("User not a project member"));


        ProjectMemberRole userRole = member.getMemberRole();
        switch (requiredUserRole) {
            case VIEWER -> {
                return project;
            }
            case EDITOR -> {
                if (userRole == ProjectMemberRole.VIEWER) {
                    throw new InsufficientProjectMemberRoleException("Member is not project editor");
                }
                return project;
            }
            case OWNER -> {
                if (userRole == ProjectMemberRole.VIEWER || userRole == ProjectMemberRole.EDITOR) {
                    throw new InsufficientProjectMemberRoleException("Member is not project owner");
                }
                return project;
            }
        }
        return project;
    }

}

