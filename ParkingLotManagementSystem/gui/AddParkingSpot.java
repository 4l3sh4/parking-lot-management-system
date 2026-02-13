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
import javax.swing.JPanel;

import model.User;
import model.SpotType;

import model.ParkingSpot;
import storage.DataManager;
import storage.SaveData;

public class AddParkingSpot implements Actionable {

    private JPanel panel;
    private JDialog dialog;

    // keep combo as a field so listeners can access it
    private JComboBox<SpotType> spotType;

    @Override
    public String getLabel() {
        return "Add new Parking Spot";
    }

    @Override
    public void execute(User u) {
        dialog = new JDialog();
        dialog.setSize(450, 200);
        dialog.setContentPane(getPanel());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public JPanel getPanel() {
        panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel label = new JLabel("Spot Type:");
        label.setForeground(Color.BLACK);
        panel.add(label);

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

                int idx = spotType.getSelectedIndex();
                if (idx < 0) return;

                SpotType selectedType = SpotType.values()[idx];

                ParkingSpot spot = new ParkingSpot(selectedType);
                DataManager.parkingSpots.add(spot);
                SaveData.saveAll();

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
