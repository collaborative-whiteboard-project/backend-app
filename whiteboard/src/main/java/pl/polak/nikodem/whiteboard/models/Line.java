package pl.polak.nikodem.whiteboard.models;

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
public class Line extends WhiteboardElement{
    @JsonProperty("x1-position")
    private String x1Position;

    @JsonProperty("y1-position")
    private String y1Position;

    @JsonProperty("x2-position")
    private String x2Position;

    @JsonProperty("y2-position")
    private String y2Position;

    @JsonProperty("stroke-width")
    private String strokeWidth;

    @JsonProperty("stroke-color")
    private String strokeColor;
}
