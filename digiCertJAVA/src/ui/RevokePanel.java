package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import db.CertificateDAO;
import model.Certificate;

public class RevokePanel extends JPanel {

    private final CertificateDAO dao = new CertificateDAO();
    private final JComboBox<String> certCombo = new JComboBox<>();
    private final javax.swing.JTextArea reasonArea = new javax.swing.JTextArea(3, 38);
    private final JButton revokeButton = new JButton("Revoke Certificate");

    public RevokePanel() {
        setLayout(new BorderLayout(0, 15));
        setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
        box.add(Style.title("Revoke Certificate"));
        box.add(gap(18));

        box.add(new JLabel("Pick the certificate to revoke:"));
        box.add(gap(5));
        JPanel pick = new JPanel();
        certCombo.setPreferredSize(new Dimension(430, 28));
        pick.add(certCombo);
        pick.add(new JButtonReload());
        box.add(pick);

        box.add(gap(15));
        box.add(new JLabel("Reason for revocation:"));
        box.add(gap(5));
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setFont(Style.NORMAL);
        box.add(new javax.swing.JScrollPane(reasonArea));

        box.add(gap(15));
        revokeButton.setBackground(Style.RED);
        revokeButton.setForeground(java.awt.Color.WHITE);
        revokeButton.setFont(Style.HEADING);
        revokeButton.setOpaque(true);
        revokeButton.setBorderPainted(false);
        revokeButton.addActionListener(e -> onRevoke());
        JPanel row = new JPanel(); row.add(revokeButton);
        box.add(row);

        add(box, BorderLayout.NORTH);
        loadActiveCerts();
    }

    public void loadActiveCerts() {
        certCombo.removeAllItems();

        new SwingWorker<java.util.List<Certificate>, Void>() {
            @Override
            protected java.util.List<Certificate> doInBackground() {
                return dao.findAll();
            }

            @Override
            protected void done() {
                java.util.List<Certificate> list;
                try {
                    list = get();
                } catch (Exception ex) {
                    return;
                }

                for (Certificate c : list) {
                    if (c.getStatus().equals("ACTIVE")) {
                        certCombo.addItem(c.getId() + "  |  " + c.getHolderName()
                                + "  |  " + c.getCourse());
                    }
                }
                if (certCombo.getItemCount() == 0) {
                    certCombo.addItem("(no active certificates)");
                }
            }
        }.execute();
    }

    private class JButtonReload extends JButton {
        JButtonReload() {
            super("Reload list");
            addActionListener(e -> loadActiveCerts());
        }
    }

    private JPanel gap(int height) {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(1, height));
        return p;
    }

    private void onRevoke() {
        String selected = (String) certCombo.getSelectedItem();
        if (selected == null || selected.startsWith("(")) {
            JOptionPane.showMessageDialog(this, "Select a certificate first.",
                    "Revoke", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = selected.substring(0, selected.indexOf("  ")).trim();
        String reason = reasonArea.getText().trim();
        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type a reason for revocation.",
                    "Revoke", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this,
                "Revoke " + id + "?\nThis cannot be undone.",
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;

        revokeButton.setEnabled(false);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return dao.revoke(id, reason);
            }

            @Override
            protected void done() {
                revokeButton.setEnabled(true);
                Boolean success;
                try {
                    success = get();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RevokePanel.this,
                            "Database error: " + ex.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (Boolean.TRUE.equals(success)) {
                    JOptionPane.showMessageDialog(RevokePanel.this,
                            id + " has been revoked.", "Done", JOptionPane.INFORMATION_MESSAGE);
                    reasonArea.setText("");
                    loadActiveCerts();
                } else {
                    JOptionPane.showMessageDialog(RevokePanel.this,
                            "Revocation failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
