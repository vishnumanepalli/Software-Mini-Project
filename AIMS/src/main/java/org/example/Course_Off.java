package org.example;
import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;

public class Course_Off {
    private int off_id;
    private String code;
    private String prof;
    private int year;
    private int sem;
    private float cg_req;
    private String dep;
    private String type;

    public Course_Off(String code, String prof, int year, int sem, float cg_req, String dep, String type) {
        this.code = code;
        this.prof = prof;
        this.year = year;
        this.sem = sem;
        this.cg_req = cg_req;
        this.dep = dep;
        this.type = type;
    }

    public int getOff_id() {
        return off_id;
    }

    public void setOff_id(int a) {
        this.off_id = a;
    }

    public String getCode() {
        return code;
    }

    public String getProf() {
        return prof;
    }

    public int getYear() {
        return year;
    }

    public int getSem() {
        return sem;
    }

    public float getCg_req() {
        return cg_req;
    }

    public String getDep() {
        return dep;
    }

    public String getType() {
        return type;
    }

    public int getPermit(Connection conn) throws SQLException {
        String sql = "SELECT * FROM course_off WHERE off_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, off_id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                int per = result.getInt("permit");
                return per;
            } else {
                System.out.println("No course offering found with given off_id.");
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving course offering by off_id: " + e.getMessage());
            throw e;
        }
    }

    public int updatePermit(Connection conn) throws SQLException {
        String sql = "UPDATE course_off SET permit = ? WHERE off_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, 1);
        pstmt.setInt(2, off_id);
        int rowsUpdated = pstmt.executeUpdate();
        if (rowsUpdated == 0) {
            System.out.println("No row(s) updated.");
            return 1;
        } else {
            System.out.println(rowsUpdated + " rows updated.");
            return 0;
        }
    }

    public void save(Connection conn) throws SQLException {
        String sql = "INSERT INTO course_off(code, prof, year, sem, cg_req, dep, type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, code);
            statement.setString(2, prof);
            statement.setInt(3, year);
            statement.setInt(4, sem);
            statement.setFloat(5, cg_req);
            statement.setString(6, dep);
            statement.setString(7, type);
            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                off_id = rs.getInt(1);
            }
            System.out.println("New course offering added to database!");
        } catch (SQLException e) {
            System.out.println("Error adding new course offering to database: " + e.getMessage());
            throw e;
        }
    }

    public static void delete(Connection conn, int off_id) throws SQLException {
        String sql = "DELETE FROM course_off WHERE off_id = ?";
        try(PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, off_id);
            statement.executeUpdate();
            System.out.println("Course offering deleted from database!");
        } catch (SQLException e) {
            System.out.println("Error deleting course offering from database: " + e.getMessage());
            throw e;
        }
    }

    public static Course_Off getByOffId(Connection conn, int off_id) throws SQLException {
        String sql = "SELECT * FROM course_off WHERE off_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, off_id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                Course_Off courseOff = new Course_Off(result.getString("code"),
                        result.getString("prof"),
                        result.getInt("year"),
                        result.getInt("sem"),
                        result.getFloat("cg_req"),
                        result.getString("dep"),
                        result.getString("type"));
                courseOff.off_id = result.getInt("off_id");
                System.out.println("Course offering retrieved from database!");
                return courseOff;
            } else {
                System.out.println("No course offering found with given off_id.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving course offering by off_id: " + e.getMessage());
            throw e;
        }
    }
}
