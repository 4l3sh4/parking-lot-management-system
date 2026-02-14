package storage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.User;
import model.Vehicle;
import model.Admin;
import model.Motorcycle;
import model.Car;
import model.SUV_Truck;
import model.Handicapped_Vehicle;

public class FileHandler {
    public static final String SPOTS_FILE = "Parking Spots.json";
    public static final String ACTIVE_TICKETS_FILE = "Active Tickets.json";
    public static final String TICKETS_HISTORY_FILE = "Tickets History.json";
    public static final String VEHICLES_FILE = "Vehicles.json";
    public static final String USERS_FILE = "Users.json";
    public static final String IDS_FILE = "IDs.json";
    public static final String RESERVATIONS_FILE = "data/Reservations.json";

    
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(User.class, new UserAdapter())
            .registerTypeAdapter(Vehicle.class, new VehicleAdapter())
            .registerTypeAdapter(Motorcycle.class, new VehicleAdapter())
            .registerTypeAdapter(Car.class, new VehicleAdapter())
            .registerTypeAdapter(SUV_Truck.class, new VehicleAdapter())
            .registerTypeAdapter(Handicapped_Vehicle.class, new VehicleAdapter())
            .setPrettyPrinting()
            .create();
    
    public static void createFilesIfNotExists() {
        File spotsFile = new File(SPOTS_FILE);
        if (!spotsFile.exists()) {
            try{
                spotsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        File activeTicketsFile = new File(ACTIVE_TICKETS_FILE);
        if (!activeTicketsFile.exists()) {
            try{
                activeTicketsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        File ticketsHistoryFile = new File(TICKETS_HISTORY_FILE);
        if (!ticketsHistoryFile.exists()) {
            try{
                ticketsHistoryFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        File vehiclesFile = new File(VEHICLES_FILE);
        if (!vehiclesFile.exists()) {
            try{
                vehiclesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        File usersFile = new File(USERS_FILE);
        if (!usersFile.exists()) {
            try{
                usersFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        File IDsFile = new File(IDS_FILE);
        if (!IDsFile.exists()) {
            try{
                IDsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        File reservationsFile = new File(RESERVATIONS_FILE);
        reservationsFile.getParentFile().mkdirs(); // ensure "data" folder exists
        if (!reservationsFile.exists()) {
            try {
                reservationsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}