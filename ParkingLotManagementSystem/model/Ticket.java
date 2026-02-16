package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import storage.DataManager;

public class Ticket {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter CODE_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private int id;
    private String ticketCode;
    private Vehicle vehicle;
    private String spotNumber;

    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    private long durationHours;
    private double hourlyRate;
    private double parkingFee;   // hours × rate
    private double fines;        // fines calculated based on scheme
    private boolean isOverstaying; // track if overstaying (>24 hours)
    private boolean isReservedSpotWithoutReservation; // track reserved spot violation

    // totalFee = parkingFee + fines
    private double totalFee;

    public Ticket(Vehicle vehicle, String spotNumber) {
        this.id = IDGenerator.getNextTicketID();
        this.vehicle = vehicle;
        this.spotNumber = spotNumber;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;

        this.durationHours = 0;
        this.hourlyRate = 0.0;
        this.parkingFee = 0.0;
        this.fines = 0.0;
        this.totalFee = 0.0;
        this.isOverstaying = false;
        this.isReservedSpotWithoutReservation = false;

        this.ticketCode =
                "T-" + vehicle.getLicensePlateNumber()
                + "-" + entryTime.format(CODE_FMT);
    }

    public int getId() { return id; }
    public String getTicketCode() { return ticketCode; }
    public Vehicle getVehicle() { return vehicle; }

    public String getSpotNumber() { return spotNumber; }

    public String getEntryDate() { return entryTime.format(DATE_FMT); }
    public String getEntryTimeToString() { return entryTime.format(TIME_FMT); }
    public String getExitDate() { return exitTime == null ? "-" : exitTime.format(DATE_FMT); }
    public String getExitTimeToString() { return exitTime == null ? "-" : exitTime.format(TIME_FMT); }

    public boolean hasExited() { return exitTime != null; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }

    public long getDurationHours() { return durationHours; }
    public double getHourlyRate() { return hourlyRate; }
    public double getParkingFee() { return parkingFee; }
    public double getFines() { return fines; }
    public boolean isOverstaying() { return isOverstaying; }
    public boolean isReservedSpotViolation() { return isReservedSpotWithoutReservation; }

    public double getTotalFee() { return totalFee; }

    public void exitVehicle(double hourlyRate) {
        this.exitTime = LocalDateTime.now();
        this.hourlyRate = hourlyRate;

        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        long hours = (long) Math.ceil(minutes / 60.0);

        if (hours < 1) hours = 1;

        this.durationHours = hours;
        this.parkingFee = hours * hourlyRate;
        
        // Calculate fines
        calculateFines();

        // totalFee = parkingFee + fines
        this.totalFee = this.parkingFee + this.fines;
    }
    
    public void exitVehicle(ParkingSpot spot) {
        this.exitTime = LocalDateTime.now();
        this.hourlyRate = spot.getHourlyRate();

        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        long hours = (long) Math.ceil(minutes / 60.0);

        if (hours < 1) hours = 1;

        this.durationHours = hours;
        
        // Check if vehicle gets free parking in this spot type (polymorphic behavior)
        if (vehicle.hasFreeParkingIn(spot.getType())) {
            this.parkingFee = 0.0;
            this.hourlyRate = 0.0;
            this.fines = 0.0;
            this.totalFee = 0.0;
            return;
        }
        
        this.parkingFee = hours * hourlyRate;
        
        // Check if reserved spot without reservation - fine applies
        if (spot.getType() == SpotType.RESERVED) {
            boolean hasReservation = checkReservation();
            if (!hasReservation) {
                this.isReservedSpotWithoutReservation = true;
            }
        }
        
        // Calculate fines
        calculateFines();
        
        // totalFee = parkingFee + fines
        this.totalFee = this.parkingFee + this.fines;
    }
    
    /**
     * Check if this ticket has an active reservation
     */
    private boolean checkReservation() {
        if (DataManager.reservations == null) return false;
        
        String plate = vehicle.getLicensePlateNumber();
        for (Reservation r : DataManager.reservations) {
            if (r != null && r.matchesPlate(plate) && 
                r.matchesSpot(spotNumber)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Calculate fines based on duration and violations
     */
    private void calculateFines() {
        this.fines = 0.0;
        
        // Check overstaying (>24 hours)
        if (FineManager.isOverstaying(this.durationHours)) {
            this.isOverstaying = true;
            double overstayingFine = FineManager.calculateFine(
                    this.durationHours, 
                    true, 
                    DataManager.currentFineScheme);
            this.fines += overstayingFine;
        }
        
        // Check reserved spot without reservation violation
        if (this.isReservedSpotWithoutReservation) {
            double reservationFine = 50.0; // Fixed RM 50 for reserved spot violation
            this.fines += reservationFine;
        }
    }
}
