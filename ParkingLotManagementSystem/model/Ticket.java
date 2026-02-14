package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    // totalFee can later become parkingFee + fines
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
        this.totalFee = 0.0;

        this.ticketCode =
                "T-" + vehicle.getLicensePlateNumber()
                + "-" + entryTime.format(CODE_FMT);
    }

    public int getId() { return id; }
    public String getTicketCode() { return ticketCode; }
    public Vehicle getVehicle() { return vehicle; }

    // keep both if you already used them elsewhere
    public String getspotNumber() { return spotNumber; }
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

    public double getTotalFee() { return totalFee; }

    public void exitVehicle(double hourlyRate) {
        this.exitTime = LocalDateTime.now();
        this.hourlyRate = hourlyRate;

        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        long hours = (long) Math.ceil(minutes / 60.0);

        if (hours < 1) hours = 1;

        this.durationHours = hours;
        this.parkingFee = hours * hourlyRate;

        // for now, totalFee = parkingFee (later you can add fines)
        this.totalFee = this.parkingFee;
    }
}
