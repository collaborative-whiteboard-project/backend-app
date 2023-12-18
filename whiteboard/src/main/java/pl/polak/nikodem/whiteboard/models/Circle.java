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
public class Circle extends WhiteboardElement {
    @JsonProperty("x-position")
    private String xPosition;

    @JsonProperty("y-position")
    private String yPosition;

    @JsonProperty("radius")
    private String radius;

    @JsonProperty("fill-color")
    private String fillColor;

    @JsonProperty("stroke-width")
    private String strokeWidth;

    @JsonProperty("stroke-color")
    private String strokeColor;

    @JsonProperty("fill-opacity")
    private String fillOpacity;
}
