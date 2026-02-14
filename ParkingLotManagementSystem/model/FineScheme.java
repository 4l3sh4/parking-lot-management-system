package model;

public enum FineScheme {
    FIXED("Fixed Fine Scheme - RM 50 flat"),
    PROGRESSIVE("Progressive Fine Scheme - Incremental by hours"),
    HOURLY("Hourly Fine Scheme - RM 20 per hour");

    private String description;

    FineScheme(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return this.name().replace('_', ' ');
    }
}
