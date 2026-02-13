package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import model.User;
import storage.DataManager;

@SuppressWarnings("serial")
public class Login extends JPanel {

    // UI palette (design only)
    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PRIMARY = new Color(33, 102, 255);
    private static final Color PRIMARY_HOVER = new Color(25, 84, 220);
    private static final Color TEXT = new Color(25, 28, 33);
    private static final Color MUTED = new Color(110, 118, 129);
    private static final Color BORDER = new Color(220, 225, 232);

    public Login() {
        setLayout(new GridBagLayout());
        setBackground(BG);
        setOpaque(true);

        // Card
        JPanel card = new JPanel(new BorderLayout(0, 18));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(28, 32, 28, 32)
        ));
        card.setPreferredSize(new Dimension(560, 420));

        // Header
        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setOpaque(false);

        JLabel title = new JLabel("Login", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 30));
        title.setForeground(PRIMARY);

        JLabel subtitle = new JLabel("Welcome back — sign in to continue", SwingConstants.CENTER);
        subtitle.setFont(new Font("Tahoma", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.CENTER);

        card.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JTextField email = styledTextField();
        JPasswordField password = styledPasswordField();

        addRow(form, gbc, 0, "Email", email);
        addRow(form, gbc, 1, "Password", password);

        // Buttons
        JPanel btnRow = new JPanel(new BorderLayout(12, 0));
        btnRow.setOpaque(false);

        JButton createAcc = secondaryButton("Create Account");
        JButton loginBtn = primaryButton("Login");

        btnRow.add(createAcc, BorderLayout.WEST);
        btnRow.add(loginBtn, BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setOpaque(false);
        body.add(form, BorderLayout.CENTER);
        body.add(btnRow, BorderLayout.SOUTH);

        card.add(body, BorderLayout.CENTER);

        // Actions (UNCHANGED)
        createAcc.addActionListener((ActionEvent e) -> NavigationHandler.switchTo(new Register()));

        loginBtn.addActionListener((ActionEvent e) -> {
            if (email.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email cannot be empty", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String pass = new String(password.getPassword());
            if (pass.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password cannot be empty", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User user = null;
            for (User u : DataManager.users) {
                if (u.getEmail().equals(email.getText().trim()) && u.getPassword().equals(pass)) {
                    user = u;
                    break;
                }
            }

            if (user != null) {
                new Dashboard(user);
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect email or password", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        add(card, new GridBagConstraints()); // centered
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String labelText, java.awt.Component field) {
        gbc.gridx = 0;
        gbc.gridy = row * 2;
        gbc.insets = new Insets(6, 0, 4, 0);
        form.add(fieldLabel(labelText), gbc);

        gbc.gridx = 0;
        gbc.gridy = row * 2 + 1;
        gbc.insets = new Insets(0, 0, 12, 0);
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

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Tahoma", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(0, 42));
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // hover effect (design only)
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
