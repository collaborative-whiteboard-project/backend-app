package pl.polak.nikodem.whiteboard.helpers;

import org.json.JSONException;
import org.json.JSONObject;
import pl.polak.nikodem.whiteboard.enums.ElementType;
import pl.polak.nikodem.whiteboard.models.Rectangle;
import pl.polak.nikodem.whiteboard.models.WhiteboardElement;

public class RectangleFactory implements WhiteboardElementFactory {
    @Override
    public WhiteboardElement createWhiteboardElement(JSONObject elementJSON) {
        try {
            return Rectangle.builder()
                            .elementType(ElementType.RECTANGLE)
                            .id(elementJSON.getString("id"))
                            .transform(elementJSON.getString("transform"))
                            .xPosition(elementJSON.getString("x-position"))
                            .yPosition(elementJSON.getString("y-position"))
                            .width(elementJSON.getString("width"))
                            .height(elementJSON.getString("height"))
                            .strokeWidth(elementJSON.getString("stroke-width"))
                            .strokeColor(elementJSON.getString("stroke-color"))
                            .fillColor(elementJSON.getString("fill-color"))
                            .fillOpacity(elementJSON.getString("fill-opacity"))
                            .build();
        } catch (JSONException e) {
            return null;
        }
    }
}

