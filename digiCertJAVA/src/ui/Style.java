package ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Style {

    public static final Color BLUE  = new Color(24, 130, 240);
    public static final Color DARK  = new Color(36, 43, 56);
    public static final Color GREEN = new Color(28, 145, 90);
    public static final Color RED   = new Color(200, 60, 60);
    public static final Color AMBER = new Color(200, 140, 0);

    public static final Font TITLE   = new Font("SansSerif", Font.BOLD, 22);
    public static final Font HEADING = new Font("SansSerif", Font.BOLD, 14);
    public static final Font NORMAL  = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font BIG     = new Font("SansSerif", Font.BOLD, 28);

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(BLUE);
        b.setForeground(Color.WHITE);
        b.setFont(HEADING);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return b;
    }

    public static JLabel title(String text) {
        JLabel l = new JLabel(text, SwingConstants.LEFT);
        l.setFont(TITLE);
        l.setForeground(DARK);
        return l;
    }
}
