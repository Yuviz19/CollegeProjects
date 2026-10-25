package ui;

import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import db.CertificateDAO;
import model.Certificate;
import util.Util;

public class IssuePanel extends JPanel {

    private final JTextField nameField = new JTextField();
    private final JTextField courseField = new JTextField();
    private final JTextField institutionField = new JTextField();
    private final JComboBox<String> gradeBox =
            new JComboBox<>(new String[] { "A+", "A", "B+", "B", "C", "Pass" });
    private final JTextField issueDateField = new JTextField(LocalDate.now().toString());
    private final JTextField expiryDateField = new JTextField(LocalDate.now().plusYears(2).toString());

    public IssuePanel() {
        setLayout(new GridLayout(0, 2, 8, 8));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(new JLabel("Holder name:"));              add(nameField);
        add(new JLabel("Course:"));                   add(courseField);
        add(new JLabel("Institution:"));              add(institutionField);
        add(new JLabel("Grade:"));                    add(gradeBox);
        add(new JLabel("Issue date (yyyy-MM-dd):"));  add(issueDateField);
        add(new JLabel("Expiry date (yyyy-MM-dd):")); add(expiryDateField);
        add(new JLabel(""));

        JButton issueButton = new JButton("Issue");
        add(issueButton);
        issueButton.addActionListener(e -> onIssue());
    }

    private void onIssue() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Holder name is required.");
            return;
        }

        LocalDate issueDate, expiryDate;
        try {
            issueDate  = LocalDate.parse(issueDateField.getText().trim());
            expiryDate = LocalDate.parse(expiryDateField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Dates must be in yyyy-MM-dd format.");
            return;
        }
        if (expiryDate.isBefore(issueDate)) {
            JOptionPane.showMessageDialog(this, "Expiry date must be after the issue date.");
            return;
        }

        CertificateDAO dao = new CertificateDAO();
        Certificate c = new Certificate();
        do {
            c.id = Util.newCertificateId();
        } while (dao.exists(c.id));

        c.holderName  = name;
        c.course      = courseField.getText().trim();
        c.institution = institutionField.getText().trim();
        c.grade       = (String) gradeBox.getSelectedItem();
        c.issueDate   = issueDate;
        c.expiryDate  = expiryDate;
        c.signature   = Util.sha256(c.signatureInput());

        if (dao.insert(c)) {
            JOptionPane.showMessageDialog(this,
                    "Certificate issued!\nCertificate ID: " + c.id
                            + "\nKeep this ID — it is needed for verification.");
            nameField.setText("");
            courseField.setText("");
            institutionField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Could not save the certificate.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
