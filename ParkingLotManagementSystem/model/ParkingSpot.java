package model;


/**
 * Write a description of class ParkingSpot here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class ParkingSpot {
    
    private int spotNumber;
    private boolean isOccupied;
    private Vehicle vehicle;
    private SpotType type;
    
    public ParkingSpot() {
        
    }
    
    public ParkingSpot(SpotType type) {
        this.spotNumber = IDGenerator.getNextSpotNum();
        this.isOccupied = false;
        this.vehicle = null;
        this.type = type;
        
    }
    
    public ParkingSpot(int spotNumber, boolean isOccupied, Vehicle vehicle, SpotType type) {
        this.spotNumber = spotNumber;
        this.isOccupied = isOccupied;
        this.vehicle = vehicle;
        this.type = type;
    }
    
    public boolean isAvailable() {
        return !isOccupied;
    }
    
    public int getSpotNumber() {
        return spotNumber;
    }
    
    public boolean isOccupied() {
        return isOccupied;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public SpotType getType() {
        return type;
    }
    
    public void setSpotNumber(int spotNumber) {
        this.spotNumber = spotNumber;
    }
    
    public void setOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }
    
    public void occupy() {
        this.isOccupied = true;
    }
    
    public void free() {
        this.isOccupied = false;
        this.vehicle = null;
    }
    
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    
    public void setSpotType(SpotType type) {
        this.type = type;
    }
    
    
}