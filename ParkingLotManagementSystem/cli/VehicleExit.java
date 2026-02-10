package cli;

import java.util.Scanner;

import model.Actionable;
import model.User;
/**
 * Write a description of class VehicleExit here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class VehicleExit implements Actionable {
    
    @Override
    public String getLabel() {
        return null;
    }
    
    @Override
    public void execute(Scanner s, User u) {
        
    }
    
    @Override
    public boolean isAdminOnly() {
        return false;
    }
}