package pl.polak.nikodem.whiteboard.services.implementations;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.polak.nikodem.whiteboard.dtos.project.ProjectContentResponse;
import pl.polak.nikodem.whiteboard.dtos.project.ProjectMemberResponse;
import pl.polak.nikodem.whiteboard.dtos.project.SimpleProjectResponse;
import pl.polak.nikodem.whiteboard.entities.Project;
import pl.polak.nikodem.whiteboard.entities.User;
import pl.polak.nikodem.whiteboard.entities.UserProject;
import pl.polak.nikodem.whiteboard.enums.ProjectMemberRole;
import pl.polak.nikodem.whiteboard.exceptions.ProjectNotFoundException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotFoundException;
import pl.polak.nikodem.whiteboard.repositories.ProjectRepository;
import pl.polak.nikodem.whiteboard.repositories.UserProjectRepository;
import pl.polak.nikodem.whiteboard.repositories.UserRepository;
import pl.polak.nikodem.whiteboard.services.interfaces.ProjectService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;

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
    public ProjectContentResponse getProjectContent(Long id) throws ProjectNotFoundException {
        Project project = projectRepository.findById(id)
                                           .orElseThrow(() -> new ProjectNotFoundException("Project with id not found"));

        return ProjectContentResponse.builder()
                                     .elements(project.getWhiteboardElementsJSON())
                                     .modifiedAt(project.getModifiedAt().toLocalDateTime())
                                     .build();
    }

    @Override
    @Transactional
    public List<ProjectMemberResponse> getProjectMembers(Long id) throws ProjectNotFoundException {
        Project project = projectRepository.findById(id)
                                           .orElseThrow(() -> new ProjectNotFoundException("Project with id not found"));

        return project.getMembers()
                      .stream()
                      .map(userProject -> ProjectMemberResponse.builder()
                                                               .id(userProject.getUser().getId())
                                                               .email(userProject.getUser().getEmail())
                                                               .memberRole(userProject.getMemberRole().name())
                                                               .build())
                      .toList();
    }

    public List<ProjectMemberResponse> getProjectOwners(Long id) throws ProjectNotFoundException {
        Project project = projectRepository.findById(id)
                                           .orElseThrow(() -> new ProjectNotFoundException("Project with id not found"));

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
    public void createNewProject(Long ownerId, String projectName) throws UserNotFoundException {
        User owner = userRepository.findById(ownerId)
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
    public void addProjectMembers(Long projectId, List<String> emails) throws UserNotFoundException, ProjectNotFoundException {
        Project project = projectRepository.findById(projectId)
                                           .orElseThrow(() -> new ProjectNotFoundException("Project with id not found"));
        for (String email : emails) {
            User user = userRepository.findByEmail(email)
                                      .orElseThrow(() -> new UserNotFoundException("User with email not found"));

            userProjectRepository.save(UserProject.builder()
                                                  .user(user)
                                                  .project(project)
                                                  .build());
        }
    }

}

