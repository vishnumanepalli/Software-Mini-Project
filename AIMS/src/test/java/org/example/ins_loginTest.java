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
import org.example.Professor;
import org.example.ins_login;

public class ins_loginTest {

    private Connection conn;

    @BeforeEach
    public void setUp() throws SQLException {
        conn = DB_connect.connect();
        Professor pr = new Professor("test1","p1","CSE");
        pr.save_new_pr(conn);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        String sql1 = "DELETE FROM pr_creds WHERE pr_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql1)) {
            statement.setString(1, "p1");
            statement.executeUpdate();
        }
        conn.close();
    }

    public void change(ins_login user) throws SQLException {
        String sql = "DELETE FROM instructor WHERE user_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, user.getUserId());
            statement.executeUpdate();
        }
    }

    @Test
    public void correctCreds() throws SQLException {
        ins_login user = new ins_login("p1", "password123");
        user.insert(conn);
        assertTrue(user.checkCredentials(conn));
        change(user);
    }

    @Test
    public void correctupdate() throws SQLException {
        ins_login user = new ins_login("p1", "password123");
        user.insert(conn);
        user.update(conn, "wrongpass");
        assertFalse(user.checkCredentials(conn));
        change(user);
    }

    @Test
    public void testDelete() throws SQLException {
        ins_login user = new ins_login("p1", "password456");
        user.insert(conn);
        user.delete(conn);
        assertFalse(user.checkCredentials(conn));
    }
}
