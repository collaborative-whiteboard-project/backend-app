package pl.polak.nikodem.whiteboard.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.polak.nikodem.whiteboard.models.WhiteboardElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Converter
public class JsonConverter implements AttributeConverter<List<WhiteboardElement>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<WhiteboardElement> whiteboardElements) {
        try {
            return objectMapper.writeValueAsString(whiteboardElements);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<WhiteboardElement> convertToEntityAttribute(String whiteboardElementsJSON) {
        try {
            JSONArray jsonArray = new JSONArray(whiteboardElementsJSON);
            List<WhiteboardElement> whiteboardElements = new ArrayList<>();
            Optional<WhiteboardElementFactory> whiteboardElementFactory;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject elementJSON = jsonArray.getJSONObject(i);
                String elementType = elementJSON.getString("element-type");

                whiteboardElementFactory = switch (elementType) {
                    case "RECT" -> Optional.of(new RectangleFactory());
                    case "CIRCLE" -> Optional.of(new CircleFactory());
                    case "LINE" -> Optional.of(new LineFactory());
                    case "PATH" -> Optional.of(new PathFactory());
                    case "TEXT" -> Optional.of(new TextFactory());
                    default -> Optional.empty();
                };

                whiteboardElementFactory.ifPresent(
                    elementFactory -> whiteboardElements.add(elementFactory.createWhiteboardElement(elementJSON))
                );
            }
            return whiteboardElements;
        } catch (JSONException e) {
            return null;
        }
    }
}
