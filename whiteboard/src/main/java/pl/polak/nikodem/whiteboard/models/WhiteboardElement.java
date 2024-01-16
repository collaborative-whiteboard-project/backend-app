package pl.polak.nikodem.whiteboard.models;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public abstract class WhiteboardElement {
    @JsonProperty("id")
    protected String id;

    @JsonProperty("element-type")
    protected ElementType elementType;

    @JsonProperty("transform")
    protected String transform;

    abstract public void updateProperty(String propertyJSONname, String value);
}
