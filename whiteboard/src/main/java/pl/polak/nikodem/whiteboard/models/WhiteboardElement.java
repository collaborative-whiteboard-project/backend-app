package pl.polak.nikodem.whiteboard.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import pl.polak.nikodem.whiteboard.enums.ElementType;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY ,property = "element-type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Rectangle.class, name = "RECTANGLE"),
        @JsonSubTypes.Type(value = Circle.class, name = "CIRCLE"),
        @JsonSubTypes.Type(value = Line.class, name = "LINE"),
        @JsonSubTypes.Type(value = Path.class, name = "PATH"),
        @JsonSubTypes.Type(value = Text.class, name = "TEXT"),
})
public abstract class WhiteboardElement {
    @JsonProperty("id")
    protected String id;

    @JsonProperty("element-type")
    protected ElementType elementType;

    @JsonProperty("transform")
    protected String transform;

    abstract public void updateProperty(String propertyJSONname, String value);
}
