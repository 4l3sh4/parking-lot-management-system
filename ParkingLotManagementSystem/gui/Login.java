package gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import model.User;
import storage.DataManager;

@SuppressWarnings("serial")
public class Login extends JPanel {

    public Login() {
        setLayout(new GridBagLayout());
        setBackground(StyledComponents.BG);
        setOpaque(true);

        // Use FormCardPanel for consistent card layout
        FormCardPanel formCard = new FormCardPanel(560, 420, "Login", "Welcome back — sign in to continue");

        // Create form fields using StyledComponents
        JTextField email = StyledComponents.createStyledTextField();
        JPasswordField password = StyledComponents.createStyledPasswordField();

        // Add form rows
        formCard.addFormRow(0, "Email", email);
        formCard.addFormRow(1, "Password", password);

        // Buttons
        JPanel btnRow = new JPanel(new BorderLayout(12, 0));
        btnRow.setOpaque(false);

        JButton createAcc = StyledComponents.createSecondaryButton("Create Account");
        JButton loginBtn = StyledComponents.createPrimaryButton("Login");

        btnRow.add(createAcc, BorderLayout.WEST);
        btnRow.add(loginBtn, BorderLayout.CENTER);

        // Assemble the body (form + buttons)
        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setOpaque(false);
        body.add(formCard.getFormArea(), BorderLayout.CENTER);
        body.add(btnRow, BorderLayout.SOUTH);

        formCard.setCardBody(body);

        // Actions
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

        // Center the form card using GridBagLayout
        add(formCard, new GridBagConstraints());
    }
}
