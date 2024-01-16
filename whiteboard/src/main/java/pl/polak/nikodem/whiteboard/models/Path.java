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
public class Path extends WhiteboardElement {
    @JsonProperty("path")
    private String path; // d attribute from svg's path element: https://developer.mozilla.org/en-US/docs/Web/SVG/Tutorial/Paths

    @JsonProperty("fill-color")
    private String fillColor;

    @JsonProperty("stroke-width")
    private String strokeWidth;

    @JsonProperty("stroke-color")
    private String strokeColor;

    @JsonProperty("fill-opacity")
    private String fillOpacity;

    @Override
    public void updateProperty(String propertyJSONname, String value) {
        switch (propertyJSONname) {
            case "transform" -> this.transform = value;
            case "path" -> this.path = value;
            case "fill-color" -> this.fillColor = value;
            case "stroke-width" -> this.strokeWidth = value;
            case "stroke-color" -> this.strokeColor = value;
            case "fill-opacity" -> this.fillOpacity = value;
            default -> {
            }
        }
    }
}
