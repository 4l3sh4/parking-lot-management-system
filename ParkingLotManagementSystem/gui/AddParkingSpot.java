package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.User;
import model.SpotType;
import model.ParkingSpot;
import storage.DataManager;
import storage.SaveData;

public class AddParkingSpot implements Actionable {

    private JPanel panel;
    private JDialog dialog;

    // fields
    private JComboBox<SpotType> spotType;
    private JTextField spotIdField;

    @Override
    public String getLabel() {
        return "Add new Parking Spot";
    }

    @Override
    public void execute(User u) {
        dialog = new JDialog();
        dialog.setSize(520, 230);
        dialog.setContentPane(getPanel());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public JPanel getPanel() {
        panel = new JPanel(new GridLayout(3, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Spot ID
        JLabel idLabel = new JLabel("Spot ID (e.g., F1-R1-S1):");
        idLabel.setForeground(Color.BLACK);
        panel.add(idLabel);

        spotIdField = new JTextField();
        panel.add(spotIdField);

        // Spot Type
        JLabel typeLabel = new JLabel("Spot Type:");
        typeLabel.setForeground(Color.BLACK);
        panel.add(typeLabel);

        spotType = new JComboBox<>(SpotType.values());
        panel.add(spotType);

        JButton cancel = new JButton("Cancel");
        cancel.setBackground(Color.LIGHT_GRAY);
        cancel.setForeground(Color.BLACK);
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        });
        panel.add(cancel);

        JButton confirm = new JButton("Confirm");
        confirm.setBackground(Color.BLUE);
        confirm.setForeground(Color.WHITE);
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String spotId = spotIdField.getText() == null ? "" : spotIdField.getText().trim().toUpperCase();
                SpotType selectedType = (SpotType) spotType.getSelectedItem();

                if (spotId.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Spot ID cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate format: F#-R#-S#
                if (!spotId.matches("^F\\d+-R\\d+-S\\d+$")) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid Spot ID format.\nUse: F#-R#-S# (e.g., F1-R1-S1)",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (selectedType == null) {
                    JOptionPane.showMessageDialog(null, "Please select a spot type.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check uniqueness
                if (DataManager.parkingSpots != null) {
                    for (ParkingSpot sp : DataManager.parkingSpots) {
                        if (sp != null && sp.getSpotNumber() != null &&
                            sp.getSpotNumber().equalsIgnoreCase(spotId)) {
                            JOptionPane.showMessageDialog(null,
                                    "Spot ID already exists: " + spotId,
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }

                // Create spot (constructor requires spotNumber + type)
                ParkingSpot spot = new ParkingSpot(spotId, selectedType);

                DataManager.parkingSpots.add(spot);
                SaveData.saveAll();

                JOptionPane.showMessageDialog(null,
                        "Added spot: " + spotId + " (" + selectedType + ")",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                closeDialog();
            }
        });
        panel.add(confirm);

        return panel;
    }

    private void closeDialog() {
        if (dialog != null) dialog.dispose();
    }
}
