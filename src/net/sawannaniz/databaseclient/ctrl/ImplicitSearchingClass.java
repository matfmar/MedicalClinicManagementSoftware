package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.Database;

import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ImplicitSearchingClass {
    public ImplicitSearchingClass() {
    }
    public ResultSet search(Database database, String table, String what, AtomicBoolean result) {
        return (database.select(what, table, result));
    }
    public abstract ResultSet search(Database database, AtomicBoolean result);
}
