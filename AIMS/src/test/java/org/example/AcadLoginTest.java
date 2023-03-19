package org.example;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;
//import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import org.example.DB_connect;
import org.example.acad_login;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class AcadLoginTest {

    private Connection conn;

    @BeforeEach
    public void setUp() throws SQLException {
        conn = DB_connect.connect();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        conn.close();
    }

    public void change(acad_login user) throws SQLException {
        String sql = "DELETE FROM acad_office WHERE user_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, user.getUser_id());
            statement.executeUpdate();
        }
        conn.close();
    }

    @Test
    public void correctCreds() throws SQLException {
        acad_login user = new acad_login("t1", "password123");
        user.insert(conn);
        assertTrue(user.checkCredentials(conn));
        user.setPassword("wrongpass");
        assertFalse(user.checkCredentials(conn));
        change(new acad_login("t1", "password123"));
    }

    @Test
    public void correctDelete() throws SQLException {
        acad_login user = new acad_login("t1", "password456");
        user.insert(conn);
        user.delete(conn);
        assertFalse(user.checkCredentials(conn));
    }

    @Test
    public void wrongDelete() throws SQLException {
        acad_login user = new acad_login("t1", "password456");
        user.insert(conn);
        user.setUser_id("t3");
        user.delete(conn);
        assertFalse(user.checkCredentials(conn));
        user.setUser_id("t1");
        change(user);
    }

    @Test
    public void trueUpdate() throws SQLException {
        acad_login user = new acad_login("t1", "oldpass");
        user.insert(conn);
        user.update(conn, "newpass");
        assertFalse(user.checkCredentials(conn));
        user.setPassword("newpass");
        assertTrue(user.checkCredentials(conn));
        change(user);
    }
    @Test
    public void falseUpdate() throws SQLException {
        acad_login user = new acad_login("t1", "oldpass");
        user.insert(conn);
        user.setUser_id("t3");
        user.update(conn, "newpass");
        assertFalse(user.checkCredentials(conn));
        change(new acad_login("t1", "oldpass"));
    }
}
