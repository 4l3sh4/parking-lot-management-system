package model;

import java.time.Duration;

public class Motorcycle extends Vehicle {
    public Motorcycle(String licensePlate) {
        super(licensePlate); // Now this works!
    }
    
    @Override
    public double calculateFee(Duration duration) {
        
        long minutes = duration.toMinutes();
        
        //for ceiling rounding
        double hoursToCharge = Math.ceil(minutes/60.0);
        
        //RM2/hour
        double totalFee = hoursToCharge * 2.0;
        
        return totalFee;
    }
}