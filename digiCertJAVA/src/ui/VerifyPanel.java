package ui;

import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.Util;

public class VerifyPanel extends JPanel {

    private final JTextField idField = new JTextField(15);
    private final javax.swing.JLabel resultLabel =
            new javax.swing.JLabel("Enter a Certificate ID and press Verify.");

    public VerifyPanel() {
        setLayout(new GridLayout(0, 1, 5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel row = new JPanel();
        row.add(new javax.swing.JLabel("Certificate ID (e.g. DC-1A2B3C4D):"));
        row.add(idField);
        JButton verifyButton = new JButton("Verify");
        row.add(verifyButton);
        add(row);
        add(new javax.swing.JLabel("Result:"));
        add(resultLabel);

        verifyButton.addActionListener(e -> onVerify());
        idField.addActionListener(e -> onVerify());
    }

    private void onVerify() {
        String id = idField.getText().trim().toUpperCase();
        if (id.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Enter a Certificate ID first.");
            return;
        }

        model.Certificate c = new db.CertificateDAO().findById(id);
        if (c == null) {
            resultLabel.setText("NOT FOUND — no certificate exists with this ID.");
            return;
        }
        if (!Util.sha256(c.signatureInput()).equals(c.signature)) {
            resultLabel.setText("TAMPERED — details were changed after issuing.");
            return;
        }
        if (c.status.equals("REVOKED")) {
            resultLabel.setText("REVOKED — reason: "
                    + (c.revokedReason.isEmpty() ? "(none recorded)" : c.revokedReason));
            return;
        }
        if (c.expiryDate.isBefore(LocalDate.now())) {
            resultLabel.setText("EXPIRED — expired on " + c.expiryDate + ".");
            return;
        }
        resultLabel.setText("VALID — authentic and active. Holder: " + c.holderName + ".");
    }
}
