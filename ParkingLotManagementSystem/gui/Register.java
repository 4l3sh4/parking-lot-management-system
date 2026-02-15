package gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import model.Admin;
import model.Client;
import model.User;
import storage.DataManager;
import storage.SaveData;
import model.IDGenerator;

@SuppressWarnings("serial")
public class Register extends JPanel {

    public Register() {
        setLayout(new GridBagLayout());
        setBackground(StyledComponents.BG);
        setOpaque(true);

        // Use FormCardPanel for consistent card layout
        FormCardPanel formCard = new FormCardPanel(660, 620, "Welcome", "Create your account to get started");

        // Create form fields using StyledComponents
        JTextField firstName = StyledComponents.createStyledTextField();
        JTextField lastName = StyledComponents.createStyledTextField();
        JTextField email = StyledComponents.createStyledTextField();
        JPasswordField password = StyledComponents.createStyledPasswordField();
        JPasswordField confirmPassword = StyledComponents.createStyledPasswordField();
        JComboBox<String> accType = StyledComponents.createStyledComboBox(new String[] { "Client", "Admin" });

        // Add form rows
        formCard.addFormRow(0, "First Name", firstName);
        formCard.addFormRow(1, "Last Name", lastName);
        formCard.addFormRow(2, "Email", email);
        formCard.addFormRow(3, "Password", password);
        formCard.addFormRow(4, "Confirm Password", confirmPassword);
        formCard.addFormRow(5, "Account Type", accType);

        // Buttons row
        JPanel buttons = new JPanel(new BorderLayout(12, 0));
        buttons.setOpaque(false);

        JButton loginBtn = StyledComponents.createSecondaryButton("Login");
        loginBtn.addActionListener((ActionEvent e) -> NavigationHandler.switchTo(new Login()));

        JButton createAccBtn = StyledComponents.createPrimaryButton("Create Account");
        createAccBtn.addActionListener((ActionEvent e) -> {

            // validations
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
                user = new Admin(
                        IDGenerator.getNextUserID(),
                        firstName.getText().trim(),
                        lastName.getText().trim(),
                        email.getText().trim(),
                        pass
                );
            } else {
                user = new Client(
                        IDGenerator.getNextUserID(),
                        firstName.getText().trim(),
                        lastName.getText().trim(),
                        email.getText().trim(),
                        pass
                );
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
        body.add(formCard.getFormArea(), BorderLayout.CENTER);
        body.add(Box.createVerticalStrut(2), BorderLayout.NORTH);
        body.add(buttons, BorderLayout.SOUTH);

        formCard.setCardBody(body);

        // Center the card using GridBagLayout
        add(formCard, new GridBagConstraints());
    }
}
