package pl.polak.nikodem.whiteboard.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.polak.nikodem.whiteboard.dtos.auth.*;
import pl.polak.nikodem.whiteboard.entities.PasswordResetToken;
import pl.polak.nikodem.whiteboard.entities.User;
import pl.polak.nikodem.whiteboard.enums.UserRole;
import pl.polak.nikodem.whiteboard.exceptions.UserNotAuthenticatedException;
import pl.polak.nikodem.whiteboard.exceptions.UserNotFoundException;
import pl.polak.nikodem.whiteboard.repositories.PasswordResetTokenRepository;
import pl.polak.nikodem.whiteboard.repositories.UserRepository;
import pl.polak.nikodem.whiteboard.services.interfaces.AuthenticationService;
import pl.polak.nikodem.whiteboard.services.interfaces.MailSenderService;

import java.time.LocalDateTime;
import java.util.UUID;


@Service(value = "AuthServiceImpl")
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtService;
    private final MailSenderService mailSenderService;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final long resetPasswordTokenExpiryTime = 1L; // In hours

    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        User user = User.builder()
                        .email(request.getEmail())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(UserRole.REGULAR_USER)
                        .build();

        user = userService.save(user);
        String jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    @Override
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
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    @Override
    public void sendResetPasswordEmail(SendResetPasswordEmailRequest request) throws UserNotFoundException {
        User user = userRepository.findByEmail(request.getEmail())
                                  .orElseThrow(() -> new UserNotFoundException("User with email not found"));
        String resetToken = UUID.randomUUID().toString();
        this.createPasswordResetTokenForUser(user, resetToken);
        String passwordResetMessage = this.mailSenderService.prepareResetPasswordMailText(resetToken );
        this.mailSenderService.sendResetPasswordMessage(request.getEmail(), "Collaborative whiteboard - reset password", passwordResetMessage);
    }

    @Override
    public void resetUserPassword(ResetPasswordRequest request) throws UserNotFoundException {
        PasswordResetToken token = this.passwordResetTokenRepository.findByToken(request.getResetToken())
                                                                    .orElseThrow(() -> new UserNotFoundException("User with reset token not found"));
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.save(user);
    }


    private void createPasswordResetTokenForUser(User user, String resetToken) {
        LocalDateTime now = LocalDateTime.now();
        PasswordResetToken token = PasswordResetToken.builder()
                                                     .user(user)
                                                     .token(resetToken)
                                                     .expiryDate(now.plusHours(resetPasswordTokenExpiryTime))
                                                     .build();
        this.passwordResetTokenRepository.save(token);
    }

}
