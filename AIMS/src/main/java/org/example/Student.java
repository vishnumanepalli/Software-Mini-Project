package org.example;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class Student {
    private String name;
    private String entry_no;
    private String department;
    private int batch;

    public Student(String name, String entry_no, String department, int batch) {
        this.name = name;
        this.entry_no = entry_no;
        this.department = department;
        this.batch = batch;
    }

    public String getName() {
        return name;
    }

    public String getEntryNo() {
        return entry_no;
    }

    public String getDepartment() {
        return department;
    }

    public int getBatch() {
        return batch;
    }

    public String getTableName() {
        return "Table_" + entry_no;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public void setEntryNo(String entry_no) {
//        this.entry_no = entry_no;
//    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }


    public void save_new_st(Connection conn) throws SQLException {
        String sql = "INSERT INTO st_creds(st_name, st_entry_no, dept, batch) VALUES (?, ?, ?, ?)";
        try(PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, entry_no);
            statement.setString(3, department);
            statement.setInt(4, batch);
            statement.executeUpdate();
            System.out.println("New student added to database!");

            // create department_entry_no_name table
            String tableName = getTableName();//department + "_" + entry_no + "_" + name;
            String createTableSql = "CREATE TABLE " + tableName + " ("
                    + "code VARCHAR(10) NOT NULL,"
                    + "grade INT DEFAULT NULL,"
                    + "year INT NOT NULL,"
                    + "sem INT NOT NULL,"
                    + "off_id INTEGER NOT NULL,"
                    + "PRIMARY KEY (code),"
                    + "FOREIGN KEY (code) REFERENCES course(code),"
                    + "FOREIGN KEY (off_id) REFERENCES course_off(off_id)"
                    + ")";
//            System.out.println("sql " + createTableSql);
            PreparedStatement createTableStatement = conn.prepareStatement(createTableSql);
            createTableStatement.executeUpdate();
            System.out.println("Table " + tableName + " created for " + name);
        } catch (SQLException e) {
            System.out.println("Error adding new student to database: " + e.getMessage());
            throw e;
        }
    }

    public static Student get_student(Connection conn, String entry_no) throws SQLException {
        String sql = "SELECT * FROM st_creds WHERE st_entry_no = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, entry_no);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String name = result.getString("st_name");
                String department = result.getString("dept");
                int batch = result.getInt("batch");
                Student student = new Student(name, entry_no, department, batch);
                return student;
            } else {
                System.out.println("No student found with entry number " + entry_no);
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error while getting student information from database: " + e.getMessage());
            return null;
        }
    }

    public void update_student(Connection conn) throws SQLException {
        String sql = "UPDATE st_creds SET st_name = ?, dept = ?, batch = ? WHERE st_entry_no = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, department);
            statement.setInt(3, batch);
            statement.setString(4, entry_no);
            statement.executeUpdate();
            System.out.println("Student information updated in database!");
        } catch (SQLException e) {
            System.out.println("Error while updating student information in database: " + e.getMessage());
            throw e;
        }
    }

    public void delete_student(Connection conn) throws SQLException {
        String sql = "DELETE FROM st_creds WHERE st_entry_no = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, entry_no);
            statement.executeUpdate();
            System.out.println("Student removed from database!");

            // drop department_entry_no_name table
            String tableName = this.getTableName(); //department + "_" + entry_no + "_" + name;
            String dropTableSql = "DROP TABLE IF EXISTS " + tableName;
            PreparedStatement dropTableStatement = conn.prepareStatement(dropTableSql);
            dropTableStatement.executeUpdate();
            System.out.println("Table " + tableName + " dropped.");
        } catch (SQLException e) {
            System.out.println("Error while deleting student information from database: " + e.getMessage());
            throw e;
        }
    }

    public double calculate_cgpa(Connection conn) throws SQLException {
        // Get all rows from the department_entry_no_name table
        String tableName =getTableName();// department + "_" + entry_no + "_" + name;
        String sql = "SELECT * FROM " + tableName;
        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            double totalGradePoints = 0.0;
            int totalCredits = 0;

            while (rs.next()) {
                String courseCode = rs.getString("code");
                int grade = rs.getInt("grade");
                if (rs.wasNull()) {
                    continue;
                }

                Course course = Course.get_course(conn, courseCode);
                int credits = course.getCredits();
                double gradePoint;
                gradePoint = (double)grade;
                totalGradePoints += gradePoint * credits;
                totalCredits += credits;
            }

            double cgpa;
            if (totalCredits == 0) {
                cgpa = 0.0;
            } else {
                cgpa = totalGradePoints / totalCredits;
            }

            // Print the CGPA
            System.out.println("CGPA for " + name + " is " + cgpa);

            // Return the calculated CGPA
            return cgpa;
        } catch (SQLException e) {
            System.out.println("Error calculating CGPA: " + e.getMessage());
            throw e;
        }
    }

    public double calculate_sgpa(Connection conn, int yr, int sem) throws SQLException {
        String tableName =getTableName();
        String sql = "SELECT * FROM " + tableName + " WHERE year = ? AND sem = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, yr);
            statement.setInt(2, sem);
            ResultSet rs = statement.executeQuery();

            double totalGradePoints = 0.0;
            int totalCredits = 0;

            while (rs.next()) {
                String courseCode = rs.getString("code");
                int grade = rs.getInt("grade");
                if (rs.wasNull()) {
                    continue;
                }

                Course course = Course.get_course(conn, courseCode);

                int credits = course.getCredits();
                double gradePoint;

                gradePoint = (double)grade;
                totalGradePoints += gradePoint * credits;
                totalCredits += credits;
            }

            double cgpa;
            if (totalCredits == 0) {
                cgpa = 0.0;
            } else {
                cgpa = totalGradePoints / totalCredits;
            }

            System.out.println("CGPA for " + name + " is " + cgpa);

            return cgpa;
        } catch (SQLException e) {
            System.out.println("Error calculating CGPA: " + e.getMessage());
            throw e;
        }
    }

    public int new_reg(Connection conn, int off_id, int year, int sem) throws SQLException {
        if (isEnrolled(conn, off_id)) {
            System.out.println("Already enrolled in this course");
            return 2;
        }
        Course_Off co = Course_Off.getByOffId(conn, off_id);
        if (co == null) {
            System.out.println("Invalid off_id provided");
            return 3;
        }
        if(!(co.getYear() == year & co.getSem() == sem)) {
            System.out.println("Wrong year or sem to register in this course");
            return 4;
        }
        double cgpa = calculate_cgpa(conn);
//        String code;
        String selectSql = "SELECT cg_req FROM course_off WHERE off_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
            pstmt.setInt(1, off_id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double cg_req = rs.getDouble("cg_req");
//                code = getCourseCode(conn, off_id);
//                String dep = getOffDep(conn, off_id);
                if(cgpa < cg_req)
                {
                    System.out.println("CGPA not sufficient");
                    return 5;
                }
                boolean cx23 = checkPreRequisites(conn, co.getCode(), getTableName());
                if(!cx23)
                {
                    System.out.println("Prerquisites not full-filled");
                    return 6;
                }
                if(!newCreditCheck(conn, Course.get_course(conn, co.getCode()).getCredits(), year, sem))
                {
                    System.out.println("Credit Limit Exceeded");
                    return 7;
                }
                if(co.getPermit(conn) == 1)
                {
                    System.out.println("This offering is freezed");
                    return 9;
                }
                if (co.getDep().equals(department)) {
                    return insert_reg(conn, off_id, co.getCode(), null, co.getYear(), co.getSem());
                } else {
                    System.out.println("Does not belong to this department to register for this course");
                    return 12;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking cg_req in course_off table: " + e.getMessage());
            throw e;
        }
        return -1;
    }

    private int insert_reg(Connection conn, int off_id, String code, Integer grade, int year, int sem) throws SQLException {
        String tableName = getTableName();//department + "_" + entry_no + "_" + name;
        String sql = "INSERT INTO " + tableName + " (off_id, code, grade, year, sem) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, off_id);
            pstmt.setString(2, code);
            pstmt.setInt(4, year);
            pstmt.setInt(5, sem);
            pstmt.setNull(3, java.sql.Types.INTEGER);
            pstmt.executeUpdate();
            return 10;
        } catch (SQLException e) {
            System.out.println("Error inserting into " + tableName + " table: " + e.getMessage());
            throw e;
        }
    }

    public int dereg(Connection conn, int off_id) throws SQLException {
        String tableName = getTableName();

        Course_Off cou = Course_Off.getByOffId(conn, off_id);
        if (cou == null) {
            System.out.println("Invalid off_id provided");
            return 1;
        }
        if(cou.getPermit(conn) == 1)
        {
            System.out.println("You cannot deregister this course");
            return 2;
        }
        String deleteSql = "DELETE FROM " + tableName + " WHERE off_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, off_id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Successfully deregistered from student table with off_id: " + off_id);
                return 3;
            } else {
                System.out.println("No registration found for course with off_id: " + off_id);
                return 4;
            }
        } catch (SQLException e) {
            System.out.println("Error deleting from " + tableName + " table: " + e.getMessage());
            throw e;
        }
    }

    public static boolean checkPreRequisites(Connection conn, String courseCode, String tableName) throws SQLException {
        String sql = "SELECT pre_req_code FROM pre_req WHERE course_code = ?";
        Set<String> preRequisites = new HashSet<>();

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, courseCode);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                preRequisites.add(rs.getString("pre_req_code"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching pre-requisites from database: " + e.getMessage());
            throw e;
        }

        sql = "SELECT code FROM " + tableName + " WHERE code = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            for (String preReq : preRequisites) {
                statement.setString(1, preReq);
                ResultSet rs = statement.executeQuery();
                if (!rs.next()) {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking pre-requisites in table " + tableName + ": " + e.getMessage());
            throw e;
        }

        return true;
    }

    private boolean isEnrolled(Connection conn, int off_id) throws SQLException {
        String tableName = getTableName();//department + "_" + entry_no + "_" + name;
        String sql = "SELECT COUNT(*) AS count FROM " + tableName + " WHERE off_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, off_id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking if off_id is already enrolled: " + e.getMessage());
            throw e;
        }

        return false;
    }

    public boolean newCreditCheck(Connection conn, int credits, int yr, int sem) throws SQLException
    {
        int v2, v3;
        int v1 = credits + getTotalCredits(conn, yr, sem);
        if(batch == yr)
        {
            return v1 <= 24 ;
        }
        else
        {
            if(sem == 1)
            {
                v2 = getTotalCredits(conn, yr - 1, 1);
                v3 = getTotalCredits(conn, yr - 1, 2);
                double avg = (v2 + v3) / 2.0;
                return  v1<= avg * 1.25;
            }
            else if(sem == 2)
            {
                v2 = getTotalCredits(conn, yr, 1);
                v3 = getTotalCredits(conn, yr - 1, 2);
                double avg = (v2 + v3) / 2.0;
                return  v1<= avg * 1.25;
            }
            return false;
        }
    }

    public int getTotalCredits(Connection conn, int yr, int sem) throws SQLException {
        int totalCredits = 0;
        String tableName = getTableName();
        String sql = "SELECT code FROM " + tableName + " WHERE year = ? AND sem = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, yr);
            statement.setInt(2, sem);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String code = rs.getString("code");
                Course course = Course.get_course(conn, code);
                int credits = course.getCredits();
                totalCredits += credits;
            }
        } catch (SQLException e) {
            System.out.println("Error getting total credits: " + e.getMessage());
            throw e;
        }
        return totalCredits;
    }

}