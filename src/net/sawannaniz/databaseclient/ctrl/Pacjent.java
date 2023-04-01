package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.Database;

import java.sql.ResultSet;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Pacjent extends ImplicitSearchingClass implements SaveableToPrzychodnia{
    public Pacjent(String i, String n, String p, String t, String a, String f, String u, int lP) {
        imie = i;
        nazwisko = n;
        pesel = p;
        telefon = t;
        adres = a;
        flagi = f;
        upowaznienia = u;
        lekarzProwadzacy = lP;
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
        String what = "id_pacjent, imie, nazwisko, pesel";
        String condition = "id_pacjent = " + Integer.toString(id);
        return (database.select(what, table, condition, result));
    }

    @Override
    public boolean insertToDatabase(Database database) {
        if (!checkInputData())
            return false;
        String table = "Pacjenci";
        String columns = "imie, nazwisko, pesel, id_lekarz, telefon, adres, osoby_upowaznione, flagi";
        String params = addCommas(imie) + "," + addCommas(nazwisko) + "," + addCommas(pesel) + "," +
                Integer.toString(lekarzProwadzacy) + "," +
                addCommas(telefon) + "," + addCommas(adres) + "," + addCommas(upowaznienia) + "," + addCommas(flagi);
        return (database.insert(table, columns, params));
    }
    public ResultSet searchDatabase(Database database, AtomicBoolean result) {
        if (!checkInputData()) {
            result.set(false);
            return null;
        }
        String what = "imie, nazwisko, pesel, adres, telefon, id_lekarz, osoby_upowaznione, flagi";
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
        return false;
    }

    @Override
    public boolean modifyInDatabase(Database database) {
        return false;
    }
    private String imie, nazwisko, pesel, telefon, adres, flagi, upowaznienia;
    private int lekarzProwadzacy;
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
