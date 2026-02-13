package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JComponent;

import model.User;

public class Dashboard {

    // design palette
    private static final Color BG = new Color(245, 247, 250);
    private static final Color SIDEBAR_BG = new Color(238, 240, 243);
    private static final Color SIDEBAR_BORDER = new Color(220, 225, 232);
    private static final Color TITLE = new Color(25, 28, 33);

    // the ONE place where pages should be swapped
    private static JPanel contentArea;

    public Dashboard(User user) {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(BG);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(260, 1080));
        sidePanel.setMinimumSize(new Dimension(260, 1080));
        sidePanel.setBackground(SIDEBAR_BG);
        sidePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, SIDEBAR_BORDER),
                BorderFactory.createEmptyBorder(22, 16, 22, 16)
        ));

        // Sidebar title (UI only)
        JLabel menuTitle = new JLabel("Menu");
        menuTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
        menuTitle.setForeground(TITLE);
        menuTitle.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        sidePanel.add(menuTitle);
        sidePanel.add(Box.createVerticalStrut(14));

        // Buttons (same labels/actions list)
        for (int i = 1; i < user.getGUIActions().length; i++) {
            SideButton btn = new SideButton(
                user.getGUIActions()[i].getLabel(),
                user.getGUIActions()[i],
                user
            );
            btn.setAlignmentX(JPanel.LEFT_ALIGNMENT);

            sidePanel.add(btn);
            sidePanel.add(Box.createVerticalStrut(10)); // spacing between buttons
        }

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
