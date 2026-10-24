package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.SwingUtilities;
import util.ExpiryMonitor;

public class MainFrame extends JFrame {

    private final String username;
    private final ExpiryMonitor expiryMonitor;

    private final IssuePanel issuePanel = new IssuePanel();
    private final ViewPanel viewPanel = new ViewPanel();
    private final VerifyPanel verifyPanel = new VerifyPanel();
    private final RevokePanel revokePanel = new RevokePanel();

    private final JButton[] viewButtons = new JButton[5];
    private final JPanel content = new JPanel(new BorderLayout());
    private final JLabel statusLabel = new JLabel(" ");

    private static final String[] VIEW_NAMES =
            { "Issue Certificate", "View / Search", "Verify", "Revoke", "Logout" };

    public MainFrame(String username) {
        super("Digital Certificate System — " + username);
        this.username = username;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainArea(), BorderLayout.CENTER);
        showView(0);

        expiryMonitor = new ExpiryMonitor((soon, expired) -> {

            String text = "Expiry check: " + soon + " expiring soon, " + expired + " expired.";
            SwingUtilities.invokeLater(() -> statusLabel.setText(text));
        });
        expiryMonitor.start();

        setSize(1040, 680);
        setLocationRelativeTo(null);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 680));
        sidebar.setBackground(Style.DARK);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.PAGE_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel title = new JLabel("<html><b style='font-size:15px'>Certificate</b>"
                + "<br><b style='font-size:15px'>Management</b></html>");
        title.setForeground(Color.WHITE);
        sidebar.add(title);
        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

        for (int i = 0; i < VIEW_NAMES.length; i++) {
            JButton b = new JButton(VIEW_NAMES[i]);
            b.setAlignmentX(0.5f);
            b.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            b.setFont(Style.HEADING);
            b.setForeground(Color.WHITE);
            b.setBackground(Style.DARK);
            b.setOpaque(true);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            b.setPreferredSize(new Dimension(200, 42));
            b.setMaximumSize(new Dimension(200, 42));

            final int viewIndex = i;
            b.addActionListener(e -> {
                if (viewIndex == 4) {
                    logout();
                } else {
                    showView(viewIndex);
                }
            });

            viewButtons[i] = b;
            sidebar.add(b);
            sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

            if (i == 3) {
                sidebar.add(Box.createRigidArea(new Dimension(0, 60)));
            }
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JPanel buildMainArea() {
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(Style.DARK);
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(6, 15, 6, 15)));

        JPanel main = new JPanel(new BorderLayout());
        main.add(content, BorderLayout.CENTER);
        main.add(statusLabel, BorderLayout.SOUTH);
        return main;
    }

    private void showView(int index) {
        JPanel panel;
        switch (index) {
            case 0: panel = issuePanel;   break;
            case 1: panel = viewPanel;    break;
            case 2: panel = verifyPanel;  break;
            default: panel = revokePanel; break;
        }

        content.removeAll();
        content.add(panel, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();

        for (int i = 0; i < 4; i++) {
            viewButtons[i].setBackground(i == index ? Style.BLUE : Style.DARK);
        }

        if (index == 1) {
            viewPanel.loadTable();
        }
    }

    private void logout() {
        expiryMonitor.stopMonitor();
        new LoginFrame().setVisible(true);
        dispose();
    }
}
