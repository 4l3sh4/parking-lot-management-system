package model;

import java.util.Set;
import java.util.HashSet;
import gui.VehicleType;

public class Motorcycle extends Vehicle {
    public Motorcycle(String licensePlate) {
        super(licensePlate);
    }
    
    @Override
    public Set<SpotType> getAllowedSpotTypes() {
        Set<SpotType> allowedTypes = new HashSet<>();
        allowedTypes.add(SpotType.COMPACT);
        allowedTypes.add(SpotType.RESERVED);
        return allowedTypes;
    }
    
    @Override
    public VehicleType getVehicleType() {
        return VehicleType.MOTORCYCLE;
    }
}