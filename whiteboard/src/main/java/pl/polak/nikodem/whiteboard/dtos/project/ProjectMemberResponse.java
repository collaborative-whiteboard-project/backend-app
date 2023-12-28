package pl.polak.nikodem.whiteboard.dtos.project;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberResponse {
    private Long id;
    private String email;
    private String memberRole;
}
