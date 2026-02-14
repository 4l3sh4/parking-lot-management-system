package gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;

import model.User;
import model.ParkingSpot;
import model.SpotType;
import model.Vehicle;
import model.Car;
import model.Motorcycle;
import model.SUV_Truck;
import model.Handicapped_Vehicle;
import storage.DataManager;

public class ShowSpotsStatus implements Actionable {

    @Override
    public String getLabel() {
        return "Parking Slots";
    }

    @Override
    public void execute(User u) {
        Dashboard.setContent(getPanel());
    }

    @Override
    public JComponent getPanel() {

        int compact = 0, regular = 0, handicapped = 0, reserved = 0;
        ArrayList<ParkingSpot> occupied = new ArrayList<>();

        for (ParkingSpot s : DataManager.parkingSpots) {
            if (s.isAvailable()) {
                SpotType t = s.getType();
                if (t == SpotType.COMPACT) compact++;
                else if (t == SpotType.REGULAR) regular++;
                else if (t == SpotType.HANDICAPPED) handicapped++;
                else if (t == SpotType.RESERVED) reserved++;
            } else {
                occupied.add(s);
            }
        }

        // ===== content that scrolls =====
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // wrapper that centers everything
        JPanel centerWrap = new JPanel();
        centerWrap.setLayout(new BoxLayout(centerWrap, BoxLayout.Y_AXIS));
        centerWrap.setOpaque(false);
        centerWrap.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel title = new JLabel("Parking Slots");
        title.setFont(new Font("Tahoma", Font.BOLD, 30));
        title.setForeground(new Color(33, 102, 255));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerWrap.add(title);
        centerWrap.add(Box.createVerticalStrut(10));

        // ===== Available =====
        JLabel availableLbl = new JLabel("Available");
        availableLbl.setFont(new Font("Tahoma", Font.BOLD, 18));
        availableLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerWrap.add(availableLbl);
        centerWrap.add(Box.createVerticalStrut(8));

        JPanel summaryBar = new JPanel(new GridLayout(1, 4, 30, 0));
        summaryBar.setBackground(new Color(191, 222, 255));
        summaryBar.setBorder(makeBlueBorder());
        summaryBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // IMPORTANT: don’t cap width, let it size naturally but not drift
        summaryBar.setPreferredSize(new Dimension(400, 70));
        summaryBar.setMaximumSize(new Dimension(600, 70));

        summaryBar.add(makeStat("Compact", compact));
        summaryBar.add(makeStat("Regular", regular));
        summaryBar.add(makeStat("Handicapped", handicapped));
        summaryBar.add(makeStat("Reserved", reserved));

        centerWrap.add(summaryBar);
        centerWrap.add(Box.createVerticalStrut(15));

        // ===== Occupied =====
        JLabel occupiedLbl = new JLabel("Occupied");
        occupiedLbl.setFont(new Font("Tahoma", Font.BOLD, 18));
        occupiedLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerWrap.add(occupiedLbl);
        centerWrap.add(Box.createVerticalStrut(10));

        if (occupied.isEmpty()) {
            JLabel none = new JLabel("No occupied spots");
            none.setFont(new Font("Tahoma", Font.PLAIN, 16));
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerWrap.add(none);
        } else {
            for (int i = 0; i < occupied.size(); i += 2) {
                JPanel row = new JPanel();
                row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
                row.setOpaque(false);
                row.setAlignmentX(Component.CENTER_ALIGNMENT);

                row.add(createCard(occupied.get(i)));

                if (i + 1 < occupied.size()) {
                    row.add(Box.createHorizontalStrut(15));
                    row.add(createCard(occupied.get(i + 1)));
                } else {
                    // keep spacing like the reference (empty right slot)
                    row.add(Box.createHorizontalStrut(15));
                    JPanel ghost = new JPanel();
                    ghost.setOpaque(false);
                    ghost.setPreferredSize(new Dimension(280, 170));
                    ghost.setMaximumSize(new Dimension(280, 170));
                    row.add(ghost);
                }

                centerWrap.add(row);
                centerWrap.add(Box.createVerticalStrut(12));
            }
        }

        centerWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(Box.createHorizontalGlue());
        content.add(centerWrap);
        content.add(Box.createHorizontalGlue());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(15);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        JPanel parent = new JPanel(new BorderLayout());
        parent.setOpaque(false);
        parent.add(scroll, BorderLayout.CENTER);

        return parent;
    }

    private JPanel createCard(ParkingSpot spot) {

        JPanel card = new JPanel(new GridLayout(4, 2, 10, 10));
        card.setBackground(new Color(191, 222, 255));
        card.setBorder(makeBlueBorder());

        // Match reference sizing better
        card.setPreferredSize(new Dimension(280, 170));
        card.setMaximumSize(new Dimension(280, 170));
        card.setMinimumSize(new Dimension(280, 170));

        Vehicle v = spot.getVehicle();

        String vehicleType = "-";
        String plate = "-";

        if (v != null) {
            plate = v.getLicensePlateNumber();

            if (v instanceof Motorcycle) vehicleType = "MOTORCYCLE";
            else if (v instanceof Car) vehicleType = "CAR";
            else if (v instanceof SUV_Truck) vehicleType = "SUV/TRUCK";
            else if (v instanceof Handicapped_Vehicle) vehicleType = "HANDICAPPED";
            else vehicleType = "UNKNOWN";
        }

        card.add(new JLabel("Slot Number:"));
        card.add(new JLabel(spot.getSpotNumber()));

        card.add(new JLabel("Slot Type:"));
        card.add(new JLabel(String.valueOf(spot.getType())));

        card.add(new JLabel("Vehicle Type:"));
        card.add(new JLabel(vehicleType));

        card.add(new JLabel("<html>Vehicle Plate<br>Number:</html>"));
        card.add(new JLabel(plate));

        return card;
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
}
