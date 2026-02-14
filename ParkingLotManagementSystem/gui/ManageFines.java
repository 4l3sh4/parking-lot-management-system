package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import model.*;
import storage.DataManager;
import storage.SaveData;

public class ManageFines implements Actionable {

    @Override
    public String getLabel() {
        return "Manage Fines";
    }

    @Override
    public void execute(User u) {
        Dashboard.setContent(getPanel());
    }

    private static final java.awt.Color PRIMARY = new java.awt.Color(33, 102, 255);
    private static final java.awt.Color SUCCESS = new java.awt.Color(34, 139, 34);
    private static final java.awt.Color WARNING = new java.awt.Color(255, 140, 0);

    @Override
    public JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Title
        JLabel title = new JLabel("Fine Management System");
        title.setFont(new Font("Tahoma", Font.BOLD, 30));
        title.setForeground(PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(20));
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Fine Scheme", createFineSchemePanel());
        tabbedPane.addTab("All Fines", createFinesListPanel());
        tabbedPane.addTab("Statistics", createStatisticsPanel());

        panel.add(tabbedPane);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

        return wrapper;
    }

    // ========================
    // PANEL 1: FINE SCHEME
    // ========================
    private JPanel createFineSchemePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Current Scheme
        JPanel currentSchemePanel = new JPanel();
        currentSchemePanel.setLayout(new BoxLayout(currentSchemePanel, BoxLayout.X_AXIS));
        currentSchemePanel.setMaximumSize(new Dimension(500, 50));
        currentSchemePanel.setOpaque(false);

        JLabel currentSchemeLabel = new JLabel("Current Scheme:");
        currentSchemeLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

        JLabel schemeValueLabel = new JLabel(DataManager.currentFineScheme.toString());
        schemeValueLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        schemeValueLabel.setForeground(SUCCESS);

        currentSchemePanel.add(currentSchemeLabel);
        currentSchemePanel.add(Box.createHorizontalStrut(10));
        currentSchemePanel.add(schemeValueLabel);
        currentSchemePanel.add(Box.createHorizontalGlue());

        // Scheme Selection
        JPanel schemeSelectionPanel = new JPanel();
        schemeSelectionPanel.setLayout(new BoxLayout(schemeSelectionPanel, BoxLayout.Y_AXIS));
        schemeSelectionPanel.setOpaque(false);
        schemeSelectionPanel.setBorder(BorderFactory.createTitledBorder("Select Fine Scheme"));
        schemeSelectionPanel.setMaximumSize(new Dimension(500, 250));

        for (FineScheme scheme : FineScheme.values()) {
            JButton btn = new JButton(scheme.toString() + " - " + scheme.getDescription());
            btn.setFont(new Font("Tahoma", Font.PLAIN, 11));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(450, 40));
            btn.addActionListener(e -> selectScheme(scheme, schemeValueLabel));
            schemeSelectionPanel.add(btn);
            schemeSelectionPanel.add(Box.createVerticalStrut(10));
        }

        // Scheme Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Scheme Details"));
        detailsPanel.setMaximumSize(new Dimension(500, 200));

        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        detailsArea.setText(getSchemeDetails());

        detailsPanel.add(new JScrollPane(detailsArea));

        panel.add(currentSchemePanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(schemeSelectionPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(detailsPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void selectScheme(FineScheme scheme, JLabel schemeLabel) {
        DataManager.currentFineScheme = scheme;
        schemeLabel.setText(scheme.toString());
        SaveData.saveAll();
        JOptionPane.showMessageDialog(null,
                "Fine scheme updated to: " + scheme.toString(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private String getSchemeDetails() {
        return "OPTION A - FIXED FINE SCHEME\n" +
               "  • Flat RM 50 fine for overstaying\n\n" +
               "OPTION B - PROGRESSIVE FINE SCHEME\n" +
               "  • First 24 hours: RM 50\n" +
               "  • Hours 24-48: Additional RM 100\n" +
               "  • Hours 48-72: Additional RM 150\n" +
               "  • Above 72 hours: Additional RM 200\n\n" +
               "OPTION C - HOURLY FINE SCHEME\n" +
               "  • RM 20 per hour for overstaying\n\n" +
               "Note: Both options also charge RM 50 for\n" +
               "using a reserved spot without reservation.";
    }

    // ========================
    // PANEL 2: ALL FINES
    // ========================
    private JPanel createFinesListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            // Refresh will be handled by creating new panel
            Dashboard.setContent(getPanel());
        });

        JButton exportBtn = new JButton("Export Outstanding Fees to CSV");
        exportBtn.addActionListener(e -> exportOutstandingFeesCSV());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(exportBtn);
        buttonPanel.add(Box.createHorizontalGlue());

        // Table
        String[] columns = {"ID", "License Plate", "Amount (RM)", "Reason", "Status", "Created", "Paid"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ArrayList<Fine> fines = FineManager.getAllFines();
        for (Fine fine : fines) {
            if (fine != null) {
                tableModel.addRow(new Object[]{
                        fine.getId(),
                        fine.getLicensePlate(),
                        String.format("%.2f", fine.getAmount()),
                        fine.getReason(),
                        fine.isPaid() ? "PAID" : "UNPAID",
                        fine.getCreatedDate() + " " + fine.getCreatedTime(),
                        fine.getPaidDate() + (fine.isPaid() ? " " + fine.getPaidTime() : "")
                });
            }
        }

        JTable table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        table.getColumnModel().getColumn(6).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 300));

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ========================
    // PANEL 3: STATISTICS
    // ========================
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        FineManager.FineStatistics stats = FineManager.getStatistics();

        // Statistics Grid
        JPanel statsGrid = new JPanel(new GridLayout(3, 2, 20, 20));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(600, 200));

        // Total Fines
        statsGrid.add(createStatCard("Total Fines", String.valueOf(stats.totalFines), PRIMARY));
        
        // Paid Fines
        statsGrid.add(createStatCard("Fines Paid", String.valueOf(stats.paidFines), SUCCESS));
        
        // Unpaid Fines
        statsGrid.add(createStatCard("Fines Unpaid", String.valueOf(stats.unpaidFines), WARNING));
        
        // Total Amount
        statsGrid.add(createStatCard("Total Amount", String.format("RM %.2f", stats.totalAmount), PRIMARY));
        
        // Paid Amount
        statsGrid.add(createStatCard("Paid Amount", String.format("RM %.2f", stats.paidAmount), SUCCESS));
        
        // Unpaid Amount
        statsGrid.add(createStatCard("Unpaid Amount", String.format("RM %.2f", stats.unpaidAmount), WARNING));

        // Scheme Info
        JPanel schemePanel = new JPanel();
        schemePanel.setLayout(new BoxLayout(schemePanel, BoxLayout.Y_AXIS));
        schemePanel.setOpaque(false);
        schemePanel.setBorder(BorderFactory.createTitledBorder("Current Fine Scheme"));
        schemePanel.setMaximumSize(new Dimension(600, 100));

        JLabel schemeLabel = new JLabel("Scheme: " + DataManager.currentFineScheme.toString());
        schemeLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

        JLabel descLabel = new JLabel("Description: " + DataManager.currentFineScheme.getDescription());
        descLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));

        schemePanel.add(schemeLabel);
        schemePanel.add(Box.createVerticalStrut(10));
        schemePanel.add(descLabel);

        panel.add(statsGrid);
        panel.add(Box.createVerticalStrut(30));
        panel.add(schemePanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createStatCard(String title, String value, java.awt.Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(true);
        card.setBackground(new java.awt.Color(240, 245, 255));
        card.setBorder(BorderFactory.createLineBorder(color, 2));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        titleLabel.setForeground(color);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        valueLabel.setForeground(color);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);

        return card;
    }

    private void exportOutstandingFeesCSV() {
        ArrayList<Fine> unpaidFines = FineManager.getAllUnpaidFines();
        
        if (unpaidFines.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No unpaid fines to export.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("outstanding_fines.csv"));
        int option = fileChooser.showSaveDialog(null);
        
        if (option != JFileChooser.APPROVE_OPTION) return;
        
        File file = fileChooser.getSelectedFile();
        
        try (PrintWriter writer = new PrintWriter(file)) {
            // Write header
            writer.println("Fine ID,License Plate,Amount (RM),Reason,Created Date,Created Time");
            
            // Write data
            for (Fine fine : unpaidFines) {
                if (fine != null) {
                    String line = String.format("%d,%s,%.2f,%s,%s,%s",
                            fine.getId(),
                            fine.getLicensePlate(),
                            fine.getAmount(),
                            escapeCsvField(fine.getReason()),
                            fine.getCreatedDate(),
                            fine.getCreatedTime());
                    writer.println(line);
                }
            }
            
            // Write summary
            writer.println();
            FineManager.FineStatistics stats = FineManager.getStatistics();
            writer.println("Summary Report");
            writer.printf("Total Unpaid Fines: %d%n", stats.unpaidFines);
            writer.printf("Total Outstanding Amount: RM %.2f%n", stats.unpaidAmount);
            
            JOptionPane.showMessageDialog(null, 
                    "Successfully exported " + unpaidFines.size() + " unpaid fines to:\n" + file.getAbsolutePath(),
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Export failed:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String escapeCsvField(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}
