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
        
        JLabel hint = new JLabel("Click Process Exit to begin.");
        hint.setFont(new Font("Tahoma", Font.PLAIN, 14));
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

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

        // 3) Find the parking spot by STRING spotNumber (F1-R1-S1)
        String spotNumber = ticket.getspotNumber(); // keep your getter name
        ParkingSpot spot = findSpotBySpotNumber(spotNumber);

        if (spot == null) {
            JOptionPane.showMessageDialog(null,
                    "Associated parking spot not found.\nSpot: " + spotNumber,
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4) Calculate fee (Ticket must enforce minimum 1 hour inside exitVehicle)
        ticket.exitVehicle(spot.getHourlyRate());

        // 5) Free the spot
        spot.free();

        // 6) Move ticket to history
        DataManager.activeTickets.remove(ticket);
        DataManager.ticketHistory.add(ticket);

        // 7) Save
        SaveData.saveAll();

        // 8) Show receipt
        JOptionPane.showMessageDialog(null,
                makeReceipt(ticket, spot),
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

    // Don’t depend on ParkingLotManager 
    private Ticket findActiveTicketByPlate(String plate) {
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
        for (ParkingSpot s : DataManager.parkingSpots) {
            if (s != null && s.getSpotNumber() != null &&
                s.getSpotNumber().equalsIgnoreCase(spotNumber)) {
                return s;
            }
        }
        return null;
    }

    private String makeReceipt(Ticket t, ParkingSpot spot) {
    
        String exitDate = (t.getExitDate() == null || t.getExitDate().trim().isEmpty())
                ? "-"
                : t.getExitDate();
    
        String exitTime = (t.getExitTimeToString() == null || t.getExitTimeToString().trim().isEmpty())
                ? "-"
                : t.getExitTimeToString();
    
        return "Ticket Code: " + t.getTicketCode() + "\n\n"
                + "Plate: " + t.getVehicle().getLicensePlateNumber() + "\n"
                + "Spot: " + spot.getSpotNumber() + "\n"
                + "Spot Type: " + spot.getType() + "\n\n"
                + "Entry: " + t.getEntryDate() + " " + t.getEntryTimeToString() + "\n"
                + "Exit:  " + exitDate + " " + exitTime + "\n\n"
                + "Total Fee: $" + t.getTotalFee();
    }
}
