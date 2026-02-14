package model;

public class Reservation {

    private String plateNumber;
    private String reservedSpotNumber;

    public Reservation(String plateNumber, String reservedSpotNumber) {
        this.plateNumber = plateNumber.toUpperCase();
        this.reservedSpotNumber = reservedSpotNumber;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getReservedSpotNumber() {
        return reservedSpotNumber;
    }

    public boolean matches(String plate) {
        return plateNumber.equalsIgnoreCase(plate);
    }

    @Override
    public String toString() {
        return "Reservation: " + plateNumber + " -> " + reservedSpotNumber;
    }
}
