package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.Database;
import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a patient - an entity stored in the Przychodnia database.
 * The fields contain all relevant data that are stored in the database.
 *
 * @author Mateusz Marzec
 * @version 1.0
 * @since 2023-04-08
 */
public class Pacjent extends Searching implements SaveableToPrzychodnia{
    /**
     * Creates a patient by a following set of parameters
     * @param i name
     * @param n surname
     * @param p PESEL
     * @param t phone number
     * @param a address
     * @param f important additional data about the patient
     * @param u permissions to share the medical data
     * @param lP a physcian that the patient is linked to
     * @param id id, as stored in the database
     */
    public Pacjent(String i, String n, String p, String t, String a, String f, String u, int lP, int id) {
        imie = i;
        nazwisko = n;
        pesel = p;
        telefon = t;
        adres = a;
        flagi = f;
        upowaznienia = u;
        lekarzProwadzacy = lP;
        idPacjent = id;
    }
    /**
     * Creates a patient by a following set of parameters
     * @param i name
     * @param n surname
     * @param p PESEL
     * @param t phone number
     * @param a address
     * @param f important additional data about the patient
     * @param u permissions to share the medical data
     * @param lP a physcian that the patient is linked to
     */
    public Pacjent(String i, String n, String p, String t, String a, String f, String u, int lP) {
        imie = i;
        nazwisko = n;
        pesel = p;
        telefon = t;
        adres = a;
        flagi = f;
        upowaznienia = u;
        lekarzProwadzacy = lP;
        idPacjent = -1;
    }

    /**
     * Creates a patient as an empty object.
     */
    public Pacjent() {
        imie = "";
        nazwisko = "";
        pesel = "";
        telefon = "";
        adres = "";
        flagi = "";
        upowaznienia = "";
        lekarzProwadzacy = 0;
        idPacjent = -1;
    }

    /**
     * Creates a patient by their id
     *
     * @param id    id of a patient as stored in the database
     */
    public Pacjent(int id) {
        imie = "";
        nazwisko = "";
        pesel = "";
        telefon = "";
        adres = "";
        flagi = "";
        upowaznienia = "";
        lekarzProwadzacy = 0;
        idPacjent = id;
    }

    /**
     * Finds a patient by their PESEL number.
     *
     * @param database  database that is searched through, see {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param pesel     PESEL number
     * @return id of the patient
     */
    public static int znajdzPacjentaPoPeselu(Database database, String pesel) {
        if (!Database.checkStringsForProperContent(pesel))
            return 0;
        String table = "Pacjenci";
        String what = "id_pacjent";
        String condition = "pesel = " + Database.addCommas(pesel);
        AtomicBoolean result = new AtomicBoolean(false);
        ResultSet res = database.select(what, table, condition, result);
        if (!result.get()) {
            return 0;
        }
        int id = 0;
        try {
            while (res.next()) {
                id = res.getInt(1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "B\u0142\u0105d odczytu danych!", "ERROR", JOptionPane.ERROR_MESSAGE);
            return 0;
        }
        return id;
    }

    /**
     * Finds data (id, name, surname and PESEL) about all patients in the database.
     *
     * @param database      database that is searched through, see {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param result        success or failure of an operation is stored in this object
     * @return     dataset in a ResultSet format
     */
    @Override
    public ResultSet search(Database database, AtomicBoolean result) {
        String table = "Pacjenci";
        String what = "id_pacjent, imie, nazwisko, pesel";
        return (database.select(what, table, result));
    }

    /**
     * Finds all data about one patient, identified by id.
     *
     * @param database  database that is searched through, see {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param id    id of a patient
     * @param result    success or failure will be stored in this object
     * @return  dataset in ResultSet format
     */
    @Override
    public ResultSet search(Database database, int id, AtomicBoolean result) {
        String table = "Pacjenci";
        String what = "id_pacjent, imie, nazwisko, pesel, id_lekarz, telefon, adres, osoby_upowaznione, flagi";
        String condition = "id_pacjent = " + Integer.toString(id);
        return (database.select(what, table, condition, result));
    }

    /**
     * Adds a new patient to the database.
     *
     * @param database  database that will be used, see {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @return  true or false whether the operation succeeded or not
     */
    @Override
    public boolean insertToDatabase(Database database) {
        if (!checkInputData())
            return false;
        String table = "Pacjenci";
        String columns = "imie, nazwisko, pesel, id_lekarz, telefon, adres, osoby_upowaznione, flagi";
        String imiePart = addCommas(imie);
        String nazwiskoPart = addCommas(nazwisko);
        String peselPart = addCommas(pesel);
        String lekarzPart, telefonPart, adresPart, upowaznieniaPart, flagiPart;
        if (lekarzProwadzacy > 0) lekarzPart = Integer.toString(lekarzProwadzacy); else lekarzPart = "NULL";
        if (telefon.isEmpty()) telefonPart = "NULL"; else telefonPart = addCommas(telefon);
        if (adres.isEmpty()) adresPart = "NULL"; else adresPart = addCommas(adres);
        if (upowaznienia.isEmpty()) upowaznieniaPart = "NULL"; else upowaznieniaPart = addCommas(upowaznienia);
        if (flagi.isEmpty()) flagiPart = "NULL"; else flagiPart = addCommas(flagi);
        String params = imiePart + "," + nazwiskoPart + "," + peselPart + "," +
                lekarzPart + "," +
                telefonPart + "," + adresPart + "," + upowaznieniaPart + "," + flagiPart;
        return (database.insert(table, columns, params));
    }

    /**
     * Finds a patient by a given set of data that reside in the created Pacjent object.
     * The conditions are ANDed.
     *
     * @param database  database that is searched through
     * @param result    success or failure is stored here
     * @return  dataset in a ResultSet format
     */
    public ResultSet searchDatabase(Database database, AtomicBoolean result) {
        if (!checkInputData()) {
            result.set(false);
            return null;
        }
        String what = "imie, nazwisko, pesel, adres, telefon, id_lekarz, osoby_upowaznione, flagi, id_pacjent";
        String table = "Pacjenci";
        String imiePart, nazwiskoPart, peselPart, adresPart, telefonPart, id_lekarzPart, upowaznieniaPart, flagiPart;
        if (!imie.isEmpty()) imiePart = "imie = " + addCommas(imie); else
            imiePart = "imie LIKE \'%\'";
        if (!nazwisko.isEmpty()) nazwiskoPart = "nazwisko = " + addCommas(nazwisko); else
            nazwiskoPart = "nazwisko LIKE \'%\'";
        if (!pesel.isEmpty()) peselPart = "pesel = " + addCommas(pesel); else
            peselPart = "pesel LIKE \'%\'";
        if (!adres.isEmpty()) adresPart = "adres LIKE " + addCommas("%"+adres+"%"); else
            adresPart = "(adres LIKE \'%\' OR adres IS NULL)";
        if (!telefon.isEmpty()) telefonPart = "telefon = " + addCommas(telefon); else
            telefonPart = "(telefon LIKE \'%\' OR telefon IS NULL)";
        if (lekarzProwadzacy >= 0) id_lekarzPart = "id_lekarz = " + Integer.toString(lekarzProwadzacy); else
            id_lekarzPart = "(id_lekarz LIKE \'%\' OR id_lekarz IS NULL)";
        if (!upowaznienia.isEmpty()) upowaznieniaPart = "osoby_upowaznione LIKE " + addCommas("%"+upowaznienia+"%"); else
            upowaznieniaPart = "(osoby_upowaznione LIKE \'%\' OR osoby_upowaznione IS NULL)";
        if (!flagi.isEmpty()) flagiPart = "flagi LIKE " + addCommas("%"+flagi+"%"); else
            flagiPart = "(flagi LIKE \'%\' OR flagi IS NULL)";
        String conditions = imiePart + " AND " + nazwiskoPart + " AND " + peselPart + " AND " +
                adresPart + " AND " + telefonPart + " AND " + id_lekarzPart + " AND " +
                upowaznieniaPart + " AND " + flagiPart;
        return (database.select(what, table, conditions, result));
    }

    /**
     * Removes patient from a database.
     *
     * @param database  a database that will be used, see {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @return  whether operation succeeded or not
     */
    @Override
    public boolean removeFromDatabase(Database database) {
        String table = "Pacjenci";
        String where = "id_pacjent = " + Integer.toString(idPacjent);
        return (database.delete(table, where));
    }

    /**
     * Updates data of a patient in a database
     *
     * @param database  database that will be used, see {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @return  whether operation succeeded or not
     */
    @Override
    public boolean modifyInDatabase(Database database) {
        if (!checkInputData())
            return false;
        String table = "Pacjenci";
        String columns = "imie, nazwisko, pesel, id_lekarz, telefon, adres, osoby_upowaznione, flagi, id_pacjent";
        String imiePart, nazwiskoPart, peselPart, lekarzPart, telefonPart, adresPart, upowaznieniaPart, flagiPart;
        imiePart = "imie = " + addCommas(imie);
        nazwiskoPart = "nazwisko = " + addCommas(nazwisko);
        peselPart = "pesel = " + addCommas(pesel);
        if (lekarzProwadzacy > 0) {
            lekarzPart = "id_lekarz = " + Integer.toString(lekarzProwadzacy);
        } else {
            lekarzPart = "id_lekarz = NULL";
        }
        if (telefon.isEmpty()) telefonPart = "telefon = NULL"; else telefonPart = "telefon = " + addCommas(telefon);
        if (adres.isEmpty()) adresPart = "adres = NULL"; else adresPart = "adres = " + addCommas(adres);
        if (upowaznienia.isEmpty()) upowaznieniaPart = "osoby_upowaznione = NULL"; else upowaznieniaPart = "osoby_upowaznione = " + addCommas(upowaznienia);
        if (flagi.isEmpty()) flagiPart = "flagi = NULL"; else flagiPart = "flagi = " + addCommas(flagi);
        String data = imiePart + "," + nazwiskoPart + "," + peselPart + "," +
                lekarzPart + "," +
                telefonPart + "," + adresPart + "," + upowaznieniaPart + "," + flagiPart;
        String condition = "id_pacjent = " + Integer.toString(idPacjent);
        return (database.update(table, data, condition));
    }
    private String imie, nazwisko, pesel, telefon, adres, flagi, upowaznienia;
    private int lekarzProwadzacy, idPacjent;

    /**
     * Checks whether fields in a Pacjent object are valid.
     * Invokes {@link net.sawannaniz.databaseclient.dbutils.Database#checkStringsForProperContent(Vector)}
     *
     * @return  whether data is valid or not
     */
    private boolean checkInputData() {
        Vector<String> strarr = new Vector<String>();
        strarr.add(imie);
        strarr.add(nazwisko);
        strarr.add(pesel);
        strarr.add(telefon);
        strarr.add(adres);
        strarr.add(upowaznienia);
        strarr.add(flagi);
        return (Database.checkStringsForProperContent(strarr));
    }

    /**
     * Adds parentheses (') before and after a string.
     * Useful when providing strings for MariaDB queries.
     * Invokes {@link net.sawannaniz.databaseclient.dbutils.Database#addCommas(String)}
     *
     * @param s     a given string
     * @return  the string with parentheses
     */
    private String addCommas(String s) {
        return Database.addCommas(s);
    }
}
