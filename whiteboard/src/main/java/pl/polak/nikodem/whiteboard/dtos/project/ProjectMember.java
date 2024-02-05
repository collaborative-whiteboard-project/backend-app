package pl.polak.nikodem.whiteboard.dtos.project;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import pl.polak.nikodem.whiteboard.enums.ProjectMemberRole;
import pl.polak.nikodem.whiteboard.validators.enums.projectMemberRole.ProjectMemberRoleEnumValidator;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMember {
    @Email(message = "not_an_email")
    @NotEmpty(message = "email_is_empty")
    @NotNull(message = "email_is_null")
    private String memberEmail;

    @ProjectMemberRoleEnumValidator(enumClass = ProjectMemberRole.class, message = "not_project_member_role_enum")
    private ProjectMemberRole memberRole;
}
