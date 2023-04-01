package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.Database;

import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ImplicitSearchingClass {
    public ImplicitSearchingClass() {   //default constructor
    }
    public ResultSet search(Database database, String table, String what, AtomicBoolean result) {   //fully custom search
        return (database.select(what, table, result));
    }
    public abstract ResultSet search(Database database, AtomicBoolean result);  //search for everything in a table
    public abstract ResultSet search(Database database, int id, AtomicBoolean result);  //search by id
}
