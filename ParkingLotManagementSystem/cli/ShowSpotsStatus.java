package cli;

import java.util.ArrayList;
import java.util.Scanner;

import model.Actionable;
import model.User;
import model.ParkingSpot;
import model.SpotType;
import model.Vehicle;
import model.Motorcycle;
import model.Car;
import model.SUV_Truck;
import model.Handicapped_Vehicle;

import storage.DataManager;
/**
 * Write a description of class ShowSpotsStatus here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class ShowSpotsStatus implements Actionable {
    
    @Override
    public String getLabel() {
        return "Show Parking Spots Status";
    }
    
    @Override
    public void execute(Scanner s, User u) {
        
        if (DataManager.parkingSpots == null || DataManager.parkingSpots.isEmpty()) {
            System.out.println("No parking spots available.");
            return;
        }
        
        int compactSpots = 0;
        int regularSpots = 0;
        int handicappedSpots = 0;
        int reservedSpots =0;
        
        int occupiedSpots = 0;
        
        System.out.println("Occupied Spots: ");
        
        ArrayList<ParkingSpot> spots = DataManager.parkingSpots;
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable()) {
                switch (spot.getType()) {
                case COMPACT:
                    compactSpots++;
                    break;
                case REGULAR:
                    regularSpots++;
                    break;
                case HANDICAPPED:
                    handicappedSpots++;
                    break;
                case RESERVED:
                    reservedSpots++;
                    break;
                }
            } else {
                occupiedSpots++;
                System.out.println("\tSpot Number: "+spot.getSpotNumber());
                System.out.println("\tSpot Type: "+spot.getType());
                
                Vehicle vehicle = spot.getVehicle();
                
                if (vehicle != null) {

                    if (vehicle instanceof Motorcycle) {
                        System.out.println("\tVehicle Type: MOTORCYCLE");
                    } else if (vehicle instanceof Car) {
                        System.out.println("\tVehicle Type: CAR");
                    } else if (vehicle instanceof SUV_Truck) {
                        System.out.println("\tVehicle Type: SUV/TRUCK");
                    } else if (vehicle instanceof Handicapped_Vehicle) {
                        System.out.println("\tVehicle Type: HANDICAPPED VEHICLE");
                    }

                    System.out.println("\tVehicle Plate: " + vehicle.getLicensePlateNumber());

                } else {
                    System.out.println("\tVehicle: Not recorded");
                }

                System.out.println("\t----------------------------\n");

            }
        }
        
        if (occupiedSpots==0) {
            System.out.println("\tNo occupied spots\n");
        }
        
        System.out.println("Available Spots: ");
        System.out.println("\tCompact: "+compactSpots);
        System.out.println("\tRegular: "+regularSpots);
        System.out.println("\tHandicapped: "+handicappedSpots);
        System.out.println("\tReserved: "+reservedSpots+"\n");
    }
    
    @Override
    public boolean isAdminOnly() {
        return false;
    }
}