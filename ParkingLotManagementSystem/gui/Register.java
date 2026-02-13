package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import model.Admin;
import model.Client;
import model.User;
import storage.DataManager;
import storage.SaveData;
import model.IDGenerator;

@SuppressWarnings("serial")
public class Register extends JPanel {

    // Simple palette (UI only)
    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PRIMARY = new Color(33, 102, 255);
    private static final Color PRIMARY_HOVER = new Color(25, 84, 220);
    private static final Color TEXT = new Color(25, 28, 33);
    private static final Color MUTED = new Color(110, 118, 129);
    private static final Color BORDER = new Color(220, 225, 232);

    public Register() {
        setLayout(new GridBagLayout());
        setBackground(BG);
        setOpaque(true);

        // Card container
        JPanel card = new JPanel(new BorderLayout(0, 18));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(28, 32, 28, 32)
        ));
        card.setPreferredSize(new Dimension(660, 620));

        // Header
        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setOpaque(false);

        JLabel title = new JLabel("Welcome", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 30));
        title.setForeground(PRIMARY);

        JLabel subtitle = new JLabel("Create your account to get started", SwingConstants.CENTER);
        subtitle.setFont(new Font("Tahoma", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.CENTER);

        card.add(header, BorderLayout.NORTH);

        // Form area (GridBag for cleaner alignment)
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Fields
        JTextField firstName = styledTextField();
        JTextField lastName = styledTextField();
        JTextField email = styledTextField();
        JPasswordField password = styledPasswordField();
        JPasswordField confirmPassword = styledPasswordField();
        JComboBox<String> accType = styledComboBox(new String[] { "Client", "Admin" });

        addRow(form, gbc, 0, "First Name", firstName);
        addRow(form, gbc, 1, "Last Name", lastName);
        addRow(form, gbc, 2, "Email", email);
        addRow(form, gbc, 3, "Password", password);
        addRow(form, gbc, 4, "Confirm Password", confirmPassword);
        addRow(form, gbc, 5, "Account Type", accType);

        // Buttons row
        JPanel buttons = new JPanel(new BorderLayout(12, 0));
        buttons.setOpaque(false);

        JButton loginBtn = secondaryButton("Login");
        loginBtn.addActionListener((ActionEvent e) -> NavigationHandler.switchTo(new Login()));

        JButton createAccBtn = primaryButton("Create Account");
        createAccBtn.addActionListener((ActionEvent e) -> {

            // validations (UNCHANGED)
            if (firstName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "First Name cannot be empty", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (lastName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Last Name cannot be empty", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (email.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email cannot be empty", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String pass = new String(password.getPassword());
            String conf = new String(confirmPassword.getPassword());

            if (pass.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password cannot be empty", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (conf.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please confirm your password", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!pass.equals(conf)) {
                JOptionPane.showMessageDialog(this, "Password doesn't match", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User user;
            if ("Admin".equals(accType.getSelectedItem())) {
                user = new Admin(IDGenerator.getNextUserID(),
                        firstName.getText().trim(),
                        lastName.getText().trim(),
                        email.getText().trim(),
                        pass);
            } else {
                user = new Client(IDGenerator.getNextUserID(),
                        firstName.getText().trim(),
                        lastName.getText().trim(),
                        email.getText().trim(),
                        pass);
            }

            DataManager.users.add(user);
            SaveData.saveAll();
            new Dashboard(user);
        });

        buttons.add(loginBtn, BorderLayout.WEST);
        buttons.add(createAccBtn, BorderLayout.CENTER);

        // Place form + buttons
        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setOpaque(false);
        body.add(form, BorderLayout.CENTER);
        body.add(Box.createVerticalStrut(2), BorderLayout.NORTH);
        body.add(buttons, BorderLayout.SOUTH);

        card.add(body, BorderLayout.CENTER);

        // Center the card on the page
        GridBagConstraints root = new GridBagConstraints();
        root.gridx = 0;
        root.gridy = 0;
        root.anchor = GridBagConstraints.CENTER;
        add(card, root);
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String labelText, Component field) {
        // label
        gbc.gridx = 0;
        gbc.gridy = row * 2;
        gbc.insets = new Insets(6, 0, 4, 0);
        form.add(fieldLabel(labelText), gbc);

        // field
        gbc.gridx = 0;
        gbc.gridy = row * 2 + 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        form.add(field, gbc);
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.BOLD, 13));
        l.setForeground(TEXT);
        return l;
    }

    private JTextField styledTextField() {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(0, 38));
        tf.setFont(new Font("Tahoma", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        tf.setBackground(Color.WHITE);
        tf.setForeground(TEXT);
        return tf;
    }

    private JPasswordField styledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setPreferredSize(new Dimension(0, 38));
        pf.setFont(new Font("Tahoma", Font.PLAIN, 14));
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        pf.setBackground(Color.WHITE);
        pf.setForeground(TEXT);
        return pf;
    }

    private JComboBox<String> styledComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setPreferredSize(new Dimension(0, 38));
        cb.setFont(new Font("Tahoma", Font.PLAIN, 14));
        cb.setBackground(Color.WHITE);
        cb.setForeground(TEXT);

        // Light border to match fields
        cb.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));

        // Small UI tweak so it looks consistent
        UIManager.put("ComboBox.buttonBackground", Color.WHITE);
        return cb;
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Tahoma", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(0, 42));
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // simple hover effect (UI only)
        b.addChangeListener(e -> {
            if (b.getModel().isRollover()) b.setBackground(PRIMARY_HOVER);
            else b.setBackground(PRIMARY);
        });

        return b;
    }

    private JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Tahoma", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(160, 42));
        b.setBackground(new Color(238, 240, 243));
        b.setForeground(TEXT);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        return b;
    }
}
