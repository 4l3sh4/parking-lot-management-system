package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import model.*;
import model.Color;
import storage.DataManager;
import storage.SaveData;
import model.Reservation;

public class VehicleEntry implements Actionable {

    private static final java.awt.Color PRIMARY = new java.awt.Color(33, 102, 255);
    private User currentUser;

    @Override
    public String getLabel() {
        return "Vehicle Entry";
    }

    @Override
    public void execute(User u) {
        this.currentUser = u;
        Dashboard.setContent(getPanel());
        // IMPORTANT: do NOT auto popup here
    }

    private JPanel panel;

    @Override
    public JPanel getPanel() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = new JLabel("Vehicle Entry");
        title.setFont(new Font("Tahoma", Font.BOLD, 30));
        title.setForeground(PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hint = new JLabel("Click Start Entry to begin.");
        hint.setFont(new Font("Tahoma", Font.PLAIN, 14));
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton start = new JButton("Start Entry");
        start.setFont(new Font("Tahoma", Font.BOLD, 14));
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.addActionListener(e -> startEntryFlow());

        panel.add(Box.createVerticalStrut(30));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(hint);
        panel.add(Box.createVerticalStrut(20));
        panel.add(start);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(panel, BorderLayout.NORTH);
        return wrapper;
    }

    // =========================
    // ENTRY FLOW
    // =========================
    private void startEntryFlow() {
        // 0) Must be logged in
        if (currentUser == null) {
            showErr("System error: user not found (not logged in).");
            return;
        }
    
        // 1) Plate
        String plate = askPlate();
        if (plate == null) return;
    
        // 2) Block duplicate active ticket for same plate
        if (hasActiveTicketForPlate(plate)) {
            showErr("This plate already has an active ticket.\nVehicle cannot enter twice.");
            return;
        }
    
        // 3) Reservation check FIRST (if exists, we auto assign reserved spot)
        Reservation res = findReservationByPlate(plate);
        ParkingSpot chosenSpot = null;
    
        if (res != null && res.isActive()) {
    
            chosenSpot = findSpotBySpotNumber(res.getSpotNumber());
    
            if (chosenSpot == null) {
                showErr("Reservation found but spot does not exist. Contact admin.");
                return;
            }
    
            // Reserved must be RESERVED type
            if (chosenSpot.getType() != SpotType.RESERVED) {
                showErr("Reservation found but assigned spot is not RESERVED. Contact admin.");
                return;
            }
    
            // Reserved spot must be available
            if (!chosenSpot.isAvailable()) {
                showErr("Reserved spot is currently occupied. Contact admin.");
                return;
            }
    
            // Must not already be used by another active ticket
            if (hasActiveTicketForSpot(chosenSpot.getSpotNumber())) {
                showErr("Reserved spot already has an active ticket.\nContact admin.");
                return;
            }
    
            JOptionPane.showMessageDialog(null,
                    "Reservation found.\nAssigned RESERVED spot: " + chosenSpot.getSpotNumber(),
                    "Reservation",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    
        // 4) Only ask vehicle type if NO reservation
        VehicleType vType = null;
        boolean hasHandicapCard = false;
    
        if (chosenSpot == null) {
            vType = askVehicleType();
            if (vType == null) return;
    
            if (vType == VehicleType.HANDICAPPED_VEHICLE) {
                hasHandicapCard = askHandicapCard();
            }
    
            // Choose from suitable AVAILABLE spots (including RESERVED)
            ArrayList<ParkingSpot> suitable = getSuitableAvailableSpots(vType);
    
            if (suitable.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "No suitable available spots for this vehicle type.",
                        "No Spots",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
    
            chosenSpot = chooseSpotGridDialog(suitable, "Select Parking Spot");
            if (chosenSpot == null) return;
        }
    
        // 5) Final safety checks
        if (chosenSpot == null) {
            showErr("System error: no spot selected.");
            return;
        }
    
        if (!chosenSpot.isAvailable()) {
            showErr("Selected spot is already occupied.");
            return;
        }
    
        if (hasActiveTicketForSpot(chosenSpot.getSpotNumber())) {
            showErr("This spot already has an active ticket.\nPlease choose another spot.");
            return;
        }
    
        // 6) Find or create vehicle
        Vehicle vehicle = findVehicleByPlate(plate);
    
        // If vehicle exists but belongs to different owner => block
        if (vehicle != null && vehicle.getVehicleOwnerID() != currentUser.getID()) {
            showErr("This plate is registered under a different owner.\nCannot use this vehicle.");
            return;
        }
    
        // If no vehicle exists, we MUST ask details
        if (vehicle == null) {
    
            // If reservation path: you still need vehicle type for object creation.
            // So ask it here only if it wasn't asked earlier.
            if (vType == null) {
                vType = askVehicleType();
                if (vType == null) return;
    
                if (vType == VehicleType.HANDICAPPED_VEHICLE) {
                    hasHandicapCard = askHandicapCard();
                }
            }
    
            vehicle = buildVehicle(plate, vType, hasHandicapCard);
            if (vehicle == null) return;
    
            DataManager.registeredVehicles.add(vehicle);
        }
    
        // 7) HARD FINAL CHECK AGAIN (plate)
        if (hasActiveTicketForPlate(vehicle.getLicensePlateNumber())) {
            showErr("This plate already has an active ticket.\n(Detected at final commit)");
            return;
        }
    
        // 8) If this spot had someone else's reservation, cancel it (spot is now taken)
        Reservation spotReservation = findReservationBySpot(chosenSpot.getSpotNumber());
        if (spotReservation != null && !spotReservation.matchesPlate(plate)) {
            spotReservation.cancel();
            JOptionPane.showMessageDialog(null,
                "This spot had an active reservation for another plate.\n"
                + "Reservation has been cancelled.",
                "Reservation Cancelled",
                JOptionPane.INFORMATION_MESSAGE);
        }

        // 9) Occupy spot + create ticket
        chosenSpot.occupy(vehicle);
    
        Ticket t = new Ticket(vehicle, chosenSpot.getSpotNumber());
        DataManager.activeTickets.add(t);
    
        // Reservation remains active until exit so it expires after the visit.
    
        SaveData.saveAll();
    
        // 9) Show ticket
        JOptionPane.showMessageDialog(null,
                makeTicketMessage(t, chosenSpot),
                "Ticket Generated",
                JOptionPane.INFORMATION_MESSAGE);
    }


    // =========================
    // UI helpers
    // =========================
    private String askPlate() {
        JTextField plateField = new JTextField(15);

        JPanel p = new JPanel(new GridLayout(1, 2, 10, 10));
        p.add(new JLabel("Plate Number:"));
        p.add(plateField);

        int ok = JOptionPane.showConfirmDialog(null, p, "Vehicle Entry - Plate",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (ok != JOptionPane.OK_OPTION) return null;

        String plate = plateField.getText().trim().toUpperCase().replaceAll("\\s+", "");
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Plate cannot be empty.");
            return null;
        }
        return plate;
    }

    private VehicleType askVehicleType() {
        JComboBox<VehicleType> box = new JComboBox<>(VehicleType.values());

        JPanel p = new JPanel(new GridLayout(1, 2, 10, 10));
        p.add(new JLabel("Vehicle Type:"));
        p.add(box);

        int ok = JOptionPane.showConfirmDialog(null, p, "Vehicle Entry - Type",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (ok != JOptionPane.OK_OPTION) return null;

        return (VehicleType) box.getSelectedItem();
    }

    private boolean askHandicapCard() {
        int res = JOptionPane.showConfirmDialog(null,
                "Does the driver have a valid handicap card?\n(Only affects pricing later)",
                "Handicap Card",
                JOptionPane.YES_NO_OPTION);
        return res == JOptionPane.YES_OPTION;
    }

    private void showErr(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String makeTicketMessage(Ticket t, ParkingSpot spot) {
        return "Ticket Code: " + t.getTicketCode() + "\n\n"
                + "Plate: " + t.getVehicle().getLicensePlateNumber() + "\n"
                + "Spot: " + spot.getSpotNumber() + "\n"
                + "Spot Type: " + spot.getType() + "\n"
                + "Entry: " + t.getEntryDate() + " " + t.getEntryTimeToString() + "\n";
    }

    // =========================
    // Vehicle creation (NO OWNER ID ASK)
    // =========================
    private Vehicle buildVehicle(String plate, VehicleType type, boolean hasCard) {

        JTextField brand = new JTextField(12);
        JTextField model = new JTextField(12);
        JComboBox<Color> colorBox = new JComboBox<>(Color.values());

        JPanel p = new JPanel(new GridLayout(3, 2, 10, 10));
        p.add(new JLabel("Brand:"));
        p.add(brand);
        p.add(new JLabel("Model:"));
        p.add(model);
        p.add(new JLabel("Color:"));
        p.add(colorBox);

        int ok = JOptionPane.showConfirmDialog(null, p, "Vehicle Entry - Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (ok != JOptionPane.OK_OPTION) return null;

        Vehicle v;
        if (type == VehicleType.CAR) v = new Car(plate);
        else if (type == VehicleType.MOTORCYCLE) v = new Motorcycle(plate);
        else if (type == VehicleType.SUV_TRUCK) v = new SUV_Truck(plate);
        else v = new Handicapped_Vehicle(plate, hasCard);

        v.setBrand(brand.getText().trim());
        v.setModel(model.getText().trim());
        v.setColor((model.Color) colorBox.getSelectedItem());

        // Owner comes from login
        v.setVehicleOwnerID(currentUser.getID());

        return v;
    }

    // =========================
    // Reservation + Spot lookup
    // =========================
    private Reservation findReservationByPlate(String plate) {
        if (DataManager.reservations == null) return null;
    
        for (Reservation r : DataManager.reservations) {
            if (r != null && r.matchesPlate(plate)) {
                return r;
            }
        }
        return null;
    }

    private Reservation findReservationBySpot(String spotNumber) {
        if (DataManager.reservations == null) return null;

        for (Reservation r : DataManager.reservations) {
            if (r != null && r.matchesSpot(spotNumber)) {
                return r;
            }
        }
        return null;
    }

    private ParkingSpot findSpotBySpotNumber(String spotNumber) {
        for (ParkingSpot s : DataManager.parkingSpots) {
            if (s != null && s.getSpotNumber().equalsIgnoreCase(spotNumber)) return s;
        }
        return null;
    }

    // =========================
    // Filtering suitable spots
    // =========================
    private ArrayList<ParkingSpot> getSuitableAvailableSpots(VehicleType vt) {
        ArrayList<ParkingSpot> result = new ArrayList<>();

        for (ParkingSpot s : DataManager.parkingSpots) {
            if (s == null) continue;

            // only show available
            if (!s.isAvailable()) continue;

            // backup safety: don’t show spots already used by active ticket
            if (hasActiveTicketForSpot(s.getSpotNumber())) continue;

            if (isSpotAllowed(vt, s.getType())) result.add(s);
        }

        return result;
    }

    private boolean isSpotAllowed(VehicleType vt, SpotType st) {
        if (st == SpotType.RESERVED) return true;
        switch (vt) {
            case MOTORCYCLE:
                return st == SpotType.COMPACT;
            case CAR:
                return st == SpotType.COMPACT || st == SpotType.REGULAR;
            case SUV_TRUCK:
                return st == SpotType.REGULAR;
            case HANDICAPPED_VEHICLE:
                return true;
            default:
                return false;
        }
    }

    // =========================
    // Duplicate protections
    // =========================
    private boolean hasActiveTicketForPlate(String plate) {
        if (plate == null) return false;
        String target = plate.trim().toUpperCase().replaceAll("\\s+", "");
    
        for (Ticket t : DataManager.activeTickets) {
            if (t == null) continue;
    
            // Best: compare using ticket's plate if you have it
            String p = null;
    
            // If your Ticket doesn't store plate directly, pull from vehicle
            if (t.getVehicle() != null && t.getVehicle().getLicensePlateNumber() != null) {
                p = t.getVehicle().getLicensePlateNumber();
            }
    
            if (p == null) continue;
    
            String norm = p.trim().toUpperCase().replaceAll("\\s+", "");
            if (norm.equals(target)) return true;
        }
        return false;
    }


    private boolean hasActiveTicketForSpot(String spotNumber) {
        for (Ticket t : DataManager.activeTickets) {
            if (t != null && t.getSpotNumber() != null &&
                    t.getSpotNumber().equalsIgnoreCase(spotNumber)) {
                return true;
            }
        }
        return false;
    }

    private Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : DataManager.registeredVehicles) {
            if (v != null && v.getLicensePlateNumber() != null &&
                    v.getLicensePlateNumber().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }

    // =========================
    // GRID SELECTOR
    // =========================
    private ParkingSpot chooseSpotGridDialog(ArrayList<ParkingSpot> suitable, String title) {

        Map<String, ArrayList<ParkingSpot>> byFloor = groupByFloor(suitable);
        final ParkingSpot[] selected = new ParkingSpot[1];

        JDialog dialog = new JDialog((JFrame) null, title, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Click an AVAILABLE spot to select.");
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        dialog.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        for (String floorKey : byFloor.keySet()) {
            tabs.addTab(floorKey, makeSpotGridPanel(byFloor.get(floorKey), selected, dialog));
        }

        dialog.add(tabs, BorderLayout.CENTER);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> {
            selected[0] = null;
            dialog.dispose();
        });

        JPanel south = new JPanel(new BorderLayout());
        south.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        south.add(cancel, BorderLayout.EAST);
        dialog.add(south, BorderLayout.SOUTH);

        dialog.setSize(560, 420);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        return selected[0];
    }

    private JPanel makeSpotGridPanel(ArrayList<ParkingSpot> spots, ParkingSpot[] selected, JDialog dialog) {
        int cols = 5;
        int rows = (int) Math.ceil(spots.size() / (double) cols);

        JPanel grid = new JPanel(new GridLayout(Math.max(rows, 1), cols, 10, 10));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (ParkingSpot s : spots) {
            JButton b = new JButton("<html><b>" + s.getSpotNumber() + "</b><br/>" + s.getType() + "</html>");
            b.setPreferredSize(new Dimension(110, 60));
            b.setEnabled(s.isAvailable());

            b.addActionListener(e -> {
                selected[0] = s;
                dialog.dispose();
            });

            grid.add(b);
        }

        int filled = spots.size();
        int total = Math.max(rows, 1) * cols;
        for (int i = filled; i < total; i++) grid.add(new JLabel(""));

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private Map<String, ArrayList<ParkingSpot>> groupByFloor(ArrayList<ParkingSpot> spots) {
        Map<String, ArrayList<ParkingSpot>> map = new LinkedHashMap<>();
        for (ParkingSpot s : spots) {
            String floor = parseFloorKey(s.getSpotNumber());
            map.computeIfAbsent(floor, k -> new ArrayList<>()).add(s);
        }
        return map;
    }

    private String parseFloorKey(String spotNumber) {
        try {
            String id = spotNumber.toUpperCase();
            if (!id.startsWith("F")) return "All Spots";
            int dash = id.indexOf('-');
            if (dash == -1) return "All Spots";
            String floorNum = id.substring(1, dash);
            return "Floor " + floorNum;
        } catch (Exception e) {
            return "All Spots";
        }
    }
}
