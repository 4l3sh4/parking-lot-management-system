package gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.*;
import java.util.List;

import model.User;
import model.Admin;
import model.ParkingSpot;
import model.SpotType;
import model.Vehicle;
import model.Reservation;
import model.Ticket;
import storage.SaveData;
import storage.DataManager;

public class ShowSpotsStatus implements Actionable {

    private User currentUser;

    @Override
    public String getLabel() {
        return "Parking Slots";
    }

    @Override
    public void execute(User u) {
        this.currentUser = u;
        Dashboard.setContent(getPanel());
    }

    @Override
    public JComponent getPanel() {

        // ===== counts =====
        int aCompact = 0, aRegular = 0, aHandicapped = 0, aReserved = 0;
        int oCompact = 0, oRegular = 0, oHandicapped = 0, oReserved = 0;

        ArrayList<ParkingSpot> spots = DataManager.parkingSpots == null
                ? new ArrayList<>()
                : DataManager.parkingSpots;

        for (ParkingSpot s : spots) {
            if (s == null) continue;

            boolean available = s.isAvailable();
            SpotType t = s.getType();

            if (available) {
                if (t == SpotType.COMPACT) aCompact++;
                else if (t == SpotType.REGULAR) aRegular++;
                else if (t == SpotType.HANDICAPPED) aHandicapped++;
                else if (t == SpotType.RESERVED) aReserved++;
            } else {
                if (t == SpotType.COMPACT) oCompact++;
                else if (t == SpotType.REGULAR) oRegular++;
                else if (t == SpotType.HANDICAPPED) oHandicapped++;
                else if (t == SpotType.RESERVED) oReserved++;
            }
        }

        boolean isAdmin = (currentUser instanceof Admin);

        // ===== content that scrolls =====
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JPanel centerWrap = new JPanel();
        centerWrap.setLayout(new BoxLayout(centerWrap, BoxLayout.Y_AXIS));
        centerWrap.setOpaque(false);
        centerWrap.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== Title =====
        JLabel title = new JLabel("Parking Slots");
        title.setFont(new Font("Tahoma", Font.BOLD, 30));
        title.setForeground(new Color(33, 102, 255));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerWrap.add(Box.createVerticalStrut(8));
        centerWrap.add(title);
        centerWrap.add(Box.createVerticalStrut(14));

        // ===== Available counts =====
        JLabel availableLbl = new JLabel("Available");
        availableLbl.setFont(new Font("Tahoma", Font.BOLD, 18));
        availableLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerWrap.add(availableLbl);
        centerWrap.add(Box.createVerticalStrut(8));

        JPanel availableBar = makeSummaryBar(aCompact, aRegular, aHandicapped, aReserved);
        centerWrap.add(availableBar);
        centerWrap.add(Box.createVerticalStrut(18));

        // ===== Occupied counts =====
        JLabel occupiedLbl = new JLabel("Occupied");
        occupiedLbl.setFont(new Font("Tahoma", Font.BOLD, 18));
        occupiedLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerWrap.add(occupiedLbl);
        centerWrap.add(Box.createVerticalStrut(8));

        JPanel occupiedBar = makeSummaryBar(oCompact, oRegular, oHandicapped, oReserved);
        centerWrap.add(occupiedBar);
        centerWrap.add(Box.createVerticalStrut(22));

        // ===== Map =====
        JLabel mapLbl = new JLabel("Map");
        mapLbl.setFont(new Font("Tahoma", Font.BOLD, 18));
        mapLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerWrap.add(mapLbl);
        centerWrap.add(Box.createVerticalStrut(10));

        // Legend
        centerWrap.add(makeLegend());
        centerWrap.add(Box.createVerticalStrut(10));

        // Tabs by floor
        JTabbedPane tabs = new JTabbedPane();
        Map<String, List<ParkingSpot>> byFloor = groupByFloor(spots);

        // Keep stable order: Floor 1, Floor 2, ...
        ArrayList<String> floorKeys = new ArrayList<>(byFloor.keySet());
        floorKeys.sort(Comparator.comparingInt(this::floorNumberSafe));

        for (String floorKey : floorKeys) {
            JPanel floorPanel = makeFloorMapPanel(byFloor.get(floorKey), isAdmin);
            tabs.addTab(floorKey, floorPanel);
        }

        tabs.setMaximumSize(new Dimension(900, 520));
        tabs.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerWrap.add(tabs);
        centerWrap.add(Box.createVerticalStrut(20));

        content.add(centerWrap);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(15);
        scroll.getHorizontalScrollBar().setUnitIncrement(15);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        JPanel parent = new JPanel(new BorderLayout());
        parent.setOpaque(false);
        parent.add(scroll, BorderLayout.CENTER);

        return parent;
    }

    // ===== summary bar helper =====
    private JPanel makeSummaryBar(int compact, int regular, int handicapped, int reserved) {
        JPanel bar = new JPanel(new GridLayout(1, 4, 30, 0));
        bar.setBackground(new Color(191, 222, 255));
        bar.setBorder(makeBlueBorder());
        bar.setAlignmentX(Component.CENTER_ALIGNMENT);

        bar.setPreferredSize(new Dimension(520, 70));
        bar.setMaximumSize(new Dimension(760, 70));

        bar.add(makeStat("Compact", compact));
        bar.add(makeStat("Regular", regular));
        bar.add(makeStat("Handicapped", handicapped));
        bar.add(makeStat("Reserved", reserved));
        return bar;
    }

    // ===== legend =====
    private JPanel makeLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        legend.setOpaque(false);

        legend.add(legendChip("Available", new Color(220, 255, 220)));
        legend.add(legendChip("Occupied", new Color(255, 220, 220)));
        legend.add(legendChip("Reserved", new Color(255, 245, 200)));

        return legend;
    }

    private JPanel legendChip(String text, Color bg) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.BOLD, 12));

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLUE, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        p.add(l, BorderLayout.CENTER);
        return p;
    }

    // ===== map panel per floor =====
    private JPanel makeFloorMapPanel(List<ParkingSpot> floorSpots, boolean isAdmin) {

        // Sort by row then slot (assumes format F?-R?-S?)
        ArrayList<ParkingSpot> sorted = new ArrayList<>(floorSpots);
        sorted.sort((a, b) -> {
            SpotKey ka = parseKey(a.getSpotNumber());
            SpotKey kb = parseKey(b.getSpotNumber());
            if (ka.row != kb.row) return Integer.compare(ka.row, kb.row);
            return Integer.compare(ka.slot, kb.slot);
        });

        // Group by row
        Map<Integer, List<ParkingSpot>> byRow = new LinkedHashMap<>();
        for (ParkingSpot s : sorted) {
            SpotKey k = parseKey(s.getSpotNumber());
            byRow.computeIfAbsent(k.row, x -> new ArrayList<>()).add(s);
        }

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        for (Integer rowNum : byRow.keySet()) {
            JLabel rowLbl = new JLabel("Row " + rowNum);
            rowLbl.setFont(new Font("Tahoma", Font.BOLD, 14));
            rowLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            wrapper.add(rowLbl);
            wrapper.add(Box.createVerticalStrut(6));

            List<ParkingSpot> rowSpots = byRow.get(rowNum);

            // fixed 10 slots per row (your requirement), but don’t crash if not exactly 10
            int cols = 10;
            int rows = (int) Math.ceil(rowSpots.size() / (double) cols);
            rows = Math.max(rows, 1);

            JPanel grid = new JPanel(new GridLayout(rows, cols, 8, 8));
            grid.setOpaque(false);
            grid.setBorder(BorderFactory.createEmptyBorder(6, 10, 12, 10));

            for (ParkingSpot s : rowSpots) {
                grid.add(makeSpotCell(s, isAdmin));
            }

            // fill blanks
            int totalCells = rows * cols;
            for (int i = rowSpots.size(); i < totalCells; i++) {
                JPanel blank = new JPanel();
                blank.setOpaque(false);
                grid.add(blank);
            }

            wrapper.add(grid);
        }

        JPanel out = new JPanel(new BorderLayout());
        out.setOpaque(false);
        out.add(wrapper, BorderLayout.CENTER);
        return out;

    }

    // ===== one spot cell =====
    private JComponent makeSpotCell(ParkingSpot spot, boolean isAdmin) {
    
        String spotNum = spot.getSpotNumber();
        SpotType type = spot.getType();
        boolean available = spot.isAvailable();
    
        Reservation res = findActiveReservationBySpot(spotNum);
        boolean reservedByReservation = (res != null);
    
        JButton b = new JButton();
        b.setLayout(new BorderLayout());
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
    
        // Background overlay
        Color bg;
        if (reservedByReservation) bg = new Color(255, 245, 200);     // reserved overlay
        else if (available) bg = new Color(220, 255, 220);
        else bg = new Color(255, 220, 220);
        b.setBackground(bg);
    
        JLabel top = new JLabel(spotNum, SwingConstants.CENTER);
        top.setFont(new Font("Tahoma", Font.BOLD, 12));
    
        String statusText = available ? "AVAILABLE" : "OCCUPIED";
        if (reservedByReservation) statusText = "BOOKED";
        JLabel mid = new JLabel(statusText, SwingConstants.CENTER);
        mid.setFont(new Font("Tahoma", Font.PLAIN, 11));
    
        JLabel bot = new JLabel(String.valueOf(type), SwingConstants.CENTER);
        bot.setFont(new Font("Tahoma", Font.PLAIN, 10));
    
        JPanel text = new JPanel(new GridLayout(3, 1));
        text.setOpaque(false);
        text.add(top);
        text.add(mid);
        text.add(bot);
    
        b.add(text, BorderLayout.CENTER);
    
        // Tooltip
        if (isAdmin) {
            String plate = (res == null) ? "-" : res.getPlate();
            b.setToolTipText("Spot: " + spotNum + " | Type: " + type + " | Booked Plate: " + plate);
        } else {
            b.setToolTipText("Spot: " + spotNum + " | Type: " + type);
        }
    
        b.setPreferredSize(new Dimension(95, 70));
    
        // Click actions (ADMIN only)
        b.setEnabled(isAdmin);
    
        if (isAdmin) {
            b.addActionListener(e -> {
                Reservation current = findActiveReservationBySpot(spotNum);
    
                // If occupied, admin should not reserve it
                if (!spot.isAvailable()) {
                    JOptionPane.showMessageDialog(null,
                            "This spot is OCCUPIED.\nCannot reserve occupied spots.",
                            "Not Allowed",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // ONLY allow reservation on RESERVED type spots
                if (spot.getType() != SpotType.RESERVED) {
                    JOptionPane.showMessageDialog(null,
                            "Only RESERVED type spots can be reserved.\n"
                            + "This spot is: " + spot.getType(),
                            "Not Allowed",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

    
                if (current != null) {
                    int ok = JOptionPane.showConfirmDialog(null,
                            "Cancel reservation?\nSpot: " + spotNum + "\nPlate: " + current.getPlate(),
                            "Cancel Reservation",
                            JOptionPane.YES_NO_OPTION);
    
                    if (ok == JOptionPane.YES_OPTION) {
                        current.cancel();
                        SaveData.saveAll();
                        Dashboard.setContent(getPanel()); // refresh
                    }
                    return;
                }
    
                // Add new reservation
                String plate = JOptionPane.showInputDialog(null,
                        "Enter plate number to reserve spot:\n" + spotNum,
                        "Add Reservation",
                        JOptionPane.PLAIN_MESSAGE);
    
                if (plate == null) return;
                plate = Reservation.normalizePlate(plate);
                if (plate.isEmpty()) return;
    
                // block duplicate reservation by plate
                if (findActiveReservationByPlate(plate) != null) {
                    JOptionPane.showMessageDialog(null,
                            "This plate already has an ACTIVE reservation.",
                            "Duplicate Reservation",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                // optional: block if plate already has active ticket
                if (hasActiveTicketForPlate(plate)) {
                    JOptionPane.showMessageDialog(null,
                            "This plate already has an ACTIVE ticket.\nExit first before reserving.",
                            "Not Allowed",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                if (DataManager.reservations == null) DataManager.reservations = new ArrayList<>();
                DataManager.reservations.add(new Reservation(plate, spotNum));
    
                SaveData.saveAll();
                Dashboard.setContent(getPanel()); // refresh
            });
        }
    
        return b;
    }


    // ===== grouping by floor =====
    private Map<String, List<ParkingSpot>> groupByFloor(List<ParkingSpot> spots) {
        Map<String, List<ParkingSpot>> map = new LinkedHashMap<>();
        for (ParkingSpot s : spots) {
            if (s == null) continue;
            String key = floorKey(s.getSpotNumber());
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        return map;
    }

    private String floorKey(String spotNumber) {
        SpotKey k = parseKey(spotNumber);
        if (k.floor <= 0) return "Floor ?";
        return "Floor " + k.floor;
    }

    private int floorNumberSafe(String floorKey) {
        try {
            // "Floor 3"
            String[] parts = floorKey.trim().split("\\s+");
            return Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return 999;
        }
    }

    // ===== parse helper: F1-R2-S10 =====
    private static class SpotKey {
        int floor;
        int row;
        int slot;
        SpotKey(int f, int r, int s) { floor = f; row = r; slot = s; }
    }

    private SpotKey parseKey(String spotNumber) {
        // Expected: F1-R2-S10
        // If weird, default to 0s so it still renders.
        int f = 0, r = 0, s = 0;
        try {
            if (spotNumber == null) return new SpotKey(0, 0, 0);
            String up = spotNumber.toUpperCase();

            // Floor
            int fi = up.indexOf('F');
            int dash1 = up.indexOf('-', fi);
            if (fi >= 0 && dash1 > fi) f = Integer.parseInt(up.substring(fi + 1, dash1));

            // Row
            int ri = up.indexOf("R", dash1);
            int dash2 = up.indexOf('-', ri);
            if (ri >= 0 && dash2 > ri) r = Integer.parseInt(up.substring(ri + 1, dash2));

            // Slot
            int si = up.indexOf("S", dash2);
            if (si >= 0) s = Integer.parseInt(up.substring(si + 1));

        } catch (Exception ignored) {}
        return new SpotKey(f, r, s);
    }

    private JPanel makeStat(String label, int value) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel l1 = new JLabel(label + ":");
        l1.setFont(new Font("Tahoma", Font.BOLD, 14));
        l1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel l2 = new JLabel(String.valueOf(value));
        l2.setFont(new Font("Tahoma", Font.PLAIN, 18));
        l2.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(Box.createVerticalGlue());
        p.add(l1);
        p.add(Box.createVerticalStrut(4));
        p.add(l2);
        p.add(Box.createVerticalGlue());
        return p;
    }

    private Border makeBlueBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );
    }
    
    private Reservation findActiveReservationBySpot(String spotNumber) {
        if (DataManager.reservations == null) return null;
        for (Reservation r : DataManager.reservations) {
            if (r != null && r.isActive() && r.matchesSpot(spotNumber)) return r;
        }
        return null;
    }
    
    private Reservation findActiveReservationByPlate(String plate) {
        if (DataManager.reservations == null) return null;
        for (Reservation r : DataManager.reservations) {
            if (r != null && r.isActive() && r.matchesPlate(plate)) return r;
        }
        return null;
    }
    
    private boolean hasActiveTicketForPlate(String plate) {
        if (DataManager.activeTickets == null) return false;
        String target = Reservation.normalizePlate(plate);
        for (Ticket t : DataManager.activeTickets) {
            if (t == null || t.getVehicle() == null) continue;
            String p = t.getVehicle().getLicensePlateNumber();
            if (Reservation.normalizePlate(p).equals(target)) return true;
        }
        return false;
    }

    
}