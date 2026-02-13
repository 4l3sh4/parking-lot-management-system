package model;

import java.time.Duration;

public class Car extends Vehicle {
    public Car(String licensePlate) {
        super(licensePlate);
    }
    
    @Override
    public double calculateFee(Duration duration) {
        
        long minutes = duration.toMinutes();
        
        //for ceiling rounding
        double hoursToCharge = Math.ceil(minutes/60.0);
        
        //RM5/hour
        double totalFee = hoursToCharge * 5.0;
        
        return totalFee;
    }
}