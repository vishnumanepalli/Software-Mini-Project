package org.example;

import java.sql.*;

import org.junit.jupiter.api.*;

import org.example.Course;
import org.example.DB_connect;
import org.example.PreReq;

public class PreReqTest {

    private static Connection conn;

    @BeforeAll
    public static void setUp() throws SQLException {
        conn = DB_connect.connect();
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        cour = new Course("CP901", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        Course.deleteCourse(conn, "NP299");
        Course.deleteCourse(conn, "CP901");
        conn.close();
    }

    @BeforeEach
    public void clearTable() throws SQLException {
        String clearTableSql = "DELETE FROM pre_req";
        try (PreparedStatement statement = conn.prepareStatement(clearTableSql)) {
            statement.executeUpdate();
        }
    }

    @Test
    public void testInsert() throws SQLException {
        // create a new pre-requisite and insert it into the database
        PreReq preReq = new PreReq("NP299", "CP901");
        preReq.insert(conn);

        // retrieve the newly inserted pre-requisite from the database and verify its attributes
        String sql = "SELECT * FROM pre_req WHERE course_code = ? AND pre_req_code = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, preReq.getCourseCode());
            statement.setString(2, preReq.getPreReqCode());
            try (ResultSet result = statement.executeQuery()) {
                Assertions.assertTrue(result.next());
                Assertions.assertEquals(preReq.getCourseCode(), result.getString("course_code"));
                Assertions.assertEquals(preReq.getPreReqCode(), result.getString("pre_req_code"));
                Assertions.assertFalse(result.next());
            }
        }
        preReq.delete(conn);
    }

    @Test
    public void correctDelete() throws SQLException {
        // create a new pre-requisite and insert it into the database
        PreReq preReq = new PreReq("NP299", "CP901");
        preReq.insert(conn);

        // delete the pre-requisite from the database and verify that it has been deleted
        preReq.delete(conn);
        String sql = "SELECT * FROM pre_req WHERE course_code = ? AND pre_req_code = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, preReq.getCourseCode());
            statement.setString(2, preReq.getPreReqCode());
            try (ResultSet result = statement.executeQuery()) {
                Assertions.assertFalse(result.next());
            }
        }
    }
    @Test
    public void wrongDelete() throws SQLException {
        // create a new pre-requisite and insert it into the database
        PreReq preReq = new PreReq("NP299", "CP901");
        preReq.insert(conn);
        PreReq preReq2 = new PreReq("CP901", "NP901");
        // delete the pre-requisite from the database and verify that it has been deleted
        preReq2.delete(conn);
        String sql = "SELECT * FROM pre_req WHERE course_code = ? AND pre_req_code = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, preReq.getCourseCode());
            statement.setString(2, preReq.getPreReqCode());
            try (ResultSet result = statement.executeQuery()) {
                Assertions.assertTrue(result.next());
            }
        }
        preReq.delete(conn);
    }
}