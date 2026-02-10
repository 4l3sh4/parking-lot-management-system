package cli;

import java.util.Scanner;

import model.Actionable;
import model.User;
import model.Vehicle;
import model.ParkingLotManager;
import storage.DataManager;
/**
 * Write a description of class ShowRegisteredVehicles here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class ShowRegisteredVehicles implements Actionable {
    
    @Override
    public String getLabel() {
        return "Show Registered Vehicles";
    }
    
    @Override
    public void execute(Scanner s, User u) {
        for (int i=0;i<DataManager.registeredVehicles.size();i++) {
            Vehicle v = DataManager.registeredVehicles.get(i);
            User user = ParkingLotManager.findUserByID(v.getVehicleOwnerID());
            System.out.println("\tVehicle License Plate Number: "+v.getLicensePlateNumber());
            System.out.println("\tVehicle Brand: "+v.getBrand());
            System.out.println("\tVehicle Model: "+v.getModel());
            System.out.println("\tVehicle Color: "+v.getColor());
            System.out.println("\tVehicle Owner: "+user.getFullName());
            System.out.println("---------------------------------------");
        }
    }
    
    @Override
    public boolean isAdminOnly() {
        return false;
    }
}