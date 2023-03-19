package org.example;

import org.example.Course;
import org.example.Course_Off;
import org.example.DB_connect;
import org.example.PreReq;
import org.example.Professor;
import org.example.Student;
//import com.databse.st_login;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StudentTest {
    private static Connection conn;

    @BeforeAll
    public static void setUp() throws SQLException {
        conn = DB_connect.connect();
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
    public void testSaveNewStudent() throws SQLException {
        Student student = new Student("John Doe", "20210001", "CSE", 2021);
        student.save_new_st(conn);
        String sql = "SELECT * FROM st_creds WHERE st_entry_no = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, student.getEntryNo());
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                assertEquals(student.getName(), result.getString("st_name"));
                assertEquals(student.getDepartment(), result.getString("dept"));
                assertEquals(student.getBatch(), result.getInt("batch"));
            } else {
//                throw new SQLException("No rows found");
            }
        }
        change(student);
    }

    @Test
    public void correctGetStudent() throws SQLException {
        Student student = new Student("Jane Doe", "20210002", "CSE", 2021);
        student.save_new_st(conn);

        Student retrievedStudent = Student.get_student(conn, student.getEntryNo());

        assertNotNull(retrievedStudent);
        assertEquals(student.getName(), retrievedStudent.getName());
        assertEquals(student.getDepartment(), retrievedStudent.getDepartment());
        assertEquals(student.getBatch(), retrievedStudent.getBatch());
        change(student);
    }

    @Test
    public void wrongGetStudent() throws SQLException {
        Student student = new Student("Jane Doe", "20210002", "CSE", 2021);
        student.save_new_st(conn);

        Student retrievedStudent = Student.get_student(conn, "asdfgh");

        assertNull(retrievedStudent);
        change(student);
    }

    @Test
    public void testUpdateStudent() throws SQLException {
        Student student = new Student("John Smith", "20210003", "MNC", 2021);
        student.save_new_st(conn);

        student.setName("John Smith Jr.");
        student.setDepartment("CSE");
        student.setBatch(2022);
        student.update_student(conn);

        Student retrievedStudent = Student.get_student(conn, student.getEntryNo());

        assertNotNull(retrievedStudent);
        assertEquals(student.getName(), retrievedStudent.getName());
        assertEquals(student.getDepartment(), retrievedStudent.getDepartment());
        assertEquals(student.getBatch(), retrievedStudent.getBatch());
        change(student);
    }

    @Test
    public void testDeleteStudent() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);

        student.delete_student(conn);

        String sql = "SELECT * FROM st_creds WHERE st_entry_no = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, student.getEntryNo());
            ResultSet result = statement.executeQuery();
            assertNotNull(result);
        }
    }

    @Test
    public void testNewRegSuccess() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);


        student.new_reg(conn, courseOff.getOff_id(), 2021, 1);

        String sql = "SELECT * FROM " + student.getTableName() + " WHERE off_id = ? ";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, courseOff.getOff_id());
            ResultSet result = statement.executeQuery();
//                assertFalse(result.next());
            assertNotNull(result);
        }
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
    }

    @Test
    public void testNewRegSuccess_2() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Course cour2 = new Course("MP299", "Test Course", 1, 0, 1, 2, 7);
        cour2.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff0 = new Course_Off("MP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff0.save(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2022, 1, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);


        student.new_reg(conn, courseOff0.getOff_id(), 2021, 1);
        student.new_reg(conn, courseOff0.getOff_id(), 2022, 1);
        String sql = "SELECT * FROM " + student.getTableName() + " WHERE off_id = ? ";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, courseOff0.getOff_id());
            ResultSet result = statement.executeQuery();
//                assertFalse(result.next());
            assertNotNull(result);
        }
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        Course_Off.delete(conn, courseOff0.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
        Course.deleteCourse(conn, "MP299");
    }

    @Test
    public void newRegFail2() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);


        student.new_reg(conn, courseOff.getOff_id(), 2021, 1);
        int val = student.new_reg(conn, courseOff.getOff_id(), 2021, 1);
        assertEquals(2,val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
    }

    @Test
    public void newRegFail3() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);


        int val = student.new_reg(conn, 1054, 2021, 1);
        assertEquals(3,val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
    }

    @Test
    public void newRegFail4() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);


        int val = student.new_reg(conn, courseOff.getOff_id(), 2023, 1);
        assertEquals(4,val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
    }

    @Test
    public void newRegFail5() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 1, (float) 2.0, "MNC", "elective");
        courseOff.save(conn);

        int val = student.new_reg(conn, courseOff.getOff_id(), 2021, 1);
        assertEquals(5,val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
    }

    @Test
    public void newRegFail6() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Course cour2 = new Course("NL299", "Test Course", 1, 0, 1, 2, 3);
        cour2.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        PreReq preReq = new PreReq("NP299", "NL299");
        preReq.insert(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);


        student.new_reg(conn, courseOff.getOff_id(), 2021, 1);
        int val = student.new_reg(conn, courseOff.getOff_id(), 2021, 1);
        assertEquals(6,val);
        preReq.delete(conn);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
        Course.deleteCourse(conn, "NL299");
    }

    @Test
    public void newRegFail7() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2020);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 2);
        cour.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);

        int val = student.new_reg(conn, courseOff.getOff_id(), 2021, 1);
        assertEquals(7,val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
    }

    @Test
    public void newRegFail7_2() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 2);
        cour.createCourse(conn);
        Course cour2 = new Course("MP299", "Test Course", 1, 0, 1, 2, 24);
        cour2.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff0 = new Course_Off("MP299", "N_125", 2021, 2, (float) 0.0, "MNC", "elective");
        courseOff0.save(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 2, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);

        student.new_reg(conn, courseOff0.getOff_id(), 2021, 2);
        int val = student.new_reg(conn, courseOff.getOff_id(), 2021, 2);
        assertEquals(7,val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        Course_Off.delete(conn, courseOff0.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
        Course.deleteCourse(conn, "MP299");
    }

    @Test
    public void newRegFail7_3() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 2);
        cour.createCourse(conn);
        Course cour2 = new Course("MP299", "Test Course", 1, 0, 1, 2, 24);
        cour2.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff0 = new Course_Off("MP299", "N_125", 2022, 2, (float) 0.0, "MNC", "elective");
        courseOff0.save(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2022, 2, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);

        student.new_reg(conn, courseOff0.getOff_id(), 2022, 2);
        int val = student.new_reg(conn, courseOff.getOff_id(), 2022, 2);
        assertEquals(7,val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        Course_Off.delete(conn, courseOff0.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
        Course.deleteCourse(conn, "MP299");
    }

    @Test
    public void newRegFail8() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 25);
        cour.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);

        int val = student.new_reg(conn, courseOff.getOff_id(), 2021, 1);
        assertEquals(7,val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
    }

    @Test
    public void newRegFail9() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);
        courseOff.updatePermit(conn);

        student.new_reg(conn, courseOff.getOff_id(), 2021, 1);
        int val = student.new_reg(conn, courseOff.getOff_id(), 2021, 1);
        assertEquals(9,val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
    }

    @Test
    public void newRegFail10() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);

        int val = student.new_reg(conn, courseOff.getOff_id(), 2021, 1);
        assertEquals(10,val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
    }

    @Test
    public void newRegFail12() throws SQLException {
        Student student = new Student("James Smith", "20210004", "CSE", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);

        int val = student.new_reg(conn, courseOff.getOff_id(), 2021, 1);
        assertEquals(12,val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
    }

    @Test
    public void deRegTest() throws SQLException {
        Student student = new Student("James Smith", "20210005", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);


        student.new_reg(conn, courseOff.getOff_id(), 2021, 1);
        int val = student.dereg(conn, courseOff.getOff_id());
        assertEquals(3, val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
    }

    @Test
    public void deRegFail1() throws SQLException {
        Student student = new Student("James Smith", "20210005", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);


        student.new_reg(conn, courseOff.getOff_id(), 2021, 1);
        int val = student.dereg(conn, 1589);
        assertEquals(1, val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
    }

    @Test
    public void deRegFail2() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);

        student.new_reg(conn, courseOff.getOff_id(), 2021, 1);
        courseOff.updatePermit(conn);
        int val = student.dereg(conn, courseOff.getOff_id());
        assertEquals(2,val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
    }

    @Test
    public void deRegFail4() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 3);
        cour.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff.save(conn);
        Course_Off courseOff2 = new Course_Off("NP299", "N_125", 2021, 1, (float) 0.0, "MNC", "program_core");
        courseOff2.save(conn);

        student.new_reg(conn, courseOff.getOff_id(), 2021, 1);
        int val = student.dereg(conn, courseOff2.getOff_id());
        assertEquals(4,val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        Course_Off.delete(conn, courseOff2.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
    }
    @Test
    public void cgpaCheck() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 2);
        cour.createCourse(conn);
        Course cour2 = new Course("MP299", "Test Course", 1, 0, 1, 2, 5);
        cour2.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff0 = new Course_Off("MP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff0.save(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 2, (float) 5.0, "MNC", "elective");
        courseOff.save(conn);

        int val1 = student.new_reg(conn, courseOff0.getOff_id(), 2021, 1);
        assertEquals(10, val1);

        // update grades to 9
        String sql = "UPDATE " + student.getTableName() + " SET grade = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, 9);
            statement.executeUpdate();
        }
        int val = student.new_reg(conn, courseOff.getOff_id(), 2021, 2);
        assertEquals(10,val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        Course_Off.delete(conn, courseOff0.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
        Course.deleteCourse(conn, "MP299");
    }

    @Test
    public void sgpaCheck() throws SQLException {
        Student student = new Student("James Smith", "20210004", "MNC", 2021);
        student.save_new_st(conn);
        Course cour = new Course("NP299", "Test Course", 1, 0, 1, 2, 2);
        cour.createCourse(conn);
        Course cour2 = new Course("MP299", "Test Course", 1, 0, 1, 2, 5);
        cour2.createCourse(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        Course_Off courseOff0 = new Course_Off("MP299", "N_125", 2021, 1, (float) 0.0, "MNC", "elective");
        courseOff0.save(conn);
        Course_Off courseOff = new Course_Off("NP299", "N_125", 2021, 2, (float) 5.0, "MNC", "elective");
        courseOff.save(conn);

        int val1 = student.new_reg(conn, courseOff0.getOff_id(), 2021, 1);
        assertEquals(10, val1);

        // update grades to 9
        String sql = "UPDATE " + student.getTableName() + " SET grade = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, 9);
            statement.executeUpdate();
        }
        double val = student.calculate_sgpa(conn, 2021, 1);
        assertEquals(9.0,val);
        change(student);
        Course_Off.delete(conn, courseOff.getOff_id());
        Course_Off.delete(conn, courseOff0.getOff_id());
        p1.delete_professor(conn);
        Course.deleteCourse(conn, "NP299");
        Course.deleteCourse(conn, "MP299");
    }
}

