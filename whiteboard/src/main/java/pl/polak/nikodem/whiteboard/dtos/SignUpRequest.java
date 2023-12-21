package pl.polak.nikodem.whiteboard.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    String email;
    String password;
}
