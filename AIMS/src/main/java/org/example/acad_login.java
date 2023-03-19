package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class acad_login {
    private String user_id;
    private String password;

    public acad_login(String user_id, String password) {
        this.user_id = user_id;
        this.password = password;
    }

    public void insert(Connection conn) throws SQLException {
        String sql = "INSERT INTO acad_office(user_id, password) VALUES (?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, user_id);
            statement.setString(2, password);
            statement.executeUpdate();
            System.out.println("New user added to acad_office table!");
        } catch (SQLException e) {
            System.out.println("Error adding new user to acad_office table: " + e.getMessage());
            throw e;
        }
    }

    public void delete(Connection conn) throws SQLException {
        String sql = "DELETE FROM acad_office WHERE user_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, user_id);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("User deleted from acad_office table!");
            } else {
                System.out.println("No user found with the given user_id in acad_office table.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting user from acad_office table: " + e.getMessage());
            throw e;
        }
    }

    public void update(Connection conn, String newPassword) throws SQLException {
        String sql = "UPDATE acad_office SET password = ? WHERE user_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, newPassword);
            statement.setString(2, user_id);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Password updated for user in acad_office table!");
            } else {
                System.out.println("No user found with the given user_id in acad_office table.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating password for user in acad_office table: " + e.getMessage());
            throw e;
        }
    }

    public boolean checkCredentials(Connection conn) throws SQLException {
        String sql = "SELECT * FROM acad_office WHERE user_id = ? AND password = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, user_id);
            statement.setString(2, password);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking acad_office login in database: " + e.getMessage());
            throw e;
        }
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

//    public String getPassword() {
//        return password;
//    }

    public void setPassword(String password) {
        this.password = password;
    }
}
