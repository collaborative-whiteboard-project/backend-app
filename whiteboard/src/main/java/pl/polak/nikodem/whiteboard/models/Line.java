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
    @JsonProperty("lini ")
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

    @Override
    public void updateProperty(String propertyJSONname, String value) {
        switch (propertyJSONname) {
            case "transform" -> this.transform = value;
            case "x1-position" -> this.x1Position = value;
            case "y1-position" -> this.y1Position = value;
            case "x2-position" -> this.x2Position = value;
            case "y2-position" -> this.y2Position = value;
            case "stroke-width" -> this.strokeWidth = value;
            case "stroke-color" -> this.strokeColor = value;
            default -> {
            }
        }
    }
}
