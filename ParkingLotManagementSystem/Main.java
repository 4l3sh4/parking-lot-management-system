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
import model.Motorcycle;
import model.SUV_Truck;
import model.Handicapped_Vehicle;


import storage.DataManager;
import storage.SaveData;
import storage.LoadData;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {

        LoadData.loadAllData();
        //cli.NavigationHandler.welcome(new Scanner(System.in));
        
        // TEMP PARKING SPOTS TEST DATA (delete later)
        if (DataManager.parkingSpots.isEmpty()) {
        
            // Available spots
            DataManager.parkingSpots.add(new ParkingSpot(1, false, null, SpotType.COMPACT));
            DataManager.parkingSpots.add(new ParkingSpot(2, false, null, SpotType.REGULAR));
            DataManager.parkingSpots.add(new ParkingSpot(3, false, null, SpotType.HANDICAPPED));
            DataManager.parkingSpots.add(new ParkingSpot(4, false, null, SpotType.RESERVED));
        
            // Occupied spots
        
            Vehicle v5 = new Car("AAA111");
            v5.setBrand("Toyota");
            v5.setModel("Vios");
            v5.setColor(Color.RED);
            v5.setVehicleOwnerID(5);
            DataManager.parkingSpots.add(new ParkingSpot(5, true, v5, SpotType.REGULAR));
        
            Vehicle v6 = new Motorcycle("BBB222");
            v6.setBrand("Yamaha");
            v6.setModel("R15");
            v6.setColor(Color.BLUE);
            v6.setVehicleOwnerID(6);
            DataManager.parkingSpots.add(new ParkingSpot(6, true, v6, SpotType.COMPACT));
        
            Vehicle v7 = new SUV_Truck("CCC333");
            v7.setBrand("Ford");
            v7.setModel("Ranger");
            v7.setColor(Color.BLACK);
            v7.setVehicleOwnerID(7);
            DataManager.parkingSpots.add(new ParkingSpot(7, true, v7, SpotType.RESERVED));
        
            Vehicle v8 = new Handicapped_Vehicle("DDD444", true);
            v8.setBrand("Honda");
            v8.setModel("City");
            v8.setColor(Color.WHITE);
            v8.setVehicleOwnerID(8);
            DataManager.parkingSpots.add(new ParkingSpot(8, true, v8, SpotType.HANDICAPPED));
        
            SaveData.saveAll();
        }

        
        NavigationHandler.initialize();

    }
}

