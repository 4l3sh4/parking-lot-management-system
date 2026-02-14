package gui;

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

        JButton exitBtn = new JButton("Process Exit");
        exitBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.addActionListener(e -> startExitFlow());

        panel.add(Box.createVerticalStrut(40));
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
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

        // 4) Calculate fee (Ticket will set exit time + duration hours + breakdown fields if you updated Ticket)
        ticket.exitVehicle(spot);
        double totalDue = ticket.getTotalFee();

        // 5) Ask payment method: CASH/CARD only
        PaymentMethod method = askPaymentMethod(totalDue);
        if (method == null) return; // user cancelled

        double amountPaid = totalDue;
        double change = 0.0;

        if (method == PaymentMethod.CASH) {
            Double paid = askCashAmount(totalDue);
            if (paid == null) return; // cancelled
            amountPaid = paid;
            change = amountPaid - totalDue;
        } else {
            // CARD
            JOptionPane.showMessageDialog(null,
                    "Processing card payment...\nAPPROVED",
                    "Card Payment",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        // 6) Free spot
        spot.free();

        // 7) Move ticket to history
        DataManager.activeTickets.remove(ticket);
        DataManager.ticketHistory.add(ticket);

        // 8) Save
        SaveData.saveAll();

        // 9) Receipt
        JOptionPane.showMessageDialog(null,
                makeReceipt(ticket, spot, method, amountPaid, change),
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

    // CASH/CARD only (enum guarantees only two options)
    private PaymentMethod askPaymentMethod(double totalDue) {
        JComboBox<PaymentMethod> box = new JComboBox<>(PaymentMethod.values());

        JPanel p = new JPanel(new GridLayout(2, 2, 10, 10));
        p.add(new JLabel("Total Due:"));
        p.add(new JLabel(String.format("RM %.2f", totalDue)));
        p.add(new JLabel("Payment Method:"));
        p.add(box);

        int ok = JOptionPane.showConfirmDialog(null, p,
                "Select Payment Method",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (ok != JOptionPane.OK_OPTION) return null;

        return (PaymentMethod) box.getSelectedItem();
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
                               PaymentMethod method, double amountPaid, double change) {

        String exitDate = (t.getExitDate() == null || t.getExitDate().trim().isEmpty())
                ? "-"
                : t.getExitDate();

        String exitTime = (t.getExitTimeToString() == null || t.getExitTimeToString().trim().isEmpty())
                ? "-"
                : t.getExitTimeToString();

        long hours = t.getDurationHours();     
        double rate = t.getHourlyRate();       
        double parkingFee = t.getParkingFee(); 

        double finesDue = 0.00;                // if you later support fines, replace with t.getFinesDue()
        double totalDue = t.getTotalFee();
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
          .append(String.format("Breakdown: %d x RM %.2f = RM %.2f\n\n", hours, rate, parkingFee))
          .append(String.format("Fines Due: RM %.2f\n", finesDue))
          .append(String.format("Total Due: RM %.2f\n", totalDue))
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
