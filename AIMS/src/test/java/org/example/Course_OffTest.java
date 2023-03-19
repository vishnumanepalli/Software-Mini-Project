package org.example;

import org.junit.jupiter.api.*;

import org.example.Course;
import org.example.Course_Off;
import org.example.DB_connect;
import org.example.Professor;

import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.DriverManager;
import java.sql.SQLException;

public class Course_OffTest {
    private static Connection conn;

    @BeforeAll
    public static void setUp() throws SQLException {
        conn = DB_connect.connect();
        Course cour = new Course("XY987", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Professor p1 = new Professor("Vishnu","N_T11", "NNE");
        p1.save_new_pr(conn);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        Course.deleteCourse(conn, "XY987");
        Professor p1 = new Professor("N_T11","N_T11", "NNE");
        p1.delete_professor(conn);
        conn.close();
    }

    public static void change(String s) throws SQLException {
        String sql = "DELETE FROM course_off WHERE code = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, s);
            statement.executeUpdate();
        }
    }

    @Test
    public void CorrectCreds() throws SQLException {
        Course_Off courseOff = new Course_Off("XY987", "N_T11", 2023, 1, 3.0f, "NNE", "elective");
        courseOff.save(conn);
        Course_Off retrievedCourseOff = Course_Off.getByOffId(conn, courseOff.getOff_id());
        Assertions.assertEquals(courseOff.getCode(), retrievedCourseOff.getCode());
        Assertions.assertEquals(courseOff.getProf(), retrievedCourseOff.getProf());
        Assertions.assertEquals(courseOff.getYear(), retrievedCourseOff.getYear());
        Assertions.assertEquals(courseOff.getSem(), retrievedCourseOff.getSem());
        Assertions.assertEquals(courseOff.getCg_req(), retrievedCourseOff.getCg_req());
        Assertions.assertEquals(courseOff.getDep(), retrievedCourseOff.getDep());
        Assertions.assertEquals(courseOff.getType(), retrievedCourseOff.getType());

        change("XY987");
    }

    @Test
    public void testGetByOffId() throws SQLException {
        Course_Off courseOff = new Course_Off("XY987", "N_T11", 2023, 1, 3.0f, "NNE", "elective");
        courseOff.save(conn);

        // retrieve the course offering using getByOffId and verify its attributes
        Course_Off retrievedCourseOff = Course_Off.getByOffId(conn, courseOff.getOff_id());
        Assertions.assertEquals(courseOff.getCode(), retrievedCourseOff.getCode());
        Assertions.assertEquals(courseOff.getProf(), retrievedCourseOff.getProf());
        Assertions.assertEquals(courseOff.getYear(), retrievedCourseOff.getYear());
        Assertions.assertEquals(courseOff.getSem(), retrievedCourseOff.getSem());
        Assertions.assertEquals(courseOff.getCg_req(), retrievedCourseOff.getCg_req());
        Assertions.assertEquals(courseOff.getDep(), retrievedCourseOff.getDep());
        Assertions.assertEquals(courseOff.getType(), retrievedCourseOff.getType());

        change("XY987");
    }

    @Test
    public void correctUpdatePermit() throws SQLException {
        Course_Off courseOff = new Course_Off("XY987", "N_T11", 2023, 1, 3.0f, "NNE", "elective");
        courseOff.save(conn);
        int val = courseOff.updatePermit(conn);

//        Course_Off retrievedCourseOff = Course_Off.getByOffId(conn, courseOff.getOff_id());
        Assertions.assertEquals(0, val);

        change("XY987");
    }
    @Test
    public void wrongUpdatePermit() throws SQLException {
        Course_Off courseOff = new Course_Off("XY987", "N_T11", 2023, 1, 3.0f, "NNE", "elective");
        courseOff.save(conn);
        courseOff.setOff_id(99999);
        int val = courseOff.updatePermit(conn);

//        Course_Off retrievedCourseOff = Course_Off.getByOffId(conn, courseOff.getOff_id());
        Assertions.assertEquals(1, val);

        change("XY987");
    }

    @Test
    public void correctgetPermit() throws SQLException {
        Course_Off courseOff = new Course_Off("XY987", "N_T11", 2023, 1, 3.0f, "NNE", "elective");
        courseOff.save(conn);
        int val = courseOff.getPermit(conn);

//        Course_Off retrievedCourseOff = Course_Off.getByOffId(conn, courseOff.getOff_id());
        Assertions.assertEquals(0, val);

        change("XY987");
    }
    @Test
    public void wronggetPermit() throws SQLException {
        Course_Off courseOff = new Course_Off("XY987", "N_T11", 2023, 1, 3.0f, "NNE", "elective");
        courseOff.save(conn);
        courseOff.setOff_id(99999);
        int val = courseOff.getPermit(conn);

//        Course_Off retrievedCourseOff = Course_Off.getByOffId(conn, courseOff.getOff_id());
        Assertions.assertEquals(-1, val);

        change("XY987");
    }

    @Test
    public void testDelete() throws SQLException {
        Course_Off courseOff = new Course_Off("XY987", "N_T11", 2023, 1, 3.0f, "NNE", "elective");
        courseOff.save(conn);

        // delete the course offering and verify that it has been deleted
        Course_Off.delete(conn, courseOff.getOff_id());
        Course_Off retrievedCourseOff = Course_Off.getByOffId(conn, courseOff.getOff_id());
        Assertions.assertNull(retrievedCourseOff);
    }
}
