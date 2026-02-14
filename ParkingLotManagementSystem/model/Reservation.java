package model;

import java.time.LocalDateTime;

public class Reservation {

    private String plate;          // normalized, e.g. "ABC123"
    private String spotNumber;     // e.g. "F1-R2-S3"
    private boolean active;
    private LocalDateTime createdAt;

    public Reservation() {
        // for Gson
    }

    public Reservation(String plate, String spotNumber) {
        this.plate = normalizePlate(plate);
        this.spotNumber = spotNumber == null ? null : spotNumber.trim().toUpperCase();
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public String getPlate() { return plate; }
    public String getSpotNumber() { return spotNumber; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void cancel() { this.active = false; }

    public boolean matchesPlate(String plate) {
        return active && this.plate != null && this.plate.equals(normalizePlate(plate));
    }

    public boolean matchesSpot(String spotNumber) {
        if (!active) return false;
        if (this.spotNumber == null || spotNumber == null) return false;
        return this.spotNumber.equalsIgnoreCase(spotNumber.trim());
    }

    public static String normalizePlate(String plate) {
        if (plate == null) return "";
        return plate.trim().toUpperCase().replaceAll("\\s+", "");
    }
}
