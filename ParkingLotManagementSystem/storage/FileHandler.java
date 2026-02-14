package storage;

import java.io.File;
import java.io.IOException;

public class FileHandler {
    public static final String SPOTS_FILE = "Parking Spots.json";
    public static final String ACTIVE_TICKETS_FILE = "Active Tickets.json";
    public static final String TICKETS_HISTORY_FILE = "Tickets History.json";
    public static final String VEHICLES_FILE = "Vehicles.json";
    public static final String USERS_FILE = "Users.json";
    public static final String IDS_FILE = "IDs.json";
    public static final String RESERVATIONS_FILE = "data/Reservations.json";
    
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