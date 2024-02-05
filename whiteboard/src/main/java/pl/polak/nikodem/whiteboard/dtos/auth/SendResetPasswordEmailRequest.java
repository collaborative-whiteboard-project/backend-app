package pl.polak.nikodem.whiteboard.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendResetPasswordEmailRequest {
    @Email(message = "not_an_email")
    @NotEmpty(message = "email_is_empty")
    @NotNull(message = "email_is_null")
    private String email;
}
