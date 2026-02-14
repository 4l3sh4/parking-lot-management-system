package cli;

import java.util.Scanner;

import model.Actionable;
import model.User;
import model.Vehicle;
import model.ParkingLotManager;
import model.ParkingSpot;
import model.Ticket;
import model.FineManager;
import util.ConsoleInput;
import storage.DataManager;
import storage.SaveData;

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
        
        // Alert if vehicle has unpaid fines
        double unpaidFines = FineManager.getTotalUnpaidFines(licensePlateNumber);
        if (unpaidFines > 0) {
            System.out.println("\n** ALERT **");
            System.out.printf("This vehicle has UNPAID FINES: RM %.2f%n", unpaidFines);
            System.out.println("You will have the option to pay these fines during exit.\n");
        }
        
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
        
        spot.occupy(vehicle);
        
        Ticket ticket = new Ticket(vehicle, spot.getSpotNumber());
        DataManager.activeTickets.add(ticket);
        SaveData.saveAll();
        System.out.println("Vehicle entered succesfully");
        System.out.println("------------------------");
        System.out.println("Ticket ID: "+ticket.getId());
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