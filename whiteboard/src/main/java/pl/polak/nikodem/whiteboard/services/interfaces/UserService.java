package pl.polak.nikodem.whiteboard.services.interfaces;

import org.springframework.security.core.userdetails.UserDetailsService;
import pl.polak.nikodem.whiteboard.dtos.user.ChangeUserDataRequest;
import pl.polak.nikodem.whiteboard.dtos.user.UserResponse;
import pl.polak.nikodem.whiteboard.entities.User;
import pl.polak.nikodem.whiteboard.exceptions.UserNotAuthenticatedException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotFoundException;

import java.util.List;

public interface UserService {
    UserDetailsService userDetailsService();
    User save(User newUser);
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id) throws UserNotFoundException;
    UserResponse getUserByEmail(String email) throws UserNotFoundException;
    String getAuthenticatedUserEmail() throws UserNotAuthenticatedException;
    UserResponse changeUserData(ChangeUserDataRequest request) throws UserNotFoundException, UserNotAuthenticatedException;
    void deleteUserById(Long id) throws UserNotFoundException;
}
