package model;

import storage.DataManager;
import java.util.ArrayList;

public class FineManager {

    /**
     * Calculate fine based on the current scheme and duration.
     * Uses Strategy pattern for extensibility.
     */
    public static double calculateFine(long durationHours, boolean isOverstaying, FineScheme scheme) {
        if (!isOverstaying) {
            return 0.0;
        }

        if (scheme == null) {
            scheme = FineScheme.FIXED; // default
        }

        // Use the strategy pattern instead of switch statement
        FineCalculationStrategy strategy = scheme.getStrategy();
        return strategy.calculateFine(durationHours);
    }

    /**
     * Check if vehicle is overstaying (more than 24 hours)
     */
    public static boolean isOverstaying(long durationHours) {
        return durationHours > 24;
    }

    /**
     * Create and add a new fine to the system
     */
    public static Fine createFine(String licensePlate, double amount, String reason) {
        Fine fine = new Fine(licensePlate, amount, reason);
        DataManager.fines.add(fine);
        return fine;
    }

    /**
     * Find all unpaid fines for a vehicle
     */
    public static ArrayList<Fine> getUnpaidFines(String licensePlate) {
        ArrayList<Fine> unpaid = new ArrayList<>();
        String normalizedPlate = licensePlate.toUpperCase();

        for (Fine fine : DataManager.fines) {
            if (fine != null && !fine.isPaid() && 
                fine.getLicensePlate().equalsIgnoreCase(normalizedPlate)) {
                unpaid.add(fine);
            }
        }
        return unpaid;
    }

    /**
     * Calculate total unpaid fines for a vehicle
     */
    public static double getTotalUnpaidFines(String licensePlate) {
        double total = 0.0;
        for (Fine fine : getUnpaidFines(licensePlate)) {
            total += fine.getAmount();
        }
        return total;
    }

    /**
     * Mark a fine as paid
     */
    public static void payFine(int fineId) {
        for (Fine fine : DataManager.fines) {
            if (fine != null && fine.getId() == fineId) {
                fine.markAsPaid();
                return;
            }
        }
    }

    /**
     * Pay all unpaid fines for a vehicle
     */
    public static void payAllFines(String licensePlate) {
        ArrayList<Fine> unpaid = getUnpaidFines(licensePlate);
        for (Fine fine : unpaid) {
            fine.markAsPaid();
        }
    }

    /**
     * Get all fines (paid and unpaid)
     */
    public static ArrayList<Fine> getAllFines() {
        return new ArrayList<>(DataManager.fines);
    }

    /**
     * Get paid fines only
     */
    public static ArrayList<Fine> getPaidFines() {
        ArrayList<Fine> paid = new ArrayList<>();
        for (Fine fine : DataManager.fines) {
            if (fine != null && fine.isPaid()) {
                paid.add(fine);
            }
        }
        return paid;
    }

    /**
     * Get unpaid fines for all vehicles
     */
    public static ArrayList<Fine> getAllUnpaidFines() {
        ArrayList<Fine> unpaid = new ArrayList<>();
        for (Fine fine : DataManager.fines) {
            if (fine != null && !fine.isPaid()) {
                unpaid.add(fine);
            }
        }
        return unpaid;
    }

    /**
     * Get statistics about fines
     */
    public static FineStatistics getStatistics() {
        FineStatistics stats = new FineStatistics();
        
        for (Fine fine : DataManager.fines) {
            if (fine == null) continue;
            
            stats.totalFines++;
            stats.totalAmount += fine.getAmount();
            
            if (fine.isPaid()) {
                stats.paidFines++;
                stats.paidAmount += fine.getAmount();
            } else {
                stats.unpaidFines++;
                stats.unpaidAmount += fine.getAmount();
            }
        }
        
        return stats;
    }

    /**
     * Statistics helper class
     */
    public static class FineStatistics {
        public int totalFines = 0;
        public int paidFines = 0;
        public int unpaidFines = 0;
        public double totalAmount = 0;
        public double paidAmount = 0;
        public double unpaidAmount = 0;

        @Override
        public String toString() {
            return String.format(
                    "Total Fines: %d | Paid: %d | Unpaid: %d\n" +
                    "Total Amount: RM %.2f | Paid: RM %.2f | Unpaid: RM %.2f",
                    totalFines, paidFines, unpaidFines,
                    totalAmount, paidAmount, unpaidAmount);
        }
    }
}
