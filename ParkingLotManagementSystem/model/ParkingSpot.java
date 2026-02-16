package model;

public class ParkingSpot {

    private String spotNumber;      // F1-R1-S1
    private SpotType type;
    private boolean occupied;
    private Vehicle vehicle;
    private double hourlyRate;

    public ParkingSpot(String spotNumber, SpotType type) {
        this.spotNumber = spotNumber;
        this.type = type;
        this.occupied = false;
        this.vehicle = null;
        this.hourlyRate = type.getHourlyRate();
    }

    public String getSpotNumber() {
        return spotNumber;
    }

    public SpotType getType() {
        return type;
    }

    public boolean isAvailable() {
        return !occupied;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void occupy(Vehicle v) {
        if (occupied) {
            throw new IllegalStateException("Spot already occupied.");
        }
        this.vehicle = v;
        this.occupied = true;
    }

    public void free() {
        this.vehicle = null;
        this.occupied = false;
    }
    

    @Override
    public String toString() {
        return spotNumber + " (" + type + ") - " + (occupied ? "Occupied" : "Available");
    }
}
