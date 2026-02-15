package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Reusable card-style panel for forms (Login, Register, etc.).
 * Provides a centered white card with header section and form area.
 */
@SuppressWarnings("serial")
public class FormCardPanel extends JPanel {
    
    private JPanel card;
    private JPanel formArea;
    private GridBagConstraints formGbc;
    
    /**
     * Creates a new form card panel with the specified dimensions and title.
     * @param width Card width in pixels
     * @param height Card height in pixels
     * @param title Main title text
     * @param subtitle Subtitle/description text (can be null)
     */
    public FormCardPanel(int width, int height, String title, String subtitle) {
        setLayout(new GridBagLayout());
        setBackground(StyledComponents.BG);
        setOpaque(true);
        
        // Card container
        card = new JPanel(new BorderLayout(0, 18));
        card.setBackground(StyledComponents.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StyledComponents.BORDER, 1, true),
                BorderFactory.createEmptyBorder(28, 32, 28, 32)
        ));
        card.setPreferredSize(new Dimension(width, height));
        
        // Header
        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setOpaque(false);
        
        JLabel titleLabel = StyledComponents.createTitleLabel(title);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(titleLabel, BorderLayout.NORTH);
        
        if (subtitle != null && !subtitle.isEmpty()) {
            JLabel subtitleLabel = StyledComponents.createSubtitleLabel(subtitle);
            subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            header.add(subtitleLabel, BorderLayout.CENTER);
        }
        
        card.add(header, BorderLayout.NORTH);
        
        // Form area (GridBag for cleaner alignment)
        formArea = new JPanel(new GridBagLayout());
        formArea.setOpaque(false);
        
        formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(10, 0, 10, 0);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.weightx = 1;
        
        add(card, new GridBagConstraints()); // centered
    }
    
    /**
     * Adds a labeled form field row.
     * @param row Row index (0-based)
     * @param labelText Label text for the field
     * @param field The input component (JTextField, JComboBox, etc.)
     */
    public void addFormRow(int row, String labelText, Component field) {
        // Label
        formGbc.gridx = 0;
        formGbc.gridy = row * 2;
        formGbc.insets = new Insets(6, 0, 4, 0);
        formArea.add(StyledComponents.createFieldLabel(labelText), formGbc);
        
        // Field
        formGbc.gridx = 0;
        formGbc.gridy = row * 2 + 1;
        formGbc.insets = new Insets(0, 0, 12, 0);
        formArea.add(field, formGbc);
    }
    
    /**
     * Sets the content to display in the card body (below the header).
     * Typically used to add the form area and buttons.
     * @param content The content panel to display
     */
    public void setCardBody(JPanel content) {
        card.add(content, BorderLayout.CENTER);
    }
    
    /**
     * Gets the form area panel where fields are added.
     * @return The form area panel
     */
    public JPanel getFormArea() {
        return formArea;
    }
    
    /**
     * Gets the card panel (the white container).
     * @return The card panel
     */
    public JPanel getCard() {
        return card;
    }
}
