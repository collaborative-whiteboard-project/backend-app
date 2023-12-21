package pl.polak.nikodem.whiteboard.services.interfaces;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import pl.polak.nikodem.whiteboard.dtos.JwtAuthenticationResponse;
import pl.polak.nikodem.whiteboard.dtos.SignInRequest;
import pl.polak.nikodem.whiteboard.dtos.SignUpRequest;
import pl.polak.nikodem.whiteboard.entities.User;
import pl.polak.nikodem.whiteboard.enums.UserRole;

public interface AuthenticationService {

    JwtAuthenticationResponse signup(SignUpRequest request);

    JwtAuthenticationResponse signin(SignInRequest request);
}
