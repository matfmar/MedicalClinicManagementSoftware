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
        String table = "Pacjentci";
        String what = "id_pacjent, imie, nazwisko, pesel";
        return (database.select(what, table, result));
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
    public ResultSet searchDatabase() {
        return null;
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
