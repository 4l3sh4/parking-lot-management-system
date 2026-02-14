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
import java.awt.Font;
import java.awt.Component;
import java.util.ArrayList;

import model.Ticket;
import model.User;
import model.Admin;
import storage.DataManager;

public class ShowActiveTickets implements Actionable {

    @Override
    public String getLabel() {
        return "Active Tickets";
    }

    private User currentUser;

    @Override
    public void execute(User u) {
        this.currentUser = u;
        Dashboard.setContent(getPanel());
    }

    private ArrayList<Ticket> getVisibleActiveTickets() {
        ArrayList<Ticket> out = new ArrayList<>();
        if (DataManager.activeTickets == null) return out;

        boolean isAdmin = (currentUser instanceof Admin);

        for (Ticket t : DataManager.activeTickets) {
            if (t == null || t.getVehicle() == null) continue;

            if (isAdmin) {
                out.add(t);
            } else {
                if (t.getVehicle().getVehicleOwnerID() == currentUser.getID()) {
                    out.add(t);
                }
            }
        }
        return out;
    }

    @Override
    public JComponent getPanel() {
        ArrayList<Ticket> list = getVisibleActiveTickets();
        int total = list.size();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        for (int i = 0; i < total; i += 2) {

            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setOpaque(false);

            // Left ticket
            row.add(activeTicket(list.get(i)));

            // Right ticket if exists
            if (i + 1 < total) {
                row.add(Box.createHorizontalStrut(15));
                row.add(activeTicket(list.get(i + 1)));
            }

            panel.add(row);
            panel.add(Box.createVerticalStrut(15));
        }

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);

        JLabel title = new JLabel("Active Tickets");
        title.setFont(new Font("Tahoma", Font.BOLD, 30));
        title.setForeground(new Color(33, 102, 255));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        wrapper.add(title);
        wrapper.add(Box.createVerticalStrut(15));

        if (total == 0) {
            JLabel none = new JLabel("No active tickets.");
            none.setFont(new Font("Tahoma", Font.PLAIN, 16));
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            wrapper.add(none);
        } else {
            wrapper.add(panel);
        }

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return scrollPane;
    }

    private String ownerLabel(int ownerId) {
        if (DataManager.users == null) return "Unknown (" + ownerId + ")";

        for (User u : DataManager.users) {
            if (u != null && u.getID() == ownerId) {
                return u.getFirstName() + " " + u.getLastName() + " (" + ownerId + ")";
            }
        }
        return "Unknown (" + ownerId + ")";
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

        ticketPanel.add(new JLabel("Ticket Code:"));
        ticketPanel.add(new JLabel(ticket.getTicketCode()));

        ticketPanel.add(new JLabel("Vehicle Plate:"));
        ticketPanel.add(new JLabel(ticket.getVehicle().getLicensePlateNumber()));

        ticketPanel.add(new JLabel("Vehicle Brand:"));
        ticketPanel.add(new JLabel(ticket.getVehicle().getBrand()));

        ticketPanel.add(new JLabel("Vehicle Model:"));
        ticketPanel.add(new JLabel(ticket.getVehicle().getModel()));

        ticketPanel.add(new JLabel("Vehicle Color:"));
        ticketPanel.add(new JLabel(String.valueOf(ticket.getVehicle().getColor())));

        ticketPanel.add(new JLabel("Vehicle Owner:"));
        int ownerId = ticket.getVehicle().getVehicleOwnerID();
        ticketPanel.add(new JLabel(ownerLabel(ownerId)));

        ticketPanel.add(new JLabel("Entry:"));
        ticketPanel.add(new JLabel(ticket.getEntryDate() + " " + ticket.getEntryTimeToString()));
        
        ticketPanel.add(new JLabel("Exit:"));
        ticketPanel.add(new JLabel(ticket.getExitDate() + " " + ticket.getExitTimeToString()));

        ticketPanel.add(new JLabel("Spot Number:"));
        ticketPanel.add(new JLabel(ticket.getSpotNumber()));

        return ticketPanel;
    }
}
