package pl.polak.nikodem.whiteboard.dtos.socket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import pl.polak.nikodem.whiteboard.models.WhiteboardElement;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWhiteboardElementData extends WhiteboardOperationData {
    @JsonProperty("element")
    private WhiteboardElement element;
}
