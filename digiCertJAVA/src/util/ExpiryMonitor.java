package util;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import db.DB;

public class ExpiryMonitor extends Thread {

    private final ExpiryListener listener;
    private volatile boolean running = true;

    public interface ExpiryListener {
        void onExpiryCheck(int expiringSoon, int expired);
    }

    public ExpiryMonitor(ExpiryListener listener) {
        super("Expiry-Monitor");
        this.listener = listener;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (running) {
            int[] counts = countExpiries();
            listener.onExpiryCheck(counts[0], counts[1]);
            try {
                Thread.sleep(60_000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void stopMonitor() {
        running = false;
        interrupt();
    }

    private int[] countExpiries() {
        String sql = "SELECT "
                + "SUM(CASE WHEN expiry_date BETWEEN ? AND ? AND status = 'ACTIVE' THEN 1 ELSE 0 END), "
                + "SUM(CASE WHEN expiry_date < ? AND status = 'ACTIVE' THEN 1 ELSE 0 END) "
                + "FROM certificates";

        LocalDate today = LocalDate.now();

        try (java.sql.Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(today));
            ps.setDate(2, Date.valueOf(today.plusDays(30)));
            ps.setDate(3, Date.valueOf(today));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new int[] { rs.getInt(1), rs.getInt(2) };
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[] { 0, 0 };
    }
}
