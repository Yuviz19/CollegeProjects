package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import db.CertificateDAO;
import model.Certificate;
import util.Util;

public class IssuePanel extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final JTextField nameField = new JTextField(22);
    private final JTextField courseField = new JTextField(22);
    private final JTextField institutionField = new JTextField(22);
    private final JTextField issueDateField = new JTextField(10);
    private final JTextField expiryDateField = new JTextField(10);
    private final JComboBox<String> gradeCombo = new JComboBox<>(
            new String[] { "A+", "A", "B+", "B", "C", "Pass" });

    public IssuePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 35, 20, 35));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.PAGE_AXIS));
        form.add(Style.title("Issue Certificate"));
        form.add(spacer(18));
        form.add(labeled("Holder Name  *", nameField));
        form.add(spacer(8));
        form.add(labeled("Course / Degree", courseField));
        form.add(spacer(8));
        form.add(labeled("Institution", institutionField));
        form.add(spacer(8));
        form.add(labeled("Grade", gradeCombo));
        form.add(spacer(8));
        form.add(dates());
        form.add(spacer(22));

        javax.swing.JButton issueButton = Style.primaryButton("Issue Certificate");
        issueButton.addActionListener(e -> onIssue());
        JPanel row = new JPanel(); row.add(issueButton);
        form.add(row);

        add(form, BorderLayout.NORTH);
        setDefaultDates();
    }

    private JPanel labeled(String label, javax.swing.JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        JLabel l = new JLabel(label);
        l.setFont(Style.HEADING);
        p.add(l);
        p.add(spacer(3));
        p.add(field);
        return p;
    }

    private JPanel dates() {
        JPanel p = new JPanel();
        JLabel l1 = new JLabel("Issue Date (yyyy-MM-dd) ");
        JLabel l2 = new JLabel("   Expiry Date (yyyy-MM-dd) ");
        l1.setFont(Style.HEADING); l2.setFont(Style.HEADING);
        p.add(l1); p.add(issueDateField); p.add(l2); p.add(expiryDateField);
        return p;
    }

    private JPanel spacer(int height) {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(1, height));
        return p;
    }

    private void setDefaultDates() {
        issueDateField.setText(LocalDate.now().toString());
        expiryDateField.setText(LocalDate.now().plusYears(2).toString());
    }

    private void onIssue() {

        String name = nameField.getText().trim();
        String course = courseField.getText().trim();
        String institution = institutionField.getText().trim();
        String grade = (String) gradeCombo.getSelectedItem();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Holder name is required.",
                    "Missing field", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate issueDate, expiryDate;
        try {
            issueDate  = LocalDate.parse(issueDateField.getText().trim(), DATE_FMT);
            expiryDate = LocalDate.parse(expiryDateField.getText().trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Dates must be in yyyy-MM-dd format.",
                    "Invalid date", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (expiryDate.isBefore(issueDate)) {
            JOptionPane.showMessageDialog(this, "Expiry date must be after the issue date.",
                    "Invalid date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final Certificate certificate =
                new Certificate(name, course, institution, grade, issueDate, expiryDate);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                CertificateDAO dao = new CertificateDAO();

                certificate.setId(Util.newCertificateId());
                while (dao.exists(certificate.getId())) {
                    certificate.setId(Util.newCertificateId());
                }
                certificate.setSignature(Util.sha256(certificate.getSignatureInput()));

                return dao.insert(certificate);
            }

            @Override
            protected void done() {
                Boolean success;
                try {
                    success = get();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(IssuePanel.this,
                            "Database error: " + ex.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (Boolean.TRUE.equals(success)) {
                    JOptionPane.showMessageDialog(IssuePanel.this,
                            "Certificate issued!\n\nCertificate ID: " + certificate.getId()
                                    + "\nKeep this ID — it is needed for verification.",
                            "Saved", JOptionPane.INFORMATION_MESSAGE);
                    nameField.setText("");
                    courseField.setText("");
                    institutionField.setText("");
                    gradeCombo.setSelectedIndex(0);
                    setDefaultDates();
                } else {
                    JOptionPane.showMessageDialog(IssuePanel.this,
                            "Could not save the certificate.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
