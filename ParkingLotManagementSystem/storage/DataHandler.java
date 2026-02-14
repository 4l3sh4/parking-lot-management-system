package storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import model.ParkingSpot;
import model.Ticket;
import model.Vehicle;
import model.User;
import model.IDGeneratorState;
import storage.DataManager;

public class DataHandler {

    public static void saveSpots() {
        List<ParkingSpot> slots = DataManager.parkingSpots;
        try (FileWriter writer = new FileWriter(new File(FileHandler.SPOTS_FILE))) {
            writer.write(JSONUtil.toJson(slots));
        } catch (IOException e) {
            System.err.println("Error saving spots: " + e.getMessage());
        }
    }

    public static void saveActiveTickets() {
        List<Ticket> activeTickets = DataManager.activeTickets;
        try (FileWriter writer = new FileWriter(new File(FileHandler.ACTIVE_TICKETS_FILE))) {
            writer.write(JSONUtil.toJson(activeTickets));
        } catch (IOException e) {
            System.err.println("Error saving active tickets: " + e.getMessage());
        }
    }
    
    public static void saveTicketsHistory() {
        List<Ticket> ticketsHistory = DataManager.ticketHistory;
        try (FileWriter writer = new FileWriter(new File(FileHandler.TICKETS_HISTORY_FILE))) {
            writer.write(JSONUtil.toJson(ticketsHistory));
        } catch (IOException e) {
            System.err.println("Error saving tickets history: " + e.getMessage());
        }
    }
        
    public static void saveVehicles() {
        List<Vehicle> vehicles = DataManager.registeredVehicles;
        try (FileWriter writer = new FileWriter(new File(FileHandler.VEHICLES_FILE))) {
            writer.write(JSONUtil.toJson(vehicles));
        } catch (IOException e) {
            System.err.println("Error saving vehicles: " + e.getMessage());
        }
    }

    public static void saveUsers() {
        List<User> users = DataManager.users;
        try (FileWriter writer = new FileWriter(new File(FileHandler.USERS_FILE))) {
            writer.write(JSONUtil.toJson(users));
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public static void saveIDs() {
        IDGeneratorState state = new IDGeneratorState();
        try (FileWriter writer = new FileWriter(new File(FileHandler.IDS_FILE))) {
            writer.write(JSONUtil.toJson(state));
        } catch (IOException e) {
            System.err.println("Error saving ids: " + e.getMessage());
        }
    }

    public static void saveAll() {
        saveSpots();
        saveActiveTickets();
        saveTicketsHistory();
        saveVehicles();
        saveUsers();
        saveIDs();
    }  
}
