package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.Database;

import java.util.Vector;

public class Lekarz implements SaveableToPrzychodnia {
    public Lekarz(String i, String n, String p, String t, String s) {
        imie = i;
        nazwisko = n;
        pwz = p;
        telefon = t;
        specjalizacje = s;
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
        String result = "\'" + s + "\'";
        return result;
    }
}
