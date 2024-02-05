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
public class Text extends WhiteboardElement {
    @JsonProperty("x-position")
    private String xPosition;

    @JsonProperty("y-position")
    private String yPosition;

    @JsonProperty("text")
    private String text;

    @JsonProperty("font-size")
    private String fontSize;

    @Override
    public void updateProperty(String propertyJSONname, String value) {
        switch (propertyJSONname) {
            case "transform" -> this.transform = value;
            case "x-position" -> this.xPosition = value;
            case "y-position" -> this.yPosition = value;
            case "text" -> this.text = value;
            case "font-size" -> this.fontSize = value;
            default -> {
            }
        }
    }
}
