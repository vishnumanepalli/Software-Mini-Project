package org.example;
import java.sql.*;

public class DB_connect {

    public static Connection connect(){
        System.out.println("first programme");
        Connection connection = null;
        String host="localhost";
        String port="5432";
        String db_name="Softie";
        String username="postgres";
        String password="Vishnu@123";
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://"+host+":"+port+"/"+db_name+"", ""+username+"", ""+password+"");
            if (connection != null) {
                System.out.println("Connection OK");
            } else {
                System.out.println("Connection Failed");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return connection;


    }

}
