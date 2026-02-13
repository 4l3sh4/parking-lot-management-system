package storage;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import model.Admin;
import model.Client;
import model.User;

public class UserAdapter implements JsonSerializer<User>, JsonDeserializer<User> {

    @Override
    public User deserialize(JsonElement element, Type type,
                            JsonDeserializationContext context) throws JsonParseException {

        JsonObject obj = element.getAsJsonObject();

        // UserType missing in your JSON -> default to Client
        String userType = (obj.has("UserType") && !obj.get("UserType").isJsonNull())
                ? obj.get("UserType").getAsString()
                : "Client";

        User user = "Admin".equalsIgnoreCase(userType) ? new Admin() : new Client();

        // Match your JSON keys (lowercase)
        if (obj.has("ID")) user.setID(obj.get("ID").getAsInt());
        if (obj.has("firstName")) user.setFirstName(obj.get("firstName").getAsString());
        if (obj.has("lastName")) user.setLastName(obj.get("lastName").getAsString());
        if (obj.has("email")) user.setEmail(obj.get("email").getAsString());
        if (obj.has("password")) user.setPassword(obj.get("password").getAsString());

        return user;
    }   


    @Override
    public JsonElement serialize(User user, Type type,
                                 JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        if (user instanceof Admin) {
            jsonObject.addProperty("UserType", "Admin");
        } else {
            jsonObject.addProperty("UserType", "Client");
        }
        jsonObject.addProperty("ID", user.getID());
        jsonObject.addProperty("firstName", user.getFirstName());
        jsonObject.addProperty("lastName", user.getLastName());
        jsonObject.addProperty("email", user.getEmail());
        jsonObject.addProperty("password", user.getPassword());
        return jsonObject;
    }
}
