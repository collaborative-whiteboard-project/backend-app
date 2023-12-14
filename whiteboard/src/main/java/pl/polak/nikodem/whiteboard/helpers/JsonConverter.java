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
            Optional<WhiteboardElementFactory> whiteboardElementFactory = Optional.empty();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject elementJSON = jsonArray.getJSONObject(i);
                String elementType = elementJSON.getString("element-type");

                switch (elementType) {
                    case "RECT" -> whiteboardElementFactory = Optional.of(new RectangleFactory());
                    case "CIRCLE" -> whiteboardElementFactory = Optional.of(new CircleFactory());
                    case "LINE" -> whiteboardElementFactory = Optional.of(new LineFactory());
                    case "PATH" -> whiteboardElementFactory = Optional.of(new PathFactory());
                    case "TEXT" -> whiteboardElementFactory = Optional.of(new TextFactory());
                }
                whiteboardElementFactory.ifPresent(
                        factory -> whiteboardElements.add(factory.createWhiteboardElement(elementJSON))
                );
            }
            return whiteboardElements;
        } catch (JSONException e) {
            return null;
        }
    }
}
