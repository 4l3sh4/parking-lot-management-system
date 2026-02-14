package model;

import java.time.LocalDateTime;

public class Reservation {

    private String plate;          // normalized, e.g. "ABC123"
    private String spotNumber;     // e.g. "F1-R2-S3"
    private boolean active;
    private LocalDateTime createdAt;

    public Reservation() {
        // Default constructor for JSON deserialization
    }

    public Reservation(String plate, String spotNumber) {
        this.plate = normalizePlate(plate);
        this.spotNumber = (spotNumber == null) ? null : spotNumber.trim().toUpperCase();
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public String getPlate() { return plate; }
    public String getSpotNumber() { return spotNumber; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void cancel() { this.active = false; }

    // ===== Existing methods (keep for compatibility) =====
    public boolean matchesPlate(String plate) {
        return active && this.plate != null && this.plate.equals(normalizePlate(plate));
    }

    public boolean matchesSpot(String spotNumber) {
        if (!active) return false;
        if (this.spotNumber == null || spotNumber == null) return false;
        return this.spotNumber.equalsIgnoreCase(spotNumber.trim());
    }

    // ===== New helper aliases (so your GUI code reads clean) =====

    // alias: nicer name for matching by plate
    public boolean matches(String plate) {
        return matchesPlate(plate);
    }

    // alias: clearer meaning
    public boolean matchesSpotNumber(String spotNumber) {
        return matchesSpot(spotNumber);
    }

    // convenience: explicit "active reservation for X"
    public boolean isActiveForPlate(String plate) {
        return isActive() && this.plate != null && this.plate.equals(normalizePlate(plate));
    }

    public boolean isActiveForSpot(String spotNumber) {
        if (!isActive()) return false;
        if (this.spotNumber == null || spotNumber == null) return false;
        return this.spotNumber.equalsIgnoreCase(spotNumber.trim());
    }

    public static String normalizePlate(String plate) {
        if (plate == null) return "";
        return plate.trim().toUpperCase().replaceAll("\\s+", "");
    }
}
