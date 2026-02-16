package model;

public enum FineScheme {
    FIXED("Fixed Fine Scheme - RM 50 flat", new FixedFineStrategy()),
    PROGRESSIVE("Progressive Fine Scheme - Incremental by hours", new ProgressiveFineStrategy()),
    HOURLY("Hourly Fine Scheme - RM 20 per hour", new HourlyFineStrategy());

    private String description;
    private FineCalculationStrategy strategy;

    FineScheme(String description, FineCalculationStrategy strategy) {
        this.description = description;
        this.strategy = strategy;
    }

    public String getDescription() {
        return description;
    }
    
    /**
     * Get the fine calculation strategy for this scheme.
     * @return The FineCalculationStrategy implementation
     */
    public FineCalculationStrategy getStrategy() {
        return strategy;
    }

    @Override
    public String toString() {
        return this.name().replace('_', ' ');
    }
}
