package pl.polak.nikodem.whiteboard.services.interfaces;

import pl.polak.nikodem.whiteboard.dtos.auth.ChangePasswordRequest;
import pl.polak.nikodem.whiteboard.dtos.auth.JwtAuthenticationResponse;
import pl.polak.nikodem.whiteboard.dtos.auth.SignInRequest;
import pl.polak.nikodem.whiteboard.dtos.auth.SignUpRequest;
import pl.polak.nikodem.whiteboard.exceptions.UserNotAuthenticatedException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotFoundException;

public interface AuthenticationService {

    JwtAuthenticationResponse signup(SignUpRequest request);

    JwtAuthenticationResponse signin(SignInRequest request);

    JwtAuthenticationResponse changePassword(ChangePasswordRequest request) throws UserNotAuthenticatedException, UserNotFoundException;
}
