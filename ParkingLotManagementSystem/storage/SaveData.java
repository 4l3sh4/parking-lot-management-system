package storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import model.ParkingSpot;
import model.Ticket;
import model.Vehicle;
import model.User;

public class SaveData {

    public static void saveSpots() {
        List<ParkingSpot> spots = DataManager.parkingSpots;
        try (FileWriter writer = new FileWriter(new File(FileHandler.SPOTS_FILE))) {
            writer.write(JSONUtil.toJson(spots));
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
        IDGeneratorState state = new IDGeneratorState(
            model.IDGenerator.getSaveNextUserID(),
            model.IDGenerator.getSaveNextSpotNum(),
            model.IDGenerator.getSaveNextTicketID(),
            model.IDGenerator.getSaveNextFineID()
        );
        try (FileWriter writer = new FileWriter(new File(FileHandler.IDS_FILE))) {
            writer.write(JSONUtil.toJson(state));
        } catch (IOException e) {
            System.err.println("Error saving ids: " + e.getMessage());
        }
    }
    
    public static void saveReservations() {
        FileHandler.createFilesIfNotExists();
        try (FileWriter writer = new FileWriter(new File(FileHandler.RESERVATIONS_FILE))) {
            writer.write(JSONUtil.toJson(DataManager.reservations));
        } catch (Exception e) {
            System.err.println("Error saving reservations: " + e.getMessage());
        }
    }

    public static void saveFines() {
        FileHandler.createFilesIfNotExists();
        try (FileWriter writer = new FileWriter(new File(FileHandler.FINES_FILE))) {
            writer.write(JSONUtil.toJson(DataManager.fines));
        } catch (Exception e) {
            System.err.println("Error saving fines: " + e.getMessage());
        }
    }

    public static void saveAll() {
        FileHandler.createFilesIfNotExists();
        
        saveSpots();
        saveActiveTickets();
        saveTicketsHistory();
        saveVehicles();
        saveUsers();
        saveIDs();
        saveReservations();
        saveFines();
    }
}
