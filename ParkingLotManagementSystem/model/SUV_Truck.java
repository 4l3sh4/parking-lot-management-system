package model;

import java.time.Duration;
/**
 * Write a description of class Car here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class SUV_Truck extends Vehicle {
    public SUV_Truck(String licensePlate) {
        super(licensePlate);
    }
    
    @Override
    public double calculateFee(Duration duration) {
        long minutes = duration.toMinutes();
        
        //ceiling rounding
        double hoursToCharge = Math.ceil(minutes/60.0);
        
        //SUV/truck park in regular so RM5/hour
        double totalFee = hoursToCharge * 5.0;
        
        return totalFee;
    }
}