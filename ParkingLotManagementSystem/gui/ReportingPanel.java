package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import model.*;
import storage.DataManager;

public class ReportingPanel implements Actionable {

    private static final java.awt.Color PRIMARY = new java.awt.Color(33, 102, 255);
    private static final java.awt.Color CARD_BG = new java.awt.Color(191, 222, 255);

    // Must match your ticket date format (example you showed: 14/02/2026)
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private User currentUser;

    @Override
    public String getLabel() {
        return "Reporting Panel";
    }

    @Override
    public void execute(User u) {
        this.currentUser = u;
        Dashboard.setContent(getPanel());
    }

    @Override
    public JComponent getPanel() {

        boolean isAdmin = (currentUser instanceof Admin);

        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Reporting Panel");
        title.setFont(new Font("Tahoma", Font.BOLD, 30));
        title.setForeground(PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        root.add(Box.createVerticalStrut(10));
        root.add(title);
        root.add(Box.createVerticalStrut(15));

        if (!isAdmin) {
            JLabel deny = new JLabel("Reporting Panel is ADMIN only.");
            deny.setFont(new Font("Tahoma", Font.BOLD, 16));
            deny.setAlignmentX(Component.CENTER_ALIGNMENT);
            root.add(deny);
            return wrapScroll(root);
        }

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Vehicles In Lot", buildVehiclesTab());
        tabs.addTab("Revenue Report", buildRevenueTab());
        tabs.addTab("Occupancy Report", buildOccupancyTab());

        tabs.setAlignmentX(Component.CENTER_ALIGNMENT);
        tabs.setMaximumSize(new Dimension(1000, 700));

        root.add(tabs);

        return wrapScroll(root);
    }

    // =========================================================
    // TAB 1: Vehicles currently in the lot (activeTickets)
    // =========================================================
    private JComponent buildVehiclesTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setOpaque(false);

        String[] cols = {"Ticket Code", "Plate", "Brand", "Model", "Color", "Owner ID", "Spot", "Entry Date", "Entry Time"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        JButton refresh = new JButton("Refresh");
        JButton export = new JButton("Export CSV");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        top.setOpaque(false);
        top.add(refresh);
        top.add(export);

        p.add(top, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);

        Runnable load = () -> {
            model.setRowCount(0);
            ArrayList<Ticket> active = DataManager.activeTickets == null ? new ArrayList<>() : DataManager.activeTickets;

            for (Ticket t : active) {
                if (t == null || t.getVehicle() == null) continue;
                Vehicle v = t.getVehicle();

                model.addRow(new Object[] {
                        t.getTicketCode(),
                        safe(v.getLicensePlateNumber()),
                        safe(v.getBrand()),
                        safe(v.getModel()),
                        safe(String.valueOf(v.getColor())),
                        v.getVehicleOwnerID(),
                        safe(t.getSpotNumber()),
                        safe(t.getEntryDate()),
                        safe(t.getEntryTimeToString())
                });
            }
        };

        refresh.addActionListener(e -> load.run());
        export.addActionListener(e -> exportTableToCSV(model, "vehicles_in_lot.csv"));

        load.run();
        return p;
    }

    // =========================================================
    // TAB 2: Revenue report (ticketHistory)
    // =========================================================
    private JComponent buildRevenueTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setOpaque(false);

        JComboBox<String> rangeBox = new JComboBox<>(new String[]{"Overall", "Day", "Week", "Month", "Year"});
        JTextField dateField = new JTextField(10);
        dateField.setToolTipText("dd/MM/yyyy (required for Day/Week/Month/Year)");

        JButton calc = new JButton("Calculate");
        JButton export = new JButton("Export CSV");

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
        controls.setOpaque(false);
        controls.add(new JLabel("Range:"));
        controls.add(rangeBox);
        controls.add(new JLabel("Date:"));
        controls.add(dateField);
        controls.add(calc);
        controls.add(export);

        JPanel summary = new JPanel();
        summary.setBackground(CARD_BG);
        summary.setBorder(borderBlue());
        summary.setLayout(new GridLayout(1, 3, 20, 0));

        JLabel totalLbl = new JLabel("Total Revenue: 0.0");
        JLabel countLbl = new JLabel("Tickets Counted: 0");
        JLabel noteLbl  = new JLabel("Note: uses EXIT date/time");

        totalLbl.setFont(new Font("Tahoma", Font.BOLD, 14));
        countLbl.setFont(new Font("Tahoma", Font.BOLD, 14));
        noteLbl.setFont(new Font("Tahoma", Font.PLAIN, 12));

        summary.add(totalLbl);
        summary.add(countLbl);
        summary.add(noteLbl);

        String[] cols = {"Ticket Code", "Plate", "Spot", "Exit Date", "Exit Time", "Fee"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(controls);
        top.add(summary);

        p.add(top, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);

        Runnable run = () -> {
            model.setRowCount(0);

            String range = (String) rangeBox.getSelectedItem();
            LocalDate base = null;

            if (!"Overall".equals(range)) {
                base = parseDateOrNull(dateField.getText().trim());
                if (base == null) {
                    JOptionPane.showMessageDialog(null, "Invalid date. Use dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            ArrayList<Ticket> history = DataManager.ticketHistory == null ? new ArrayList<>() : DataManager.ticketHistory;

            double sum = 0.0;
            int count = 0;

            for (Ticket t : history) {
                if (t == null || t.getVehicle() == null) continue;

                String exitDateStr = safe(t.getExitDate());
                String exitTimeStr = safe(t.getExitTimeToString());
                if (exitDateStr.isEmpty()) continue;

                LocalDate exitDate;
                try {
                    exitDate = LocalDate.parse(exitDateStr, DATE_FMT);
                } catch (Exception ex) {
                    continue;
                }

                if (!matchRange(range, base, exitDate)) continue;

                double fee = t.getTotalFee();
                sum += fee;
                count++;

                model.addRow(new Object[] {
                        t.getTicketCode(),
                        safe(t.getVehicle().getLicensePlateNumber()),
                        safe(t.getSpotNumber()),
                        exitDateStr,
                        exitTimeStr,
                        fee
                });
            }

            totalLbl.setText("Total Revenue: " + sum);
            countLbl.setText("Tickets Counted: " + count);
        };

        calc.addActionListener(e -> run.run());
        export.addActionListener(e -> exportTableToCSV(model, "revenue_report.csv"));

        run.run();
        return p;
    }

    // =========================================================
    // TAB 3: Occupancy report (parkingSpots + reservations)
    // =========================================================
    private JComponent buildOccupancyTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setOpaque(false);

        JPanel summary = new JPanel();
        summary.setBackground(CARD_BG);
        summary.setBorder(borderBlue());
        summary.setLayout(new GridLayout(2, 5, 20, 6)); // <-- changed

        JLabel totalLbl = new JLabel("Total Spots: 0");
        JLabel availLbl = new JLabel("Available: 0");
        JLabel bookedLbl = new JLabel("Booked: 0");
        JLabel occLbl   = new JLabel("Occupied: 0");
        JLabel rateLbl  = new JLabel("Occupancy Rate: 0%");

        JLabel cLbl = new JLabel("Compact: 0");
        JLabel rLbl = new JLabel("Regular: 0");
        JLabel hLbl = new JLabel("Handicapped: 0");
        JLabel reservedMaxLbl = new JLabel("Reserved Spots (Max): 0");

        for (JLabel l : new JLabel[]{totalLbl, availLbl, bookedLbl, occLbl, rateLbl, cLbl, rLbl, hLbl, reservedMaxLbl}) {
            l.setFont(new Font("Tahoma", Font.BOLD, 13));
        }

        summary.add(totalLbl);
        summary.add(availLbl);
        summary.add(bookedLbl);
        summary.add(occLbl);
        summary.add(rateLbl);

        summary.add(cLbl);
        summary.add(rLbl);
        summary.add(hLbl);
        summary.add(reservedMaxLbl);
        summary.add(new JLabel("")); // filler

        String[] cols = {"Spot", "Type", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        JButton refresh = new JButton("Refresh");
        JButton export = new JButton("Export CSV");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        top.setOpaque(false);
        top.add(refresh);
        top.add(export);

        JPanel north = new JPanel();
        north.setOpaque(false);
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(top);
        north.add(summary);

        p.add(north, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);

        Runnable load = () -> {
            model.setRowCount(0);

            ArrayList<ParkingSpot> spots = DataManager.parkingSpots == null ? new ArrayList<>() : DataManager.parkingSpots;

            int total = 0, available = 0, booked = 0, occupied = 0;
            int compact = 0, regular = 0, handicapped = 0, reservedTypeCount = 0;

            for (ParkingSpot s : spots) {
                if (s == null) continue;
                total++;
            
                SpotType t = s.getType();
                if (t == SpotType.COMPACT) compact++;
                else if (t == SpotType.REGULAR) regular++;
                else if (t == SpotType.HANDICAPPED) handicapped++;
                else if (t == SpotType.RESERVED) reservedTypeCount++;
            
                Reservation r = findActiveReservationBySpot(s.getSpotNumber());
            
                String status;
                if (!s.isAvailable()) {
                    status = "OCCUPIED";
                    occupied++;
                }
                else if (r != null) {
                    status = "BOOKED";
                    booked++;
                }
                else {
                    status = "AVAILABLE";
                    available++;
                }
            
                model.addRow(new Object[]{
                        safe(s.getSpotNumber()),
                        String.valueOf(s.getType()),
                        status
                });
            }


            totalLbl.setText("Total Spots: " + total);
            availLbl.setText("Available: " + available);
            bookedLbl.setText("Booked: " + booked);
            occLbl.setText("Occupied: " + occupied);

            double rate = (total == 0) ? 0 : (occupied * 100.0 / total);
            rateLbl.setText(String.format("Occupancy Rate: %.1f%%", rate));

            cLbl.setText("Compact: " + compact);
            rLbl.setText("Regular: " + regular);
            hLbl.setText("Handicapped: " + handicapped);
            reservedMaxLbl.setText("Reserved Spots (Max): " + reservedTypeCount);
        };

        refresh.addActionListener(e -> load.run());
        export.addActionListener(e -> exportTableToCSV(model, "occupancy_report.csv"));

        load.run();
        return p;
    }

    // =========================================================
    // Reservation helper (BOOKED state)
    // =========================================================
    private Reservation findActiveReservationBySpot(String spotNumber) {
        if (DataManager.reservations == null) return null;
        for (Reservation r : DataManager.reservations) {
            if (r != null && r.isActive() && r.matchesSpot(spotNumber)) return r;
        }
        return null;
    }


    // =========================================================
    // Helpers
    // =========================================================
    private static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }

    private static JScrollPane wrapScroll(JComponent c) {
        JScrollPane scroll = new JScrollPane(c);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(15);
        scroll.getHorizontalScrollBar().setUnitIncrement(15);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return scroll;
    }

    private static javax.swing.border.Border borderBlue() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(java.awt.Color.BLUE, 2),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );
    }

    private static void exportTableToCSV(DefaultTableModel model, String defaultName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(defaultName));
        int ok = chooser.showSaveDialog(null);
        if (ok != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        try (PrintWriter pw = new PrintWriter(file)) {

            for (int c = 0; c < model.getColumnCount(); c++) {
                pw.print(csv(model.getColumnName(c)));
                if (c < model.getColumnCount() - 1) pw.print(",");
            }
            pw.println();

            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    pw.print(csv(String.valueOf(model.getValueAt(r, c))));
                    if (c < model.getColumnCount() - 1) pw.print(",");
                }
                pw.println();
            }

            JOptionPane.showMessageDialog(null,
                    "Exported:\n" + file.getAbsolutePath(),
                    "Done",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Export failed:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String csv(String s) {
        if (s == null) return "";
        String out = s.replace("\"", "\"\"");
        if (out.contains(",") || out.contains("\"") || out.contains("\n")) return "\"" + out + "\"";
        return out;
    }

    private static LocalDate parseDateOrNull(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return LocalDate.parse(s, DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static boolean matchRange(String range, LocalDate base, LocalDate exitDate) {
        if ("Overall".equals(range)) return true;
        if (base == null) return false;

        switch (range) {
            case "Day":
                return exitDate.equals(base);
            case "Week": {
                LocalDate end = base.plusDays(6);
                return !exitDate.isBefore(base) && !exitDate.isAfter(end);
            }
            case "Month":
                return exitDate.getYear() == base.getYear() && exitDate.getMonthValue() == base.getMonthValue();
            case "Year":
                return exitDate.getYear() == base.getYear();
            default:
                return false;
        }
    }
}
