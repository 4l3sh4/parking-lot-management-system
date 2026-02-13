package model;

import cli.Logout;
import cli.ShowActiveTickets;
import cli.ShowTicketsHistory;
import cli.ShowRegisteredVehicles;
import cli.ShowSpotsStatus;
import cli.AddNewParkingSpot;
import cli.ShowTicketsBySpotNumber;
import cli.ShowTicketsByVehiclePlate;

public class Admin extends User {

    public Admin() {
        super();
        initActions();
    }

    public Admin(int ID, String firstName, String lastName, String email, String password) {
        super(ID, firstName, lastName, email, password);
        initActions(); 
    }

    private void initActions() {
        super.actions = new Actionable[] {
                new AddNewParkingSpot(),
                new ShowSpotsStatus(),
                new ShowActiveTickets(),
                new ShowRegisteredVehicles(),
                new ShowTicketsHistory(),
                new ShowTicketsBySpotNumber(),
                new ShowTicketsByVehiclePlate(),
                new Logout()
        };

        super.guiActions = new gui.Actionable[] {
                new gui.ShowActiveTickets(),
                new gui.AddParkingSpot(),       
                new gui.ShowSpotsStatus(),
                new gui.ShowRegisteredVehicles(),
                new gui.ShowTicketsHistory(),
                new gui.Logout()
        };
    }
}
