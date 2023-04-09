package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.*;

/**
 * The interface that should be implemented by any object that is going to be saved in the Przychodnia database.
 * Demands definition of functions providing insertion, removal and update to/from/in a database.
 */
public interface SaveableToPrzychodnia {
    /**
     * A function responsible for adding the object to the database.
     *
     * @param database database that will be used, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @return  whether the operation succeeded or not
     */
    public boolean insertToDatabase(Database database);

    /**
     * A function responsible for removal of the object from the database.
     *
     * @param database  database that will be used, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @return  whether the operation succeeded or not
     */
    public boolean removeFromDatabase(Database database);

    /**
     * A function responsible for data update in a database.
     *
     * @param database database that will be used, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @return  whether the operation succeeded or not
     */
    public boolean modifyInDatabase(Database database);
}
