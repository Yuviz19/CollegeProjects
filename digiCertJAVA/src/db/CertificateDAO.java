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

            ps.setString(1, c.getId());
            ps.setString(2, c.getHolderName());
            ps.setString(3, c.getCourse());
            ps.setString(4, c.getInstitution());
            ps.setString(5, c.getGrade());
            ps.setDate(6, Date.valueOf(c.getIssueDate()));
            ps.setDate(7, Date.valueOf(c.getExpiryDate()));
            ps.setString(8, c.getStatus());
            ps.setString(9, c.getSignature());
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
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

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
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rowToCertificate(rs) : null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Certificate> findAll() {
        List<Certificate> list = new ArrayList<>();
        String sql = "SELECT * FROM certificates ORDER BY issue_date DESC";

        try (Connection con = DB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

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
                + "WHERE id LIKE ? OR holder_name LIKE ? OR course LIKE ? OR institution LIKE ? "
                + "ORDER BY issue_date DESC";

        String like = "%" + text + "%";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rowToCertificate(rs));
                }
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
        c.setId(rs.getString("id"));
        c.setHolderName(rs.getString("holder_name"));
        c.setCourse(rs.getString("course"));
        c.setInstitution(rs.getString("institution"));
        c.setGrade(rs.getString("grade"));
        c.setIssueDate(rs.getDate("issue_date").toLocalDate());
        c.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
        c.setStatus(rs.getString("status"));
        c.setSignature(rs.getString("signature"));
        String reason = rs.getString("revoked_reason");
        c.setRevokedReason(reason != null ? reason : "");
        return c;
    }
}
