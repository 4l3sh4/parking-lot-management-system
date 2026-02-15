package gui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.User;

/**
 * Reusable sidebar panel for the dashboard navigation menu.
 * Displays a list of action buttons based on user permissions.
 */
@SuppressWarnings("serial")
public class SidebarPanel extends JPanel {
    
    private User user;
    
    /**
     * Creates a new sidebar panel for the given user.
     * @param user The current logged-in user
     */
    public SidebarPanel(User user) {
        this.user = user;
        initializeUI();
    }
    
    /**
     * Sets up the sidebar UI components.
     */
    private void initializeUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(260, 1080));
        setMinimumSize(new Dimension(260, 1080));
        setBackground(StyledComponents.SIDEBAR_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, StyledComponents.SIDEBAR_BORDER),
                BorderFactory.createEmptyBorder(22, 16, 22, 16)
        ));
        
        // Sidebar title
        JLabel menuTitle = StyledComponents.createSectionLabel("Menu");
        menuTitle.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        
        add(menuTitle);
        add(Box.createVerticalStrut(14));
        
        // Add navigation buttons based on user actions
        gui.Actionable[] actions = user.getGUIActions();
        for (int i = 0; i < actions.length; i++) {
            SideButton btn = new SideButton(
                actions[i].getLabel(),
                actions[i],
                user
            );
            btn.setAlignmentX(JPanel.LEFT_ALIGNMENT);
            
            add(btn);
            add(Box.createVerticalStrut(10)); // spacing between buttons
        }
    }
    
    /**
     * Gets the current user associated with this sidebar.
     * @return The current user
     */
    public User getUser() {
        return user;
    }
}
