package model;

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
        super.guiActions = new gui.Actionable[] {
                new gui.ShowActiveTickets(),      
                new gui.ShowSpotsStatus(),
                new gui.ShowTicketsHistory(),
                new gui.ManageFines(),
                new gui.ReportingPanel(), 
                new gui.Logout()
        };
    }
}
