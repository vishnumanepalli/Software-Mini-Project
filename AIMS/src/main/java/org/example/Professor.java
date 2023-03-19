package org.example;
import java.sql.*;

public class Professor {
    private String name;
    private String id;
    private String department;

    public Professor(String name, String id, String department) {
        this.name = name;
        this.id = id;
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

//    public void setId(String id) {
//        this.id = id;
//    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public static Professor get_professor(Connection conn, String pr_id) throws SQLException {
        String sql = "SELECT * FROM pr_creds WHERE pr_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, pr_id);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String name = result.getString("pr_name");
                String department = result.getString("dept");
                Professor professor = new Professor(name, pr_id, department);
                return professor;
            } else {
                System.out.println("No professor found with ID " + pr_id);
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error while getting professor information from database: " + e.getMessage());
            throw e;
        }
    }

    public void update_professor(Connection conn) throws SQLException {
        String sql = "UPDATE pr_creds SET pr_name = ?, dept = ? WHERE pr_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, department);
            statement.setString(3, id);
            statement.executeUpdate();
            System.out.println("Professor information updated in database!");
        } catch (SQLException e) {
            System.out.println("Error while updating professor information in database: " + e.getMessage());
            throw e;
        }
    }

    public void delete_professor(Connection conn) throws SQLException {
        String sql = "DELETE FROM pr_creds WHERE pr_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
            System.out.println("Professor removed from database!");
        } catch (SQLException e) {
            System.out.println("Error while deleting professor from database: " + e.getMessage());
            throw e;
        }
    }

    public void save_new_pr(Connection conn) throws SQLException {
        String sql = "INSERT INTO pr_creds(pr_name, pr_id, dept) VALUES (?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, id);
            statement.setString(3, department);
            statement.executeUpdate();
            System.out.println("New professor added to database!");
        } catch (SQLException e) {
            System.out.println("Error while adding new professor to database: " + e.getMessage());
            throw e;
        }
    }
}
