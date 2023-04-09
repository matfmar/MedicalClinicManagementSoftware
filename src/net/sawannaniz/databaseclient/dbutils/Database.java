package net.sawannaniz.databaseclient.dbutils;

import javax.swing.*;
import java.sql.*;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/**
 * It represents and provides a direct interface to Przychodnia database.
 * Its methods are called by instances of classes included in {@link net.sawannaniz.databaseclient.ctrl} package.
 *
 * @author Mateusz Marzec
 * @version 1.0
 * @since 2023-04-09
 */
public class Database {
    /**
     * Creates a database object based on given username and password.
     * The rest of the parameters are implicit for the default Przychodnia database on a cloud server on 172.106.0.62 under port 18601.
     * Also contains access to a default java keystore.
     *
     * @param us    username
     * @param pwd   password
     */
    public Database(String us, String pwd) {
        user = us;
        password = pwd;
        address = "172.106.0.62";
        port = "18601";
        db_name = "Przychodnia";
        pwdEntry = "password";
        //JDBC_URL = "jdbc:mariadb://localhost:3306/Przychodnia";
        String ssl = "&trustStore=myTrustStore.jks&trustStorePassword=" + pwdEntry;
        JDBC_URL = "jdbc:mariadb://" + address + ":" + port + "/" + db_name +
                "?user=" + user + "&password=" + password +
                ssl;
    }

    /**
     * Creates a database object based on username, password, IP address of the database, port, database name.
     * Also requires password to java keystore and information about requirement for SSL/TLS protocol when connecting or sending data.
     *
     * @param us    username
     * @param pwd   password
     * @param ad    IP address of the database
     * @param p     port number
     * @param db    database name - to avoid misunderstanding, this should be "Przychodnia"
     * @param sslStr    whether SSL/TLS protocol is to be used ("YES") or not ("NO");
     * @param pE    entry password to access java keystore (used for SSL/TLS secured connections)
     */
    public Database(String us, String pwd, String ad, String p, String db, String sslStr, String pE) {
        user = us;
        password = pwd;
        address = ad;
        port = p;
        db_name = db;
        pwdEntry = pE;
        String ssl = "";
        if (sslStr == "YES")
            ssl = "&trustStore=myTrustStore.jks&trustStorePassword=" + pwdEntry;
        JDBC_URL = "jdbc:mariadb://" + address + ":" + port + "/" + db_name +
                "?user=" + user + "&password=" + password +
                ssl;

        //jdbc:mariadb://localhost/myDb?user=myUser&password=MyPwd&trustStore=/pathToTrustStore/myTrustStore.jks&trustStorePassword=mypwd
        //JDBC_URL = "jdbc:mariadb://" + address + ":" + port + "/" + db_name +
        //        "?user=" + user + "&password=" + password;
        //JDBC_URL = "jdbc:mariadb://" + address + ":" + port + "/" + db_name;
        //JDBC_URL = "jdbc:mariadb://localhost:3306/Przychodnia";
    }

    /**
     * Returns current date in YYYY-MM-DD format.
     *
     * @return datetime in YYYY-MM-DD format
     */
    public static String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        return (dtf.format(now));
    }

    /**
     * Checks whether a vector of Strings is valid.
     * Valid means that do not contain anything else than letters, digits or spaces.
     *
     * @param strTable  a vector of Strings to be checked
     * @return  whether data is valid or not
     */
    public static boolean checkStringsForProperContent(Vector<String> strTable) {
        char c=';';
        for (String s : strTable) {
            for (int i = 0; i < s.length(); ++i) {
                c = s.charAt(i);
                if (!((Character.isLetter(c) || Character.isDigit(c)) || Character.isSpaceChar(c))) {
                    JOptionPane.showMessageDialog(null, "B\u0142\u0105d danych wej\u015bciowych!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * An overloaded version of the method, it is used for one String only.
     * As above, String should contain nothing more than letters, digits and spaces.
     *
     * @param s String to be checked for validity
     * @return  whether data is valid
     */
    public static boolean checkStringsForProperContent(String s) {
        char c=';';
            for (int i = 0; i < s.length(); ++i) {
                c = s.charAt(i);
                if (!((Character.isLetter(c) || Character.isDigit(c)) || Character.isSpaceChar(c))) {
                    JOptionPane.showMessageDialog(null, "B\u0142\u0105d danych wej\u015bciowych!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        return true;
    }

    /**
     * Removes seconds part from datetime.
     * It is used for user-friendly datetime presentation (seconds are useless when making appointments in a clinic).
     * The String should be in a proper datetime format, as this function just removes three last characters (:ss).
     *
     * @param s String in a datetime format
     * @return  The same string without 3 last characters (seconds part).
     */
    public static String removeSecondsFromDatetime(String s) {
        if (s.isEmpty())
            return "";
        s = s.substring(0, s.length()-3);
        return s;
    }

    /**
     * Checks whether the provided String is in a proper datetime format.
     * Proper datetime format means YYYY-MM-DD HH:mm:SS
     *
     * @param s The String that will be checked
     * @return  whether the format is correct
     */
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
                    JOptionPane.showMessageDialog(null, "B\u0142\u0105d danych wej\u015bciowych!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            else {
                if (Character.compare(c, shouldBe) != 0) {
                    JOptionPane.showMessageDialog(null, "B\u0142\u0105d danych wej\u015bciowych!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks whether the String contains a proper ICD-10 code.
     * The required format includes a letter followed by two digits followed by dot and one digit.
     *
     * @param s the String that will be checked
     * @return  whether data is correct
     */
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

    /**
     * Checks whether the provided String contains valid IP address.
     * Requires IPv4 address format with nothing after the fourth byte.
     *
     * @param s the string that will be checked
     * @return  whether data is correct
     */
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

    /**
     * Adds parentheses before and after a String.
     * It is useful for making proper SQL queries.
     *
     * @param s String to be modified
     * @return  The same String after modification
     */
    public static String addCommas(String s) {
        String result = "'" + s + "'";
        return result;
    }

    /**
     * Provides an interface for a MariaDB stored procedure STR_TO_DATE() which converts varchar to datetime.
     *
     * @param s String that will become datetime in a database
     * @return  String after modification
     */
    public static String convertStringToDatetime(String s) {
        String realDataOd = "STR_TO_DATE(" + addCommas(s) + ", " + addCommas("%Y-%m-%d %h:%i:%s") + ")";
        return realDataOd;
    }

    /**
     * Connects to the database.
     * All important parameters for connection are in the object's fields, provided upon its creation.
     *
     * @return whether the operation succeeded or not.
     */
    public boolean connect() {
        boolean status = true;
        if (!checkLoginParameters()) {
            JOptionPane.showMessageDialog(null, "\u0179le wpisane parametry po\u0142\u0105czenia!", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            //connection = DriverManager.getConnection(JDBC_URL, user, password);
            connection = DriverManager.getConnection(JDBC_URL);
        } catch (SQLException ex) {
            System.out.println("B\u0142\u0105d po\u0142\u0105czenia: " + ex.getMessage());
            return false;
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
            return false;
        }
        try {
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println("B\u0142\u0105d po\u0142\u0105czenia: " + ex.getMessage());
            return false;
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
            return false;
        }
        System.out.println("Connection established.");
        return true;
    }

    /**
     * Finds a database role of the logged user.
     *
     * @param result whether the operation was successful or not is stored in this object
     * @return  the role, see {@link Role}
     */
    public Role determineCurrentRole(AtomicBoolean result) {
        String command = "SELECT CURRENT_ROLE;";
        Role role = null;
        try {
            ResultSet resultSet = statement.executeQuery(command);
            while (resultSet.next()) {
                String s = resultSet.getString(1);
                //System.out.println("Role: " + s);
                if (s == null) {
                    role = Role.NO_ROLE;
                    break;
                }
                switch (s) {
                    case "lekarz": role = Role.LEKARZ; break;
                    default: role = Role.NO_ROLE; break;
                }
                //System.out.println(role.toString());
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

    /**
     * Inserts something to the database as identified by the parameters.
     *
     * @param table which table will be used
     * @param columns   which columns will be involved (separated with commas)
     * @param values    which values will be inserted (separated with commas, meets the columns order)
     * @return  whether operation was successful or not
     */
    public boolean insert(String table, String columns, String values) {
        String command = "INSERT INTO " + table + " (" + columns + ") " + "VALUES (" + values + ");";
        //System.out.println(command);
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

    /**
     * Deletes something from the database as identified by the parameters.
     *
     * @param table which table will be involved
     * @param where the conditions that should be met to delete the row (separated with commas)
     * @return  whether the operation was successful or not
     */
    public boolean delete(String table, String where) {
        String command = "DELETE FROM " + table + " WHERE " + where + ";";
        //System.out.println(command);
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

    /**
     * Updates data in a database, as identified by the parameters.
     * Actually it requires an explicit String which will become a prepared statement for two strings and one integer.
     * This method is used only when realizing appointments, as verification of data is then difficult.
     *
     * @param command   explicit String that will become a prepared statement
     * @param s1    first string parameter
     * @param s2    second string parameter
     * @param i1    integer parameter
     * @return      whether the operation was successful or not
     */
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
        //System.out.println(statement.toString());
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

    /**
     * An overloaded version of the {@code update()} method. Updates the database according to the given parameters.
     *
     * @param table table which will be updated
     * @param data  data to be updated, provided in an SQL format parameter = value
     * @param condition condition for rows to be updated (separated with commas)
     * @return  whether the operation was successful or not
     */
    public boolean update(String table, String data, String condition) {
        String command = "UPDATE " + table + " SET " + data + " WHERE " + condition + ";";
        //System.out.println(command);
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

    /**
     * Reads data from the database according to the provided parameters.
     *
     * @param what  which columns are to be retrieved
     * @param table which table is searched through
     * @param condition conditions of search (separated with commas)
     * @param result    result of the operation will be stored here
     * @return  dataset in a ResultSet format
     */
    public ResultSet select(String what, String table, String condition, AtomicBoolean result) {
        String command = "SELECT " + what + " FROM " + table + " WHERE " + condition + " ;";
        //System.out.println(command);
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

    /**
     * An overloaded version of {@code select()} method. Returns all rows (no condition is present).
     *
     * @param what  columns to be retrieved (separated with commas)
     * @param table table to be searched through
     * @param result    whether operation succeeded or not is stored here
     * @return  dataset in ResultSet format
     */
    public ResultSet select(String what, String table, AtomicBoolean result) {
        String command = "SELECT " + what + " FROM " + table + " ;";
        //System.out.println(command);
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

    /**
     * An overloaded version of {@code select()} method, which requires an explicit SELECT query as a parameter.
     *
     * @param cmd   String which contains explicit SELECT query
     * @param result    result of the operation will be stored here
     * @return  dataset in ResultSet format
     */
    public ResultSet select(String cmd, AtomicBoolean result) {
        String command = cmd;
        //System.out.println(command);
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

    /**
     * Closes connection with a database.
     */
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

    /**
     * List of Roles corresponding to roles defined for a certain MariaDB server and Przychodnia database
     *
     * @author Mateusz marzec
     * @version 1.0
     * @since 2023-04-09
     */
    public enum Role {
        NO_ROLE,
        LEKARZ
    }
    private String JDBC_URL = "jdbc:mariadb://localhost:3306/Przychodnia";
    private String user, password, address, port, db_name, pwdEntry;
    private Connection connection;
    private Statement statement;

    /**
     * Checks whether login parameters are valid.
     * Checks port number and database name.
     *
     * @return whether login parameters are valid or not.
     */
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
