package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import model.ParkingSpot;
import model.Reservation;
import model.SpotType;
import model.User;
import model.Vehicle;
import storage.DataManager;
import storage.SaveData;

public class VipReservation implements Actionable {

    private static final Color PRIMARY = new Color(33, 102, 255);
    private User currentUser;

    @Override
    public String getLabel() {
        return "VIP Reservation";
    }

    @Override
    public void execute(User u) {
        this.currentUser = u;
        Dashboard.setContent(getPanel());
    }

    @Override
    public JComponent getPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = new JLabel("VIP Reservation");
        title.setFont(new Font("Tahoma", Font.BOLD, 30));
        title.setForeground(PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(24));
        panel.add(title);
        panel.add(Box.createVerticalStrut(16));

        if (currentUser == null) {
            JLabel msg = new JLabel("User not found. Please log in again.");
            msg.setFont(new Font("Tahoma", Font.PLAIN, 14));
            msg.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(msg);
            return wrap(panel);
        }

        JLabel status = new JLabel(currentUser.isVip() ? "Status: VIP" : "Status: Not VIP");
        status.setFont(new Font("Tahoma", Font.BOLD, 16));
        status.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(status);
        panel.add(Box.createVerticalStrut(12));

        if (!currentUser.isVip()) {
            JButton register = new JButton("Register as VIP");
            register.setFont(new Font("Tahoma", Font.BOLD, 14));
            register.setAlignmentX(Component.CENTER_ALIGNMENT);
            register.addActionListener(e -> registerVip());
            panel.add(register);
            return wrap(panel);
        }

        Reservation active = findActiveReservationForUser();
        if (active != null) {
            JLabel current = new JLabel("Active reservation: " + active.getSpotNumber()
                    + " (Plate: " + active.getPlate() + ")");
            current.setFont(new Font("Tahoma", Font.PLAIN, 14));
            current.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(current);
            panel.add(Box.createVerticalStrut(10));

            JButton cancel = new JButton("Cancel Reservation");
            cancel.setFont(new Font("Tahoma", Font.BOLD, 14));
            cancel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cancel.addActionListener(e -> cancelReservation(active));
            panel.add(cancel);
            return wrap(panel);
        }

        JLabel hint = new JLabel("Reserve a RESERVED spot ahead of time.");
        hint.setFont(new Font("Tahoma", Font.PLAIN, 14));
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(hint);
        panel.add(Box.createVerticalStrut(12));

        JButton reserve = new JButton("Reserve Spot");
        reserve.setFont(new Font("Tahoma", Font.BOLD, 14));
        reserve.setAlignmentX(Component.CENTER_ALIGNMENT);
        reserve.addActionListener(e -> startReservationFlow());
        panel.add(reserve);

        return wrap(panel);
    }

    private JPanel wrap(JPanel content) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(content, BorderLayout.NORTH);
        return wrapper;
    }

    private void registerVip() {
        int ok = JOptionPane.showConfirmDialog(null,
                "Register as VIP?\nThis allows you to reserve RESERVED spots.",
                "VIP Registration",
                JOptionPane.YES_NO_OPTION);

        if (ok != JOptionPane.YES_OPTION) return;

        currentUser.setVip(true);
        SaveData.saveUsers();
        Dashboard.setContent(getPanel());
    }

    private void cancelReservation(Reservation reservation) {
        reservation.cancel();
        SaveData.saveReservations();
        Dashboard.setContent(getPanel());
    }

    private void startReservationFlow() {
        if (!currentUser.isVip()) {
            JOptionPane.showMessageDialog(null,
                    "You must register as VIP to reserve spots.",
                    "Not Allowed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<ParkingSpot> availableReserved = getAvailableReservedSpots();
        if (availableReserved.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No RESERVED spots are available for booking.",
                    "No Spots",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField plateField = new JTextField(12);
        JComboBox<String> spotBox = new JComboBox<>(spotNumbers(availableReserved));

        JPanel p = new JPanel(new GridLayout(2, 2, 10, 10));
        p.add(new JLabel("Plate:"));
        p.add(plateField);
        p.add(new JLabel("Reserved Spot:"));
        p.add(spotBox);

        int ok = JOptionPane.showConfirmDialog(null, p,
                "Reserve Spot",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (ok != JOptionPane.OK_OPTION) return;

        String plate = Reservation.normalizePlate(plateField.getText());
        String spotNumber = (String) spotBox.getSelectedItem();

        if (plate == null || plate.isEmpty() || spotNumber == null || spotNumber.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Plate and spot are required.");
            return;
        }

        if (findActiveReservationByPlate(plate) != null) {
            JOptionPane.showMessageDialog(null,
                    "This plate already has an active reservation.",
                    "Duplicate Reservation",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (hasActiveTicketForPlate(plate)) {
            JOptionPane.showMessageDialog(null,
                    "This plate already has an active ticket.",
                    "Not Allowed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        DataManager.reservations.add(new Reservation(plate, spotNumber));
        SaveData.saveReservations();
        Dashboard.setContent(getPanel());
    }

    private List<String> getOwnedPlates() {
        List<String> plates = new ArrayList<>();
        if (DataManager.registeredVehicles == null) return plates;

        for (Vehicle v : DataManager.registeredVehicles) {
            if (v == null) continue;
            if (v.getVehicleOwnerID() == currentUser.getID()) {
                plates.add(Reservation.normalizePlate(v.getLicensePlateNumber()));
            }
        }

        return plates;
    }

    private List<ParkingSpot> getAvailableReservedSpots() {
        List<ParkingSpot> spots = new ArrayList<>();
        if (DataManager.parkingSpots == null) return spots;

        for (ParkingSpot s : DataManager.parkingSpots) {
            if (s == null) continue;
            if (s.getType() != SpotType.RESERVED) continue;
            if (!s.isAvailable()) continue;
            if (findActiveReservationBySpot(s.getSpotNumber()) != null) continue;
            spots.add(s);
        }
        return spots;
    }

    private String[] spotNumbers(List<ParkingSpot> spots) {
        String[] arr = new String[spots.size()];
        for (int i = 0; i < spots.size(); i++) {
            arr[i] = spots.get(i).getSpotNumber();
        }
        return arr;
    }

    private Reservation findActiveReservationByPlate(String plate) {
        if (DataManager.reservations == null) return null;
        for (Reservation r : DataManager.reservations) {
            if (r != null && r.isActive() && r.matchesPlate(plate)) return r;
        }
        return null;
    }

    private Reservation findActiveReservationBySpot(String spotNumber) {
        if (DataManager.reservations == null) return null;
        for (Reservation r : DataManager.reservations) {
            if (r != null && r.isActive() && r.matchesSpot(spotNumber)) return r;
        }
        return null;
    }

    private Reservation findActiveReservationForUser() {
        if (DataManager.reservations == null) return null;
        List<String> plates = getOwnedPlates();
        for (Reservation r : DataManager.reservations) {
            if (r == null || !r.isActive()) continue;
            for (String plate : plates) {
                if (r.matchesPlate(plate)) return r;
            }
        }
        return null;
    }

    private boolean hasActiveTicketForPlate(String plate) {
        if (DataManager.activeTickets == null) return false;
        String target = Reservation.normalizePlate(plate);
        for (model.Ticket t : DataManager.activeTickets) {
            if (t == null || t.getVehicle() == null) continue;
            String p = t.getVehicle().getLicensePlateNumber();
            if (Reservation.normalizePlate(p).equals(target)) return true;
        }
        return false;
    }
}
