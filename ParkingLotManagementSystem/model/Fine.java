package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Fine {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private int id;
    private String licensePlate;
    private double amount;
    private String reason;  // "Overstaying" or "Reserved Spot Without Reservation"
    private boolean paid;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public Fine(String licensePlate, double amount, String reason) {
        this.id = IDGenerator.getNextFineID();
        this.licensePlate = licensePlate.toUpperCase();
        this.amount = amount;
        this.reason = reason;
        this.paid = false;
        this.createdAt = LocalDateTime.now();
        this.paidAt = null;
    }

    // Getters
    public int getId() { return id; }
    public String getLicensePlate() { return licensePlate; }
    public double getAmount() { return amount; }
    public String getReason() { return reason; }
    public boolean isPaid() { return paid; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getPaidAt() { return paidAt; }

    public String getCreatedDate() {
        return createdAt == null ? "-" : createdAt.format(DATE_FMT);
    }

    public String getCreatedTime() {
        return createdAt == null ? "-" : createdAt.format(TIME_FMT);
    }

    public String getPaidDate() {
        return paidAt == null ? "-" : paidAt.format(DATE_FMT);
    }

    public String getPaidTime() {
        return paidAt == null ? "-" : paidAt.format(TIME_FMT);
    }

    // Setters
    public void markAsPaid() {
        this.paid = true;
        this.paidAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("Fine #%d | Plate: %s | Amount: RM %.2f | %s | Status: %s",
                id, licensePlate, amount, reason, (paid ? "PAID" : "UNPAID"));
    }
}
