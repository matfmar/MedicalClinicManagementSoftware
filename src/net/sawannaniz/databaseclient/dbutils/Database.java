package net.sawannaniz.databaseclient.dbutils;

import javax.swing.*;
import java.sql.*;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Database {
    public Database(String us, String pwd) {
        user = us;
        password = pwd;
        address = "";
        port = "";
        db_name = "";
        //JDBC_URL = "jdbc:mariadb://localhost:3306/Przychodnia";
        JDBC_URL = "jdbc:mariadb://localhost:3306/Przychodnia?user=" + user + "&password=" + password;
    }
    public Database(String us, String pwd, String ad, String p, String db, String sslStr) {
        user = us;
        password = pwd;
        address = ad;
        port = p;
        db_name = db;
        String ssl = "";
        if (sslStr == "YES")
            ssl = "&trustStore=myTrustStore.jks&trustStorePassword=password";
        JDBC_URL = "jdbc:mariadb://" + address + ":" + port + "/" + db_name +
                "?user=" + user + "&password=" + password +
                ssl;

        //jdbc:mariadb://localhost/myDb?user=myUser&password=MyPwd&trustStore=/pathToTrustStore/myTrustStore.jks&trustStorePassword=mypwd
        //JDBC_URL = "jdbc:mariadb://" + address + ":" + port + "/" + db_name +
        //        "?user=" + user + "&password=" + password;
        //JDBC_URL = "jdbc:mariadb://" + address + ":" + port + "/" + db_name;
        //JDBC_URL = "jdbc:mariadb://localhost:3306/Przychodnia";
    }
    public static String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        return (dtf.format(now));
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
    public static String removeSecondsFromDatetime(String s) {
        if (s.isEmpty())
            return "";
        s = s.substring(0, s.length()-3);
        return s;
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
    public static boolean checkStringForICD10(String s) {
        if (s.length() == 5) {
            char l1, c1, c2, c3, k;
            l1 = s.charAt(0);
            c1 = s.charAt(1);
            c2 = s.charAt(2);
            c3 = s.charAt(4);
            k = s.charAt(3);
            if (Character.isDigit(c1) && (Character.isDigit(c2) && Character.isDigit(c3))) {
                if (Character.isLetter(l1) && (Character.compare(k, '.') == 0)) {
                    return true;
                }
            }
        }
        if (s.length() == 3) {
            char l1, c1, c2;
            l1 = s.charAt(0);
            c1 = s.charAt(1);
            c2 = s.charAt(2);
            if (Character.isLetter(c1) && (Character.isDigit(c1) && Character.isDigit(c2)))
                return true;
        }
        return false;
    }
    public static boolean checkStringForIP(String s) {
        char c;
        char dot = '.';
        for (int i=0; i<s.length(); ++i) {
            c = s.charAt(i);
            if (!Character.isDigit(c))
                if (Character.compare(c, dot) != 0)
                    return false;
        }
        return true;
    }
    public static String addCommas(String s) {
        String result = "'" + s + "'";
        return result;
    }
    public static String convertStringToDatetime(String s) {
        String realDataOd = "STR_TO_DATE(" + addCommas(s) + ", " + addCommas("%Y-%m-%d %h:%i:%s") + ")";
        return realDataOd;
    }
    public boolean connect() {
        boolean status = true;
        if (!checkLoginParameters()) {
            JOptionPane.showMessageDialog(null, "Zle wpisane parametry polaczenia", "error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            //connection = DriverManager.getConnection(JDBC_URL, user, password);
            connection = DriverManager.getConnection(JDBC_URL);
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
    public Role determineCurrentRole(AtomicBoolean result) {
        String command = "SELECT CURRENT_ROLE;";
        Role role = null;
        try {
            ResultSet resultSet = statement.executeQuery(command);
            while (resultSet.next()) {
                String s = resultSet.getString(1);
                System.out.println("Role: " + s);
                if (s == null) {
                    role = Role.NO_ROLE;
                    break;
                }
                switch (s) {
                    case "lekarz": role = Role.LEKARZ; break;
                    default: role = Role.NO_ROLE; break;
                }
                System.out.println(role.toString());
            }
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
        return role;
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
    public boolean update(String command, String s1, String s2, int i1) {        //ps for wpis z wizyty - safer
        PreparedStatement st;
        try {
            st = connection.prepareStatement(command);
            st.setString(1, s1);
            st.setString(2, s2);
            st.setInt(3, i1);
        } catch (SQLException ex) {
            System.out.println("Failed to prepare statement");
            return false;
        }
        System.out.println(statement.toString());
        int resultInt = 0;
        try {
            resultInt = st.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Failed to update data: " + ex.getMessage());
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
    public enum Role {
        NO_ROLE,
        LEKARZ
    }
    private String JDBC_URL = "jdbc:mariadb://localhost:3306/Przychodnia";
    private String user, password, address, port, db_name;
    private Connection connection;
    private Statement statement;
    private boolean checkLoginParameters() {
        Vector<String> v = new Vector<String>();
        v.add(port);
        v.add(db_name);
        if (!checkStringsForProperContent(v))
            return false;
        if (!checkStringForIP(address))
            return false;
        return true;
    }
}
