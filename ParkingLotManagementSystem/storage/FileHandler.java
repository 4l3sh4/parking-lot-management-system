package storage;

import java.io.File;
import java.io.IOException;

public class FileHandler {
    // Data directory path
    private static final String DATA_DIR = "data";
    
    // All JSON files stored in data/ folder
    public static final String SPOTS_FILE = DATA_DIR + "/Parking Spots.json";
    public static final String ACTIVE_TICKETS_FILE = DATA_DIR + "/Active Tickets.json";
    public static final String TICKETS_HISTORY_FILE = DATA_DIR + "/Tickets History.json";
    public static final String VEHICLES_FILE = DATA_DIR + "/Vehicles.json";
    public static final String USERS_FILE = DATA_DIR + "/Users.json";
    public static final String IDS_FILE = DATA_DIR + "/IDs.json";
    public static final String RESERVATIONS_FILE = DATA_DIR + "/Reservations.json";
    public static final String FINES_FILE = DATA_DIR + "/Fines.json";
    
    public static void createFilesIfNotExists() {
        // Ensure data directory exists
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
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
        if (!reservationsFile.exists()) {
            try {
                reservationsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File finesFile = new File(FINES_FILE);
        if (!finesFile.exists()) {
            try {
                finesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}