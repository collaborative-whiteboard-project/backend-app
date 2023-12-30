package pl.polak.nikodem.whiteboard.services.interfaces;

import pl.polak.nikodem.whiteboard.dtos.auth.JwtAuthenticationResponse;
import pl.polak.nikodem.whiteboard.dtos.auth.SignInRequest;
import pl.polak.nikodem.whiteboard.dtos.auth.SignUpRequest;

public interface AuthenticationService {

    JwtAuthenticationResponse signup(SignUpRequest request);

    JwtAuthenticationResponse signin(SignInRequest request);
}
