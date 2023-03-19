package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.example.DB_connect;
import org.example.Department;

public class DepartmentTest {

    private static Connection conn;

    @BeforeAll
    public static void setUp() throws SQLException {
        conn = DB_connect.connect();
//        Department dep = new Department("XNC", "Xenology and Yenology");
//        dep.insert(conn);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        conn.close();
    }

    public static void change(String s) throws SQLException {
        String sql = "DELETE FROM department WHERE dep = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, s);
            statement.executeUpdate();
        }
    }

    @Test
    public void correctInsert() throws SQLException {
        Department dep = new Department("NMP", "Nove Motopilasis");
        dep.insert(conn);
        String sql = "SELECT * FROM department WHERE dep = 'NMP'";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            assertTrue(rs.next());
            assertEquals("NMP", rs.getString("dep"));
            assertEquals("Nove Motopilasis", rs.getString("dep_name"));
        }
        change("NMP");
    }

    @Test
    public void testDelete() throws SQLException {
        Department dep = new Department("TME", "Tera Mechanical Engineering");
        dep.insert(conn);
        assertTrue(dep.check(conn));
        dep.delete(conn);
        assertFalse(dep.check(conn));
    }

    @Test
    public void testCheck() throws SQLException {
        Department dep0 = new Department("XNC", "Xenology and Yenology");
        dep0.insert(conn);
        Department dep = new Department("XNC", "Xenology and Yenology");
        assertTrue(dep.check(conn));
        Department dep2 = new Department("NIT", "No Information Technology");
        assertFalse(dep2.check(conn));
        change("XNC");
    }
}
