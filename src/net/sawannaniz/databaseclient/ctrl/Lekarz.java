package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.Database;
import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * It represents a physician corresponding to the one stored in the table Lekarze of the database.
 *
 * @author Mateusz Marzec
 * @version 1.0
 * @since 2023-04-08
 */
public class Lekarz extends Searching implements SaveableToPrzychodnia {
    /**
     *Creates a physician based on id, name, surname, licence, phone and specialty.
     *
     * @param id    id, as stored in the database
     * @param i     name
     * @param n     surname
     * @param p     licence number
     * @param t     phone number
     * @param s     specialties, provided in clear text without any special characters (only letters, digits, and spaces)
     */
    public Lekarz(int id, String i, String n, String p, String t, String s) {
        imie = i;
        nazwisko = n;
        pwz = p;
        telefon = t;
        specjalizacje = s;
        id_lekarz = id;
    }
    /**
     *Creates a physician based on name, surname, licence, phone and specialty.
     *
     * @param i     name
     * @param n     surname
     * @param p     licence number
     * @param t     phone number
     * @param s     specialties, provided in clear text without any special characters (only letters, digits, and spaces)
     */
    public Lekarz(String i, String n, String p, String t, String s) {
        imie = i;
        nazwisko = n;
        pwz = p;
        telefon = t;
        specjalizacje = s;
        id_lekarz = -1;
    }

    /**
     * Creates a physician as an empty object with no parameters.
     */
    public Lekarz() {
        imie = "";
        nazwisko = "";
        pwz = "";
        telefon = "";
        specjalizacje = "";
        id_lekarz = -1;
    }

    /**
     * Creates a physician based on id, as stored in the database.
     *
     * @param id    id, as stored in the database
     */
    public Lekarz(int id) {
        imie = "";
        nazwisko = "";
        pwz = "";
        telefon = "";
        specjalizacje = "";
        id_lekarz = id;
    }

    /**
     * Finds a physician in the database by their licence number.
     *
     * @param database  database that will be searched, see {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param pwz       licence number
     * @return          id of a physician as stored in a database
     */
    public static int znajdzLekarzaPoPWZ(Database database, String pwz) {
        pwz = pwz.trim();
        if (pwz.isEmpty())
            return -1;
        AtomicBoolean result = new AtomicBoolean(false);
        Lekarz lekarz = new Lekarz();
        ResultSet res = lekarz.searchPWZ(database, pwz, result);
        if (!result.get()) {
            JOptionPane.showMessageDialog(null,"B\u0142\u0105d szukania lekarza!",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
        int i = -1;
        try {
            while (res.next()) {
                i = res.getInt(1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"B\u0142\u0105d szukania lekarza!",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
        return i;

    }

    /**
     * Finds a physician in a database by id.
     *
     * @param database  database that will be searched, see {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param id        id of a physician, as stored in the database
     * @return          returns a String which links the name and the surname together
     */
    public static String znajdzLekarzaDoTabelki(Database database, int id) {
        if (id <= 0)
            return "";
        AtomicBoolean result = new AtomicBoolean(false);
        Lekarz lekarz = new Lekarz();
        ResultSet res = lekarz.search(database, id, result);
        if (!result.get()) {
            JOptionPane.showMessageDialog(null,"B\u0142\u0105d szukania lekarza: " + Integer.toString(id) + " do tabelki!",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return "";
        }
        String s = "";
        try {
            while (res.next()) {
                s = res.getString(2) + " " + res.getString(3);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"B\u0142\u0105d szukania lekarza: " + Integer.toString(id) + " do tabelki!",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return "";
        }
        return s;
    }

    /**
     * Finds data (id, name, surname and licence) about all physicians in a database.
     *
     * @param database  a database that is searched through, {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param result    success or failure of the operation is stored in this object
     * @return          dataset in ResultSet format
     */
    @Override
    public ResultSet search(Database database, AtomicBoolean result) {
        String table = "Lekarze";
        String what = "id_lekarz, imie, nazwisko, pwz";
        return (database.select(what, table, result));
    }

    /**
     * Finds data (id, name, surname, licence, phone and specialties) about a physician found by id.
     *
     * @param database  a database that is searched though, {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param id        id of a physcian
     * @param result    success or failure of the operation is stored in this object
     * @return          dataset in ResultSet format
     */
    @Override
    public ResultSet search(Database database, int id, AtomicBoolean result) {
        String table = "Lekarze";
        String what = "id_lekarz, imie, nazwisko, pwz, telefon, specjalizacje";
        String condition = "id_lekarz = " + Integer.toString(id);
        return (database.select(what, table, condition, result));
    }

    /**
     * Finds a physician (id) by their licence number.
     *
     * @param database  a database that is searched through {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param pwz       a licence number
     * @param result    success or failure of the operation is stored in this object
     * @return          id in a ResultSet format
     */
    public ResultSet searchPWZ(Database database, String pwz, AtomicBoolean result) {
        String table = "Lekarze";
        String what = "id_lekarz";
        String condition = "pwz = " + addCommas(pwz);
        return (database.select(what, table, condition, result));
    }

    /**
     * Inserts a new physician to the database.
     *
     * @param database  a database that the physician is inserted in {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @return true or false whether operation was successful or not
     */
    @Override
    public boolean insertToDatabase(Database database) {
        if (!checkInputData())
            return false;
        String table = "Lekarze";
        String columns = "imie, nazwisko, pwz, telefon, specjalizacje";
        String imiePart = addCommas(imie);
        String nazwiskoPart = addCommas(nazwisko);
        String pwzPart = addCommas(pwz);
        String telefonPart, specjalizacjePart;
        if (telefon.isEmpty()) telefonPart = "NULL"; else telefonPart = addCommas(telefon);
        if (specjalizacje.isEmpty()) specjalizacjePart = "NULL"; else specjalizacjePart = addCommas(specjalizacje);
        String params = imiePart + "," + nazwiskoPart + "," + pwzPart + "," +
                telefonPart + "," + specjalizacjePart;
        return (database.insert(table, columns, params));
    }

    /**
     * Finds a physician in a database by a provided set of data stored in a previously created Lekarz object.
     * The provided data are AND-ed.
     *
     * @param database  a database that will be searched through {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param result    success or failure of the operation will be stored in this object
     * @return          a dataset in a ResultSet format
     */
    public ResultSet searchDatabase(Database database, AtomicBoolean result) {
        if (!checkInputData()) {
            result.set(false);
            return null;
        }
        String what = "imie, nazwisko, pwz, telefon, specjalizacje, id_lekarz";
        String table = "Lekarze";
        if ((imie.isEmpty() && nazwisko.isEmpty()) && (pwz.isEmpty() && specjalizacje.isEmpty()))
            return (database.select(what, table, result));
        String imiePart = "", nazwiskoPart = "", pwzPart = "", specjalizacjaPart = "";
        if (!imie.isEmpty()) imiePart = "imie = " + addCommas(imie);
        if (!nazwisko.isEmpty()) nazwiskoPart = "nazwisko = " + addCommas(nazwisko);
        if (!pwz.isEmpty()) pwzPart = "pwz = " + addCommas(pwz);
        if (!specjalizacje.isEmpty()) specjalizacjaPart = "specjalizacje LIKE " + addCommas("%" + specjalizacje + "%");
        String conditions = imiePart + " AND " + nazwiskoPart + " AND " + pwzPart + " AND " + specjalizacjaPart;
        conditions = conditions.trim();
        while (conditions.startsWith("AND")) {
            conditions = conditions.substring(3);
            conditions = conditions.trim();
        }
        while (conditions.endsWith("AND")) {
            conditions = conditions.substring(0, conditions.length() - 3);
            conditions = conditions.trim();
        }
        conditions = conditions.replace("AND  AND  AND", "AND");
        conditions = conditions.replace("AND  AND","AND");
        return (database.select(what, table, conditions, result));
    }

    /**
     * Removes a physician - identified by id - from the given database.
     *
     * @param database  a database that will be used {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @return  true or false whether the operation was successful
     */
    @Override
    public boolean removeFromDatabase(Database database) {
        String table = "Lekarze";
        String where = "id_lekarz = " + Integer.toString(id_lekarz);
        return (database.delete(table, where));
    }

    /**
     * Updates data of a physician - identified by id - in a database.
     *
     * @param database  a database that will be used {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @return  true or false whether the operation was successful
     */
    @Override
    public boolean modifyInDatabase(Database database) {
        String table = "Lekarze";
        if (!checkInputData())
            return false;
        String imiePart = "", nazwiskoPart = "", pwzPart = "", specjalizacjaPart = "", telefonPart = "";
        imiePart = "imie = " + addCommas(imie);
        nazwiskoPart = "nazwisko = " + addCommas(nazwisko);
        pwzPart = "pwz = " + addCommas(pwz);
        if (!telefon.isEmpty()) telefonPart = "telefon = " + addCommas(telefon); else telefonPart = "telefon = NULL";
        if (!specjalizacje.isEmpty()) specjalizacjaPart = "specjalizacje = " + addCommas(specjalizacje); else specjalizacjaPart = "specjalizacje = NULL";
        String data = imiePart + ", " + nazwiskoPart + ", " + pwzPart + ", " + telefonPart + ", " + specjalizacjaPart;
        String condition = "id_lekarz = " + Integer.toString(id_lekarz);
        return (database.update(table, data, condition));
    }

    private String imie, nazwisko, pwz, telefon, specjalizacje;
    private int id_lekarz;

    /**
     * Checks whether the fields contain valid data (digit/letter/space).
     * Invokes {@link net.sawannaniz.databaseclient.dbutils.Database#checkStringsForProperContent(String)} method.
     * @return  true or false whether the data is correct
     */
    private boolean checkInputData() {
        Vector<String> strarr = new Vector<String>();
        strarr.add(imie);
        strarr.add(nazwisko);
        strarr.add(pwz);
        strarr.add(telefon);
        strarr.add(specjalizacje);
        return (Database.checkStringsForProperContent(strarr));
    }

    /**
     * Adds parentheses (') before and after a given string.
     * Invokes {@link net.sawannaniz.databaseclient.dbutils.Database#addCommas(String)} method.
     * @param s     a string that will be modified
     * @return      string after modification
     */
    private String addCommas(String s) {
        return Database.addCommas(s);
    }
}
