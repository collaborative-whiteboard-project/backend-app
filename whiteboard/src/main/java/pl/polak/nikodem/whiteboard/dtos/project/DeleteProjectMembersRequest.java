package pl.polak.nikodem.whiteboard.dtos.project;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteProjectMembersRequest {
    @NotEmpty(message = "emails_list_empty")
    List<String> emails;
}
