package cli;

import java.util.Scanner;

import model.Actionable;
import model.User;
import model.Ticket;
import storage.DataManager;

public class ShowActiveTickets implements Actionable {
    
    @Override
    public String getLabel() {
        return "Show Active Tickets";
    }
    
    @Override
    public void execute(Scanner s, User u) {
        for (int i=0;i<DataManager.activeTickets.size();i++) {
            Ticket ticket = DataManager.activeTickets.get(i);
            System.out.println(ticket.toString());
        }
    }
    
    @Override
    public boolean isAdminOnly() {
        return true;
    }
}