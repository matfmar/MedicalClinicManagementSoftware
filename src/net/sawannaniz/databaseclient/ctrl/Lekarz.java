package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.Database;

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
        return false;
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
}
