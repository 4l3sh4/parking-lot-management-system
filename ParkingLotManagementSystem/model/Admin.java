package model;

import cli.Logout;
import cli.ShowActiveTickets;
import cli.ShowRegisteredVehicles;
import cli.ShowSpotsStatus;
import cli.AddNewParkingSpot;
/**
 * Write a description of class Admin here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Admin extends User {
    
    public Admin(){
        super.actions = new Actionable[] {
                new AddNewParkingSpot(),
                new ShowSpotsStatus(),
                new ShowActiveTickets(),
                new ShowRegisteredVehicles(), 
                new Logout()
                
        };
    }
    
     public Admin(int ID, String firstName, String lastName, String email, String password) {
        super(ID, firstName, lastName, email, password);
        super.actions = new Actionable[] {
                new AddNewParkingSpot(),
                new ShowSpotsStatus(),
                new ShowActiveTickets(),
                new ShowRegisteredVehicles(), 
                new Logout()

        };
    }
    
}