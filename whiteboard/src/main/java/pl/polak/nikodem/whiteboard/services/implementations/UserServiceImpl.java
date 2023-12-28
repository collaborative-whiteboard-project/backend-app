package pl.polak.nikodem.whiteboard.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.polak.nikodem.whiteboard.dtos.user.UserResponse;
import pl.polak.nikodem.whiteboard.entities.User;
import pl.polak.nikodem.whiteboard.exceptions.UserNotFoundException;
import pl.polak.nikodem.whiteboard.repositories.UserRepository;
import pl.polak.nikodem.whiteboard.services.interfaces.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

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
                           .role(user.getRole().name())
                           .build();
    }

    @Override
    public UserResponse getUserByEmail(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User with id not found"));
        return UserResponse.builder()
                           .id(user.getId())
                           .email(email)
                           .role(user.getRole().name())
                           .build();
    }


}
