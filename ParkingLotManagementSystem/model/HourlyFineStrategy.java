package model;

/**
 * Hourly fine strategy - charges RM 20 per hour of overstaying.
 */
public class HourlyFineStrategy implements FineCalculationStrategy {
    
    private static final double HOURLY_FINE_RATE = 20.0;
    
    @Override
    public double calculateFine(long durationHours) {
        return durationHours * HOURLY_FINE_RATE;
    }
    
    @Override
    public String getDescription() {
        return "Hourly Fine Scheme - RM " + HOURLY_FINE_RATE + " per hour";
    }
}
