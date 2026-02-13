package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import model.User;
import model.SpotType;
import model.ParkingSpot;      
import storage.DataManager;      
import storage.SaveData;

public class AddNewParkingSpot implements Actionable {

    private JPanel panel;
    private JComboBox<SpotType> spotType;

    @Override
    public String getLabel() {
        return "Add New Parking Spot";
    }

    @Override
    public void execute(User u) {
        // build panel if needed
        JPanel p = getPanel();

        JDialog dialog = new JDialog();
        dialog.setSize(450, 250);
        dialog.setContentPane(p);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public JPanel getPanel() {
        panel = new JPanel(new GridLayout(2, 2));

        // Label (Swing)
        JLabel lbl = new JLabel("Spot Type:");
        lbl.setForeground(Color.BLACK);
        panel.add(lbl);

        // ComboBox (Swing)
        spotType = new JComboBox<>(SpotType.values());
        panel.add(spotType);

        // Cancel button (Swing)
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

        // Confirm button (Swing)
        JButton confirm = new JButton("Confirm");
        confirm.setBackground(Color.BLUE);
        confirm.setForeground(Color.WHITE);
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SpotType selected = (SpotType) spotType.getSelectedItem();
                    ParkingSpot spot = new ParkingSpot(selected);
                    DataManager.parkingSpots.add(spot);
                    SaveData.saveAll();
                closeDialog();
            }
        });
        panel.add(confirm);

        return panel;
    }

    private void closeDialog() {
        Window w = SwingUtilities.getWindowAncestor(panel);
        if (w != null) w.dispose();
    }
}
