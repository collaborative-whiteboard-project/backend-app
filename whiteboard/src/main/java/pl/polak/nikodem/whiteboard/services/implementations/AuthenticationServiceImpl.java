package pl.polak.nikodem.whiteboard.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.polak.nikodem.whiteboard.dtos.auth.ChangePasswordRequest;
import pl.polak.nikodem.whiteboard.dtos.auth.JwtAuthenticationResponse;
import pl.polak.nikodem.whiteboard.dtos.auth.SignInRequest;
import pl.polak.nikodem.whiteboard.dtos.auth.SignUpRequest;
import pl.polak.nikodem.whiteboard.entities.User;
import pl.polak.nikodem.whiteboard.enums.UserRole;
import pl.polak.nikodem.whiteboard.exceptions.UserNotAuthenticatedException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotFoundException;
import pl.polak.nikodem.whiteboard.repositories.UserRepository;
import pl.polak.nikodem.whiteboard.services.interfaces.AuthenticationService;


@Service(value = "AuthServiceImpl")
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse signup(SignUpRequest request) {
        User user = User.builder()
                       .email(request.getEmail())
                       .password(passwordEncoder.encode(request.getPassword()))
                       .role(UserRole.REGULAR_USER)
                       .build();

        user = userService.save(user);
        String jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    public JwtAuthenticationResponse signin(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail())
                                 .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        String jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    @Override
    public JwtAuthenticationResponse changePassword(ChangePasswordRequest request) throws UserNotAuthenticatedException, UserNotFoundException {
        String userEmail = this.userService.getAuthenticatedUserEmail();
        User user = this.userRepository.findByEmail(userEmail)
                                       .orElseThrow(() -> new UserNotFoundException("User not found"));
       user.setPassword(passwordEncoder.encode(request.getPassword()));
       user = userService.save(user);
       String jwt = jwtService.generateToken(user);
       return  JwtAuthenticationResponse.builder().token(jwt).build();
    }
}
