package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.*;
public interface SaveableToPrzychodnia {
    public boolean insertToDatabase(Database database);
    public boolean removeFromDatabase(Database database);
    public boolean modifyInDatabase(Database database);
}
