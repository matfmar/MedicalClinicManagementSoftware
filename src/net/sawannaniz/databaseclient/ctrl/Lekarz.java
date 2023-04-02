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
        id_lekarz = -1;
    }
    public Lekarz() {
        imie = "";
        nazwisko = "";
        pwz = "";
        telefon = "";
        specjalizacje = "";
        id_lekarz = -1;
    }
    public Lekarz(int id) {
        imie = "";
        nazwisko = "";
        pwz = "";
        telefon = "";
        specjalizacje = "";
        id_lekarz = id;
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

    @Override
    public boolean removeFromDatabase(Database database) {
        String table = "Lekarze";
        String where = "id_lekarz = " + Integer.toString(id_lekarz);
        return (database.delete(table, where));
    }

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
