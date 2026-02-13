package model;

import java.io.IOException;
import java.io.ObjectInputStream;

import cli.Logout;
import cli.ShowSpotsStatus;
import cli.VehicleEntry;
import cli.VehicleExit;

public class Client extends User {

    public Client() {
        super();
        initActions();
    }

    public Client(int ID, String firstName, String lastName, String email, String password) {
        super(ID, firstName, lastName, email, password);
        initActions();
    }

    private void initActions() {
        super.actions = new Actionable[] {
                new VehicleEntry(),
                new VehicleExit(),
                new ShowSpotsStatus(),
                new Logout()
        };

        super.guiActions = new gui.Actionable[] {
                new gui.ShowSpotsStatus(),
                new gui.VehicleEntry(),
                new gui.VehicleExit(),
                new gui.ShowActiveTickets(), 
                new gui.Logout()
        };
    }

    // ✅ runs after loading from file (constructors won't run!)
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initActions();
    }
}
