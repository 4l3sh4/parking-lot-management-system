package model;

import gui.VehicleType;

/**
 * Factory class for creating Vehicle instances.
 * Implements the Factory pattern for extensibility.
 * To add a new vehicle type:
 * 1. Create a new Vehicle subclass implementing getAllowedSpotTypes() and getVehicleType()
 * 2. Add the type to VehicleType enum
 * 3. Add a case in this factory
 */
public class VehicleFactory {
    
    /**
     * Create a vehicle based on the vehicle type.
     * @param type The type of vehicle to create
     * @param licensePlate The license plate number
     * @param hasHandicappedCard Whether the vehicle has a handicapped card (only for HANDICAPPED_VEHICLE)
     * @return A new Vehicle instance, or null if type is not recognized
     */
    public static Vehicle createVehicle(VehicleType type, String licensePlate, boolean hasHandicappedCard) {
        if (type == null || licensePlate == null) {
            return null;
        }
        
        switch (type) {
            case MOTORCYCLE:
                return new Motorcycle(licensePlate);
            case CAR:
                return new Car(licensePlate);
            case SUV_TRUCK:
                return new SUV_Truck(licensePlate);
            case HANDICAPPED_VEHICLE:
                return new Handicapped_Vehicle(licensePlate, hasHandicappedCard);
            default:
                return null;
        }
    }
    
    /**
     * Create a vehicle based on the vehicle type (overload for non-handicapped vehicles).
     * @param type The type of vehicle to create
     * @param licensePlate The license plate number
     * @return A new Vehicle instance, or null if type is not recognized
     */
    public static Vehicle createVehicle(VehicleType type, String licensePlate) {
        return createVehicle(type, licensePlate, false);
    }
}
