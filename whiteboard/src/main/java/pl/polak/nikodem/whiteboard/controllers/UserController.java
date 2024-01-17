package pl.polak.nikodem.whiteboard.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.polak.nikodem.whiteboard.dtos.user.ChangeUserDataRequest;
import pl.polak.nikodem.whiteboard.dtos.user.UserRequest;
import pl.polak.nikodem.whiteboard.dtos.user.UserResponse;
import pl.polak.nikodem.whiteboard.exceptions.UserNotAuthenticatedException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotFoundException;
import pl.polak.nikodem.whiteboard.services.interfaces.ProjectService;
import pl.polak.nikodem.whiteboard.services.interfaces.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserById(@PathVariable Long id) throws UserNotFoundException {
        return userService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserByEmail(@RequestBody @Valid UserRequest request) throws UserNotFoundException {
        return userService.getUserByEmail(request.getEmail());
    }

    @PatchMapping("/change")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse changeUserData(@RequestBody @Valid ChangeUserDataRequest request) throws UserNotFoundException, UserNotAuthenticatedException {
        return this.userService.changeUserData(request);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Long id) {
        this.userService.deleteUserById(id);
    }
}
