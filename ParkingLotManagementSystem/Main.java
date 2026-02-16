import gui.NavigationHandler;
import storage.LoadData;

import storage.DataManager;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        // Load all data (if files are empty / missing, LoadData will create defaults where needed)
        LoadData.loadAllData();

        // Safety: make sure lists are not null (so system starts clean with empty DB)
        if (DataManager.users == null) DataManager.users = new ArrayList<>();
        if (DataManager.registeredVehicles == null) DataManager.registeredVehicles = new ArrayList<>();
        if (DataManager.activeTickets == null) DataManager.activeTickets = new ArrayList<>();
        if (DataManager.ticketHistory == null) DataManager.ticketHistory = new ArrayList<>();
        if (DataManager.fines == null) DataManager.fines = new ArrayList<>();
        if (DataManager.reservations == null) DataManager.reservations = new ArrayList<>();

        NavigationHandler.initialize();
    }
}
