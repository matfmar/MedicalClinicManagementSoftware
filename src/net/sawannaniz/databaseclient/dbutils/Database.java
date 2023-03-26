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
    public void Connect() {
        try {
            Class.forName(JDBC_NAME);
        } catch (ClassNotFoundException ex) {
            System.out.println("Fail to load MariaDB connecting class");
            System.exit(-1);
        }
        //get the connection
        try {
            connection = DriverManager.getConnection(JDBC_URL, user, password);
        } catch (SQLException ex) {
            System.out.println("Failed to get connection: " + ex.getMessage());
            System.exit(-1);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            System.exit(-1);
        }
    }
}
