package model;

import java.time.Duration;

/**
 * Enumeration class VehicleType - write a description of the enum class here
 *
 * @author (your name here)
 * @version (version number or date here)
 */

public interface Payable {
    
    public double calculateFee(Duration duration);
}