import gui.NavigationHandler;
import storage.LoadData;
import model.User;
import storage.FileHandler;

import model.Vehicle;
import model.Car;     
import model.Ticket;
import model.Color;
import model.ParkingSpot;
import model.SpotType;

import storage.DataManager;
import storage.SaveData;
import storage.LoadData;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {

        LoadData.loadAllData();
        //cli.NavigationHandler.welcome(new Scanner(System.in));
        
        // TEMP TEST DATA (delete later)
        if (DataManager.activeTickets.isEmpty()) {
        
            Vehicle v1 = new Car("ABC123");
            v1.setBrand("Toyota");
            v1.setModel("Vios");
            v1.setColor(Color.RED);
            v1.setVehicleOwnerID(1);
            DataManager.activeTickets.add(new Ticket(v1, 1));
        
            Vehicle v2 = new Car("DEF456");
            v2.setBrand("Honda");
            v2.setModel("City");
            v2.setColor(Color.BLUE);
            v2.setVehicleOwnerID(2);
            DataManager.activeTickets.add(new Ticket(v2, 2));
        
            Vehicle v3 = new Car("GHI789");
            v3.setBrand("Mazda");
            v3.setModel("CX5");
            v3.setColor(Color.WHITE);
            v3.setVehicleOwnerID(3);
            DataManager.activeTickets.add(new Ticket(v3, 3));
        
            Vehicle v4 = new Car("JKL321");
            v4.setBrand("Perodua");
            v4.setModel("Bezza");
            v4.setColor(Color.BLACK);
            v4.setVehicleOwnerID(4);
            DataManager.activeTickets.add(new Ticket(v4, 4));
        
            SaveData.saveAll();
        }

        NavigationHandler.initialize();

    }
}

