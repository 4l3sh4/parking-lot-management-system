package model;

/**
 * Strategy interface for calculating fines based on duration.
 * Implementing new fine schemes requires only creating a new strategy class.
 */
public interface FineCalculationStrategy {
    
    /**
     * Calculate the fine amount based on the duration.
     * @param durationHours The duration for which the vehicle overstayed
     * @return The fine amount
     */
    double calculateFine(long durationHours);
    
    /**
     * Get a description of this fine calculation strategy.
     * @return A string describing how this strategy works
     */
    String getDescription();
}
