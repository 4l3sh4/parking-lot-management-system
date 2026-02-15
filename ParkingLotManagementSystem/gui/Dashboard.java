package gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JComponent;

import model.User;

public class Dashboard {

    // design palette
    private static final Color BG = new Color(245, 247, 250);

    // the ONE place where pages should be swapped
    private static JPanel contentArea;

    public Dashboard(User user) {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(BG);

        // Use the extracted SidebarPanel class
        SidebarPanel sidePanel = new SidebarPanel(user);

        // Main content wrapper (adds padding + background only)
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG);
        content.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        // content area where we swap panels
        contentArea = new JPanel(new BorderLayout());
        contentArea.setOpaque(false);
        content.add(contentArea, BorderLayout.CENTER);

        // default first page
        if (user.getGUIActions().length > 0) {
            user.getGUIActions()[0].execute(user);
        }

        dashboard.add(sidePanel, BorderLayout.WEST);
        dashboard.add(content, BorderLayout.CENTER);

        NavigationHandler.switchTo(dashboard);
    }

    public static void setContent(JComponent panel) {
        if (contentArea == null) return;

        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }
}
