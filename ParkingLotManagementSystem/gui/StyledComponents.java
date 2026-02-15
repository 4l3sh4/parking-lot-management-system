package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Utility class providing styled Swing components with consistent design.
 * Contains color palette and factory methods for creating styled UI elements.
 */
public class StyledComponents {
    
    // Color Palette
    public static final Color BG = new Color(245, 247, 250);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color PRIMARY = new Color(33, 102, 255);
    public static final Color PRIMARY_HOVER = new Color(25, 84, 220);
    public static final Color SECONDARY_BG = new Color(238, 240, 243);
    public static final Color TEXT = new Color(25, 28, 33);
    public static final Color MUTED = new Color(110, 118, 129);
    public static final Color BORDER = new Color(220, 225, 232);
    public static final Color SIDEBAR_BG = new Color(238, 240, 243);
    public static final Color SIDEBAR_BORDER = new Color(220, 225, 232);
    public static final Color TITLE = new Color(25, 28, 33);
    
    // Typography
    public static final Font FONT_TITLE = new Font("Tahoma", Font.BOLD, 30);
    public static final Font FONT_SECTION = new Font("Tahoma", Font.BOLD, 16);
    public static final Font FONT_LABEL = new Font("Tahoma", Font.BOLD, 13);
    public static final Font FONT_BUTTON = new Font("Tahoma", Font.BOLD, 14);
    public static final Font FONT_TEXT = new Font("Tahoma", Font.PLAIN, 14);
    
    // Private constructor to prevent instantiation
    private StyledComponents() {}
    
    /**
     * Creates a styled text field with consistent borders and padding.
     * @return JTextField with standard styling
     */
    public static JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(0, 38));
        tf.setFont(FONT_TEXT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        tf.setBackground(Color.WHITE);
        tf.setForeground(TEXT);
        return tf;
    }
    
    /**
     * Creates a styled password field with consistent borders and padding.
     * @return JPasswordField with standard styling
     */
    public static JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setPreferredSize(new Dimension(0, 38));
        pf.setFont(FONT_TEXT);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        pf.setBackground(Color.WHITE);
        pf.setForeground(TEXT);
        return pf;
    }
    
    /**
     * Creates a styled combo box with consistent appearance.
     * @param items The items to populate the combo box
     * @return JComboBox with standard styling
     */
    public static JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setPreferredSize(new Dimension(0, 38));
        cb.setFont(FONT_TEXT);
        cb.setBackground(Color.WHITE);
        cb.setForeground(TEXT);
        return cb;
    }
    
    /**
     * Creates a primary button (blue background, white text) with hover effect.
     * @param text Button label text
     * @return JButton with primary styling
     */
    public static JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BUTTON);
        b.setPreferredSize(new Dimension(0, 42));
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        
        // Hover effect
        b.addChangeListener(e -> {
            if (b.getModel().isRollover()) {
                b.setBackground(PRIMARY_HOVER);
            } else {
                b.setBackground(PRIMARY);
            }
        });
        
        return b;
    }
    
    /**
     * Creates a secondary button (gray background) with border.
     * @param text Button label text
     * @return JButton with secondary styling
     */
    public static JButton createSecondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BUTTON);
        b.setPreferredSize(new Dimension(160, 42));
        b.setBackground(SECONDARY_BG);
        b.setForeground(TEXT);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        return b;
    }
    
    /**
     * Creates a label for form fields with consistent font and color.
     * @param text Label text
     * @return JLabel with field label styling
     */
    public static JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT);
        return l;
    }
    
    /**
     * Creates a title label with large bold font.
     * @param text Title text
     * @return JLabel with title styling
     */
    public static JLabel createTitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE);
        l.setForeground(PRIMARY);
        return l;
    }
    
    /**
     * Creates a subtitle/hint label with smaller muted text.
     * @param text Subtitle text
     * @return JLabel with subtitle styling
     */
    public static JLabel createSubtitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TEXT);
        l.setForeground(MUTED);
        return l;
    }
    
    /**
     * Creates a section header label with medium bold font.
     * @param text Section header text
     * @return JLabel with section header styling
     */
    public static JLabel createSectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SECTION);
        l.setForeground(TITLE);
        return l;
    }
}
