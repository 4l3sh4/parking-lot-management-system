package gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;

import model.User;
import model.Vehicle;
import model.Car;
import model.Motorcycle;
import model.SUV_Truck;
import model.Handicapped_Vehicle;
import storage.DataManager;

public class ShowRegisteredVehicles implements Actionable {

    @Override
    public String getLabel() {
        return "Registered Vehicles";
    }

    @Override
    public void execute(User u) {
        System.out.println("CLICKED REGISTERED VEHICLES");
        Dashboard.setContent(getPanel());
    }

    @Override
    public JComponent getPanel() {

        ArrayList<Vehicle> vehicles = DataManager.registeredVehicles;

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);

        // ===== Title =====
        JLabel title = new JLabel("Registered Vehicles");
        title.setFont(new Font("Tahoma", Font.BOLD, 28));
        title.setForeground(new Color(33, 102, 255));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        wrapper.add(title);
        wrapper.add(Box.createVerticalStrut(15));

        // ===== Cards =====
        if (vehicles == null || vehicles.isEmpty()) {

            JLabel empty = new JLabel("No registered vehicles found.");
            empty.setFont(new Font("Tahoma", Font.PLAIN, 16));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            wrapper.add(empty);

        } else {

            for (int i = 0; i < vehicles.size(); i += 2) {

                JPanel row = new JPanel();
                row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
                row.setOpaque(false);
                row.setAlignmentX(Component.CENTER_ALIGNMENT);

                row.add(createCard(vehicles.get(i)));

                if (i + 1 < vehicles.size()) {
                    row.add(Box.createHorizontalStrut(15));
                    row.add(createCard(vehicles.get(i + 1)));
                } else {
                    row.add(Box.createHorizontalStrut(15));
                    JPanel ghost = new JPanel();
                    ghost.setOpaque(false);
                    ghost.setPreferredSize(new Dimension(380, 180));
                    ghost.setMaximumSize(new Dimension(380, 180));
                    row.add(ghost);
                }

                wrapper.add(row);
                wrapper.add(Box.createVerticalStrut(12));
            }
        }

        content.add(wrapper, BorderLayout.NORTH);

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

    private JPanel createCard(Vehicle v) {

        JPanel card = new JPanel(new GridLayout(5, 2, 10, 10));
        card.setBackground(new Color(191, 222, 255));
        card.setBorder(makeBlueBorder());

        card.setPreferredSize(new Dimension(380, 180));
        card.setMaximumSize(new Dimension(380, 180));
        card.setMinimumSize(new Dimension(380, 180));

        String vehicleType = "UNKNOWN";

        if (v instanceof Motorcycle) vehicleType = "MOTORCYCLE";
        else if (v instanceof Car) vehicleType = "CAR";
        else if (v instanceof SUV_Truck) vehicleType = "SUV/TRUCK";
        else if (v instanceof Handicapped_Vehicle) vehicleType = "HANDICAPPED";

        card.add(new JLabel("License Plate:"));
        card.add(new JLabel(v.getLicensePlateNumber()));

        card.add(new JLabel("Vehicle Type:"));
        card.add(new JLabel(vehicleType));

        card.add(new JLabel("Brand:"));
        card.add(new JLabel(v.getBrand()));

        card.add(new JLabel("Model:"));
        card.add(new JLabel(v.getModel()));

        card.add(new JLabel("Color:"));
        card.add(new JLabel(String.valueOf(v.getColor())));

        return card;
    }

    private Border makeBlueBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );
    }
}
