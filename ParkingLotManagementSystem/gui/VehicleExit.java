package gui;

import java.awt.Color;
import javax.swing.*;
import java.awt.*;

import model.*;
import storage.DataManager;
import storage.SaveData;

public class VehicleExit implements Actionable {

    @Override
    public String getLabel() {
        return "Vehicle Exit";
    }

    private User currentUser;

    @Override
    public void execute(User u) {
        this.currentUser = u;
        Dashboard.setContent(getPanel());
    }

    private JPanel panel;
    private static final java.awt.Color PRIMARY = new java.awt.Color(33, 102, 255);

    @Override
    public JPanel getPanel() {

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = new JLabel("Vehicle Exit");
        title.setFont(new Font("Tahoma", Font.BOLD, 30));
        title.setForeground(PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hint = new JLabel("Click Process Exit to begin.");
        hint.setFont(new Font("Tahoma", Font.PLAIN, 14));
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton exitBtn = new JButton("Process Exit");
        exitBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.addActionListener(e -> startExitFlow());

        panel.add(Box.createVerticalStrut(40));
        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(hint);
        panel.add(Box.createVerticalStrut(25));
        panel.add(exitBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(panel, BorderLayout.NORTH);

        return wrapper;
    }

    // =========================
    // EXIT FLOW
    // =========================
    private void startExitFlow() {

        // 1) Ask plate
        String plate = askPlate();
        if (plate == null) return;

        // 2) Find active ticket by plate & owner security check
        Ticket ticket = findActiveTicketByPlate(plate);
        if (ticket == null) {
            JOptionPane.showMessageDialog(null,
                    "No active ticket found for this plate.",
                    "Not Found",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean isAdmin = (currentUser instanceof Admin);
        int ownerId = ticket.getVehicle().getVehicleOwnerID();

        if (!isAdmin) {
            if (currentUser == null || currentUser.getID() != ownerId) {
                JOptionPane.showMessageDialog(null,
                        "You are NOT allowed to exit this vehicle.\nThis ticket belongs to another user.",
                        "Access Denied",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // 3) Find spot
        String spotNumber = ticket.getSpotNumber();
        ParkingSpot spot = findSpotBySpotNumber(spotNumber);

        if (spot == null) {
            JOptionPane.showMessageDialog(null,
                    "Associated parking spot not found.\nSpot: " + spotNumber,
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4a) Unpaid fines from PREVIOUS sessions
        double unpaidFinesFromPrevious = FineManager.getTotalUnpaidFines(plate);

        // 4b) Calculate fee & current session fines
        ticket.exitVehicle(spot);

        double currentSessionFines = ticket.getFines();
        double currentSessionFinesToPay = currentSessionFines;

        double overstayingFineAmount = ticket.isOverstaying()
                ? FineManager.calculateFine(ticket.getDurationHours(), true, DataManager.currentFineScheme)
                : 0.0;

        double reservedSpotFineAmount = ticket.isReservedSpotViolation() ? 50.0 : 0.0;

        // 4c) Option to defer CURRENT session fine payment
        boolean deferCurrentSessionFine = false;
        if (currentSessionFines > 0) {
            int deferChoice = JOptionPane.showConfirmDialog(null,
                    String.format("Fine for current session: RM %.2f\n\nYou can defer this payment to your next visit.\n\nDo you want to PAY NOW?",
                            currentSessionFines),
                    "Fine Payment Option",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (deferChoice != JOptionPane.YES_OPTION) {
                deferCurrentSessionFine = true;
                currentSessionFinesToPay = 0; // not included in this payment
            }
        }

        // 4d) Option to pay previous unpaid fines now
        if (unpaidFinesFromPrevious > 0) {
            int payPrev = JOptionPane.showConfirmDialog(null,
                    String.format("This vehicle has UNPAID FINES from previous sessions: RM %.2f\n\nDo you want to pay them now?",
                            unpaidFinesFromPrevious),
                    "Unpaid Fines",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (payPrev != JOptionPane.YES_OPTION) {
                unpaidFinesFromPrevious = 0; // not included in this payment
            }
        }

        // =========================
        // REQUIRED DISPLAY VALUES
        // =========================
        long hoursParked = ticket.getDurationHours();
        double parkingFee = ticket.getParkingFee();

        // "Any unpaid fines" shown should match what will be charged in THIS payment flow
        double unpaidFinesChargedNow = unpaidFinesFromPrevious + currentSessionFinesToPay;

        // Total due for THIS payment flow
        double totalDue = parkingFee + unpaidFinesChargedNow;

        // 5) Ask payment method with FULL summary (hours, fee, fines, total)
        PaymentMethod method = askPaymentMethod(hoursParked, parkingFee, unpaidFinesChargedNow, totalDue);
        if (method == null) return; // cancelled

        double amountPaid = totalDue;
        double change = 0.0;

        if (method == PaymentMethod.CASH) {
            Double paid = askCashAmount(totalDue);
            if (paid == null) return; // cancelled
            amountPaid = paid;
            change = amountPaid - totalDue;
        } else {
            JOptionPane.showMessageDialog(null,
                    "Processing card payment...\nAPPROVED",
                    "Card Payment",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        // 5b) Determine how much was paid towards fines
        double amountPaidTowardsFines = Math.min(amountPaid, unpaidFinesChargedNow);

        // 5c) Mark previous unpaid fines as paid if they were included and fully paid
        if (unpaidFinesFromPrevious > 0 && amountPaidTowardsFines >= unpaidFinesFromPrevious) {
            FineManager.payAllFines(plate);
        }

        // 5d) Create Fine records for CURRENT session and mark paid if applicable
        double paidCurrentSessionFines = Math.max(0.0, amountPaidTowardsFines - unpaidFinesFromPrevious);

        if (ticket.isOverstaying() && overstayingFineAmount > 0) {
            Fine fine = FineManager.createFine(plate, overstayingFineAmount, "Overstaying (more than 24 hours)");
            if (!deferCurrentSessionFine && paidCurrentSessionFines >= overstayingFineAmount) {
                fine.markAsPaid();
                paidCurrentSessionFines -= overstayingFineAmount;
            }
        }

        if (ticket.isReservedSpotViolation() && reservedSpotFineAmount > 0) {
            Fine fine = FineManager.createFine(plate, reservedSpotFineAmount, "Reserved spot without reservation");
            if (!deferCurrentSessionFine && paidCurrentSessionFines >= reservedSpotFineAmount) {
                fine.markAsPaid();
                paidCurrentSessionFines -= reservedSpotFineAmount;
            }
        }

        // 6) Free spot
        spot.free();

        // 7) Move ticket to history
        DataManager.activeTickets.remove(ticket);
        DataManager.ticketHistory.add(ticket);

        // 7b) End reservation after exit (if this was a reserved booking)
        cancelReservationForExit(spot.getSpotNumber());

        // 8) Save
        SaveData.saveAll();

        // 9) Receipt (keep showing original components for clarity)
        JOptionPane.showMessageDialog(null,
                makeReceipt(ticket, spot, method, amountPaid, change,
                        unpaidFinesFromPrevious, currentSessionFines, deferCurrentSessionFine),
                "Exit Processed",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================
    // HELPERS
    // =========================

    private String askPlate() {
        JTextField plateField = new JTextField(15);

        JPanel p = new JPanel(new GridLayout(1, 2, 10, 10));
        p.add(new JLabel("Plate Number:"));
        p.add(plateField);

        int ok = JOptionPane.showConfirmDialog(null, p,
                "Vehicle Exit",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (ok != JOptionPane.OK_OPTION) return null;

        String plate = plateField.getText().trim().toUpperCase();
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Plate cannot be empty.");
            return null;
        }
        return plate;
    }

    // ✅ Updated: shows Hours Parked, Parking Fee, Unpaid Fines, Total Due (requirement)
    private PaymentMethod askPaymentMethod(long hoursParked,
                                          double parkingFee,
                                          double unpaidFines,
                                          double totalDue) {

        JComboBox<PaymentMethod> box = new JComboBox<>(PaymentMethod.values());

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        p.add(createInfoRow("Hours Parked:", hoursParked + " hour(s)"));
        p.add(Box.createVerticalStrut(6));
        p.add(createInfoRow("Parking Fee:", String.format("RM %.2f", parkingFee)));
        p.add(Box.createVerticalStrut(6));
        p.add(createInfoRow("Unpaid Fines:", String.format("RM %.2f", unpaidFines)));
        p.add(Box.createVerticalStrut(10));

        JPanel totalRow = createInfoRow("Total Payment Due:", String.format("RM %.2f", totalDue));
        totalRow.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        p.add(totalRow);

        p.add(Box.createVerticalStrut(12));

        JPanel methodRow = new JPanel(new BorderLayout(10, 0));
        methodRow.add(new JLabel("Payment Method:"), BorderLayout.WEST);
        methodRow.add(box, BorderLayout.CENTER);
        p.add(methodRow);

        int ok = JOptionPane.showConfirmDialog(null, p,
                "Select Payment Method",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (ok != JOptionPane.OK_OPTION) return null;

        return (PaymentMethod) box.getSelectedItem();
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        JLabel left = new JLabel(label);
        left.setFont(new Font("Tahoma", Font.PLAIN, 13));

        JLabel right = new JLabel(value);
        right.setFont(new Font("Tahoma", Font.BOLD, 13));

        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);

        return row;
    }

    private Double askCashAmount(double totalDue) {
        while (true) {
            String input = JOptionPane.showInputDialog(null,
                    String.format("Total Due: RM %.2f\nEnter cash amount paid:", totalDue),
                    "Cash Payment",
                    JOptionPane.PLAIN_MESSAGE);

            if (input == null) return null; // cancelled

            input = input.trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter an amount.");
                continue;
            }

            try {
                double paid = Double.parseDouble(input);
                if (paid < totalDue) {
                    JOptionPane.showMessageDialog(null,
                            "Insufficient cash.\nAmount must be >= total due.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                return paid;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                        "Invalid number format.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelReservationForExit(String spotNumber) {
        if (DataManager.reservations == null) return;
        for (Reservation r : DataManager.reservations) {
            if (r == null || !r.isActive()) continue;
            if (r.matchesSpot(spotNumber)) {
                r.cancel();
                return;
            }
        }
    }

    private Ticket findActiveTicketByPlate(String plate) {
        if (DataManager.activeTickets == null) return null;

        for (Ticket t : DataManager.activeTickets) {
            if (t != null && t.getVehicle() != null &&
                    t.getVehicle().getLicensePlateNumber() != null &&
                    t.getVehicle().getLicensePlateNumber().equalsIgnoreCase(plate)) {
                return t;
            }
        }
        return null;
    }

    private ParkingSpot findSpotBySpotNumber(String spotNumber) {
        if (DataManager.parkingSpots == null) return null;

        for (ParkingSpot s : DataManager.parkingSpots) {
            if (s != null && s.getSpotNumber() != null &&
                    s.getSpotNumber().equalsIgnoreCase(spotNumber)) {
                return s;
            }
        }
        return null;
    }

    private String makeReceipt(Ticket t, ParkingSpot spot,
                               PaymentMethod method, double amountPaid, double change,
                               double unpaidFinesFromPrevious, double currentSessionFines,
                               boolean deferCurrentSessionFine) {

        String exitDate = (t.getExitDate() == null || t.getExitDate().trim().isEmpty())
                ? "-"
                : t.getExitDate();

        String exitTime = (t.getExitTimeToString() == null || t.getExitTimeToString().trim().isEmpty())
                ? "-"
                : t.getExitTimeToString();

        long hours = t.getDurationHours();
        double rate = t.getHourlyRate();
        double parkingFee = t.getParkingFee();

        // Calculate what was actually paid vs deferred
        double currentSessionFinesPaid = deferCurrentSessionFine ? 0 : currentSessionFines;
        double finesDue = currentSessionFinesPaid + unpaidFinesFromPrevious;
        double totalDue = t.getParkingFee() + finesDue;
        double remainingBalance = Math.max(0.0, totalDue - amountPaid);

        StringBuilder sb = new StringBuilder();
        sb.append("Ticket Code: ").append(t.getTicketCode()).append("\n\n")
          .append("Plate: ").append(t.getVehicle().getLicensePlateNumber()).append("\n")
          .append("Spot: ").append(spot.getSpotNumber()).append("\n")
          .append("Spot Type: ").append(spot.getType()).append("\n\n")
          .append("Entry: ").append(t.getEntryDate()).append(" ").append(t.getEntryTimeToString()).append("\n")
          .append("Exit:  ").append(exitDate).append(" ").append(exitTime).append("\n\n")
          .append("Duration: ").append(hours).append(" hour(s)\n")
          .append(String.format("Hourly Rate: RM %.2f / hour\n", rate))
          .append(String.format("Parking Fee: %d x RM %.2f = RM %.2f\n\n", hours, rate, parkingFee));

        // Fine details
        if (currentSessionFines > 0) {
            if (deferCurrentSessionFine) {
                sb.append("Fines (Current Session - DEFERRED): RM ").append(String.format("%.2f\n", currentSessionFines));
                sb.append("  ** Payment deferred to next visit **\n");
            } else {
                sb.append("Fines (Current Session): RM ").append(String.format("%.2f\n", currentSessionFines));
            }
            if (t.isOverstaying()) {
                sb.append("  - Overstaying: RM ").append(String.format("%.2f\n", currentSessionFines));
            }
            if (t.isReservedSpotViolation()) {
                sb.append("  - Reserved Spot Without Reservation: RM 50.00\n");
            }
        }

        if (unpaidFinesFromPrevious > 0) {
            sb.append(String.format("Fines (Previous Sessions): RM %.2f\n", unpaidFinesFromPrevious));
        }

        sb.append("\n")
          .append(String.format("Total Fines Due: RM %.2f\n", finesDue));

        if (deferCurrentSessionFine && currentSessionFines > 0) {
            sb.append(String.format("Total Fines Deferred: RM %.2f\n", currentSessionFines));
        }

        sb.append(String.format("Total Due: RM %.2f\n", totalDue))
          .append("Payment Method: ").append(method).append("\n")
          .append(String.format("Total Paid: RM %.2f\n", amountPaid))
          .append(String.format("Remaining Balance: RM %.2f\n", remainingBalance));

        if (method == PaymentMethod.CASH) {
            sb.append(String.format("Change: RM %.2f\n", change));
        } else {
            sb.append(String.format("Card Charged: RM %.2f\n", totalDue));
        }

        return sb.toString();
    }
}
