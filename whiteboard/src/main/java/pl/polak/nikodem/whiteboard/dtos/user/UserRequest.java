package pl.polak.nikodem.whiteboard.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @Email(message = "not_an_email")
    @NotEmpty(message = "email_is_empty")
    @NotNull(message = "email_is_null")
    private String email;
}
