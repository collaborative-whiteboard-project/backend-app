package pl.polak.nikodem.whiteboard.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {
    String email;
    String password;
}
