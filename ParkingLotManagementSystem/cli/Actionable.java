package cli;

import java.util.Scanner;
import model.User;

public interface Actionable {
    
    public String getLabel();
    
    public void execute(Scanner s, User u);
    
    public boolean isAdminOnly();
}