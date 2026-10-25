package ui;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import db.CertificateDAO;

public class RevokePanel extends JPanel {

    private final JComboBox<String> certBox = new JComboBox<>();
    private final JTextArea reasonArea = new JTextArea(3, 30);

    public RevokePanel() {
        setLayout(new GridLayout(0, 1, 5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new javax.swing.JLabel("Certificate to revoke:"));
        add(certBox);
        add(new javax.swing.JLabel("Reason for revocation:"));
        add(new JScrollPane(reasonArea));
        JButton revokeButton = new JButton("Revoke");
        add(revokeButton);
        revokeButton.addActionListener(e -> onRevoke());

        loadActiveCerts();
    }

    private void loadActiveCerts() {
        certBox.removeAllItems();
        for (model.Certificate c : new CertificateDAO().findAll()) {
            if (c.status.equals("ACTIVE")) {
                certBox.addItem(c.id + "  |  " + c.holderName + "  |  " + c.course);
            }
        }
    }

    private void onRevoke() {
        String selected = (String) certBox.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a certificate first.");
            return;
        }
        String id = selected.substring(0, selected.indexOf("  ")).trim();

        String reason = reasonArea.getText().trim();
        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type a reason for revocation.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Revoke " + id + "? This cannot be undone.",
                "Confirm", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }

        if (new CertificateDAO().revoke(id, reason)) {
            JOptionPane.showMessageDialog(this, id + " has been revoked.");
            reasonArea.setText("");
            loadActiveCerts();
        } else {
            JOptionPane.showMessageDialog(this, "Revocation failed.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
