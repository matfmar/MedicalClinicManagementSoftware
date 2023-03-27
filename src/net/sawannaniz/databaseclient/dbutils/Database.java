package net.sawannaniz.databaseclient.dbutils;

import java.sql.*;

public class Database {
    private final String JDBC_NAME = "org.mariadb.jdbc";
    private final String JDBC_URL = "jdbc:mariadb://localhost:3306/Przychodnia";
    private String user, password;
    private Connection connection;
    public Database(String us, String pwd) {
        user = us;
        password = pwd;
    }
    public boolean Connect() {
        boolean status = true;
        try {
            connection = DriverManager.getConnection(JDBC_URL, user, password);
        } catch (SQLException ex) {
            System.out.println("Failed to get connection: " + ex.getMessage());
            return false;
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            return false;
        }
        System.out.println("Connection established.");
        return true;
    }
    public void Close() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Failed to close a connection");
        }
    }
}
