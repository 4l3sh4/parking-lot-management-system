package model;

import cli.Logout;
import cli.ShowActiveTickets;
import cli.ShowTicketsHistory;
import cli.ShowSpotsStatus;
import cli.ShowTicketsBySpotNumber;
import cli.ShowTicketsByVehiclePlate;

public class Admin extends User {

    public Admin() {
        super();
        setRole("ADMIN");
        initActions();
    }

    public Admin(int ID, String firstName, String lastName, String email, String password) {
        super(ID, firstName, lastName, email, password);
        setRole("ADMIN");
        initActions(); 
    }

    private void initActions() {
        super.actions = new Actionable[] {

                new ShowSpotsStatus(),
                new ShowActiveTickets(),
                new ShowTicketsHistory(),
                new ShowTicketsBySpotNumber(),
                new ShowTicketsByVehiclePlate(),
                new Logout()
        };

        super.guiActions = new gui.Actionable[] {
                new gui.ShowActiveTickets(),      
                new gui.ShowSpotsStatus(),
                new gui.ShowTicketsHistory(),
                new gui.ReportingPanel(), 
                new gui.Logout()
        };
    }
}
