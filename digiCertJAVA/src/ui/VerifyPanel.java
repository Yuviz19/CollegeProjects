package ui;

import java.awt.BorderLayout;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import db.CertificateDAO;
import model.Certificate;
import util.Util;

public class VerifyPanel extends JPanel {

    private final CertificateDAO dao = new CertificateDAO();
    private final JTextField idField = new JTextField(16);
    private final JPanel resultPanel = new JPanel();

    public VerifyPanel() {
        setLayout(new BorderLayout(0, 15));
        setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.PAGE_AXIS));
        top.add(Style.title("Verify Certificate"));

        JPanel inputRow = new JPanel();
        inputRow.add(new JLabel("Certificate ID (e.g. DC-1A2B3C4D):"));
        inputRow.add(idField);
        javax.swing.JButton verifyButton = Style.primaryButton("Verify");
        inputRow.add(verifyButton);
        top.add(inputRow);
        add(top, BorderLayout.NORTH);

        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.PAGE_AXIS));
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        resultPanel.add(new JLabel("The verification result appears here."));
        add(resultPanel, BorderLayout.CENTER);

        verifyButton.addActionListener(e -> onVerify());
        idField.addActionListener(e -> onVerify());
    }

    private void onVerify() {
        final String id = idField.getText().trim().toUpperCase();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a Certificate ID first.");
            return;
        }

        new SwingWorker<Certificate, Void>() {
            @Override
            protected Certificate doInBackground() {
                return dao.findById(id);
            }

            @Override
            protected void done() {
                Certificate c;
                try {
                    c = get();
                } catch (Exception ex) {
                    showResult("ERROR", "Database error.", Style.RED, null);
                    return;
                }
                showResult(verdict(c), explanation(c), verdictColor(c), c);
            }
        }.execute();
    }

    private String verdict(Certificate c) {
        if (c == null) return "NOT FOUND";
        if (!Util.sha256(c.getSignatureInput()).equals(c.getSignature())) return "TAMPERED";
        if (c.getStatus().equals("REVOKED")) return "REVOKED";
        if (c.getExpiryDate().isBefore(LocalDate.now())) return "EXPIRED";
        return "VALID";
    }

    private String explanation(Certificate c) {
        if (c == null) return "No certificate exists with this ID.";
        if (!Util.sha256(c.getSignatureInput()).equals(c.getSignature()))
            return "Signature does not match — the details were changed after issuing.";
        if (c.getStatus().equals("REVOKED"))
            return "Authentic, but this certificate was revoked" + reasonSuffix(c);
        if (c.getExpiryDate().isBefore(LocalDate.now()))
            return "Authentic, but it expired on " + c.getExpiryDate() + ".";
        return "This certificate is authentic and active.";
    }

    private String reasonSuffix(Certificate c) {
        return c.getRevokedReason().isEmpty() ? " (no reason recorded)."
                : " — reason: " + c.getRevokedReason() + ".";
    }

    private java.awt.Color verdictColor(Certificate c) {
        if (c == null) return Style.AMBER;
        if (!Util.sha256(c.getSignatureInput()).equals(c.getSignature())) return Style.RED;
        if (c.getStatus().equals("REVOKED")) return Style.RED;
        if (c.getExpiryDate().isBefore(LocalDate.now())) return Style.AMBER;
        return Style.GREEN;
    }

    private void showResult(String verdictText, String explanation, java.awt.Color color,
                            Certificate c) {
        resultPanel.removeAll();

        JLabel verdictLabel = new JLabel(verdictText);
        verdictLabel.setFont(Style.BIG);
        verdictLabel.setForeground(color);
        resultPanel.add(verdictLabel);

        JLabel reasonLabel = new JLabel(explanation);
        reasonLabel.setFont(Style.NORMAL);
        resultPanel.add(reasonLabel);

        if (c != null) {
            resultPanel.add(new javax.swing.Box.Filler(
                    new java.awt.Dimension(1, 14), new java.awt.Dimension(1, 14),
                    new java.awt.Dimension(1, 14)));

            String[] lines = {
                    "Certificate ID:      " + c.getId(),
                    "Holder:              " + c.getHolderName(),
                    "Course / Degree:     " + c.getCourse(),
                    "Institution:         " + c.getInstitution(),
                    "Grade:               " + c.getGrade(),
                    "Issue Date:          " + c.getIssueDate(),
                    "Expiry Date:         " + c.getExpiryDate(),
                    "Signature (SHA-256): " + c.getSignature()
            };
            for (String line : lines) {
                JLabel l = new JLabel(line);
                l.setFont(Style.NORMAL);
                resultPanel.add(l);
            }
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }
}
