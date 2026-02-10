package cli;

import java.util.Scanner;

import model.User;
import model.Admin;
import util.ConsoleInput;
import model.Actionable;
/**
 * Write a description of class NavigationHandler here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class NavigationHandler {
    
    public static void welcome(Scanner scanner) {
        System.out.println("============================");
        System.out.println("Welcome to our Parking Lot Management System");
        System.out.println("1. Login");
        System.out.println("2. Create new Account");
        System.out.println("3. Exit");
        int input = ConsoleInput.readInt(scanner);
        switch (input) {
        case 1:
            AuthHandler.login(scanner);
            break;
        case 2:
            AuthHandler.createNewAccount(scanner);
            break;
        case 3:
            exit();
            break;
            default:
                welcome(scanner);
        }
    }
    
    public static void exit() {
        System.out.println("Thanks for using our Parking Lot Management System");
        System.out.println("Have a wonderful day!");
    }
    
    public static void showMenu(Scanner s, User user) {
        System.out.println("============================");
        for (int i=0;i<user.getActions().length;i++) {
            System.out.println((i+1)+". "+user.getActions()[i].getLabel());
        }
        System.out.println("============================");
        int selected = ConsoleInput.readInt(s);
        selected--;
        if (selected<0 || selected>=user.getActions().length) {
            System.out.println("Invalid input");
            showMenu(s, user);
        } else if (user.getActions()[selected].isAdminOnly() && (! (user instanceof Admin))){
            System.out.println("Admin only can perform this action");
            showMenu(s, user);
        } else {
            user.getActions()[selected].execute(s, user);
            if (!user.getActions()[selected].getLabel().equals("Logout")){
                showMenu(s, user); 
            }
        }
    }
}