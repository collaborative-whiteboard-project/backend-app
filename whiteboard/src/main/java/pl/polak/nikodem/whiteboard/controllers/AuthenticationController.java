package pl.polak.nikodem.whiteboard.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.polak.nikodem.whiteboard.dtos.JwtAuthenticationResponse;
import pl.polak.nikodem.whiteboard.dtos.SignInRequest;
import pl.polak.nikodem.whiteboard.dtos.SignUpRequest;
import pl.polak.nikodem.whiteboard.services.interfaces.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(value = "/signup", consumes = "application/json")
    public JwtAuthenticationResponse signup(@RequestBody SignUpRequest request) {
        return authenticationService.signup(request);
    }

    @PostMapping(value = "signin")
    public JwtAuthenticationResponse signin(@RequestBody SignInRequest request) {
        return authenticationService.signin(request);
    }
}
