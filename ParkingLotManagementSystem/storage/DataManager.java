package storage;

import java.util.ArrayList;

import model.ParkingSpot;
import model.Ticket;
import model.Vehicle;
import model.User;
import model.Reservation;
import model.Fine;
import model.FineScheme;


public class DataManager {

    public static ArrayList<ParkingSpot> parkingSpots = new ArrayList<>();
    public static ArrayList<Ticket> activeTickets = new ArrayList<>();
    public static ArrayList<Ticket> ticketHistory = new ArrayList<>();
    public static ArrayList<Vehicle> registeredVehicles = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<>();
    public static ArrayList<Reservation> reservations = new ArrayList<>();
    public static ArrayList<Fine> fines = new ArrayList<>();
    
    // Fine scheme - admin can choose any of the three options
    public static FineScheme currentFineScheme = FineScheme.FIXED;

}