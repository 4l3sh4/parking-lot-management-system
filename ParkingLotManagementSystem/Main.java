import gui.NavigationHandler;
import storage.LoadData;

import model.Vehicle;
import model.Car;     
import model.Ticket;
import model.Color;
import model.Motorcycle;
import model.SUV_Truck;
import model.Handicapped_Vehicle;

import storage.DataManager;
import storage.SaveData;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) {

        LoadData.loadAllData();
        //cli.NavigationHandler.welcome(new Scanner(System.in));

        // ===== REGISTERED VEHICLES =====
        if (DataManager.registeredVehicles == null || DataManager.registeredVehicles.isEmpty()) {
        
            DataManager.registeredVehicles = new ArrayList<>();
        
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
        
            System.out.println("Test vehicles generated.");
        }

        if (DataManager.activeTickets == null || DataManager.activeTickets.isEmpty()) {

            DataManager.activeTickets = new ArrayList<>();
        
            Ticket t1 = new Ticket(DataManager.registeredVehicles.get(0), "F1-R1-S3");
            Ticket t2 = new Ticket(DataManager.registeredVehicles.get(1), "F1-R2-S4");
        
            DataManager.activeTickets.add(t1);
            DataManager.activeTickets.add(t2);
        
            System.out.println("Test active tickets generated.");
        }
        
        if (DataManager.ticketHistory == null || DataManager.ticketHistory.isEmpty()) {
        
            DataManager.ticketHistory = new ArrayList<>();
        
            Ticket old1 = new Ticket(DataManager.registeredVehicles.get(2), "F2-R1-S1");
            old1.exitVehicle(5.0);
            DataManager.ticketHistory.add(old1);
        
            Ticket old2 = new Ticket(DataManager.registeredVehicles.get(3), "F3-R2-S8");
            old2.exitVehicle(10.0);
            DataManager.ticketHistory.add(old2);
        
            System.out.println("Test ticket history generated.");
        }

        SaveData.saveAll();
        
        NavigationHandler.initialize();

    }
}

