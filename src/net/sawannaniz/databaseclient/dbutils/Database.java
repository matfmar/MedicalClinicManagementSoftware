package net.sawannaniz.databaseclient.dbutils;

import java.sql.*;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Database {
    public Database(String us, String pwd) {
        user = us;
        password = pwd;
    }
    public static boolean checkStringsForProperContent(Vector<String> strTable) {
        char c=';';
        for (String s : strTable) {
            for (int i = 0; i < s.length(); ++i) {
                c = s.charAt(i);
                if (!((Character.isLetter(c) || Character.isDigit(c)) || Character.isSpaceChar(c))) {
                    System.out.println("Dane wejsciowe nie spelniaja kryteriow!");
                    return false;
                }
            }
        }
        return true;
    }
    public static boolean checkStringsForProperContent(String s) {
        char c=';';
            for (int i = 0; i < s.length(); ++i) {
                c = s.charAt(i);
                if (!((Character.isLetter(c) || Character.isDigit(c)) || Character.isSpaceChar(c))) {
                    System.out.println("Dane wejsciowe nie spelniaja kryteriow!");
                    return false;
                }
            }
        return true;
    }
    public static boolean checkStringForProperDatetime(String s) {
        char c = ';', shouldBe = ';';
        for (int i=0; i<s.length(); ++i) {
            switch (i) {
                case 4: shouldBe = '-'; break;
                case 7: shouldBe = '-'; break;
                case 10: shouldBe = ' '; break;
                case 13: shouldBe = ':'; break;
                case 16: shouldBe = ':'; break;
                default: shouldBe = ';'; break;
            }
            c = s.charAt(i);
            if (shouldBe == ';') {
                if (!Character.isDigit(c)) {
                    System.out.println("Dane wejsciowe nie spelniaja kryteriow!");
                    return false;
                }
            }
            else {
                if (Character.compare(c, shouldBe) != 0) {
                    System.out.println("Dane wejsciowe nie spelniaja kryteriow!");
                    return false;
                }
            }
        }
        return true;
    }
    public static String addCommas(String s) {
        String result = "\'" + s + "\'";
        return result;
    }
    public static String convertStringToDatetime(String s) {
        String realDataOd = "STR_TO_DATE(" + addCommas(s) + ", " + addCommas("%Y-%m-%d %h:%i:%s") + ")";
        return realDataOd;
    }
    public boolean connect() {
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
        try {
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println("Failed to create statement: " + ex.getMessage());
            return false;
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            return false;
        }
        System.out.println("Connection established.");
        return true;
    }
    public boolean insert(String table, String columns, String values) {
        String command = "INSERT INTO " + table + " (" + columns + ") " + "VALUES (" + values + ");";
        System.out.println(command);
        int resultInt = 0;
        try {
            resultInt = statement.executeUpdate(command);
        } catch (SQLException ex) {
            System.out.println("Failed to create insert data: " + ex.getMessage());
            return false;
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            return false;
        }
        return true;
    }
    public boolean delete(String table, String where) {
        String command = "DELETE FROM " + table + " WHERE " + where + ";";
        System.out.println(command);
        int resultInt = 0;
        try {
            resultInt = statement.executeUpdate(command);
        } catch (SQLException ex) {
            System.out.println("Failed to delete data: " + ex.getMessage());
            return false;
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            return false;
        }
        return true;
    }
    public boolean update(String table, String data, String condition) {
        String command = "UPDATE " + table + " SET " + data + " WHERE " + condition + ";";
        System.out.println(command);
        int resultInt = 0;
        try {
            resultInt = statement.executeUpdate(command);
        } catch (SQLException ex) {
            System.out.println("Failed to update data: " + ex.getMessage());
            return false;
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            return false;
        }
        return true;
    }
    public ResultSet select(String what, String table, String condition, AtomicBoolean result) {
        String command = "SELECT " + what + " FROM " + table + " WHERE " + condition + " ;";
        System.out.println(command);
        ResultSet resultSet;
        try {
            resultSet = statement.executeQuery(command);
        } catch (SQLException ex) {
            System.out.println("Failed to create insert data: " + ex.getMessage());
            result.set(false);
            return null;
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            result.set(false);
            return null;
        }
        result.set(true);
        return resultSet;
    }
    //overloaded for no conditions
    public ResultSet select(String what, String table, AtomicBoolean result) {
        String command = "SELECT " + what + " FROM " + table + " ;";
        System.out.println(command);
        ResultSet resultSet;
        try {
            resultSet = statement.executeQuery(command);
        } catch (SQLException ex) {
            System.out.println("Failed to create insert data: " + ex.getMessage());
            result.set(false);
            return null;
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            result.set(false);
            return null;
        }
        result.set(true);
        return resultSet;
    }
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Failed to close a connection");
            return;
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            return;
        }
        System.out.println("Connection closed successfully");
    }
    private final String JDBC_URL = "jdbc:mariadb://localhost:3306/Przychodnia";
    private String user, password;
    private Connection connection;
    private Statement statement;
}
