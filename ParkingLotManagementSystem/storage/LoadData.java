package storage;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import com.google.gson.reflect.TypeToken;

import model.ParkingSpot;
import model.Ticket;
import model.Vehicle;
import model.User;
import model.IDGenerator;
import model.IDGeneratorState;
import model.SpotType;
import storage.SaveData;


public class LoadData {

    public static void loadSpots() {
    
        FileHandler.createFilesIfNotExists();
    
        try (FileReader reader = new FileReader(new File(FileHandler.SPOTS_FILE))) {
    
            Type listType = new TypeToken<ArrayList<ParkingSpot>>() {}.getType();
            List<ParkingSpot> spots = FileHandler.gson.fromJson(reader, listType);
    
            if (spots == null || spots.isEmpty()) {
                initDefaultSpots();
            } else {
                DataManager.parkingSpots = (ArrayList<ParkingSpot>) spots;
            }
    
        } catch (Exception e) {
            initDefaultSpots();
        }
    }
    
        
    private static void initDefaultSpots() {
    
        DataManager.parkingSpots = new ArrayList<>();
    
        int floors = 4;
        int rows = 4;
        int slotsPerRow = 10;
    
        for (int f = 1; f <= floors; f++) {
    
            for (int r = 1; r <= rows; r++) {
    
                for (int s = 1; s <= slotsPerRow; s++) {
    
                    String id = "F" + f + "-R" + r + "-S" + s;
    
                    SpotType type;
    
                    // RULES (edit if you want)
                    if (s == 1) {
                        type = SpotType.HANDICAPPED;
                    }
                    else if (s == 2) {
                        type = SpotType.RESERVED;
                    }
                    else if (s <= 5) {
                        type = SpotType.COMPACT;
                    }
                    else {
                        type = SpotType.REGULAR;
                    }
    
                    DataManager.parkingSpots.add(
                            new ParkingSpot(id, type)
                    );
                }
            }
        }
    }

    public static void loadActiveTickets() {
        FileHandler.createFilesIfNotExists();
        try (FileReader reader = new FileReader(new File(FileHandler.ACTIVE_TICKETS_FILE))) {
            Type listType = new TypeToken<ArrayList<Ticket>>() {}.getType();
            List<Ticket> activeTickets = FileHandler.gson.fromJson(reader, listType);
            if (activeTickets == null)
                activeTickets = new ArrayList<>();
            DataManager.activeTickets = (ArrayList<Ticket>) activeTickets;
        } catch (IOException e) {
            System.err.println("Error loading active tickets: " + e.getMessage());
            DataManager.activeTickets = new ArrayList<>();
        }
    }
    
    public static void loadTicketsHistory() {
        FileHandler.createFilesIfNotExists();
        try (FileReader reader = new FileReader(new File(FileHandler.TICKETS_HISTORY_FILE))) {
            Type listType = new TypeToken<ArrayList<Ticket>>() {}.getType();
            List<Ticket> ticketHistory = FileHandler.gson.fromJson(reader, listType);
            if (ticketHistory == null)
                ticketHistory = new ArrayList<>();
            DataManager.ticketHistory = (ArrayList<Ticket>) ticketHistory;
        } catch (IOException e) {
            System.err.println("Error loading tickets history: " + e.getMessage());
            DataManager.ticketHistory = new ArrayList<>();
        }
    }

    public static void loadVehicles() {
        FileHandler.createFilesIfNotExists();
        try (FileReader reader = new FileReader(new File(FileHandler.VEHICLES_FILE))) {
            Type listType = new TypeToken<ArrayList<Vehicle>>() {}.getType();
            List<Vehicle> vehicles = FileHandler.gson.fromJson(reader, listType);
            if (vehicles == null)
                vehicles = new ArrayList<>();
            DataManager.registeredVehicles = (ArrayList<Vehicle>) vehicles;
        } catch (IOException e) {
            System.err.println("Error loading registered vehicles: " + e.getMessage());
            DataManager.registeredVehicles = new ArrayList<>();
        }
    }

    public static void loadUsers() {
        FileHandler.createFilesIfNotExists();
        try (FileReader reader = new FileReader(new File(FileHandler.USERS_FILE))) {
    
            com.google.gson.JsonElement root = com.google.gson.JsonParser.parseReader(reader);
    
            ArrayList<User> users = new ArrayList<>();
    
            if (root != null && root.isJsonArray()) {
                com.google.gson.JsonArray arr = root.getAsJsonArray();
    
                for (com.google.gson.JsonElement el : arr) {
                    if (!el.isJsonObject()) continue;
    
                    com.google.gson.JsonObject o = el.getAsJsonObject();
    
                    int id = o.get("ID").getAsInt();
                    String first = o.get("firstName").getAsString();
                    String last = o.get("lastName").getAsString();
                    String email = o.get("email").getAsString();
                    String pass = o.get("password").getAsString();
    
                    String role = "CLIENT";
                    if (o.has("role") && !o.get("role").isJsonNull()) {
                        role = o.get("role").getAsString();
                    }
    
                    User u;
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        u = new model.Admin(id, first, last, email, pass);
                    } else {
                        u = new model.Client(id, first, last, email, pass);
                    }
    
                    users.add(u);
                }
            }
    
            DataManager.users = users;
    
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            DataManager.users = new ArrayList<>();
        }
    }

    public static void loadIDs() {
        FileHandler.createFilesIfNotExists();
        try (FileReader reader = new FileReader(new File(FileHandler.IDS_FILE))) {
            Type type = new TypeToken<IDGeneratorState>() {}.getType();
            IDGeneratorState state = FileHandler.gson.fromJson(reader, type);
            if (state == null)
                state = new IDGeneratorState();
            IDGenerator.setData(state.getNextUserID(), state.getNextSpotNum(),
                            state.getNextTicketID());
        } catch (IOException e) {
            System.err.println("Error loading IDs: " + e.getMessage());
        }
    }
    
    public static void loadAllData() {
        FileHandler.createFilesIfNotExists();
        loadSpots();
        loadActiveTickets();
        loadTicketsHistory();
        loadVehicles();
        loadUsers();
        loadIDs();
    }
    
}
