package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.sql.Connection;
//import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.example.Course;
import org.example.DB_connect;

public class CourseTest {
    private static Connection conn;

    @BeforeEach
    public void setUp() throws SQLException {
        // Initialize database connection
        conn = DB_connect.connect();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Close database connection
        conn.close();
    }

    @Test
    public void testCreateCourse() throws SQLException {
        Course cour = new Course("AC123", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Course course = Course.get_course(conn, "AC123");
        assertEquals("AC123", course.getCode());
        assertEquals("Test Course", course.getTitle());
        assertEquals(1, course.getLec());
        assertEquals(0, course.getTut());
        assertEquals(1, course.getPer());
        assertEquals(2, course.getS_hrs());
        assertEquals(3, course.getCredits());

        Course.deleteCourse(conn, "AC123");
    }

    @Test
    public void correctGetCourse() throws SQLException {
        // Test creation of new course
        Course cour = new Course("AB123", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Course course = Course.get_course(conn, "AB123");
        assertEquals("AB123", course.getCode());
        assertEquals("Test Course", course.getTitle());
        assertEquals(1, course.getLec());
        assertEquals(0, course.getTut());
        assertEquals(1, course.getPer());
        assertEquals(2, course.getS_hrs());
        assertEquals(3, course.getCredits());
        Course.deleteCourse(conn, "AB123");
    }

    @Test
    public void wrongGetCourse() throws SQLException {
        // Test creation of new course
        Course cour = new Course("AB123", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Course course = Course.get_course(conn, "AN123");
        assertNull(course);
        Course.deleteCourse(conn, "AB123");
    }

    @Test
    public void correctUpdate() throws SQLException {
        // Test update of existing course
        Course cour = new Course("ZX101", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Course course = Course.get_course(conn, "ZX101");
        course.setCredits(4);
        int res  = course.updateCourse(conn);
        assertEquals(1, res);
        Course.deleteCourse(conn, "ZX101");
    }
    @Test
    public void falseUpdate() throws SQLException {
        // Test update of existing course
        Course cour = new Course("ZX101", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Course course = Course.get_course(conn, "ZX101");
        course.setCredits(4);
        course.setCode("HS567");
        int res  = course.updateCourse(conn);
        assertEquals(0, res);
        Course.deleteCourse(conn, "ZX101");
    }

    @Test
    public void correctDelete() throws SQLException {
        // Test deletion of existing course
        Course cour = new Course("XY112", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        int val  = Course.deleteCourse(conn, "XY112");
        assertEquals(1, val);
    }

    @Test
    public void wrongDelete() throws SQLException {
        // Test deletion of existing course
        Course cour = new Course("XY102", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        int val  = Course.deleteCourse(conn, "NN102");
        assertEquals(0, val);
        Course.deleteCourse(conn, "XY102");
    }
}
