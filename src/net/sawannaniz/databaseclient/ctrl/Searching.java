package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.Database;

import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Searching {
    public Searching() {}   //default constructor
    public ResultSet search(Database database, String custom, AtomicBoolean result) {   //fully custom search
        ResultSet resSet = database.select(custom, result);
        return resSet;
    }
    public abstract ResultSet search(Database database, AtomicBoolean result);  //search for everything in a table
    public abstract ResultSet search(Database database, int id, AtomicBoolean result);  //search by id
}
