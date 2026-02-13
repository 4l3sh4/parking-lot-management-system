package gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Color;

import model.User;

public class ShowSpotsStatus implements Actionable{
    @Override
    public String getLabel() {
        return "Parking Spots";
    }
    
    @Override
    public void execute(User u) {
        
    }
    
    private JPanel panel;
    
    @Override
    public JPanel getPanel() {
        panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel(getLabel(), JLabel.CENTER);
        label.setForeground(Color.BLACK);

        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}