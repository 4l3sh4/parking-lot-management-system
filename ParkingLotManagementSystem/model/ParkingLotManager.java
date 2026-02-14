package model;

import storage.DataManager;

public class ParkingLotManager {
    
    public static Vehicle findByPlate(String plate) {
        Vehicle v = null;
        for (Vehicle vehicle : DataManager.registeredVehicles) {
            if (vehicle.getLicensePlateNumber().toLowerCase().equals(plate.toLowerCase())) {
                v = vehicle;
                break;
            }
        }
        return v;
    }
    
    public static Ticket findActiveTicketByPlate(String plate) {
        Ticket ticket = null;
        for (Ticket t : DataManager.activeTickets) {
            if (t.getVehicle().getLicensePlateNumber().toLowerCase().equals(plate.toLowerCase())) {
                ticket = t;
                break;
            }
        }
        return ticket;
    }
    
    public static Ticket findActiveTicketBySpot(String spotNum) {
        Ticket ticket = null;
        for (Ticket t : DataManager.activeTickets) {
            if (t.getSpotNumber().equalsIgnoreCase(spotNum)) {
                ticket = t;
                break;
            }
        }
        return ticket;
    }

    public static ParkingSpot findAvailableSpot(Vehicle vehicle) {
        
        SpotType[] types;
        if (vehicle instanceof Motorcycle) {
            types = new SpotType[] {SpotType.COMPACT};
        } else if (vehicle instanceof Car) {
            types = new SpotType[] {SpotType.COMPACT, SpotType.REGULAR};
        } else if (vehicle instanceof SUV_Truck) {
            types = new SpotType[] {SpotType.REGULAR};
        } else if (vehicle instanceof Handicapped_Vehicle) {
            types = new SpotType[] {SpotType.COMPACT, SpotType.REGULAR, SpotType.HANDICAPPED};
        } else { 
            return null;
        }
        
        ParkingSpot requiredSpot = null;
        
        outerLoop:
        for (int i=0; i<types.length; i++) {
            SpotType type = types[i];
            for (int j=0; j<DataManager.parkingSpots.size(); j++) {
                ParkingSpot spot = DataManager.parkingSpots.get(j);
                if (spot.getType().equals(type) && spot.isAvailable()) {
                    requiredSpot = spot;
                    break outerLoop;
                }
            }
        }
        return requiredSpot;
    }
    
    public static User findUserByID(int ID) {
        User user = null;
        for (User u : DataManager.users) {
            if (u.getID() == ID) {
                user = u;
                break;
            }
        }
        return user;
    }
    
    public static ParkingSpot findSpotByNum(String spotNumber) {
        for (ParkingSpot s : DataManager.parkingSpots) {
    
            if (s.getSpotNumber().equalsIgnoreCase(spotNumber)) {
                return s;
            }
        }
        return null;
    }

}