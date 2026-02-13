import gui.NavigationHandler;
import storage.LoadData;
import model.User;
import storage.FileHandler;

import java.time.LocalDateTime;
import model.Vehicle;
import model.Car;     
import model.Ticket;
import model.Color;
import model.ParkingSpot;
import model.SpotType;
import model.Motorcycle;
import model.SUV_Truck;
import model.Handicapped_Vehicle;
import model.Ticket;

import storage.DataManager;
import storage.SaveData;
import storage.LoadData;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {

        LoadData.loadAllData();
        //cli.NavigationHandler.welcome(new Scanner(System.in));
        
        // ===== TEMP TEST REGISTERED VEHICLES =====
        if (DataManager.registeredVehicles.isEmpty()) {
        
            Vehicle v1 = new Car("REG111");
            v1.setBrand("Toyota");
            v1.setModel("Vios");
            v1.setColor(Color.RED);
            v1.setVehicleOwnerID(1);
            DataManager.registeredVehicles.add(v1);
        
            Vehicle v2 = new Motorcycle("REG222");
            v2.setBrand("Yamaha");
            v2.setModel("R15");
            v2.setColor(Color.BLUE);
            v2.setVehicleOwnerID(2);
            DataManager.registeredVehicles.add(v2);
        
            Vehicle v3 = new SUV_Truck("REG333");
            v3.setBrand("Ford");
            v3.setModel("Ranger");
            v3.setColor(Color.BLACK);
            v3.setVehicleOwnerID(3);
            DataManager.registeredVehicles.add(v3);
        
            Vehicle v4 = new Handicapped_Vehicle("REG444", true);
            v4.setBrand("Honda");
            v4.setModel("City");
            v4.setColor(Color.WHITE);
            v4.setVehicleOwnerID(4);
            DataManager.registeredVehicles.add(v4);
        
            SaveData.saveAll();
        }    
        
        // ===== TEMP TEST TICKET HISTORY =====
        if (DataManager.ticketHistory.isEmpty()) {
        
            Vehicle v1 = new Car("P101010");
            v1.setBrand("Toyota");
            v1.setModel("Vios");
            v1.setColor(Color.RED);
            v1.setVehicleOwnerID(1);
        
            Ticket t1 = new Ticket(
                1,
                v1,
                LocalDateTime.now().minusHours(8),
                LocalDateTime.now().minusHours(4),
                35.0,
                1
            );
        
            Vehicle v2 = new Car("P121212");
            v2.setBrand("Honda");
            v2.setModel("City");
            v2.setColor(Color.WHITE);
            v2.setVehicleOwnerID(2);
        
            Ticket t2 = new Ticket(
                2,
                v2,
                LocalDateTime.now().minusHours(6),
                LocalDateTime.now().minusHours(3),
                14.0,
                2
            );
        
            Vehicle v3 = new Car("P131313");
            v3.setBrand("Mazda");
            v3.setModel("CX5");
            v3.setColor(Color.BLACK);
            v3.setVehicleOwnerID(3);
        
            Ticket t3 = new Ticket(
                3,
                v3,
                LocalDateTime.now().minusHours(10),
                LocalDateTime.now().minusHours(5),
                42.0,
                3
            );
        
            DataManager.ticketHistory.add(t1);
            DataManager.ticketHistory.add(t2);
            DataManager.ticketHistory.add(t3);
        
            SaveData.saveAll();
        }
        NavigationHandler.initialize();

    }
}

