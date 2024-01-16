package pl.polak.nikodem.whiteboard.services.implementations;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.polak.nikodem.whiteboard.dtos.project.ProjectContentResponse;
import pl.polak.nikodem.whiteboard.dtos.project.ProjectMember;
import pl.polak.nikodem.whiteboard.dtos.project.ProjectMemberResponse;
import pl.polak.nikodem.whiteboard.dtos.project.SimpleProjectResponse;
import pl.polak.nikodem.whiteboard.entities.Project;
import pl.polak.nikodem.whiteboard.entities.User;
import pl.polak.nikodem.whiteboard.entities.UserProject;
import pl.polak.nikodem.whiteboard.enums.ProjectMemberRole;
import pl.polak.nikodem.whiteboard.exceptions.InsufficientProjectMemberRoleException;
import pl.polak.nikodem.whiteboard.exceptions.ProjectNotFoundException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotAProjectMemberException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotFoundException;
import pl.polak.nikodem.whiteboard.repositories.ProjectRepository;
import pl.polak.nikodem.whiteboard.repositories.UserProjectRepository;
import pl.polak.nikodem.whiteboard.repositories.UserRepository;
import pl.polak.nikodem.whiteboard.services.interfaces.ProjectService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;

    @Override
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
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
                                                            .build())
                       .toList();
    }

    @Override
    @Transactional
    public ProjectContentResponse getProjectContent(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException {
        Project project = getProjectForAuthenticatedUser(id, userEmail, ProjectMemberRole.VIEWER);

        return ProjectContentResponse.builder()
                                     .elements(project.getWhiteboardElementsJSON())
                                     .modifiedAt(project.getModifiedAt().toLocalDateTime())
                                     .build();
    }

    @Override
    @Transactional
    public List<ProjectMemberResponse> getProjectMembers(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException {
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
    public List<ProjectMemberResponse> getProjectOwners(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException {
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
        Project newProject = projectRepository.save(Project.builder().projectName(projectName).build());
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
                                                  .memberRole(ProjectMemberRole.valueOf(member.getMemberRole()))
                                                  .build());
        }
    }

    @Override
    @Transactional
    public void deleteProjectMembers(Long projectId, String userEmail, List<String> members) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException {
        Project project = getProjectForAuthenticatedUser(projectId, userEmail, ProjectMemberRole.OWNER);
        List<UserProject> userProjectList = project.getMembers();
        userProjectList.stream().filter(userProject -> {
            User member = userProject.getUser();
            return members.contains(member.getEmail());
        }).forEach(userProjectRepository::delete);
    }

    @Override
    @Transactional
    public void deleteProject(Long id, String userEmail) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException {
        Project project = getProjectForAuthenticatedUser(id, userEmail, ProjectMemberRole.OWNER);
        projectRepository.delete(project);
    }

    @Override
    @Transactional
    public boolean isUserIsProjectMember(Long projectId, String userEmail) throws ProjectNotFoundException {
        Project project = projectRepository.findById(projectId)
                                           .orElseThrow(() -> new ProjectNotFoundException("Project with id not found"));
        List<UserProject> projectMembers = project.getMembers();
        return projectMembers.stream()
                             .anyMatch(member -> member.getUser()
                                                       .getEmail()
                                                       .equals(userEmail));
    }


    private Project getProjectForAuthenticatedUser(Long id, String userEmail, ProjectMemberRole requiredUserRole) throws ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException {
        Project project = projectRepository.findById(id)
                                           .orElseThrow(() -> new ProjectNotFoundException("Project with id not found"));

        List<UserProject> projectMembers = project.getMembers();
        UserProject user = projectMembers.stream()
                                         .filter(userProject -> userProject.getUser().getEmail().equals(userEmail))
                                         .findAny()
                                         .orElseThrow(() -> new UserNotAProjectMemberException("User not a project member"));

        ProjectMemberRole userRole = user.getMemberRole();
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

