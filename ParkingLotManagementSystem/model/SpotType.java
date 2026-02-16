package model;


/**
 * Enumeration class SpotType - defines parking spot types and their rates.
 * Adding a new spot type requires only adding it here with its rate.
 */
public enum SpotType
{
    COMPACT(2.0),      //motorcycle, car, handicapped vehicle
    REGULAR(5.0),      //car, handicapped vehicle
    HANDICAPPED(2.0),  // handicapped vehicle
    RESERVED(10.0);    //VIP
    
    private final double hourlyRate;
    
    SpotType(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
    
    /**
     * Get the hourly rate for this spot type.
     * @return hourly rate in currency units
     */
    public double getHourlyRate() {
        return hourlyRate;
    }
}