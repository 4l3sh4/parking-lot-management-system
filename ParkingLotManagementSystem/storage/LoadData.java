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

public class LoadData {

    public static void loadSpots() {
        FileHandler.createFilesIfNotExists();
        try (FileReader reader = new FileReader(new File(FileHandler.SPOTS_FILE))) {
            Type listType = new TypeToken<ArrayList<ParkingSpot>>() {}.getType();
            List<ParkingSpot> spots = FileHandler.gson.fromJson(reader, listType);
            if (spots == null)
                spots = new ArrayList<>();
            DataManager.parkingSpots = (ArrayList<ParkingSpot>) spots;
        } catch (IOException e) {
            System.err.println("Error loading slots: " + e.getMessage());
            DataManager.parkingSpots = new ArrayList<>();
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
            Type listType = new TypeToken<ArrayList<User>>() {}.getType();
            List<User> users = FileHandler.gson.fromJson(reader, listType);
            if (users == null)
                users = new ArrayList<>();
            DataManager.users = (ArrayList<User>) users;
        } catch (IOException e) {
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
