package pl.polak.nikodem.whiteboard.helpers;

import org.json.JSONException;
import org.json.JSONObject;
import pl.polak.nikodem.whiteboard.enums.ElementType;
import pl.polak.nikodem.whiteboard.models.Text;
import pl.polak.nikodem.whiteboard.models.WhiteboardElement;

public class TextFactory implements WhiteboardElementFactory {
    @Override
    public WhiteboardElement createWhiteboardElement(JSONObject elementJSON) {
        try {
            return Text.builder()
                       .elementType(ElementType.TEXT)
                       .id(elementJSON.getString("id"))
                       .transform(elementJSON.getString("transform"))
                       .xPosition(elementJSON.getString("x-position"))
                       .yPosition(elementJSON.getString("y-position"))
                       .text(elementJSON.getString("text"))
                       .fontSize(elementJSON.getString("font-size"))
                       .build();
        } catch (JSONException e) {
            return null;
        }
    }
}
