package pl.polak.nikodem.whiteboard.helpers;

import org.json.JSONObject;
import pl.polak.nikodem.whiteboard.models.WhiteboardElement;

public interface WhiteboardElementFactory {
    public WhiteboardElement createWhiteboardElement(JSONObject elementJSON);
}
