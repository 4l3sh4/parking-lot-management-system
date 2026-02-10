import java.util.ArrayList;
import java.util.Scanner;

import cli.NavigationHandler;
import storage.DataManager;

import model.ParkingSpot;
import model.SpotType;
/**
 * Enumeration class VehicleType - write a description of the enum class here
 *
 * @author (your name here)
 * @version (version number or date here)
 */


public class Main {
    
    public static void main(String[] args) {
        
        DataManager.activeTickets = new ArrayList<>();
        DataManager.parkingSpots = new ArrayList<>();
        DataManager.registeredVehicles = new ArrayList<>();
        DataManager.ticketHistory = new ArrayList<>();
        DataManager.users = new ArrayList<>();
        
        NavigationHandler.welcome(new Scanner(System.in));
        
    }
    
    
}
