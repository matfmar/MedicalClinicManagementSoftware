package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.*;
import java.util.Vector;

public class Pomieszczenie implements SaveableToPrzychodnia {
    public Pomieszczenie(String nr, int p) {
        numer = nr;
        pietro = p;
    }
    public boolean insertToDatabase(Database database) {
        String table = "Pomieszczenia";
        String columns = "numer, pietro";
        Vector<String> strTable = new Vector<String>();
        strTable.add(numer);
        strTable.add(Integer.toString(pietro));
        if (!(Database.checkStringsForProperContent(strTable)))
            return false;
        String params = "\'"+numer+"\'" + ", " + "\'"+Integer.toString(pietro)+"\'";
        return (database.insert(table, columns, params));
    }
    public boolean removeFromDatabase(Database database) {
        return true;
    }
    public boolean modifyInDatabase(Database database) {
        return true;
    }
    private String numer;
    private int pietro;
}
