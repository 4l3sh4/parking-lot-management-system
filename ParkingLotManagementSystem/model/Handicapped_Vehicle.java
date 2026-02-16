package model;

import java.util.Set;
import java.util.HashSet;
import gui.VehicleType;

public class Handicapped_Vehicle extends Vehicle {
    //RM2/hour (FREE only if have handicapped card holder in handicapped spots) 
    private boolean hasHandicappedCard;

    public Handicapped_Vehicle(String licensePlate, boolean hasHandicappedCard) {
        super(licensePlate);
        this.hasHandicappedCard = hasHandicappedCard;
    }
    
    public boolean getHasHandicappedCard() {
        return hasHandicappedCard;
    }
    
    @Override
    public Set<SpotType> getAllowedSpotTypes() {
        // Handicapped vehicles can park in all spot types
        Set<SpotType> allowedTypes = new HashSet<>();
        allowedTypes.add(SpotType.COMPACT);
        allowedTypes.add(SpotType.REGULAR);
        allowedTypes.add(SpotType.HANDICAPPED);
        allowedTypes.add(SpotType.RESERVED);
        return allowedTypes;
    }
    
    @Override
    public VehicleType getVehicleType() {
        return VehicleType.HANDICAPPED_VEHICLE;
    }
    
    /**
     * Handicapped vehicles with valid cards get free parking in handicapped spots.
     */
    @Override
    public boolean hasFreeParkingIn(SpotType spotType) {
        return hasHandicappedCard && spotType == SpotType.HANDICAPPED;
    }
}