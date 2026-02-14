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

    private double totalFee;

    public Ticket(Vehicle vehicle, String spotNumber) {
        this.id = IDGenerator.getNextTicketID();
        this.vehicle = vehicle;
        this.spotNumber = spotNumber;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
        this.totalFee = 0;

        this.ticketCode =
                "T-" + vehicle.getLicensePlateNumber()
                + "-" + entryTime.format(CODE_FMT);
    }

    public int getId() { return id; }
    public String getTicketCode() { return ticketCode; }
    public Vehicle getVehicle() { return vehicle; }
    public String getspotNumber() { return spotNumber; }
    public String getSpotNumber() { return spotNumber; }
    public String getEntryDate() {return entryTime.format(DATE_FMT);}
    public String getEntryTimeToString() {return entryTime.format(TIME_FMT);}
    public String getExitDate() {return exitTime == null ? "-" : exitTime.format(DATE_FMT);}
    public String getExitTimeToString() {return exitTime == null ? "-" : exitTime.format(TIME_FMT);}
    public boolean hasExited() {return exitTime != null;}
    public double getTotalFee() { return totalFee; }

    public void exitVehicle(double hourlyRate) {
    
        this.exitTime = LocalDateTime.now();
    
        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        long hours = (long) Math.ceil(minutes / 60.0);
    
        if (hours < 1) {
            hours = 1;
        }
    
        totalFee = hours * hourlyRate;
    }

}
