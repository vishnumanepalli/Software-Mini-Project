package org.example;
import java.sql.*;
//import java.util.*;

public class Course {
    private String code;
    private String title;
    private int lec;
    private int tut;
    private int per;
    private int s_hrs;
    private int credits;

    public Course(String code, String title, int lec, int tut, int per, int s_hrs, int credits) {
        this.code = code;
        this.title = title;
        this.lec = lec;
        this.tut = tut;
        this.per = per;
        this.s_hrs = s_hrs;
        this.credits = credits;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

//    public void setTitle(String title) {
//        this.title = title;
//    }

    public int getLec() {
        return lec;
    }

//    public void setLec(int lec) {
//        this.lec = lec;
//    }

    public int getTut() {
        return tut;
    }

//    public void setTut(int tut) {
//        this.tut = tut;
//    }

    public int getPer() {
        return per;
    }

//    public void setPer(int per) {
//        this.per = per;
//    }

    public int getS_hrs() {
        return s_hrs;
    }

//    public void setS_hrs(int s_hrs) {
//        this.s_hrs = s_hrs;
//    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
    public static Course get_course(Connection conn, String courseCode) throws SQLException {
        String sql = "SELECT * FROM course WHERE code = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, courseCode);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                Course course = new Course(
                        result.getString("code"),
                        result.getString("title"),
                        result.getInt("lec"),
                        result.getInt("tut"),
                        result.getInt("per"),
                        result.getInt("s_hrs"),
                        result.getInt("credits"));
                return course;
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving course: " + e.getMessage());
            throw e;
        }
    }

    public int updateCourse(Connection conn) throws SQLException {
        String sql = "UPDATE course SET title = ?, lec = ?, tut = ?, per = ?, s_hrs = ?, credits = ? WHERE code = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setInt(2, lec);
            pstmt.setInt(3, tut);
            pstmt.setInt(4, per);
            pstmt.setInt(5, s_hrs);
            pstmt.setInt(6, credits);
            pstmt.setString(7, code);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Course updated successfully.");
                return 1;
            } else {
                System.out.println("No course found with code: " + code);
                return 0;
            }
        } catch (SQLException e) {
            System.out.println("Error while updating course_cat information in database: " + e.getMessage());
            throw e;
        }
    }

    public static int deleteCourse(Connection conn, String code) throws SQLException {
        String sql = "DELETE FROM course WHERE code = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Course deleted successfully.");
                return 1;
            } else {
                System.out.println("No course found");
                return 0;
            }
        }catch (SQLException e) {
            System.out.println("Error while deleting course information from database: " + e.getMessage());
            throw e;
        }
    }


    public void createCourse(Connection conn) throws SQLException {
        String sql = "INSERT INTO course (code, title, lec, tut, per, s_hrs, credits) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, title);
            pstmt.setInt(3, lec);
            pstmt.setInt(4, tut);
            pstmt.setInt(5, per);
            pstmt.setInt(6, s_hrs);
            pstmt.setInt(7, credits);
            pstmt.executeUpdate();
            System.out.printf("New course %s added successfully.\n",code);
        } catch (SQLException e) {
            System.out.println("Error adding new course to database: " + e.getMessage());
            throw e;
        }
    }
}
