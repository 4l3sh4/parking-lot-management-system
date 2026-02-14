package cli;

import java.util.Scanner;

import model.Actionable;
import model.User;
import model.ParkingLotManager;
import model.Ticket;
import model.ParkingSpot;
import model.PaymentMethod;
import model.FineManager;

import storage.DataManager;
import storage.SaveData;
import util.ConsoleInput;

public class VehicleExit implements Actionable {

    @Override
    public String getLabel() {
        return "Vehicle Exit";
    }

    @Override
    public void execute(Scanner s, User u) {

        int selected;
        do {
            System.out.println("Select Ticket by: ");
            System.out.println("1. Vehicle Plate Number");
            System.out.println("2. Parking Spot Number");
            selected = ConsoleInput.readInt(s);
        } while (selected != 1 && selected != 2);

        Ticket ticket = null;

        if (selected == 1) {
            System.out.print("Enter Vehicle Plate Number: ");
            String plate = ConsoleInput.readLineString(s).trim();
            ticket = ParkingLotManager.findActiveTicketByPlate(plate);

        } else {
            System.out.print("Enter Parking Spot Number (e.g., F1-R1-S1): ");
            String spotNum = ConsoleInput.readLineString(s).trim();
            ticket = ParkingLotManager.findActiveTicketBySpot(spotNum);
        }

        if (ticket == null) {
            System.out.println("Ticket not found.");
            return;
        }

        if (ticket.hasExited()) {
            System.out.println("Vehicle exited before.");
            return;
        }

        ParkingSpot spot = ParkingLotManager.findSpotByNum(ticket.getSpotNumber());
        if (spot == null) {
            System.out.println("Spot not found for ticket: " + ticket.getSpotNumber());
            return;
        }

        String plate = ticket.getVehicle().getLicensePlateNumber();

        // Check for unpaid fines from PREVIOUS sessions BEFORE calculating current session
        double unpaidFinesFromPrevious = FineManager.getTotalUnpaidFines(plate);

        // Calculate fee (sets exit time + duration hours + breakdown fields if Ticket was updated)
        ticket.exitVehicle(spot);
        
        double currentSessionFines = ticket.getFines();
        double currentSessionFinesToPay = currentSessionFines;
        
        // Give option to defer current session fine payment (applies to all fine schemes)
        boolean deferCurrentSessionFine = false;
        if (currentSessionFines > 0) {
            System.out.printf("\nFine for current session: RM %.2f%n", currentSessionFines);
            System.out.println("You can defer this payment to your next visit.");
            System.out.print("Do you want to PAY NOW? (y/n): ");
            String payChoice = ConsoleInput.readString(s).toLowerCase();
            
            if (!payChoice.equals("y")) {
                deferCurrentSessionFine = true;
                currentSessionFinesToPay = 0; // Don't include in total due
                System.out.println("Fine payment deferred to next visit.");
            }
        }
        
        double totalDue = ticket.getParkingFee() + currentSessionFinesToPay + unpaidFinesFromPrevious;
        
        // Show unpaid fines from previous sessions if any
        if (unpaidFinesFromPrevious > 0) {
            System.out.printf("\n** ALERT: This vehicle has UNPAID FINES from previous sessions: RM %.2f **%n", unpaidFinesFromPrevious);
            System.out.print("Do you want to pay them now? (y/n): ");
            String payFinesChoice = ConsoleInput.readString(s).toLowerCase();
            
            if (!payFinesChoice.equals("y")) {
                totalDue -= unpaidFinesFromPrevious; // Remove from total if not paying
                unpaidFinesFromPrevious = 0;
                System.out.println("Previous fines will remain unpaid.");
            }
        }

        // Only accept CASH / CARD
        PaymentMethod method = askPaymentMethod(s);

        double amountPaid = 0.0;
        double change = 0.0;

        if (method == PaymentMethod.CARD) {
            amountPaid = totalDue;
            change = 0.0;
            System.out.println("Processing card payment... APPROVED");
        } else {
            do {
                System.out.printf("Total due: RM %.2f%n", totalDue);
                System.out.print("Enter cash amount paid: ");
                amountPaid = ConsoleInput.readDouble(s);

                if (amountPaid < totalDue) {
                    System.out.println("Insufficient cash. Please enter an amount >= total due.");
                }
            } while (amountPaid < totalDue);

            change = amountPaid - totalDue;
        }
        
        // Determine how much was paid towards fines
        double amountPaidTowardsFines = Math.min(amountPaid, unpaidFinesFromPrevious + currentSessionFinesToPay);
        
        // Mark previous unpaid fines as paid if they were included in the payment
        if (unpaidFinesFromPrevious > 0 && amountPaidTowardsFines >= unpaidFinesFromPrevious) {
            FineManager.payAllFines(plate);
        }

        // Create Fine records for deferred or unpaid fines from CURRENT session
        if (deferCurrentSessionFine) {
            // Customer chose to defer payment - create Fine records for entire current session fines
            if (ticket.isOverstaying()) {
                FineManager.createFine(plate, currentSessionFines, "Overstaying (more than 24 hours)");
            }
            if (ticket.isReservedSpotViolation()) {
                FineManager.createFine(plate, 50.0, "Reserved spot without reservation");
            }
        } else {
            // Customer chose to pay now - only create Fine records if there's unpaid amount
            double unpaidCurrentSessionFines = Math.max(0, currentSessionFines - (amountPaidTowardsFines - unpaidFinesFromPrevious));
            if (unpaidCurrentSessionFines > 0) {
                if (ticket.isOverstaying()) {
                    double overstayingFineAmount = Math.min(currentSessionFines, unpaidCurrentSessionFines);
                    FineManager.createFine(plate, overstayingFineAmount, "Overstaying (more than 24 hours)");
                }
                if (ticket.isReservedSpotViolation()) {
                    FineManager.createFine(plate, 50.0, "Reserved spot without reservation");
                }
            }
        }

        // Complete exit
        spot.free();
        DataManager.ticketHistory.add(ticket);
        DataManager.activeTickets.remove(ticket);
        cancelReservationForExit(spot.getSpotNumber());
        SaveData.saveAll();

        // Receipt 
        System.out.println("\nVehicle exited successfully");
        System.out.println("\t----------------------------");
        System.out.println("\tTicket Code: " + ticket.getTicketCode());
        System.out.println("\tPlate Number: " + ticket.getVehicle().getLicensePlateNumber());
        System.out.println("\tSpot Number: " + ticket.getSpotNumber());
        System.out.println("\tSpot Type: " + spot.getType());

        System.out.println("\tEntry Date/Time: " + ticket.getEntryDate() + " " + ticket.getEntryTimeToString());
        System.out.println("\tExit Date/Time:  " + ticket.getExitDate() + " " + ticket.getExitTimeToString());

        long hours = ticket.getDurationHours();     
        double rate = ticket.getHourlyRate();       
        double parkingFee = ticket.getParkingFee(); 

        System.out.println("\tDuration (hours): " + hours);
        System.out.printf("\tHourly Rate: RM %.2f / hour%n", rate);
        System.out.printf("\tParking Fee Breakdown: %d x RM %.2f = RM %.2f%n", hours, rate, parkingFee);

        // Show fine details
        if (currentSessionFines > 0) {
            if (deferCurrentSessionFine) {
                System.out.printf("\tFines (Current Session - DEFERRED): RM %.2f%n", currentSessionFines);
                System.out.println("\t  ** Payment deferred to next visit **");
            } else {
                System.out.printf("\tFines (Current Session): RM %.2f%n", currentSessionFines);
            }
            if (ticket.isOverstaying()) {
                System.out.printf("\t  - Overstaying: RM %.2f%n", currentSessionFines);
            }
            if (ticket.isReservedSpotViolation()) {
                System.out.println("\t  - Reserved Spot Without Reservation: RM 50.00");
            }
        }
        
        if (unpaidFinesFromPrevious > 0) {
            System.out.printf("\tFines (Previous Sessions): RM %.2f%n", unpaidFinesFromPrevious);
        }
        
        double finesDue = (deferCurrentSessionFine ? 0 : currentSessionFines) + unpaidFinesFromPrevious;
        System.out.printf("\tTotal Fines Due: RM %.2f%n", finesDue);
        
        if (deferCurrentSessionFine && currentSessionFines > 0) {
            System.out.printf("\tTotal Fines Deferred: RM %.2f%n", currentSessionFines);
        }

        System.out.printf("\tTotal Due: RM %.2f%n", totalDue);
        System.out.println("\tPayment Method: " + method);
        System.out.printf("\tTotal Paid: RM %.2f%n", amountPaid);

        double remainingBalance = Math.max(0.0, totalDue - amountPaid);
        System.out.printf("\tRemaining Balance: RM %.2f%n", remainingBalance);

        if (method == PaymentMethod.CASH) {
            System.out.printf("\tChange: RM %.2f%n", change);
        } else {
            System.out.printf("\tCard Charged: RM %.2f%n", totalDue);
        }

        System.out.println("\t----------------------------");
    }

    private PaymentMethod askPaymentMethod(Scanner s) {
        int choice;
        do {
            System.out.println("Select payment method:");
            System.out.println("1. Cash");
            System.out.println("2. Card");
            choice = ConsoleInput.readInt(s);
        } while (choice != 1 && choice != 2);

        return (choice == 1) ? PaymentMethod.CASH : PaymentMethod.CARD;
    }

    private void cancelReservationForExit(String spotNumber) {
        if (DataManager.reservations == null) return;
        for (model.Reservation r : DataManager.reservations) {
            if (r == null || !r.isActive()) continue;
            if (r.matchesSpot(spotNumber)) {
                r.cancel();
                return;
            }
        }
    }

    @Override
    public boolean isAdminOnly() {
        return false;
    }
}
