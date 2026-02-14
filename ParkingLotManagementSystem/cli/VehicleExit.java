package cli;

import java.util.Scanner;

import model.Actionable;
import model.User;
import model.ParkingLotManager;
import model.Ticket;
import model.ParkingSpot;
import model.PaymentMethod;

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

        // Calculate fee (sets exit time + duration hours + breakdown fields if Ticket was updated)
        ticket.exitVehicle(spot.getHourlyRate());

        double totalDue = ticket.getTotalFee();

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

        // Complete exit
        spot.free();
        DataManager.ticketHistory.add(ticket);
        DataManager.activeTickets.remove(ticket);
        SaveData.saveAll();

        // Receipt 
        System.out.println("Vehicle exited successfully");
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

        double finesDue = 0.00; // if you later have fines: ticket.getFinesDue()
        System.out.printf("\tFines Due: RM %.2f%n", finesDue);

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

    @Override
    public boolean isAdminOnly() {
        return false;
    }
}
