package cli;

import java.util.Scanner;

import model.Actionable;
import model.User;
import model.Ticket;
import storage.DataManager;

public class ShowTicketsHistory implements Actionable {
    
    @Override
    public String getLabel() {
        return "Show Tickets History";
    }
    
    @Override
    public void execute(Scanner s, User u) {
        for (Ticket t : DataManager.ticketHistory) {
            System.out.println(t.toString());
        }
    }
    
    @Override
    public boolean isAdminOnly() {
        return true;
    }
    
}