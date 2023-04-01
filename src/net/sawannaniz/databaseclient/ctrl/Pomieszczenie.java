package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.*;

import java.sql.ResultSet;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Pomieszczenie extends ImplicitSearchingClass implements SaveableToPrzychodnia {
    public Pomieszczenie(int idPom, String nr, int p) {
        numer = nr;
        pietro = p;
        id = idPom;
        bezPietra = false;
    }
    public Pomieszczenie(int idPom) {
        numer = "";
        pietro = -1;
        bezPietra = false;
        id = idPom;
    }
    public Pomieszczenie(String nr, int p) {
        numer = nr;
        pietro = p;
        bezPietra = false;
        id = 0;
    }
    public Pomieszczenie(String nr, int p, boolean bp) {
        numer = nr;
        pietro = p;
        bezPietra = bp;
        id = 0;
    }
    @Override
    public ResultSet search(Database database, int id, AtomicBoolean result) {
        String table = "Pomieszczenia";
        String what = "id_pomieszczenie, numer, pietro";
        String condition = "id_pomieszczenie = " + Integer.toString(id);
        return (database.select(what, table, condition, result));
    }
    @Override
    public ResultSet search(Database database, AtomicBoolean result) {
        String table = "Pomieszczenia";
        String what = "id_pomieszczenie, numer, pietro";
        return (database.select(what, table, result));
    }

    public boolean insertToDatabase(Database database) {
        String table = "Pomieszczenia";
        if (!checkNumerAndPietro())
            return false;
        String columns = "", params = "";
        if (bezPietra) {
            columns = "numer";
            params = "\'" + numer + "\'";
        }
        else {
            columns = "numer, pietro";
            params = "\'" + numer + "\'" + ", " + "\'" + Integer.toString(pietro) + "\'";
        }
        return (database.insert(table, columns, params));
    }
    public ResultSet searchDatabase(Database database, boolean jestPietro, AtomicBoolean result) {
        if (!checkNumerAndPietro()) {
            result.set(false);
            return null;
        }
        boolean jestNumer = !(numer.isEmpty());
        String what = "numer, pietro, id_pomieszczenie";
        String table = "Pomieszczenia";
        String numerPart = "", pietroPart = "", conditions = "";
        if (jestNumer) {
            numerPart = " numer = \'" + numer + "\'";
            conditions = numerPart;
        }
        if (jestPietro) {
            pietroPart = " pietro = " + Integer.toString(pietro);
            conditions = pietroPart;
        }
        if (jestNumer && jestPietro) {
            conditions = numerPart + " AND " + pietroPart;
        }
        if (conditions.isEmpty()) {
            return (database.select(what, table, result));
        }
        else {
            return (database.select(what, table, conditions, result));
        }
    }
    public boolean removeFromDatabase(Database database) {
        String table = "Pomieszczenia";
        String where = "id_pomieszczenie = " + Integer.toString(id);
        return (database.delete(table, where));
    }
    public boolean modifyInDatabase(Database database) {
        String table = "Pomieszczenia";
        if (!checkNumerAndPietro())
            return false;
        String numerPart = "numer = " + Database.addCommas(numer);
        String pietroPart = "pietro = " + Integer.toString(pietro);
        String data = numerPart + ", " + pietroPart;
        String condition = "id_pomieszczenie = " + Integer.toString(id);
        return (database.update(table, data, condition));
    }
    private String numer;
    private int pietro, id;
    private boolean bezPietra;
    private boolean checkNumerAndPietro() {
        Vector<String> strTable = new Vector<String>();
        strTable.add(numer);
        strTable.add(Integer.toString(pietro));
        return (Database.checkStringsForProperContent(strTable));
    }
}
