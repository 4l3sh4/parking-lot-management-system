package storage;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>,
                JsonDeserializer<LocalDateTime> {
                
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    @Override
    public JsonElement serialize(LocalDateTime dateTime, Type type,
                        JsonSerializationContext context) {
        return new JsonPrimitive(dateTime.format(formatter));                     
    }
    
    @Override
    public LocalDateTime deserialize(JsonElement element, Type type,
                        JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDateTime.parse(element.getAsString(), formatter);
    }
}