package pl.polak.nikodem.whiteboard.helpers;

import org.json.JSONException;
import org.json.JSONObject;
import pl.polak.nikodem.whiteboard.enums.ElementType;
import pl.polak.nikodem.whiteboard.models.Line;
import pl.polak.nikodem.whiteboard.models.WhiteboardElement;

public class LineFactory implements WhiteboardElementFactory {
    @Override
    public WhiteboardElement createWhiteboardElement(JSONObject elementJSON) {
        try {
            return Line.builder()
                       .elementType(ElementType.LINE)
                       .id(elementJSON.getString("id"))
                       .transform(elementJSON.getString("transform"))
                       .x1Position(elementJSON.getString("x1-position"))
                       .y1Position(elementJSON.getString("y1-position"))
                       .x2Position(elementJSON.getString("x2-position"))
                       .y2Position(elementJSON.getString("y2-position"))
                       .strokeWidth(elementJSON.getString("stroke-width"))
                       .strokeColor(elementJSON.getString("stroke-color"))
                       .build();
        } catch (JSONException e) {
            return null;
        }
    }
}
