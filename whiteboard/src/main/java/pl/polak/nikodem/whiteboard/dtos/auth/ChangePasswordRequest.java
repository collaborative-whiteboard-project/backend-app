package pl.polak.nikodem.whiteboard.dtos.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @Size(min = 8, message = "password_too_short")
    @Size(max = 100, message = "password_too_long")
    @NotNull(message = "password_is_null")
    private String password;
}
