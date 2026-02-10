package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import model.IDGenerator;
/**
 * Write a description of class Ticket here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Ticket {
    
    private int ID;
    private Vehicle vehicle;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double totalFee;
    private int spotNumber;
    
    public static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm");
    
    public Ticket() {
        
    }
    
    public Ticket(Vehicle vehicle, int spotNumber) {
        this.ID = IDGenerator.getNextTicketID();
        this.vehicle = vehicle;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
        this.totalFee = 0;
        this.spotNumber = spotNumber;
    }
    
    public Ticket(int ID, Vehicle vehicle, LocalDateTime entryTime, LocalDateTime exitTime, double totalFee, int spotNumber) {
        
        this.ID = ID;
        this.vehicle = vehicle;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.totalFee = totalFee;
        this.spotNumber = spotNumber;
    }
    
    public int getID() {
        return ID;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public LocalDateTime getEntryTime() {
        return entryTime;
    }
    
    public LocalDateTime getExitTime() {
        return exitTime;
    }
    
    public String getEntryDate() {
        return dateFormat.format(entryTime);
    }
    
    public String getEntryTimeToString() {
        return timeFormat.format(entryTime);
    }
    
    public String getExitDate() {
        return dateFormat.format(exitTime);
    }
    
    public String getExitTimeToString() {
        return timeFormat.format(exitTime);
    }
    
    public double getTotalFee() {
        return totalFee;
    }
    
    public int getSpotNumber() {
        return spotNumber;
    }
    
    public void setID(int ID) {
        this.ID = ID;
    }
    
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    
    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }
    
    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }
    
    public void setTotalFee(double totalFee) {
        this.totalFee = totalFee;
    }
    
    public void setSpotNumber(int spotNumber) {
        this.spotNumber = spotNumber;
    }
    
}