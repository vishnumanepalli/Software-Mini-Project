package org.example;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
//import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.example.DB_connect;
import org.example.Student;
import org.example.st_login;

public class st_loginTest {

    private Connection conn;

    @BeforeEach
    public void setUp() throws SQLException {
        conn = DB_connect.connect();
        Student st = new Student("test1","s1","CSE",2020);
        st.save_new_st(conn);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        String sql1 = "DELETE FROM st_creds WHERE st_entry_no = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql1)) {
            statement.setString(1, "s1");
            statement.executeUpdate();
        }
        String sql2 = "DROP table table_s1";
        try (PreparedStatement statement = conn.prepareStatement(sql2)) {
            statement.executeUpdate();
        }
        conn.close();
    }


    public void change(st_login st) throws SQLException {
        String sql = "DELETE FROM student WHERE user_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, st.getUserId());
            statement.executeUpdate();
        }
    }

    @Test
    public void correctCreds() throws SQLException {
        st_login user = new st_login("s1", "password123");
        user.insert(conn);
        assertTrue(user.checkCredentials(conn));
        st_login user2 = new st_login("s1", "wrongpass");
        assertFalse(user2.checkCredentials(conn));
        change(user);
    }

    @Test
    public void testDelete() throws SQLException {
        st_login user = new st_login("s1", "password456");
        user.insert(conn);
        user.delete(conn);
        assertFalse(user.checkCredentials(conn));
    }

    @Test
    public void testUpdate() throws SQLException {
        st_login user = new st_login("s1", "oldpass");
        user.insert(conn);
        user.update(conn, "newpass");
        assertFalse(user.checkCredentials(conn));
        user = new st_login("s1", "newpass");
        assertTrue(user.checkCredentials(conn));
        change(user);
    }
}
