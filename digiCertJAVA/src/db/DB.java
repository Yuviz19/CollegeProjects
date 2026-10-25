package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.Util;

public class DB {

    private static final String URL =
            "jdbc:mysql://localhost:3306/digiCertJAVA_db?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean login(String username, String password) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getString("password_hash").equals(Util.sha256(password));

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
