package pl.polak.nikodem.whiteboard.dtos.project;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleProjectResponse {
    private Long id;
    private String name;
    private LocalDateTime modifiedAt;
}
