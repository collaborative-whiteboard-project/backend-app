package pl.polak.nikodem.whiteboard.dtos.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProjectRequest {

    @NotNull(message = "project_name_id_null")
    @NotEmpty(message = "project_name_is_empty")
    @NotBlank(message = "project_name_is_empty")
    @Size(max = 100, message = "project_name_too_long")
    private String projectName;
}
