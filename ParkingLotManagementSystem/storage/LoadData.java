package storage;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import model.ParkingSpot;
import model.Ticket;
import model.Vehicle;
import model.User;
import model.IDGenerator;
import model.SpotType;
import model.Fine;


public class LoadData {

    public static void loadSpots() {
    
        FileHandler.createFilesIfNotExists();
    
        try {
            String json = readFile(FileHandler.SPOTS_FILE);
            List<ParkingSpot> spots = JSONUtil.fromJsonArray(json, ParkingSpot.class);
    
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
        try {
            String json = readFile(FileHandler.ACTIVE_TICKETS_FILE);
            List<Ticket> activeTickets = JSONUtil.fromJsonArray(json, Ticket.class);
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
        try {
            String json = readFile(FileHandler.TICKETS_HISTORY_FILE);
            List<Ticket> ticketHistory = JSONUtil.fromJsonArray(json, Ticket.class);
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
        try {
            String json = readFile(FileHandler.VEHICLES_FILE);
            List<Vehicle> vehicles = JSONUtil.fromJsonArray(json, Vehicle.class);
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
        try {
            String json = readFile(FileHandler.USERS_FILE);
            List<User> users = JSONUtil.fromJsonArray(json, User.class);
            if (users == null)
                users = new ArrayList<>();
            DataManager.users = (ArrayList<User>) users;
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            DataManager.users = new ArrayList<>();
        }
    }

    public static void loadIDs() {
        FileHandler.createFilesIfNotExists();
        try {
            String json = readFile(FileHandler.IDS_FILE);
            IDGeneratorState state = JSONUtil.fromJson(json, IDGeneratorState.class);
            if (state == null)
                state = new IDGeneratorState();
            IDGenerator.setData(state.getNextUserID(), state.getNextSpotNum(),
                            state.getNextTicketID(), state.getNextFineID());
        } catch (IOException e) {
            System.err.println("Error loading IDs: " + e.getMessage());
        }
    }
    
    public static void loadReservations() {
        FileHandler.createFilesIfNotExists();
        try {
            String json = readFile(FileHandler.RESERVATIONS_FILE);
            List<model.Reservation> list = JSONUtil.fromJsonArray(json, model.Reservation.class);
            if (list == null) list = new ArrayList<>();
            DataManager.reservations = (ArrayList<model.Reservation>) list;
        } catch (Exception e) {
            System.err.println("Error loading reservations: " + e.getMessage());
            DataManager.reservations = new ArrayList<>();
        }
    }

    public static void loadFines() {
        FileHandler.createFilesIfNotExists();
        try {
            String json = readFile(FileHandler.FINES_FILE);
            List<Fine> fines = JSONUtil.fromJsonArray(json, Fine.class);
            if (fines == null) fines = new ArrayList<>();
            DataManager.fines = (ArrayList<Fine>) fines;
        } catch (Exception e) {
            System.err.println("Error loading fines: " + e.getMessage());
            DataManager.fines = new ArrayList<>();
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
        loadReservations();
        loadFines();
    }
    
    // Helper method to read file content
    private static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
}
