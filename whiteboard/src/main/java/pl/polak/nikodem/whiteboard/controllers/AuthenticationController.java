package pl.polak.nikodem.whiteboard.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.polak.nikodem.whiteboard.dtos.auth.*;
import pl.polak.nikodem.whiteboard.exceptions.UserNotAuthenticatedException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotFoundException;
import pl.polak.nikodem.whiteboard.services.interfaces.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(value = "/signup", consumes = "application/json")
    public JwtAuthenticationResponse signup(@RequestBody @Valid SignUpRequest request) {
        return authenticationService.signup(request);
    }

    @PostMapping(value = "/signin")
    public JwtAuthenticationResponse signin(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signin(request);
    }

    @PatchMapping("/change/user/password")
    public JwtAuthenticationResponse changePassword(@RequestBody @Valid ChangePasswordRequest request) throws UserNotFoundException, UserNotAuthenticatedException {
        return this.authenticationService.changePassword(request);
    }

    @PostMapping("/reset/password/email")
    public void sendResetPasswordEmail(@RequestBody @Valid SendResetPasswordEmailRequest request) throws UserNotFoundException {
        this.authenticationService.sendResetPasswordEmail(request);
    }

    @PatchMapping("/reset/password")
    public void resetUserPassword(@RequestBody @Valid ResetPasswordRequest request) {

    }

}
