package pl.polak.nikodem.whiteboard.dtos.project;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeProjectDataRequest {
    @NotNull(message = "project_name_id_null")
    @NotEmpty(message = "project_name_is_empty")
    @Size(max = 100, message = "project_name_too_long")
    private String projectName;

    @NotNull(message = "members_is-null")
    @NotEmpty(message = "members_list_empty")
    private List<@Valid ProjectMember> members;
}
