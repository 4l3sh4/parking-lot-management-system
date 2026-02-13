package storage;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import model.Vehicle;
import model.Motorcycle;
import model.Car;
import model.SUV_Truck;
import model.Handicapped_Vehicle;
import model.Color;


public class VehicleAdapter implements JsonSerializer<Vehicle>, JsonDeserializer<Vehicle> {

    @Override
    public Vehicle deserialize(JsonElement element, Type type,
                               JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = element.getAsJsonObject();
        String vehicleType = jsonObject.get("VehicleType").getAsString();
        String plate = jsonObject.get("LicensePlateNumber").getAsString();
        
        Vehicle vehicle;

        switch (vehicleType) {

            case "MOTORCYCLE":
                vehicle = new Motorcycle(plate);
                break;

            case "CAR":
                vehicle = new Car(plate);
                break;

            case "SUV_TRUCK":
                vehicle = new SUV_Truck(plate);
                break;

            case "HANDICAPPED_VEHICLE":
                boolean hasCard = jsonObject.has("HasHandicappedCard")
                    && !jsonObject.get("HasHandicappedCard").isJsonNull()
                    && jsonObject.get("HasHandicappedCard").getAsBoolean();
                vehicle = new Handicapped_Vehicle(plate, hasCard);
                break;
                
            default:
                return null;
        }
        vehicle.setLicensePlateNumber(plate);
        vehicle.setLicensePlateNumber(jsonObject.get("LicensePlateNumber").getAsString());
        vehicle.setVehicleOwnerID(jsonObject.get("OwnerID").getAsInt());
        vehicle.setColor(context.deserialize(jsonObject.get("Color"), Color.class));
        vehicle.setBrand(jsonObject.get("Brand").getAsString());
        vehicle.setModel(jsonObject.get("Model").getAsString());
        return vehicle;
    }

    @Override
    public JsonElement serialize(Vehicle vehicle, Type type,
                        JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        if (vehicle instanceof Motorcycle) {
            jsonObject.addProperty("VehicleType", "MOTORCYCLE");
        } else if (vehicle instanceof Car) {
            jsonObject.addProperty("VehicleType", "CAR");
        } else if (vehicle instanceof SUV_Truck) {
            jsonObject.addProperty("VehicleType", "SUV_TRUCK");
        } else if (vehicle instanceof Handicapped_Vehicle) {
            jsonObject.addProperty("VehicleType", "HANDICAPPED_VEHICLE");
        } else {
            return null;
        }
        jsonObject.addProperty("LicensePlateNumber", vehicle.getLicensePlateNumber());
        jsonObject.addProperty("OwnerID", vehicle.getVehicleOwnerID());
        jsonObject.add("Color", context.serialize(vehicle.getColor(), Color.class));
        jsonObject.addProperty("Brand", vehicle.getBrand());
        jsonObject.addProperty("Model", vehicle.getModel());
        return jsonObject;
    }
}
