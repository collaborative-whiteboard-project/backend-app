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
public class Rectangle extends WhiteboardElement {
    @JsonProperty("x-position")
    private String xPosition;

    @JsonProperty("y-position")
    private String yPosition;

    @JsonProperty("width")
    private String width;

    @JsonProperty("height")
    private String height;

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
            case "x-position" -> this.xPosition = value;
            case "y-position" -> this.yPosition = value;
            case "width" -> this.width = value;
            case "height" -> this.height = value;
            case "fill-color" -> this.fillColor = value;
            case "stroke-width" -> this.strokeWidth = value;
            case "stroke-color" -> this.strokeColor = value;
            case "fill-opacity" -> this.fillOpacity = value;
            default -> {
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rectangle rect) {
            return this.id == rect.id &&
                    this.xPosition == rect.xPosition &&
                    this.yPosition == rect.yPosition &&
                    this.width == rect.width &&
                    this.height == rect.height &&
                    this.strokeColor == rect.strokeColor &&
                    this.strokeWidth == rect.strokeWidth &&
                    this.fillColor == rect.fillColor &&
                    this.fillOpacity == rect.fillOpacity &&
                    this.elementType == rect.elementType &&
                    this.transform == rect.transform;

        }
        return false;
    }
}
