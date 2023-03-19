package org.example;

import org.example.Course;
import org.example.Course_Off;
import org.example.DB_connect;
import org.example.PreReq;
import org.example.Professor;
import org.example.Student;
//import com.databse.st_login;
import org.example.Main;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MainMethodTest {
    private static Connection conn;

    @BeforeAll
    public static void setUp() throws SQLException {
        conn = DB_connect.connect();
        Main.pre_run(conn);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        conn.close();
    }

    public void change(Student st) throws SQLException {
        String sql = "DELETE FROM st_creds WHERE st_entry_no = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, st.getEntryNo());
            statement.executeUpdate();
            System.out.println("Student removed from database!");

            // drop department_entry_no_name table
            String tableName = st.getTableName(); //department + "_" + entry_no + "_" + name;
            String dropTableSql = "DROP TABLE IF EXISTS " + tableName;
            PreparedStatement dropTableStatement = conn.prepareStatement(dropTableSql);
            dropTableStatement.executeUpdate();
            System.out.println("Table " + tableName + " dropped.");
        }
    }

    @Test
    public void testSuc() throws SQLException {
        int a = Main.suc_msg(false);
        assertEquals(a,1);
        a=Main.suc_msg(true);
        assertEquals(a,1);
    }

    @Test
    public void uploadTest() throws SQLException {
        String s = "C:\\Users\\vishn\\Downloads\\sheet-2.csv";
        int a = Main.upload_grades(conn, s);
        assertEquals(a,1);
    }

    @Test
    public void transcriptTest() throws SQLException, FileNotFoundException {
        Student student = new Student("John Doe", "20210001", "CSE", 2011);
        student.save_new_st(conn);
        int a = Main.transcript(conn, student);
        assertEquals(a,1);
        student.delete_student(conn);
    }

    @Test
    public void gradesTest() throws SQLException, FileNotFoundException {
        Student student = new Student("John Doe", "20210001", "CSE", 2021);
        student.save_new_st(conn);
        int a = Main.printStGrades(conn, student.getTableName());
        assertEquals(a,1);
        student.delete_student(conn);
    }

    @Test
    public void coursesTest() throws SQLException, FileNotFoundException {
        int a = Main.printAllCourses(conn);
        assertEquals(a,1);
    }

    @Test
    public void courseOffTest() throws SQLException, FileNotFoundException {
        int a = Main.printAllCourseOff(conn);
        assertEquals(a,1);
    }

//    @Test
//    public void preTest() throws SQLException, FileNotFoundException {
//    	int a = a1.pre_run(conn);
//    	assertEquals(a,1);
//    }

    @Test
    public void gradCheck() throws SQLException, FileNotFoundException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 24);
        cour.createCourse(conn);
        Course cour2 = new Course("MP299", "Test Course", 1, 0, 1, 2, 24);
        cour2.createCourse(conn);
        Course cour3 = new Course("LL299", "Test Course", 1, 0, 1, 2, 24);
        cour3.createCourse(conn);
        Course cour4 = new Course("MM299", "Test Course", 1, 0, 1, 2, 24);
        cour4.createCourse(conn);
        Course cour7 = new Course("CC299", "Test Course", 1, 0, 1, 2, 24);
        cour7.createCourse(conn);
        Course cour5 = Course.get_course(conn, "BTP1");
        Course cour6 = Course.get_course(conn, "BTP2");
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff0 = new Course_Off("MP299", "N_125", 2021, 1, (float) 0.0, "MNC", "engineering_core");
        courseOff0.save(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 2, (float) 0.0, "MNC", "program_core");
        courseOff.save(conn);
        Course_Off courseOff1 = new Course_Off("LL299", "N_125", 2022, 1, (float) 0.0, "MNC", "program_core");
        courseOff1.save(conn);
        Course_Off courseOff2 = new Course_Off("MM299", "N_125", 2022, 2, (float) 0.0, "MNC", "program_core");
        courseOff2.save(conn);
        Course_Off courseOff3 = new Course_Off("BTP1", "N_125", 2023, 2, (float) 0.0, "MNC", "btp");
        courseOff3.save(conn);
        Course_Off courseOff4 = new Course_Off("BTP2", "N_125", 2023, 2, (float) 0.0, "MNC", "btp");
        courseOff4.save(conn);
        Course_Off courseOff6 = new Course_Off("CC299", "N_125", 2023, 1, (float) 0.0, "MNC", "engineering_core");
        courseOff6.save(conn);

        student.new_reg(conn, courseOff0.getOff_id(), 2021, 1);
        student.new_reg(conn, courseOff.getOff_id(), 2021, 2);
        student.new_reg(conn, courseOff1.getOff_id(), 2022, 1);
        student.new_reg(conn, courseOff2.getOff_id(), 2022, 2);
        student.new_reg(conn, courseOff6.getOff_id(), 2023, 1);
        student.new_reg(conn, courseOff3.getOff_id(), 2023, 2);
        student.new_reg(conn, courseOff4.getOff_id(), 2023, 2);

        String sql = "UPDATE " + student.getTableName() + " SET grade = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, 9);
            statement.executeUpdate();
        }
        Main.printStGrades(conn, student.getTableName());
        Main.transcript(conn, student);
        Main.printAllCourseOff(conn);
        System.out.println(Main.isGraduate(conn, student)+"vfgb");
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        Course_Off.delete(conn, courseOff0.getOff_id());
        Course_Off.delete(conn, courseOff1.getOff_id());
        Course_Off.delete(conn, courseOff2.getOff_id());
        Course_Off.delete(conn, courseOff3.getOff_id());
        Course_Off.delete(conn, courseOff4.getOff_id());
        Course_Off.delete(conn, courseOff6.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "CC299");
        Course.deleteCourse(conn, "NP299");
        Course.deleteCourse(conn, "LL299");
        Course.deleteCourse(conn, "MM299");
        Course.deleteCourse(conn, "MP299");
    }
}

