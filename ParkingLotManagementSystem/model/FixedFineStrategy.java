package model;

/**
 * Fixed fine strategy - charges a flat rate of RM 50 for overstaying.
 */
public class FixedFineStrategy implements FineCalculationStrategy {
    
    private static final double FIXED_FINE_AMOUNT = 50.0;
    
    @Override
    public double calculateFine(long durationHours) {
        return FIXED_FINE_AMOUNT;
    }
    
    @Override
    public String getDescription() {
        return "Fixed Fine Scheme - RM " + FIXED_FINE_AMOUNT + " flat";
    }
}
