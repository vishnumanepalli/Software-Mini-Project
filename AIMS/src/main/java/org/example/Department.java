package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Department {
    private String dep;
    private String dep_name;

    public Department(String dep, String dep_name) {
        this.dep = dep;
        this.dep_name = dep_name;
    }

    public void insert(Connection conn) throws SQLException {
        String sql = "INSERT INTO department(dep, dep_name) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dep);
            pstmt.setString(2, dep_name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public void delete(Connection conn) throws SQLException {
        String sql = "DELETE FROM department WHERE dep = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dep);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public boolean check(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM department WHERE dep = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dep);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt("count") > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
