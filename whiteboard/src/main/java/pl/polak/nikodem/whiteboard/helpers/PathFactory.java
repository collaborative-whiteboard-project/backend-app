package pl.polak.nikodem.whiteboard.helpers;

import org.json.JSONException;
import org.json.JSONObject;
import pl.polak.nikodem.whiteboard.enums.ElementType;
import pl.polak.nikodem.whiteboard.models.Path;
import pl.polak.nikodem.whiteboard.models.WhiteboardElement;

public class PathFactory implements WhiteboardElementFactory {
    @Override
    public WhiteboardElement createWhiteboardElement(JSONObject elementJSON) {
        try {
            return Path.builder()
                       .elementType(ElementType.PATH)
                       .id(elementJSON.getString("id"))
                       .transform(elementJSON.getString("transform"))
                       .path(elementJSON.getString("path"))
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
