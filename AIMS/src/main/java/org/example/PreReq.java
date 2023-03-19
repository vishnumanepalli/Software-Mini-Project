package org.example;
import java.sql.*;
//import java.util.*;

public class PreReq {
    private String courseCode;
    private String preReqCode;

    public PreReq(String courseCode, String preReqCode) {
        this.courseCode = courseCode;
        this.preReqCode = preReqCode;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getPreReqCode() {
        return preReqCode;
    }

    public void setPreReqCode(String preReqCode) {
        this.preReqCode = preReqCode;
    }

    public void insert(Connection conn) throws SQLException {
        String sql = "INSERT INTO pre_req(course_code, pre_req_code) VALUES (?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, courseCode);
            statement.setString(2, preReqCode);
            statement.executeUpdate();
            System.out.println("New pre-requisite added to database!");
        } catch (SQLException e) {
            System.out.println("Error adding new pre-requisite to database: " + e.getMessage());
            throw e;
        }
    }

    public void delete(Connection conn) throws SQLException {
        String sql = "DELETE FROM pre_req WHERE course_code = ? AND pre_req_code = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, courseCode);
            statement.setString(2, preReqCode);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Pre-requisite deleted from database.");
            } else {
                System.out.println("Pre-requisite not found in database.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting pre-requisite from database: " + e.getMessage());
            throw e;
        }
    }
}
