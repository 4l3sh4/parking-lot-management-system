package cli;

import java.util.ArrayList;
import java.util.Scanner;

import model.*;
import storage.DataManager;

public class ViewFines implements Actionable {

    @Override
    public String getLabel() {
        return "View All Fines";
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }

    @Override
    public void execute(Scanner scanner, User u) {
        if (!(u instanceof Admin)) {
            System.out.println("Access denied. Only admins can view fines.");
            return;
        }

        System.out.println("\n========== FINE MANAGEMENT ==========");
        System.out.println("1. View All Fines");
        System.out.println("2. View Unpaid Fines");
        System.out.println("3. View Fines by License Plate");
        System.out.println("4. View Fine Statistics");
        System.out.println("5. Back");
        System.out.print("Select option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                viewAllFines();
                break;
            case "2":
                viewUnpaidFines();
                break;
            case "3":
                viewFinesByPlate(scanner);
                break;
            case "4":
                viewStatistics();
                break;
            case "5":
                return;
            default:
                System.out.println("Invalid option.");
        }
    }

    private void viewAllFines() {
        ArrayList<Fine> fines = FineManager.getAllFines();

        if (fines.isEmpty()) {
            System.out.println("\nNo fines recorded.");
            return;
        }

        System.out.println("\n========== ALL FINES ==========");
        System.out.printf("%-6s %-12s %-12s %-40s %-10s %-20s\n",
                "ID", "Plate", "Amount(RM)", "Reason", "Status", "Created");
        System.out.println("-".repeat(100));

        for (Fine fine : fines) {
            if (fine != null) {
                System.out.printf("%-6d %-12s %-12.2f %-40s %-10s %-20s\n",
                        fine.getId(),
                        fine.getLicensePlate(),
                        fine.getAmount(),
                        fine.getReason(),
                        fine.isPaid() ? "PAID" : "UNPAID",
                        fine.getCreatedDate() + " " + fine.getCreatedTime());
            }
        }

        FineManager.FineStatistics stats = FineManager.getStatistics();
        System.out.println("-".repeat(100));
        System.out.println("\nSummary: " + stats.toString());
    }

    private void viewUnpaidFines() {
        ArrayList<Fine> unpaid = FineManager.getAllUnpaidFines();

        if (unpaid.isEmpty()) {
            System.out.println("\nNo unpaid fines.");
            return;
        }

        System.out.println("\n========== UNPAID FINES ==========");
        System.out.printf("%-6s %-12s %-12s %-40s %-20s\n",
                "ID", "Plate", "Amount(RM)", "Reason", "Created");
        System.out.println("-".repeat(90));

        double totalUnpaid = 0;
        for (Fine fine : unpaid) {
            if (fine != null && !fine.isPaid()) {
                System.out.printf("%-6d %-12s %-12.2f %-40s %-20s\n",
                        fine.getId(),
                        fine.getLicensePlate(),
                        fine.getAmount(),
                        fine.getReason(),
                        fine.getCreatedDate() + " " + fine.getCreatedTime());
                totalUnpaid += fine.getAmount();
            }
        }

        System.out.println("-".repeat(90));
        System.out.printf("Total Unpaid Fines: RM %.2f\n", totalUnpaid);
    }

    private void viewFinesByPlate(Scanner scanner) {
        System.out.print("\nEnter license plate: ");
        String plate = scanner.nextLine().trim().toUpperCase();

        ArrayList<Fine> fines = FineManager.getUnpaidFines(plate);

        if (fines.isEmpty()) {
            System.out.println("No fines found for plate: " + plate);
            return;
        }

        System.out.println("\n========== FINES FOR " + plate + " ==========");
        System.out.printf("%-6s %-12s %-40s %-10s %-20s\n",
                "ID", "Amount(RM)", "Reason", "Status", "Created");
        System.out.println("-".repeat(88));

        double totalFines = 0;
        for (Fine fine : fines) {
            if (fine != null) {
                System.out.printf("%-6d %-12.2f %-40s %-10s %-20s\n",
                        fine.getId(),
                        fine.getAmount(),
                        fine.getReason(),
                        fine.isPaid() ? "PAID" : "UNPAID",
                        fine.getCreatedDate() + " " + fine.getCreatedTime());
                if (!fine.isPaid()) {
                    totalFines += fine.getAmount();
                }
            }
        }

        System.out.println("-".repeat(88));
        System.out.printf("Total Unpaid Fines: RM %.2f\n", totalFines);
    }

    private void viewStatistics() {
        FineManager.FineStatistics stats = FineManager.getStatistics();

        System.out.println("\n========== FINE STATISTICS ==========");
        System.out.println("Total Fines: " + stats.totalFines);
        System.out.println("  - Paid: " + stats.paidFines);
        System.out.println("  - Unpaid: " + stats.unpaidFines);
        System.out.println("\nTotal Amount: RM " + String.format("%.2f", stats.totalAmount));
        System.out.println("  - Paid: RM " + String.format("%.2f", stats.paidAmount));
        System.out.println("  - Unpaid: RM " + String.format("%.2f", stats.unpaidAmount));
        System.out.println("\nCurrent Fine Scheme: " + DataManager.currentFineScheme.toString());
        System.out.println("Description: " + DataManager.currentFineScheme.getDescription());
    }
}
