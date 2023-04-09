package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.Database;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class which should be inherited by any object that is going to be searched for in the Przychodnia database.
 * This class provides a defined method for general fully custom search.
 * Demands from the derived classes to provide definition of:
 * <ul>
 *     <li> searching by id,</li>
 *     <li> providing all objects that the database contains </li>
 * </ul>
 *
 */
public abstract class Searching {
    /**
     * Default constructor.
     */
    public Searching() {}   //default constructor

    /**
     * Provides a custom search based on an explicitly defined SQL query provided as a parameter.
     *
     * @param database  database that will be searched through, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param custom    SQL query
     * @param result    whether operation was successful or not
     * @return          dataset in ResultSet format.
     */
    public ResultSet search(Database database, String custom, AtomicBoolean result) {   //fully custom search
        ResultSet resSet = database.select(custom, result);
        return resSet;
    }

    /**
     * A function that is going to return all objects of the specific type.
     *
     * @param database  database that will be searched through, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param result    whether the operation succeeded or not
     * @return          dataset in ResultSet format.
     */
    public abstract ResultSet search(Database database, AtomicBoolean result);  //search for everything in a table

    /**
     * A function that is going to find an entity identified by id.
     *
     * @param database database that will be searched through, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param id    id of the object, as stored in the database
     * @param result    whether the operation succeeded or not
     * @return      dataset in ResultSet format.
     */
    public abstract ResultSet search(Database database, int id, AtomicBoolean result);  //search by id
}
