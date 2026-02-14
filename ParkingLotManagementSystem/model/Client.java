package model;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Client extends User {

    public Client() {
        super();
        setRole("CLIENT");
        initActions();
    }

    public Client(int ID, String firstName, String lastName, String email, String password) {
        super(ID, firstName, lastName, email, password);
        setRole("CLIENT");
        initActions();
    }

    private void initActions() {
        super.guiActions = new gui.Actionable[] {
                new gui.ShowSpotsStatus(),
            new gui.VipReservation(),
                new gui.VehicleEntry(),
                new gui.VehicleExit(),
                new gui.ShowActiveTickets(), 
                new gui.ShowTicketsHistory(),
                new gui.Logout()
        };
    }

    // ✅ runs after loading from file (constructors won't run!)
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initActions();
    }
}
