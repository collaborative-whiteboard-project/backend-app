package pl.polak.nikodem.whiteboard.dtos.socket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteWhiteboardElementData extends WhiteboardOperationData {
    @JsonProperty("id")
    private String id;
}
