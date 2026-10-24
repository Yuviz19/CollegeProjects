package ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import db.DB;

public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);
    private final JButton loginButton = Style.primaryButton("Login");
    private final JLabel errorLabel = new JLabel(" ");

    public LoginFrame() {
        setTitle("Digital Certificate System — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(buildForm());
        loginButton.addActionListener(this::onLogin);
        getRootPane().setDefaultButton(loginButton);
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Digital Certificate System", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        panel.add(title, gc);

        gc.gridwidth = 1;
        gc.gridx = 0; gc.gridy = 1;
        panel.add(new JLabel("Username:"), gc);
        gc.gridx = 1;
        panel.add(usernameField, gc);

        gc.gridx = 0; gc.gridy = 2;
        panel.add(new JLabel("Password:"), gc);
        gc.gridx = 1;
        panel.add(passwordField, gc);

        gc.gridx = 1; gc.gridy = 3;
        panel.add(loginButton, gc);

        gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 2;
        errorLabel.setForeground(Style.RED);
        panel.add(errorLabel, gc);

        return panel;
    }

    private void showError(String text) {
        errorLabel.setText(text);
    }

    private void onLogin(ActionEvent e) {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Please fill in both fields.");
            return;
        }

        loginButton.setEnabled(false);
        showError("Checking...");

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return DB.login(user, pass);
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);

                Boolean ok;
                try {
                    ok = get();
                } catch (Exception ex) {
                    showError("Database error: " + ex.getCause().getMessage());
                    return;
                }

                if (Boolean.TRUE.equals(ok)) {
                    new MainFrame(user).setVisible(true);
                    dispose();
                } else {
                    showError("Wrong username or password.");
                }
            }
        }.execute();
    }
}
