import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import db.DB;
import ui.LoginFrame;

public final class Main {

    public static void main(String[] args) {
        try {
            DB.getConnection().close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Cannot reach MySQL. Start the MySQL service and check\n"
                            + "USER/PASSWORD in src/db/DB.java.\n\nDetails: " + e.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
