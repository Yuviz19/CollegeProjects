package db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Certificate;

public class CertificateDAO {

    public boolean insert(Certificate c) {
        String sql = "INSERT INTO certificates "
                + "(id, holder_name, course, institution, grade, issue_date, expiry_date, status, signature) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.id);
            ps.setString(2, c.holderName);
            ps.setString(3, c.course);
            ps.setString(4, c.institution);
            ps.setString(5, c.grade);
            ps.setDate(6, Date.valueOf(c.issueDate));
            ps.setDate(7, Date.valueOf(c.expiryDate));
            ps.setString(8, c.status);
            ps.setString(9, c.signature);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exists(String id) {
        String sql = "SELECT 1 FROM certificates WHERE id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Certificate findById(String id) {
        String sql = "SELECT * FROM certificates WHERE id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rowToCertificate(rs) : null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Certificate> findAll() {
        List<Certificate> list = new ArrayList<>();
        try (Connection con = DB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM certificates ORDER BY issue_date DESC")) {

            while (rs.next()) {
                list.add(rowToCertificate(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Certificate> search(String text) {
        List<Certificate> list = new ArrayList<>();
        String sql = "SELECT * FROM certificates "
                + "WHERE id LIKE ? OR holder_name LIKE ? OR course LIKE ? OR institution LIKE ?";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String like = "%" + text + "%";
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, like);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rowToCertificate(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean revoke(String id, String reason) {
        String sql = "UPDATE certificates SET status = 'REVOKED', revoked_reason = ? WHERE id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, reason);
            ps.setString(2, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Certificate rowToCertificate(ResultSet rs) throws SQLException {
        Certificate c = new Certificate();
        c.id            = rs.getString("id");
        c.holderName    = rs.getString("holder_name");
        c.course        = rs.getString("course");
        c.institution   = rs.getString("institution");
        c.grade         = rs.getString("grade");
        c.issueDate     = rs.getDate("issue_date").toLocalDate();
        c.expiryDate    = rs.getDate("expiry_date").toLocalDate();
        c.status        = rs.getString("status");
        c.signature     = rs.getString("signature");
        String reason   = rs.getString("revoked_reason");
        c.revokedReason = reason != null ? reason : "";
        return c;
    }
}
