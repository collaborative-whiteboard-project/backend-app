package pl.polak.nikodem.whiteboard.dtos.project;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleProjectResponse {
    private Long id;
    private String name;
    private LocalDateTime modifiedAt;
    private List<ProjectMember> members;
}
