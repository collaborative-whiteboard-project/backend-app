package pl.polak.nikodem.whiteboard.services.implementations;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.polak.nikodem.whiteboard.dtos.user.ChangeUserDataRequest;
import pl.polak.nikodem.whiteboard.dtos.user.UserResponse;
import pl.polak.nikodem.whiteboard.entities.Project;
import pl.polak.nikodem.whiteboard.entities.User;
import pl.polak.nikodem.whiteboard.entities.UserProject;
import pl.polak.nikodem.whiteboard.enums.UserRole;
import pl.polak.nikodem.whiteboard.exceptions.UserNotAuthenticatedException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotFoundException;
import pl.polak.nikodem.whiteboard.repositories.ProjectRepository;
import pl.polak.nikodem.whiteboard.repositories.UserRepository;
import pl.polak.nikodem.whiteboard.services.interfaces.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository.findByEmail(username)
                                     .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
            }
        };
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                    .map(user -> UserResponse.builder()
                                             .id(user.getId())
                                             .email(user.getEmail())
                                             .firstName(user.getFirstName())
                                             .lastName(user.getLastName())
                                             .role(user.getRole().name())
                                             .build())
                    .toList();
    }

    public User save(User newUser) {
        return userRepository.save(newUser);
    }

    @Override
    public UserResponse getUserById(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id not found"));
        return UserResponse.builder()
                           .id(id)
                           .email(user.getEmail())
                           .firstName(user.getFirstName())
                           .lastName(user.getLastName())
                           .role(user.getRole().name())
                           .build();
    }


    @Override
    public UserResponse getUserByEmail(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email)
                                  .orElseThrow(() -> new UserNotFoundException("User with id not found"));
        return UserResponse.builder()
                           .id(user.getId())
                           .email(email)
                           .firstName(user.getFirstName())
                           .lastName(user.getLastName())
                           .role(user.getRole().name())
                           .build();
    }


    @Override
    public String getAuthenticatedUserEmail() throws UserNotAuthenticatedException {
        return this.getAuthenticatedUser().getUsername();
    }


    @Override
    public UserResponse changeUserData(ChangeUserDataRequest request) throws UserNotFoundException, UserNotAuthenticatedException {
        User user = this.userRepository.findById(request.getId())
                                       .orElseThrow(() -> new UserNotFoundException("User with id not found"));

        UserDetails authenticatedUserDetails = this.getAuthenticatedUser();
        User authenticatedUser = this.userRepository.findByEmail(authenticatedUserDetails.getUsername())
                                                    .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getEmail().equals(authenticatedUser.getEmail()) || authenticatedUser.getRole()
                                                                                     .equals(UserRole.ADMIN)) {
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setRole(request.getRole());
            this.userRepository.save(user);
        }

        return UserResponse.builder()
                           .id(user.getId())
                           .email(user.getEmail())
                           .firstName(user.getFirstName())
                           .lastName(user.getLastName())
                           .role(user.getRole().toString())
                           .build();
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) throws UserNotFoundException {
        User user = this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id not found."));
        List<UserProject> projects = user.getProjects();
        projects.forEach(project -> {
            Project project1 = project.getProject();
            if (project1.getMembers().size() == 1) {
                projectRepository.delete(project1);
            }
        });
        userRepository.deleteById(id);
    }
    private UserDetails getAuthenticatedUser() throws UserNotAuthenticatedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new UserNotAuthenticatedException("User not authenticated");
        }

        if (auth instanceof UsernamePasswordAuthenticationToken authToken) {
            Object principal = authToken.getPrincipal();

            if (principal instanceof UserDetails userDetails) {
                return userDetails;
            }
        }
        throw new UserNotAuthenticatedException("User not authenticated");
    }
}
