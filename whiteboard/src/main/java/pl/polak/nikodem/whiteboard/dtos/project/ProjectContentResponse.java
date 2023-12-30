package pl.polak.nikodem.whiteboard.dtos.project;

import lombok.*;
import pl.polak.nikodem.whiteboard.models.WhiteboardElement;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectContentResponse {
    private List<WhiteboardElement> elements;
    private LocalDateTime modifiedAt;
}

