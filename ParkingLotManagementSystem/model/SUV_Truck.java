package model;

import java.util.Set;
import java.util.HashSet;
import gui.VehicleType;

/**
 * SUV/Truck vehicle type.
 * Can only park in REGULAR and RESERVED spots.
 */
public class SUV_Truck extends Vehicle {
    public SUV_Truck(String licensePlate) {
        super(licensePlate);
    }
    
    @Override
    public Set<SpotType> getAllowedSpotTypes() {
        Set<SpotType> allowedTypes = new HashSet<>();
        allowedTypes.add(SpotType.REGULAR);
        allowedTypes.add(SpotType.RESERVED);
        return allowedTypes;
    }
    
    @Override
    public VehicleType getVehicleType() {
        return VehicleType.SUV_TRUCK;
    }
}