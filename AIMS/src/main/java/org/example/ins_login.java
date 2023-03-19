package org.example;
import java.sql.*;

public class ins_login {
    private String user_id;
    private String password;

    public ins_login(String user_id, String password) {
        this.user_id = user_id;
        this.password = password;
    }

    public String getUserId() {
        return user_id;
    }

    public void insert(Connection conn) throws SQLException {
        String sql = "INSERT INTO instructor (user_id, password) VALUES (?, ?)";
        try(PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, user_id);
            statement.setString(2, password);
            statement.executeUpdate();
            System.out.printf("New instructor %s added to database!\n",user_id);
        } catch (SQLException e) {
            System.out.println("Error adding new instructor to database: " + e.getMessage());
            throw e;
        }
    }

    public void delete(Connection conn) throws SQLException {
        String sql = "DELETE FROM instructor WHERE user_id = ?";
        try(PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, user_id);
            statement.executeUpdate();
            System.out.println("Instructor " + user_id + " deleted from database!");
        } catch (SQLException e) {
            System.out.println("Error deleting instructor " + user_id + " from database: " + e.getMessage());
            throw e;
        }
    }

    public void update(Connection conn, String newPassword) throws SQLException {
        String sql = "UPDATE instructor SET password = ? WHERE user_id = ?";
        try(PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, newPassword);
            statement.setString(2, user_id);
            statement.executeUpdate();
            System.out.println("Password updated for instructor " + user_id + " in database!");
        } catch (SQLException e) {
            System.out.println("Error updating password for instructor " + user_id + " in database: " + e.getMessage());
            throw e;
        }
    }

    public boolean checkCredentials(Connection conn) throws SQLException {
        String sql = "SELECT * FROM instructor WHERE user_id = ? AND password = ? ";
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
            System.out.println("Error checking instructor login in database: " + e.getMessage());
            throw e;
        }
    }
}
