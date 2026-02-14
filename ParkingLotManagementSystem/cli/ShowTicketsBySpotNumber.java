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
        System.out.print("Enter parking slot number (e.g., F1-R1-S1): ");
        String spotnum = ConsoleInput.readString(s).trim();

        boolean found = false;

        for (Ticket t : DataManager.activeTickets) {
            if (t.getSpotNumber().equalsIgnoreCase(spotnum)) {
                System.out.println(t);
                found = true;
            }
        }

        for (Ticket t : DataManager.ticketHistory) {
            if (t.getSpotNumber().equalsIgnoreCase(spotnum)) {
                System.out.println(t);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No tickets found for spot number: " + spotnum);
        }
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }
}
