package model;

import java.time.Duration;
/**
 * Write a description of class Car here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Handicapped_Vehicle extends Vehicle {
    //RM2/hour (FREE only if have handicapped card holder in handicapped spots) 
    private boolean hasHandicappedCard;

    public Handicapped_Vehicle(String licensePlate, boolean hasHandicappedCard) {
        super(licensePlate);
        this.hasHandicappedCard = hasHandicappedCard;
    }

    @Override
    public double calculateFee(java.time.Duration duration) {
        //FREE only if handicapped card holder
        if (hasHandicappedCard) {
            return 0.0;
        }

        long minutes = duration.toMinutes();
        double hoursToCharge = Math.ceil(minutes / 60.0);
        return hoursToCharge * 2.0; // Discounted rate
    }
}