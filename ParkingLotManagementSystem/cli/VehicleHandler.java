package cli;

import java.util.Scanner;

import model.Color;
import model.User;
import model.Vehicle;
import model.VehicleType;
import model.Motorcycle;
import model.Car;
import model.SUV_Truck;
import model.Handicapped_Vehicle;
import util.ConsoleInput;

import storage.DataManager;
/**
 * Write a description of class VehicleHandler here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class VehicleHandler {
    
    public static Vehicle AddNewVehicle(Scanner s, User u, String licensePlateNumber) {
        System.out.println("Select Vehicle Type");
        VehicleType[] types = VehicleType.values();
        int typeIndex;
        do {
            for (int i=0;i<types.length;i++) {
                System.out.println("\t"+(i+1)+". "+types[i]);
            }
            typeIndex = ConsoleInput.readInt(s)-1;
        } while (typeIndex<0 || typeIndex>=types.length);
        VehicleType type = types[typeIndex];
        
        System.out.println("Select Vehicle Color");
        Color[] colors = Color.values();
        int colorIndex;
        do {
            for (int i=0;i<colors.length;i++) {
                System.out.println("\t"+(i+1)+". "+colors[i]);
            }
            colorIndex = ConsoleInput.readInt(s)-1;
        } while (colorIndex<0 || colorIndex>=colors.length);
        Color color = colors[colorIndex];
        
        System.out.print("Vehicle Brand: ");
        String brand = ConsoleInput.readString(s);
        System.out.println("Vehicle Model: ");
        String model = ConsoleInput.readString(s);
        
        Vehicle vehicle;
        switch (type) {
            case VehicleType.MOTORCYCLE:
                vehicle = new Motorcycle(licensePlateNumber);
                break;
            case VehicleType.CAR:
                vehicle = new Car(licensePlateNumber);
                break;
            case VehicleType.SUV_TRUCK:
                vehicle = new SUV_Truck(licensePlateNumber);
                break;
            case VehicleType.HANDICAPPED_VEHICLE:
                System.out.println("Does the vehicle have a handicapped card? (y/n)");
                String response = ConsoleInput.readString(s).toLowerCase();
                boolean hasCard = response.equals("y");
                
                vehicle = new Handicapped_Vehicle(licensePlateNumber, hasCard); 
                break;
            default:
                System.out.println("Invalid vehicle type");
                return null;
        }
        
        vehicle.setLicensePlateNumber(licensePlateNumber);
        vehicle.setVehicleOwnerID(u.getID());
        vehicle.setColor(color);
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        DataManager.registeredVehicles.add(vehicle);
        return vehicle;
    }
}