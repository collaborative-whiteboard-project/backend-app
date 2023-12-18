package pl.polak.nikodem.whiteboard.helpers;

import org.json.JSONException;
import org.json.JSONObject;
import pl.polak.nikodem.whiteboard.enums.ElementType;
import pl.polak.nikodem.whiteboard.models.Circle;
import pl.polak.nikodem.whiteboard.models.WhiteboardElement;

public class CircleFactory implements WhiteboardElementFactory {
    @Override
    public WhiteboardElement createWhiteboardElement(JSONObject elementJSON) {
        try {
            return Circle.builder()
                         .elementType(ElementType.CIRCLE)
                         .id(elementJSON.getString("id"))
                         .transform(elementJSON.getString("transform"))
                         .xPosition(elementJSON.getString("x-position"))
                         .yPosition(elementJSON.getString("y-position"))
                         .radius(elementJSON.getString("radius"))
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
