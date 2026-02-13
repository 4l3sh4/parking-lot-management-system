package gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import model.User;

@SuppressWarnings("serial")
public class SideButton extends JLabel {

    // UI colors (design)
    private static final Color BASE_BG   = new Color(238, 240, 243);
    private static final Color HOVER_BG  = new Color(220, 225, 232);
    private static final Color SELECT_BG = new Color(33, 102, 255);   // selected blue
    private static final Color TEXT      = new Color(25, 28, 33);
    private static final Color SELECT_TXT = Color.WHITE;

    // track currently selected button
    private static SideButton selectedBtn = null;

    private boolean selected = false;

    public SideButton(String text, Actionable action, User user) {
        super(text);

        setFont(new Font("Tahoma", Font.PLAIN, 16));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setOpaque(true);
        setPreferredSize(new Dimension(250, 46));
        setMinimumSize(new Dimension(250, 46));
        setMaximumSize(new Dimension(250, 46));

        setAlignmentX(LEFT_ALIGNMENT);
        setHorizontalAlignment(LEADING);
        setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 10));

        // default look
        applyStyle();

        addMouseListener(new MouseListener() {
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!selected) {
                    setBackground(HOVER_BG);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!selected) {
                    setBackground(BASE_BG);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // keep selected color on the active page
                setSelectedExclusive(SideButton.this);

                // same functionality as before
                action.execute(user);
            }
        });
    }

    private void applyStyle() {
        if (selected) {
            setBackground(SELECT_BG);
            setForeground(SELECT_TXT);
        } else {
            setBackground(BASE_BG);
            setForeground(TEXT);
        }
    }

    public void setSelected(boolean value) {
        this.selected = value;
        applyStyle();
    }

    private static void setSelectedExclusive(SideButton btn) {
        if (selectedBtn != null) {
            selectedBtn.setSelected(false);
        }
        selectedBtn = btn;
        selectedBtn.setSelected(true);
    }
}
