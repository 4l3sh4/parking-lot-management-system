package storage;

import java.util.ArrayList;

import model.ParkingSpot;
import model.Ticket;
import model.Vehicle;
import model.User;
import model.Reservation;


public class DataManager {

    public static ArrayList<ParkingSpot> parkingSpots = new ArrayList<>();
    public static ArrayList<Ticket> activeTickets = new ArrayList<>();
    public static ArrayList<Ticket> ticketHistory = new ArrayList<>();
    public static ArrayList<Vehicle> registeredVehicles = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<>();
    public static ArrayList<Reservation> reservations = new ArrayList<>();

}