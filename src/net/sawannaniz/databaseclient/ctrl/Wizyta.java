package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.ctrl.*;
import net.sawannaniz.databaseclient.dbutils.Database;

import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class Wizyta extends ImplicitSearchingClass implements SaveableToPrzychodnia {
    public Wizyta() {

    }
    @Override
    public ResultSet search(Database database, AtomicBoolean result) {
        return null;
    }
    @Override
    public ResultSet search(Database database, int id, AtomicBoolean result) {
        return null;
    }
    public boolean insertToDatabase(Database database) {
        return false;
    }
    public boolean removeFromDatabase(Database database) {
        return false;
    }
    public boolean modifyInDatabase(Database database) {
        return false;
    }
}
