package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import model.Ticket;
import model.User;
import storage.DataManager;

public class ShowTicketsHistory implements Actionable {

    @Override
    public String getLabel() {
        return "Tickets History";
    }

    @Override
    public void execute(User u) {
        Dashboard.setContent(getPanel());
    }

    @Override
    public JComponent getPanel() {

        ArrayList<Ticket> tickets = DataManager.ticketHistory;

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        // =========================
        // ===== TITLE SECTION =====
        // =========================

        JLabel title = new JLabel("Tickets History");
        title.setFont(new Font("Tahoma", Font.BOLD, 30));
        title.setForeground(new Color(33, 102, 255));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));
        filterBar.setOpaque(false);

        // Slot dropdown
        filterBar.add(new JLabel("Slot Number:"));
        JComboBox<String> slotBox = new JComboBox<>();
        slotBox.addItem("Any");

        for (Ticket t : tickets) {
            String slot = String.valueOf(t.getSpotNumber());
            if (((DefaultComboBoxModel<String>) slotBox.getModel()).getIndexOf(slot) == -1) {
                slotBox.addItem(slot);
            }
        }
        filterBar.add(slotBox);

        // Plate search field
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

        // =========================
        // ===== CARDS SECTION =====
        // =========================

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

        // =========================
        // ===== FILTER LOGIC ======
        // =========================

        Runnable refreshCards = () -> {

            cardsPanel.removeAll();

            String selectedSlot = (String) slotBox.getSelectedItem();
            String plateSearch = plateField.getText().trim().toLowerCase();

            ArrayList<Ticket> filtered = new ArrayList<>();

            for (Ticket t : tickets) {

                boolean match = true;

                if (!selectedSlot.equals("Any")) {
                    if (t.getSpotNumber() != Integer.parseInt(selectedSlot)) {
                        match = false;
                    }
                }

                if (!plateSearch.isEmpty()) {
                    if (!t.getVehicle().getLicensePlateNumber()
                            .toLowerCase()
                            .contains(plateSearch)) {
                        match = false;
                    }
                }

                if (match) {
                    filtered.add(t);
                }
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

        // Initial load
        refreshCards.run();

        searchBtn.addActionListener(e -> refreshCards.run());

        return wrapper;
    }

    private JPanel createCard(Ticket ticket) {

        JPanel card = new JPanel(new GridLayout(10, 2, 8, 8));
        card.setBackground(new Color(191, 222, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        card.setPreferredSize(new Dimension(300, 260));
        card.setMaximumSize(new Dimension(300, 260));
        card.setMinimumSize(new Dimension(300, 260));

        card.add(new JLabel("Ticket ID:"));
        card.add(new JLabel(String.valueOf(ticket.getID())));

        card.add(new JLabel("Vehicle Plate:"));
        card.add(new JLabel(ticket.getVehicle().getLicensePlateNumber()));

        card.add(new JLabel("Vehicle Brand:"));
        card.add(new JLabel(ticket.getVehicle().getBrand()));

        card.add(new JLabel("Vehicle Model:"));
        card.add(new JLabel(ticket.getVehicle().getModel()));

        card.add(new JLabel("Vehicle Color:"));
        card.add(new JLabel(String.valueOf(ticket.getVehicle().getColor())));

        card.add(new JLabel("Vehicle Owner:"));
        card.add(new JLabel(String.valueOf(ticket.getVehicle().getVehicleOwnerID())));

        card.add(new JLabel("Entry Date:"));
        card.add(new JLabel(ticket.getEntryDate()));

        card.add(new JLabel("Entry Time:"));
        card.add(new JLabel(ticket.getEntryTimeToString()));

        card.add(new JLabel("Slot Number:"));
        card.add(new JLabel(String.valueOf(ticket.getSpotNumber())));

        card.add(new JLabel("Total Fee:"));
        card.add(new JLabel(String.valueOf(ticket.getTotalFee())));

        return card;
    }
}
