package pl.polak.nikodem.whiteboard.dtos.project;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProjectMembersRequest {

    @NotNull(message = "members_is-null")
    @NotEmpty(message = "members_list_empty")
    private List<@Valid ProjectMember> members;
}
