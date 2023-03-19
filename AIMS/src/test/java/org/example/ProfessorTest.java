package org.example;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.example.DB_connect;
import org.example.Professor;

public class ProfessorTest {

    private Connection conn;

    @BeforeEach
    public void setUp() throws Exception {
        conn = DB_connect.connect();
    }

    @Test
    public void testSaveNewPr() throws SQLException {
        Professor professor = new Professor("John Doe", "JD123", "CSE");
        professor.save_new_pr(conn);

        Professor retrievedProf = Professor.get_professor(conn, "JD123");
        assertNotNull(retrievedProf);
        assertEquals(professor.getName(), retrievedProf.getName());
        assertEquals(professor.getId(), retrievedProf.getId());
        assertEquals(professor.getDepartment(), retrievedProf.getDepartment());
        professor.delete_professor(conn);
    }

    @Test
    public void testUpdateProfessor() throws SQLException {
        Professor professor = new Professor("John Doe", "JD123", "CSE");
        professor.save_new_pr(conn);

        professor.setName("Jane Doe");
        professor.setDepartment("EEE");
        professor.update_professor(conn);

        Professor retrievedProf = Professor.get_professor(conn, "JD123");
        assertNotNull(retrievedProf);
        assertEquals(professor.getName(), retrievedProf.getName());
        assertEquals(professor.getId(), retrievedProf.getId());
        assertEquals(professor.getDepartment(), retrievedProf.getDepartment());
        professor.delete_professor(conn);
    }

    @Test
    public void testDeleteProfessor() throws SQLException {
        Professor professor = new Professor("John Doe", "JD123", "CSE");
        professor.save_new_pr(conn);

        professor.delete_professor(conn);

        Professor retrievedProf = Professor.get_professor(conn, "JD123");
        assertNull(retrievedProf);
    }

    @AfterEach
    public void tearDown() throws Exception {
        conn.close();
    }

}
