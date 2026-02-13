package cli;

import java.util.Scanner;

import model.Actionable;
import model.Ticket;
import model.User;
import storage.DataManager;
import util.ConsoleInput;


public class ShowTicketsBySpotNumber implements Actionable {
    
    @Override
    public String getLabel() {
        return "Show Tickets By Slot Number";
    }
    
    @Override
    public void execute(Scanner s, User u) {
        System.out.println("Enter parking slot number: ");
        int spotnum = ConsoleInput.readInt(s);
        
        for (Ticket t : DataManager.activeTickets) {
            if (t.getSpotNumber() == spotnum) {
                System.out.println(t.toString());
            }
        }
        
        for (Ticket t : DataManager.ticketHistory) {
            if (t.getSpotNumber() == spotnum) {
                System.out.println(t.toString());
            }
        }
    }
    
    @Override
    public boolean isAdminOnly() {
        return true;
    }
}
