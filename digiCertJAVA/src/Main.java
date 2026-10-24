import java.time.LocalDate;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import db.CertificateDAO;
import db.DB;
import model.Certificate;
import ui.LoginFrame;
import util.Util;

public final class Main {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Throwable unavailable) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }
        }

        try {
            DB.testConnection();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Cannot reach MySQL. Start the MySQL service and check\n"
                            + "USER/PASSWORD in src/db/DB.java.\n\nDetails: " + e.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        seedDemoData();

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    private static void seedDemoData() {
        try {
            CertificateDAO dao = new CertificateDAO();
            if (!dao.findAll().isEmpty()) return;

            add(dao, "Alice Johnson", "B.Tech Computer Science", "ABC Institute of Technology",
                    "A", LocalDate.now().minusDays(30),  LocalDate.now().plusYears(2));
            add(dao, "Rahul Mehta",   "Diploma in Electronics",  "Modern Polytechnic College",
                    "B", LocalDate.now().minusDays(120), LocalDate.now().plusMonths(14));
            add(dao, "Priya Sharma",   "M.Sc Mathematics",       "University of Data Sciences",
                    "A+", LocalDate.now().minusYears(1), LocalDate.now().plusDays(14));
            add(dao, "Ahmed Khan",     "B.Sc Physics",           "ABC Institute of Technology",
                    "B+", LocalDate.now().minusYears(3), LocalDate.now().minusMonths(2));

            Certificate revoked = new Certificate("David Miller", "B.A. Economics",
                    "National College", "C",
                    LocalDate.now().minusMonths(8), LocalDate.now().plusYears(1));
            revoked.setId(Util.newCertificateId());
            revoked.setSignature(Util.sha256(revoked.getSignatureInput()));
            dao.insert(revoked);
            dao.revoke(revoked.getId(), "Fake document submitted during admission");
        } catch (Exception e) {
            System.out.println("Demo data skipped: " + e.getMessage());
        }
    }

    private static void add(CertificateDAO dao, String name, String course, String institution,
                            String grade, LocalDate issueDate, LocalDate expiryDate) {
        Certificate c = new Certificate(name, course, institution, grade, issueDate, expiryDate);
        c.setId(Util.newCertificateId());
        c.setSignature(Util.sha256(c.getSignatureInput()));
        dao.insert(c);
    }
}
