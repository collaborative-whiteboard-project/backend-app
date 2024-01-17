package pl.polak.nikodem.whiteboard.dtos.project;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private List<ProjectMember> members;
    private LocalDateTime modifiedAt;
}
