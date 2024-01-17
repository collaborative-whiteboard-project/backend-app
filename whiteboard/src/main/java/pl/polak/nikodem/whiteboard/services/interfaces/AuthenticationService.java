package pl.polak.nikodem.whiteboard.services.interfaces;

import pl.polak.nikodem.whiteboard.dtos.auth.*;
import pl.polak.nikodem.whiteboard.exceptions.UserNotAuthenticatedException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotFoundException;

public interface AuthenticationService {

    JwtAuthenticationResponse signup(SignUpRequest request);
    JwtAuthenticationResponse signin(SignInRequest request);
    JwtAuthenticationResponse changePassword(ChangePasswordRequest request) throws UserNotAuthenticatedException, UserNotFoundException;
    void sendResetPasswordEmail(SendResetPasswordEmailRequest request) throws UserNotFoundException;
    void resetUserPassword(ResetPasswordRequest request) throws UserNotFoundException;
}
