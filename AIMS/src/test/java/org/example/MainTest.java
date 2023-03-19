package org.example;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.example.DB_connect;
import org.example.Professor;
import org.example.Student;
import org.example.Main;
import org.example.acad_login;
import org.example.ins_login;
import org.example.st_login;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {
    private static Connection conn;

    @BeforeAll
    public static void setUp() throws SQLException {
        conn = DB_connect.connect();
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        conn.close();
    }

    @Test
    public void testMainWithStudentLogin() throws Exception {
        Student student = new Student("Jane Doe", "20210002", "CSE", 2021);
        student.save_new_st(conn);
        st_login st1 = new st_login("20210002", "iitropar");
        st1.insert(conn);
        Professor p1 = new Professor("testname", "N_125", "CSE");
        p1.save_new_pr(conn);
        ins_login it4 = new ins_login("N_125", "iitropar");
        it4.insert(conn);
        // Prepare test input
        String input = "1\n" + // select student login
                "20210002\n" + // enter user ID
                "iitropar\n" + // enter password
                "0\n"+
                "1\n"+
                "2\n"+
                "3\n"+
                "1\n"+
                "2020\n"+
                "2\n"+
                "4\n"+
                "3\n"
                ;
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Prepare expected output
        String expectedOutput = "Connection OK\n"+
                "1. Student\n" +
                "2. Instructor\n" +
                "3. Academic Officer\n" +
                "Select login type:" +
                "Enter user ID:" +
                "Enter password:" +
                "Login successful!\n"+
                "0. Graduation Check\n" +
                "1. View Grades\n" +
                "2. Get current CGPA\n" +
                "3. Register Course\n" +
                "4. Deregister Course\n" +
                "Select operation type:";

        // Redirect output to a stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // Run the test
        Main.main(new String[0]);

        // Check the output
//        assertEquals(expectedOutput, out.toString());
        st1.delete(conn);
        student.delete_student(conn);
        it4.delete(conn);
        p1.delete_professor(conn);
    }
}
