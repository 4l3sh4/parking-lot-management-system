package cli;

import java.util.Scanner;

import model.Actionable;
import model.ParkingSpot;
import model.User;
import model.SpotType;
import storage.DataManager;
import util.ConsoleInput;
/**
 * Write a description of class AddNewParkingSpot here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class AddNewParkingSpot implements Actionable {
    
    @Override
    public String getLabel() {
        return "Add new Parking Spot";
    }
    
    @Override
    public void execute(Scanner s, User u) {
        System.out.println("Select new spot type");
        SpotType[] types = SpotType.values();
        int selected;
        do {
            for (int i=0;i<types.length;i++) {
                System.out.println((i+1)+". "+types[i]);
            }
            selected = ConsoleInput.readInt(s)-1;
        } while (selected<0 || selected>=types.length);
        ParkingSpot spot = new ParkingSpot(types[selected]);
        DataManager.parkingSpots.add(spot);
        System.out.println("Parking Spot added succesfully");
    }
    
    @Override
    public boolean isAdminOnly() {
        return true;
    }
}