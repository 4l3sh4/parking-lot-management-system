package gui;

import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Box;

import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;

import model.Ticket;
import model.User;
import storage.DataManager;

public class ShowActiveTickets implements Actionable {

    @Override
    public String getLabel() {
        return "Active Tickets";
    }

    @Override
    public void execute(User u) {
        Dashboard.setContent(getPanel());
    }

    @Override
    public JComponent getPanel() {
        int total = DataManager.activeTickets.size();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        for (int i = 0; i < total; i += 2) {

            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setOpaque(false);

            // Left ticket
            row.add(activeTicket(DataManager.activeTickets.get(i)));

            // Right ticket if exists
            if (i + 1 < total) {
                row.add(Box.createHorizontalStrut(15));
                row.add(activeTicket(DataManager.activeTickets.get(i + 1)));
            }

            panel.add(row);
            panel.add(Box.createVerticalStrut(15));
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return scrollPane;
    }

    private JPanel activeTicket(Ticket ticket) {

        JPanel ticketPanel = new JPanel(new GridLayout(9, 2, 15, 15));
        ticketPanel.setBackground(new Color(191, 222, 255));

        ticketPanel.setPreferredSize(new Dimension(350, 300));
        ticketPanel.setMaximumSize(new Dimension(350, 300));
        ticketPanel.setMinimumSize(new Dimension(350, 300));

        ticketPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.blue, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Ticket ID
        ticketPanel.add(new JLabel("Ticket ID:"));
        ticketPanel.add(new JLabel(String.valueOf(ticket.getID())));

        // License Plate Number
        ticketPanel.add(new JLabel("Vehicle License Plate Number:"));
        ticketPanel.add(new JLabel(ticket.getVehicle().getLicensePlateNumber()));

        // Brand
        ticketPanel.add(new JLabel("Vehicle Brand:"));
        ticketPanel.add(new JLabel(ticket.getVehicle().getBrand()));

        // Model
        ticketPanel.add(new JLabel("Vehicle Model:"));
        ticketPanel.add(new JLabel(ticket.getVehicle().getModel()));

        // Color
        ticketPanel.add(new JLabel("Vehicle Color:"));
        ticketPanel.add(new JLabel(ticket.getVehicle().getColor().toString()));

        // Owner
        ticketPanel.add(new JLabel("Vehicle Owner:"));
        ticketPanel.add(new JLabel(String.valueOf(ticket.getVehicle().getVehicleOwnerID())));

        // Entry Date
        ticketPanel.add(new JLabel("Entry Date:"));
        ticketPanel.add(new JLabel(ticket.getEntryDate()));

        // Entry Time
        ticketPanel.add(new JLabel("Entry Time:"));
        ticketPanel.add(new JLabel(ticket.getEntryTimeToString()));

        // Spot Number
        ticketPanel.add(new JLabel("Spot Number:"));
        ticketPanel.add(new JLabel(String.valueOf(ticket.getSpotNumber())));

        return ticketPanel;
    }
}
