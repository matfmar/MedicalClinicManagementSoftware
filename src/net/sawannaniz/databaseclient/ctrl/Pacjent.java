package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.Database;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Pacjent extends Searching implements SaveableToPrzychodnia{
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

    @Override
    public ResultSet search(Database database, AtomicBoolean result) {
        String table = "Pacjenci";
        String what = "id_pacjent, imie, nazwisko, pesel";
        return (database.select(what, table, result));
    }
    @Override
    public ResultSet search(Database database, int id, AtomicBoolean result) {
        String table = "Pacjenci";
        String what = "id_pacjent, imie, nazwisko, pesel, id_lekarz, telefon, adres, osoby_upowaznione, flagi";
        String condition = "id_pacjent = " + Integer.toString(id);
        return (database.select(what, table, condition, result));
    }

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

    @Override
    public boolean removeFromDatabase(Database database) {
        String table = "Pacjenci";
        String where = "id_pacjent = " + Integer.toString(idPacjent);
        return (database.delete(table, where));
    }

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
    private String addCommas(String s) {
        return Database.addCommas(s);
    }
}
