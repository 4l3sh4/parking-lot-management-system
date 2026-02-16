package model;

import java.util.Set;
import gui.VehicleType;

public abstract class Vehicle {
    
    private String licensePlateNumber; 
    private int vehicleOwnerID;
    private Color color;
    private String brand;
    private String model;
    
    
    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }
    
    public void setVehicleOwnerID(int vehicleOwnerID) {
        this.vehicleOwnerID = vehicleOwnerID;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Vehicle(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }
    
    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }
    
    public int getVehicleOwnerID() {
        return vehicleOwnerID;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public String getModel() {
        return model;
    }
    
    public Color getColor() {
        return color;
    }
    
    /**
     * Returns the set of allowed spot types for this vehicle.
     * This method must be implemented by all vehicle subclasses.
     * @return Set of SpotType that this vehicle can park in
     */
    public abstract Set<SpotType> getAllowedSpotTypes();
    
    /**
     * Returns the vehicle type enum for this vehicle.
     * This method must be implemented by all vehicle subclasses.
     * @return VehicleType enum value
     */
    public abstract VehicleType getVehicleType();
    
    /**
     * Determines if this vehicle gets free parking in a specific spot type.
     * Override this method to implement special pricing logic.
     * @param spotType The spot type to check
     * @return true if parking is free, false otherwise
     */
    public boolean hasFreeParkingIn(SpotType spotType) {
        return false; // Default: no vehicles get free parking
    }
}