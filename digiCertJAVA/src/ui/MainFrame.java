package ui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {

    public MainFrame(String username) {
        super("Digital Certificate System — " + username);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(850, 550);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Issue",          new IssuePanel());
        tabs.addTab("View / Search",  new ViewPanel());
        tabs.addTab("Verify",         new VerifyPanel());
        tabs.addTab("Revoke",         new RevokePanel());
        setContentPane(tabs);
    }
}
