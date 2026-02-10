package cli;

import java.util.Scanner;

import model.Actionable;
import model.User;
import model.Vehicle;
import model.ParkingLotManager;
import model.ParkingSpot;
import model.Ticket;
import util.ConsoleInput;
import storage.DataManager;
/**
 * Write a description of class VehicleEntry here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class VehicleEntry implements Actionable {
    
    @Override
    public String getLabel() {
        return "Vehicle Entry";
    }
    
    @Override
    public void execute(Scanner s, User u) {
        
        String selected;
        String licensePlateNumber;
        do {
            System.out.print("Enter Vehicle License Plate Number: ");
            licensePlateNumber = ConsoleInput.readString(s);
            System.out.println("Vehicle License Plate Number: "+licensePlateNumber);
            do {
                System.out.println("Are you sure that you want to continue? (y/n)");
                selected = ConsoleInput.readString(s).toLowerCase();
            } while (!selected.equals("y") && !selected.equals("n"));
        } while (!selected.equals("y"));
        
        Vehicle vehicle = ParkingLotManager.findByPlate(licensePlateNumber);
        
        while (vehicle == null) {
            vehicle = VehicleHandler.AddNewVehicle(s, u, licensePlateNumber);
        }
        
        ParkingSpot spot = ParkingLotManager.findAvailableSpot(vehicle);
        if (spot == null) {
            //no available spots
            System.out.println("Unfortunately no available spots");
            System.out.println("Please try again later");
            return;
        }
        
        spot.occupy();
        spot.setVehicle(vehicle);
        
        Ticket ticket = new Ticket(vehicle, spot.getSpotNumber());
        DataManager.activeTickets.add(ticket);
        System.out.println("Vehicle entered succesfully");
        System.out.println("------------------------");
        System.out.println("Ticket ID: "+ticket.getID());
        System.out.println("Spot Number: "+spot.getSpotNumber());
        System.out.println("Spot Type: "+spot.getType());
        System.out.println("Entry Date:"+ticket.getEntryDate());
        System.out.println("Entry Time:"+ticket.getEntryTimeToString());
    }
    
    @Override
    public boolean isAdminOnly() {
        return false;
    }
}