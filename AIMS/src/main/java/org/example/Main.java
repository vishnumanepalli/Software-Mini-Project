package org.example;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
//import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.*;
//import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    public static int suc_msg(boolean suc)
    {
        if (suc) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid credentials.");
        }
        return 1;
    }

    public static boolean isGraduate(Connection conn, Student st) throws SQLException {
        String tableName = st.getTableName();
        String sql = "SELECT * FROM " + tableName;
        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            int p_coreCredits = 0;
            int btp = 0;
            int e_coreCredits = 0;
            int electiveCredits = 0;

            while (rs.next()) {
                String code = rs.getString("code");
                int grade = rs.getInt("grade");
                int off = rs.getInt("off_id");
                int f = 0;
                Course c_t = Course.get_course(conn, code);
                int credits = c_t.getCredits();


                Course_Off co_t = Course_Off.getByOffId(conn, off);
                String type = co_t.getType();

                if (type.equals("btp")) {
                    btp += credits;
                }
                else if (type.equals("program_core")) {
                    p_coreCredits += credits;
                } else if (type.equals("engineering_core")) {
                    e_coreCredits += credits;
                } else {
                    electiveCredits += credits;
                }

                if (grade < 4) {
                    return false;
                }
            }
            if (p_coreCredits >= 60 && e_coreCredits >= 30 && btp >= 6 && p_coreCredits + electiveCredits + e_coreCredits >= 120) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error while calculating CGPA: " + e.getMessage());
            throw e;
        }
    }

    public static int upload_grades(Connection conn, String inputFilePath) throws SQLException {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            int a=0;
            while ((line = br.readLine()) != null) {
                if(a==0)
                {
                    a=1;
                    continue;
                }
                String[] values = line.split(",");
                String stEntryNo = values[0];
                String course = values[1];
                int grade = Integer.parseInt(values[2]);

                // Get the student object from the database
                Student student = Student.get_student(conn, stEntryNo);
                if (student == null) {
                    continue;
                }

                // Update the grade in the department_entryno_name table
                String tableName = student.getTableName();//student.getDepartment() + "_" + student.getEntryNo() + "_" + student.getName();
                String updateSql = "UPDATE " + tableName + " SET grade = ? WHERE code = ?";
                try (PreparedStatement statement = conn.prepareStatement(updateSql)) {
                    statement.setInt(1, grade);
                    statement.setString(2, course);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Error while updating grade in " + tableName + " table: " + e.getMessage());
                    throw e;
                }
            }
            System.out.println("upload successful");
            return 1;
        } catch (IOException e) {
            System.out.println("Error reading input file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public static int transcript(Connection conn, Student st) throws SQLException, FileNotFoundException {
        String tableName = st.getTableName();
        int year = st.getBatch();
        int sem = 1;
        String directoryPath = "C:\\Users\\vishn\\OneDrive\\Desktop";
        String fileName = tableName + ".txt";
        File file_0 = new File(directoryPath, fileName);
        try {
            boolean result = file_0.createNewFile();
            if (result) {
                System.out.println("File created successfully.");
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
        try (PrintWriter writer = new PrintWriter(file_0)) {
            writer.println("Transcript for " + tableName);
            writer.println("CGPA for student : " + st.calculate_cgpa(conn));
            while(!(year == (st.getBatch()+5) && sem == 1)) {
                writer.println("SGPA for " + year + " year " + sem + " sem is " + st.calculate_sgpa(conn, year, sem));
                String sql = "SELECT * FROM " + tableName + " WHERE year = ? AND sem = ?";
                try (PreparedStatement statement = conn.prepareStatement(sql)){
                    statement.setInt(1,year);
                    statement.setInt(2,sem);
                    if(sem == 2)
                    {
                        sem = 1;
                        year = year + 1;
                    }
                    else {
                        sem = 2;
                    }
                    ResultSet result = statement.executeQuery();
//			    ResultSetMetaData rsmd = result.getMetaData();
//			    int columnsNumber = rsmd.getColumnCount();
                    writer.printf("%-20s%-40s%-20s%-10s%-10s%n", "code", "name", "prog", "credits", "grade");
                    writer.println();
                    while (result.next()) {
                        String code = result.getString("code");
                        String grade = result.getString("grade");
                        int oid = result.getInt("off_id");
                        Course_Off off = Course_Off.getByOffId(conn, oid);
                        Course c_t = Course.get_course(conn, code);
                        String name = c_t.getTitle();
                        int credits = c_t.getCredits();
                        String prog = off.getType();

                        writer.printf("%-20s%-40s%-20s%-10d%-10s%n", code, name, prog, credits, grade);
                        writer.println();
                    }

                    System.out.println("All values in table " + tableName + " retrieved from database and written to " + tableName + ".txt!");
                    return 1;
                } catch (SQLException e) {
                    System.out.println("Error retrieving all values from table " + tableName + " from database: " + e.getMessage());
                    throw e;
                }
            }
        }
        return 0;
    }

    public static int printStGrades(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            ResultSet result = statement.executeQuery();
            ResultSetMetaData rsmd = result.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            for (int i = 1; i <= columnsNumber; i++) {
                System.out.printf("%-20s", rsmd.getColumnName(i));
            }
            System.out.println();
            while (result.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    System.out.printf("%-20s", result.getString(i));
                }
                System.out.println();
            }
            System.out.println("All values in table " + tableName + " retrieved from database!");
            return 1;
        } catch (SQLException e) {
            System.out.println("Error retrieving all values from table " + tableName + " from database: " + e.getMessage());
            throw e;
        }
    }

    public static int printAllCourses(Connection conn) throws SQLException {
        String sql = "SELECT * FROM course";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            ResultSet result = statement.executeQuery();
            System.out.printf("%-20s%-40s%-5s%-5s%-5s%-7s%-5s\n", "code", "title", "lec", "tut", "per", "s_hrs", "credits");
            while (result.next()) {
                String code = result.getString("code");
                String title = result.getString("title");
                int lec = result.getInt("lec");
                int tut = result.getInt("tut");
                int per = result.getInt("per");
                int s_hrs = result.getInt("s_hrs");
                int credits = result.getInt("credits");

                System.out.printf("%-20s%-40s%-5d%-5d%-5d%-7d%-5d\n", code, title, lec, tut, per, s_hrs, credits);
            }
            System.out.println("All courses retrieved from database!");
            return 1;
        } catch (SQLException e) {
            System.out.println("Error retrieving all courses from database: " + e.getMessage());
            throw e;
        }
    }

    public static int printAllCourseOff(Connection conn) throws SQLException {
        String sql = "SELECT * FROM course_off";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            ResultSet result = statement.executeQuery();
            System.out.printf("%-10s%-10s%-10s%-10s%-10s%-10s%-10s%-20s%-5s\n", "off_id", "code", "prof", "year", "sem", "cg_req", "dep", "type", "permit");
            while (result.next()) {
                int off_id = result.getInt("off_id");
                String code = result.getString("code");
                String prof = result.getString("prof");
                int year = result.getInt("year");
                int sem = result.getInt("sem");
                float cg_req = result.getFloat("cg_req");
                String dep = result.getString("dep");
                String type = result.getString("type");
                int permit = result.getInt("permit");
                System.out.printf("%-10d%-10s%-10s%-10d%-10d%-10.2f%-10s%-20s%-5d\n", off_id, code, prof, year, sem, cg_req, dep, type, permit);
            }
            System.out.println("All course offerings retrieved from database!");
            return 1;
        } catch (SQLException e) {
            System.out.println("Error retrieving all course offerings from database: " + e.getMessage());
            throw e;
        }
    }

    @SuppressWarnings("unused")
    public static int pre_run(Connection conn) throws SQLException
    {
        Department d1 = new Department("CSE", "Computer Science");
        d1.insert(conn);
        Department d2 = new Department("MNC", "Maths & Computing");
        d2.insert(conn);
        Department d3 = new Department("EEE", "Electrical");
        d3.insert(conn);
        Department d4 = new Department("CIV", "Civil");
        d4.insert(conn);
        Department d5 = new Department("CHE", "Chemical");
        d5.insert(conn);
        Department d6 = new Department("MECH", "Mechanical");
        d6.insert(conn);
        Department d7 = new Department("MMT", "Metallurgy");
        d7.insert(conn);
        System.out.println("Departments inserted successfully");

        Student s1 = new Student("Sandy", "2022CSB1064", "CSE", 2022);
        s1.save_new_st(conn);
        st_login st1 = new st_login("2022CSB1064", "iitropar");
        st1.insert(conn);
        Student s2 = new Student("Rosie", "2020CSB1097", "CSE", 2020);
        s2.save_new_st(conn);
        st_login st2 = new st_login("2020CSB1097", "iitropar");
        st2.insert(conn);
        Student s3 = new Student("Lisa", "2021CSB1098", "CSE", 2021);
        s3.save_new_st(conn);
        st_login st3 = new st_login("2021CSB1098", "iitropar");
        st3.insert(conn);
        Student s4 = new Student("Jennie", "2022MCB1094", "MNC", 2022);
        s4.save_new_st(conn);
        st_login st4 = new st_login("2022MCB1094", "iitropar");
        st4.insert(conn);
        Student s5 = new Student("Ariana", "2020EEB1084", "EEE", 2020);
        s5.save_new_st(conn);
        st_login st5 = new st_login("2020EEB1084", "iitropar");
        st5.insert(conn);
        Student s6 = new Student("Nancy", "2021MEB1087", "MECH", 2021);
        s6.save_new_st(conn);
        st_login st6 = new st_login("2021MEB1087", "iitropar");
        st6.insert(conn);
        System.out.println("Students inserted successfully");

        Professor p1 = new Professor("Vishnu","P_101", "CSE");
        p1.save_new_pr(conn);
        ins_login it1 = new ins_login("P_101", "iitropar");
        it1.insert(conn);
        Professor p2 = new Professor("Sreya","P_103", "CSE");
        p2.save_new_pr(conn);
        ins_login it2 = new ins_login("P_103", "iitropar");
        it2.insert(conn);
        Professor p3 = new Professor("Lashi","P_104", "MECH");
        p3.save_new_pr(conn);
        ins_login it3 = new ins_login("P_104", "iitropar");
        it3.insert(conn);
        Professor p4 = new Professor("Karthik","P_107", "EEE");
        p4.save_new_pr(conn);
        ins_login it4 = new ins_login("P_107", "iitropar");
        it4.insert(conn);
        Professor p5 = new Professor("Bapi","P_111", "CIV");
        p5.save_new_pr(conn);
        ins_login it5 = new ins_login("P_111", "iitropar");
        it5.insert(conn);
        System.out.println("Professors inserted successfully");

        Course c1=new Course("CS908", "Infinite Computation", 9, 8, 7, 6, 5);
        c1.createCourse(conn);
        Course ci=new Course("BTP1", "B T Project", 0, 0, 0, 0, 3);
        ci.createCourse(conn);
        Course cj=new Course("BTP2", "B T Project", 0, 0, 0, 0, 3);
        cj.createCourse(conn);
        Course c2=new Course("CS306", "Theorey of Computation", 9, 8, 7, 6, 5);
        c2.createCourse(conn);
        Course c3=new Course("CS304", "Computer Networks", 9, 8, 7, 6, 5);
        c3.createCourse(conn);
        Course c4=new Course("CS305", "Software Engg", 9, 8, 7, 6, 5);
        c4.createCourse(conn);
        Course c5=new Course("MA908", "Infinite Maths", 9, 8, 7, 6, 5);
        c5.createCourse(conn);
        Course c6=new Course("ME201", "Basic mechanics", 9, 8, 7, 6, 5);
        c6.createCourse(conn);
        System.out.println("All Courses inserted successfully");

        acad_login at1 = new acad_login("Acad", "iitropar");
        at1.insert(conn);

        return 1;
    }

    public static void main(String[] args) throws Exception
    {
        Connection conn = DB_connect.connect();
        if (conn != null)
        {
            try (Scanner scanner = new Scanner(System.in))
            {
//				pre_run(conn);


                System.out.println("1. Student");
                System.out.println("2. Instructor");
                System.out.println("3. Academic Officer");
                System.out.print("Select login type:");
                int loginType = scanner.nextInt();

                System.out.print("Enter user ID:");
                String userId = scanner.next();

                System.out.print("Enter password:");
                String password = scanner.next();

                boolean loginSuccessful = false;

                if (loginType == 1)
                {
                    st_login sl1 = new st_login(userId, password);
                    loginSuccessful = sl1.checkCredentials(conn);
                    Student s1 = Student.get_student(conn, userId);
                    suc_msg(loginSuccessful);
                    while(loginSuccessful)
                    {
                        System.out.println("0. Graduation Check");
                        System.out.println("1. View Grades");
                        System.out.println("2. Get current CGPA");
                        System.out.println("3. Register Course");
                        System.out.println("4. Deregister Course");
                        System.out.print("Select operation type:");
                        int opType = scanner.nextInt();
                        if(opType == 0)
                        {
                            System.out.println(isGraduate(conn, null));
                        }
                        else if(opType == 1)
                        {
                            printStGrades(conn, s1.getTableName());
                        }
                        else if(opType == 2)
                        {
                            double cg = s1.calculate_cgpa(conn);
                            System.out.printf("Current CGPA: %f\n", cg);
                        }
                        else if(opType == 3)
                        {
                            printAllCourseOff(conn);
                            System.out.print("Enter off_id which you like to register : ");
                            int c_of = scanner.nextInt();
                            System.out.print("Enter year : ");
                            int year = scanner.nextInt();
                            System.out.print("Enter sem : ");
                            int sem = scanner.nextInt();
                            s1.new_reg(conn, c_of, year, sem);
                        }
                        else if(opType == 4)
                        {
                            printStGrades(conn, s1.getTableName());
                            System.out.print("Enter off_id which you like to deregister : ");
                            int o_id = scanner.nextInt();
                            s1.dereg(conn, o_id);
                        }
                        else
                        {
                            break;
                        }
                    }
                }
                else if (loginType == 2)
                {
                    ins_login il1 = new ins_login(userId, password);
                    loginSuccessful = il1.checkCredentials(conn);
                    Professor p1 = Professor.get_professor(conn, userId);
                    suc_msg(loginSuccessful);
                    while(loginSuccessful)
                    {
                        System.out.println("1. View Grade of all Students");
                        System.out.println("2. Register Offering");
                        System.out.println("3. Deregister Offering");
                        System.out.println("4. Update course grdades");
                        System.out.print("Select operation type:");
                        int opType = scanner.nextInt();
                        if(opType == 1)
                        {
                            System.out.print("Enter Student ID for viewing grades:");
                            String viewGradeSt = scanner.next();
                            Student v1 = Student.get_student(conn, viewGradeSt);
                            printStGrades(conn,v1.getTableName());

                        }
                        else if(opType == 2)
                        {
                            printAllCourses(conn);
                            System.out.println("Please enter the following information for the new course offering:");

                            System.out.print("Course code: ");
                            String code = scanner.next();

                            System.out.print("Year: ");
                            int year = scanner.nextInt();
                            scanner.nextLine();

                            System.out.print("Semester: ");
                            int sem = scanner.nextInt();
                            scanner.nextLine();

                            System.out.print("CGPA requirement: ");
                            float cg_req = scanner.nextFloat();
                            scanner.nextLine();

                            System.out.print("Department: ");
                            String dep = scanner.next();

                            System.out.print("Type: ");
                            String type = scanner.next();

                            Course_Off co_1 = new Course_Off(code, p1.getId(), year, sem, cg_req, dep, type);
                            co_1.save(conn);

                            String cou1 = code;
                            System.out.println(cou1);
                            String cou2;
                            while(true)
                            {
                                System.out.print("Enter new prereq for this course: ");
                                cou2 = scanner.next();
                                if(cou2.equals("q"))
                                {
                                    break;
                                }
                                PreReq pq1 = new PreReq(cou1, cou2);
                                pq1.insert(conn);
                            }

                        }
                        else if(opType == 3)
                        {
                            printAllCourseOff(conn);
                            System.out.print("Enter off_id to degister offering : ");
                            int o_id = scanner.nextInt();
                            Course_Off.delete(conn, o_id);
                        }
                        else if(opType == 4)
                        {
                            System.out.print("Enter Link for uploading grades : ");
                            String file = scanner.next();
                            upload_grades(conn, file);//"C:\\Users\\vishn\\Downloads\\sheet-2.csv"
                        }
                        else
                        {
                            break;
                        }
                    }
                }
                else if (loginType == 3)
                {
                    acad_login al1= new acad_login(userId, password);
                    loginSuccessful = al1.checkCredentials(conn);
                    suc_msg(loginSuccessful);
                    while(loginSuccessful)
                    {
                        System.out.println("0. Delete Course Catalogue");
                        System.out.println("1. Add Course Catalogue");
                        System.out.println("2. View Grade of all Students");
                        System.out.println("3. Generate transcript of students");
                        System.out.println("4. Freeze Offer for registering");
                        System.out.print("Select operation type:");
                        int opType = scanner.nextInt();
                        if(opType == 0)
                        {
                            printAllCourses(conn);

                            System.out.print("Enter course code to delete: ");
                            String code = scanner.next();
                            Course.deleteCourse(conn, code);
                        }
                        if(opType == 1)
                        {
//							printAllCourses(conn);

                            System.out.print("Enter course code: ");
                            String code = scanner.next();

                            System.out.print("Enter course title: ");
                            String title = scanner.next();

                            System.out.print("Enter number of lecture hours: ");
                            int lec = scanner.nextInt();

                            System.out.print("Enter number of tutorial hours: ");
                            int tut = scanner.nextInt();

                            System.out.print("Enter number of practical hours: ");
                            int per = scanner.nextInt();

                            System.out.print("Enter number of self study hours: ");
                            int s_hrs = scanner.nextInt();

                            System.out.print("Enter number of credits: ");
                            int credits = scanner.nextInt();

                            Course n_edit = new Course(code, title, lec, tut, per, s_hrs, credits);
                            n_edit.createCourse(conn);
                        }
                        else if(opType == 2)
                        {
                            System.out.print("Enter Student ID for viewing grades ; ");
                            String viewGradeSt = scanner.next();
                            Student v1 = Student.get_student(conn, viewGradeSt);
                            printStGrades(conn,v1.getTableName());
                        }
                        else if(opType == 3)
                        {

                            System.out.print("Enter Student ID for generating Transcript : ");
                            String transSt = scanner.next();
                            System.out.println(Student.get_student(conn, transSt).getTableName());
                            transcript(conn, Student.get_student(conn, transSt));
                        }
                        else if(opType == 4)
                        {
                            printAllCourseOff(conn);
                            System.out.println("Enter offering ID you need to freeze registering : ");
                            int off = scanner.nextInt();
                            Course_Off.getByOffId(conn, off).updatePermit(conn);
                        }
                        else
                        {
                            break;
                        }
                    }
                }
                else
                {
                    System.out.println("Invalid login type selected.");
                }
            }
        }
        else
        {
            System.out.println("Connection failed");
        }
    }
}
