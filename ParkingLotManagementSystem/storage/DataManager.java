package storage;

import java.util.ArrayList;

import model.ParkingSpot;
import model.Ticket;
import model.Vehicle;
import model.User;
/**
 * Write a description of class DataManager here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */

public class DataManager {
    
    public static ArrayList<ParkingSpot> parkingSpots;
    public static ArrayList<Ticket> activeTickets;
    public static ArrayList<Ticket> ticketHistory;
    public static ArrayList<Vehicle> registeredVehicles;
    public static ArrayList<User> users;
}
