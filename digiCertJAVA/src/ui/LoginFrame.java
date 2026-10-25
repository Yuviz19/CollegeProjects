package ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import db.DB;

public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);

    public LoginFrame() {
        setTitle("Digital Certificate System — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(380, 180);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.gridy = 0; panel.add(new JLabel("Username:"), gc);
        gc.gridx = 1;             panel.add(usernameField, gc);
        gc.gridx = 0; gc.gridy = 1; panel.add(new JLabel("Password:"), gc);
        gc.gridx = 1;             panel.add(passwordField, gc);

        JButton loginButton = new JButton("Login");
        gc.gridx = 1; gc.gridy = 2; panel.add(loginButton, gc);
        loginButton.addActionListener(e -> login());
        getRootPane().setDefaultButton(loginButton);

        setContentPane(panel);
    }

    private void login() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in both fields.");
            return;
        }

        if (DB.login(user, pass)) {
            new MainFrame(user).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Wrong username or password.",
                    "Login failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
