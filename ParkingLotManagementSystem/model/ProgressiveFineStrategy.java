package model;

/**
 * Progressive fine strategy - charges incrementally based on duration brackets.
 * - First 24 hours: RM 50
 * - Hours 24-48: Additional RM 100
 * - Hours 48-72: Additional RM 150
 * - Above 72 hours: Additional RM 200
 */
public class ProgressiveFineStrategy implements FineCalculationStrategy {
    
    @Override
    public double calculateFine(long durationHours) {
        double fine = 0.0;

        if (durationHours > 0) {
            fine += 50.0; // First 24 hours
        }
        if (durationHours > 24) {
            fine += 100.0; // Hours 24-48
        }
        if (durationHours > 48) {
            fine += 150.0; // Hours 48-72
        }
        if (durationHours > 72) {
            fine += 200.0; // Above 72 hours
        }

        return fine;
    }
    
    @Override
    public String getDescription() {
        return "Progressive Fine Scheme - Incremental by hours";
    }
}
