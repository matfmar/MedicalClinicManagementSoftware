package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.Database;

import java.sql.ResultSet;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Lekarz extends ImplicitSearchingClass implements SaveableToPrzychodnia {
    public Lekarz(String i, String n, String p, String t, String s) {
        imie = i;
        nazwisko = n;
        pwz = p;
        telefon = t;
        specjalizacje = s;
    }
    public Lekarz() {
        imie = "";
        nazwisko = "";
        pwz = "";
        telefon = "";
        specjalizacje = "";
    }
    @Override
    public ResultSet search(Database database, AtomicBoolean result) {
        String table = "Lekarze";
        String what = "id_lekarz, imie, nazwisko, pwz";
        return (database.select(what, table, result));
    }
    @Override
    public ResultSet search(Database database, int id, AtomicBoolean result) {
        String table = "Lekarze";
        String what = "id_lekarz, imie, nazwisko, pwz";
        String condition = "id_lekarz = " + Integer.toString(id);
        return (database.select(what, table, condition, result));
    }
    @Override
    public boolean insertToDatabase(Database database) {
        if (!checkInputData())
            return false;
        String table = "Lekarze";
        String columns = "imie, nazwisko, pwz, telefon, specjalizacje";
        String params = addCommas(imie) + "," + addCommas(nazwisko) + "," + addCommas(pwz) + "," +
                addCommas(telefon) + "," + addCommas(specjalizacje);
        return (database.insert(table, columns, params));
    }
    public ResultSet searchDatabase(Database database, AtomicBoolean result) {
        if (!checkInputData()) {
            result.set(false);
            return null;
        }
        String what = "imie, nazwisko, pwz, telefon, specjalizacje";
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

    @Override
    public boolean removeFromDatabase(Database database) {
        return false;
    }

    @Override
    public boolean modifyInDatabase(Database database) {
        return false;
    }
    private String imie, nazwisko, pwz, telefon, specjalizacje;
    private boolean checkInputData() {
        Vector<String> strarr = new Vector<String>();
        strarr.add(imie);
        strarr.add(nazwisko);
        strarr.add(pwz);
        strarr.add(telefon);
        strarr.add(specjalizacje);
        return (Database.checkStringsForProperContent(strarr));
    }
    private String addCommas(String s) {
        return Database.addCommas(s);
    }
}
