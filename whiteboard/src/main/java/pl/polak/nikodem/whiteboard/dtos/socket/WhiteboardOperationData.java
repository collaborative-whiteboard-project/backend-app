package pl.polak.nikodem.whiteboard.dtos.socket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class WhiteboardOperationData {
    @JsonProperty("project-id")
    private String projectId;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
}
