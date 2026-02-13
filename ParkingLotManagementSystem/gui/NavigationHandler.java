package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class NavigationHandler {

    private static JFrame frame;

    public static void initialize() {
        frame = new JFrame("Smart Parking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1080, 720);
        frame.setLocationRelativeTo(null);

        frame.getContentPane().add(new Login());
        frame.setVisible(true);
    }

    public static void switchTo(JPanel panel) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);
        frame.getContentPane().revalidate();
        frame.getContentPane().repaint();
    }

}
