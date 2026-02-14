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

        // Safety: must have logged in user
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

        // 3) Vehicle type
        VehicleType vType = askVehicleType();
        if (vType == null) return;

        // 4) Handicap card (pricing later; still collect if you want)
        boolean hasHandicapCard = false;
        if (vType == VehicleType.HANDICAPPED_VEHICLE) {
            hasHandicapCard = askHandicapCard();
        }

        // 5) Reservation check (basic)
        Reservation res = findReservationByPlate(plate);
        ParkingSpot chosenSpot = null;

        if (res != null) {
            chosenSpot = findSpotBySpotNumber(res.getReservedSpotNumber());

            if (chosenSpot == null) {
                showErr("Reservation found but spot does not exist. Contact admin.");
                return;
            }
            if (chosenSpot.getType() != SpotType.RESERVED) {
                showErr("Reservation found but assigned spot is not RESERVED. Contact admin.");
                return;
            }
            if (!chosenSpot.isAvailable()) {
                showErr("Reserved spot is currently occupied. Contact admin.");
                return;
            }
            if (hasActiveTicketForSpot(chosenSpot.getSpotNumber())) {
                showErr("Reserved spot already has an active ticket.\nContact admin.");
                return;
            }

            JOptionPane.showMessageDialog(null,
                    "Reservation found.\nAssigned RESERVED spot: " + chosenSpot.getSpotNumber(),
                    "Reservation",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        // 6) If no reservation, choose from suitable AVAILABLE spots (grid)
        if (chosenSpot == null) {
            ArrayList<ParkingSpot> suitable = getSuitableAvailableSpots_NoReserved(vType);

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

        // Final safety checks
        if (!chosenSpot.isAvailable()) {
            showErr("Selected spot is already occupied.");
            return;
        }

        // HARD BLOCK: no duplicate spot in active tickets
        if (hasActiveTicketForSpot(chosenSpot.getSpotNumber())) {
            showErr("This spot already has an active ticket.\nPlease choose another spot.");
            return;
        }

        // 7) Find or create vehicle
        Vehicle vehicle = findVehicleByPlate(plate);

        // If exists but belongs to different owner => block (optional but smart)
        if (vehicle != null && vehicle.getVehicleOwnerID() != currentUser.getID()) {
            showErr("This plate is registered under a different owner.\nCannot use this vehicle.");
            return;
        }

        if (vehicle == null) {
            vehicle = buildVehicle(plate, vType, hasHandicapCard);
            if (vehicle == null) return;
            DataManager.registeredVehicles.add(vehicle);
        }

        // 8) Occupy spot properly
        chosenSpot.occupy(vehicle);

        // 9) Create ticket
        Ticket t = new Ticket(vehicle, chosenSpot.getSpotNumber());
        DataManager.activeTickets.add(t);

        SaveData.saveAll();

        // 10) Show ticket
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

        String plate = plateField.getText().trim().toUpperCase();
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
                + "Entry Date: " + t.getEntryDate() + "\n"
                + "Entry Time: " + t.getEntryTimeToString();
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
            if (r != null && r.matches(plate)) return r;
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
    private ArrayList<ParkingSpot> getSuitableAvailableSpots_NoReserved(VehicleType vt) {
        ArrayList<ParkingSpot> result = new ArrayList<>();

        for (ParkingSpot s : DataManager.parkingSpots) {
            if (s == null) continue;

            // only show available
            if (!s.isAvailable()) continue;

            // backup safety: don’t show spots already used by active ticket
            if (hasActiveTicketForSpot(s.getSpotNumber())) continue;

            // NON-reservation users: never show RESERVED
            if (s.getType() == SpotType.RESERVED) continue;

            if (isSpotAllowed(vt, s.getType())) result.add(s);
        }

        return result;
    }

    private boolean isSpotAllowed(VehicleType vt, SpotType st) {
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
        for (Ticket t : DataManager.activeTickets) {
            if (t != null && t.getVehicle() != null &&
                    t.getVehicle().getLicensePlateNumber().equalsIgnoreCase(plate)) {
                return true;
            }
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
