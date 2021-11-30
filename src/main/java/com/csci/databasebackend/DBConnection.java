package com.csci.databasebackend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static String dbHost = "jdbc:mysql://localhost:3306/BookStore";
    private static String username = "root";
    private static String password = "12345678";
    private static Connection conn;

    public static Connection createDBConnection() {
        try {

            conn = DriverManager.getConnection(dbHost,username,password);

        } catch (SQLException e) {
            System.out.println("Cannot connect to DB");
            System.out.println(e);
            e.printStackTrace();
        } finally {
            return conn;
        }
    }
}
