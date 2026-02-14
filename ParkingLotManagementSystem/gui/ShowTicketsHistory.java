package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import model.Ticket;
import model.User;
import model.Admin;
import storage.DataManager;

public class ShowTicketsHistory implements Actionable {

    @Override
    public String getLabel() {
        return "Tickets History";
    }

    private User currentUser;

    @Override
    public void execute(User u) {
        this.currentUser = u;
        Dashboard.setContent(getPanel());
    }

    private ArrayList<Ticket> getVisibleHistoryTickets() {
        ArrayList<Ticket> out = new ArrayList<>();
        if (DataManager.ticketHistory == null) return out;

        boolean isAdmin = (currentUser instanceof Admin);

        for (Ticket t : DataManager.ticketHistory) {
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

        ArrayList<Ticket> tickets = getVisibleHistoryTickets();

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        // ===== TITLE =====
        JLabel title = new JLabel("Tickets History");
        title.setFont(new Font("Tahoma", Font.BOLD, 30));
        title.setForeground(new Color(33, 102, 255));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== FILTER BAR =====
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));
        filterBar.setOpaque(false);

        // Slot dropdown
        filterBar.add(new JLabel("Slot Number:"));
        JComboBox<String> slotBox = new JComboBox<>();
        slotBox.addItem("Any");

        for (Ticket t : tickets) {
            if (t == null) continue;
            String slot = t.getSpotNumber();
            if (slot == null) continue;

            DefaultComboBoxModel<String> m = (DefaultComboBoxModel<String>) slotBox.getModel();
            if (m.getIndexOf(slot) == -1) slotBox.addItem(slot);
        }
        filterBar.add(slotBox);

        // Plate search
        filterBar.add(new JLabel("Vehicle Plate:"));
        JTextField plateField = new JTextField(10);
        filterBar.add(plateField);

        JButton searchBtn = new JButton("Search");
        filterBar.add(searchBtn);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        topPanel.add(title);
        topPanel.add(Box.createVerticalStrut(15));
        topPanel.add(filterBar);
        topPanel.add(Box.createVerticalStrut(20));

        wrapper.add(topPanel, BorderLayout.NORTH);

        // ===== CARDS =====
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(cardsPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(15);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        wrapper.add(scroll, BorderLayout.CENTER);

        // ===== FILTER LOGIC =====
        Runnable refreshCards = () -> {

            cardsPanel.removeAll();

            String selectedSlot = (String) slotBox.getSelectedItem();
            String plateSearch = plateField.getText().trim().toLowerCase();

            ArrayList<Ticket> filtered = new ArrayList<>();

            for (Ticket t : tickets) {
                if (t == null || t.getVehicle() == null) continue;

                boolean match = true;

                // Slot filter
                if (selectedSlot != null && !"Any".equals(selectedSlot)) {
                    String ts = t.getSpotNumber();
                    if (ts == null || !ts.equalsIgnoreCase(selectedSlot)) match = false;
                }

                // Plate filter
                if (!plateSearch.isEmpty()) {
                    String p = t.getVehicle().getLicensePlateNumber();
                    if (p == null || !p.toLowerCase().contains(plateSearch)) match = false;
                }

                if (match) filtered.add(t);
            }

            if (filtered.isEmpty()) {
                JLabel empty = new JLabel("No matching tickets found.");
                empty.setFont(new Font("Tahoma", Font.PLAIN, 16));
                empty.setAlignmentX(Component.CENTER_ALIGNMENT);
                cardsPanel.add(empty);
            } else {
                for (int i = 0; i < filtered.size(); i += 2) {

                    JPanel row = new JPanel();
                    row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
                    row.setOpaque(false);
                    row.setAlignmentX(Component.CENTER_ALIGNMENT);

                    row.add(createCard(filtered.get(i)));

                    if (i + 1 < filtered.size()) {
                        row.add(Box.createHorizontalStrut(15));
                        row.add(createCard(filtered.get(i + 1)));
                    }

                    cardsPanel.add(row);
                    cardsPanel.add(Box.createVerticalStrut(15));
                }
            }

            cardsPanel.revalidate();
            cardsPanel.repaint();
        };

        refreshCards.run();
        searchBtn.addActionListener(e -> refreshCards.run());

        return wrapper;
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

    private JPanel createCard(Ticket ticket) {

        JPanel card = new JPanel(new GridLayout(12, 2, 8, 8));
        card.setBackground(new Color(191, 222, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        card.setPreferredSize(new Dimension(350, 270));
        card.setMaximumSize(new Dimension(350, 270));
        card.setMinimumSize(new Dimension(350, 270));

        card.add(new JLabel("Ticket Code:"));
        card.add(new JLabel(ticket.getTicketCode()));

        card.add(new JLabel("Vehicle Plate:"));
        card.add(new JLabel(ticket.getVehicle().getLicensePlateNumber()));

        card.add(new JLabel("Vehicle Brand:"));
        card.add(new JLabel(ticket.getVehicle().getBrand()));

        card.add(new JLabel("Vehicle Model:"));
        card.add(new JLabel(ticket.getVehicle().getModel()));

        card.add(new JLabel("Vehicle Color:"));
        card.add(new JLabel(String.valueOf(ticket.getVehicle().getColor())));

        card.add(new JLabel("Vehicle Owner:"));
        int ownerId = ticket.getVehicle().getVehicleOwnerID();
        card.add(new JLabel(ownerLabel(ownerId)));

        card.add(new JLabel("Entry:"));
        card.add(new JLabel(ticket.getEntryDate() + " " + ticket.getEntryTimeToString()));
        
        card.add(new JLabel("Exit:"));
        card.add(new JLabel(ticket.getExitDate() + " " + ticket.getExitTimeToString()));

        card.add(new JLabel("Slot Number:"));
        card.add(new JLabel(ticket.getSpotNumber()));

        card.add(new JLabel("Total Fee:"));
        card.add(new JLabel(String.valueOf(ticket.getTotalFee())));

        return card;
    }
}
