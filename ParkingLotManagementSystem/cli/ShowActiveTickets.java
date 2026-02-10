package cli;

import java.util.Scanner;

import model.Actionable;
import model.User;
import model.Ticket;
import model.Vehicle;
import model.ParkingLotManager;
import storage.DataManager;
/**
 * Write a description of class ShowActiveTickets here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class ShowActiveTickets implements Actionable {
    
    @Override
    public String getLabel() {
        return "Show Active Tickets";
    }
    
    @Override
    public void execute(Scanner s, User u) {
        for (int i=0;i<DataManager.activeTickets.size();i++) {
            Ticket ticket = DataManager.activeTickets.get(i);
            User user = ParkingLotManager.findUserByID(ticket.getVehicle().getVehicleOwnerID());
            System.out.println("\tTicket ID: "+ticket.getID());
            System.out.println("\tVehicle License Plate Number: "+ticket.getVehicle().getLicensePlateNumber());
            System.out.println("\tVehicle Brand: "+ticket.getVehicle().getBrand());
            System.out.println("\tVehicle Model: "+ticket.getVehicle().getModel());
            System.out.println("\tVehicle Color: "+ticket.getVehicle().getColor());
            System.out.println("\tVehicle Owner: "+user.getFullName());
            System.out.println("\tEntry Date: "+ticket.getEntryDate());
            System.out.println("\tEntry Time: "+ticket.getEntryTimeToString());
            System.out.println("\tSpot Number: "+ticket.getSpotNumber());
            System.out.println("---------------------------------------");
        }
    }
    
    @Override
    public boolean isAdminOnly() {
        return true;
    }
}