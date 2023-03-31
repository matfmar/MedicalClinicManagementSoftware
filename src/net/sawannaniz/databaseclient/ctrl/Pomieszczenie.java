package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.*;

import java.sql.ResultSet;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Pomieszczenie implements SaveableToPrzychodnia {
    public Pomieszczenie(String nr, int p) {
        numer = nr;
        pietro = p;
        bezPietra = false;
    }
    public Pomieszczenie(String nr, int p, boolean bp) {
        numer = nr;
        pietro = p;
        bezPietra = bp;
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
        boolean jestNumer = !(numer.isEmpty());
        String what = "numer, pietro";
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
        return true;
    }
    public boolean modifyInDatabase(Database database) {
        return true;
    }
    private String numer;
    private int pietro;
    private boolean bezPietra;
    private boolean checkNumerAndPietro() {
        Vector<String> strTable = new Vector<String>();
        strTable.add(numer);
        strTable.add(Integer.toString(pietro));
        return (Database.checkStringsForProperContent(strTable));
    }
}
