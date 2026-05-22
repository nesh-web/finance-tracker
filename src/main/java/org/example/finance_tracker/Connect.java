package org.example.finance_tracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {

    public static Connection connection() {
        try {
            String url = "jdbc:mysql://localhost:3306/finance_db";
            String user = "root";
            String password = "";

            return DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
            return null;
        }
    }
}