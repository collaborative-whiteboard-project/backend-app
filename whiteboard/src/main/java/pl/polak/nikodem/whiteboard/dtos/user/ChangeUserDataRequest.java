package pl.polak.nikodem.whiteboard.dtos.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import pl.polak.nikodem.whiteboard.enums.UserRole;
import pl.polak.nikodem.whiteboard.validators.enums.EnumValidator;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUserDataRequest {
    @NotNull(message = "id_is_null")
    private Long id;

    @NotNull(message = "first_name_is_null")
    @Size(min = 2, message = "first_name_too_short")
    @Size(max = 30, message = "first_name_too_long")
    private String firstName;

    private String lastName;

    @NotNull(message = "role_is_ull")
    @EnumValidator(enumClass = UserRole.class, message = "not_user_role_enum")
    private UserRole role;
}
