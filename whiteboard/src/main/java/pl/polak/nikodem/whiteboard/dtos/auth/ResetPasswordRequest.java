package pl.polak.nikodem.whiteboard.dtos.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    @NotEmpty(message = "reset_token_is_empty")
    @NotNull(message = "reset_token_is_null")
    String resetToken;

    @Size(min = 8, message = "password_too_short")
    @Size(max = 100, message = "password_too_long")
    @NotNull(message = "password_is_null")
    String password;
}
