package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.*;
import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a room where an appointment takes place, references this entity stored in Przychodnia database.
 *
 * @author Mateusz Marzec
 * @version 1.0
 * @since 2023-04-09
 */
public class Pomieszczenie extends Searching implements SaveableToPrzychodnia {
    /**
     * Creates an empty room.
     */
    public Pomieszczenie() {
        numer = "";
        pietro = -1;
        bezPietra = false;
        id = -1;
    }

    /**
     * Creates a room identified by its number, the floor and its id - as stored in Przychodnia database.
     *
     * @param idPom id of the room
     * @param nr    room number
     * @param p     floor of the room
     */
    public Pomieszczenie(int idPom, String nr, int p) {
        numer = nr;
        pietro = p;
        id = idPom;
        bezPietra = false;
    }

    /**
     * Creates a room by its id, as stored in Przychodnia database.
     *
     * @param idPom id of the room
     */
    public Pomieszczenie(int idPom) {
        numer = "";
        pietro = -1;
        bezPietra = false;
        id = idPom;
    }

    /**
     * Creates a room based on its number and the floor.
     *
     * @param nr    room number
     * @param p     floor of the room
     */
    public Pomieszczenie(String nr, int p) {
        numer = nr;
        pietro = p;
        bezPietra = false;
        id = 0;
    }

    /**
     * Creates a room based on the number, whether the floor is given (if yes, then with floor number too).
     *
     * @param nr    room number
     * @param p     floor number
     * @param bp    whether floor is to be given or not (true or false)
     */
    public Pomieszczenie(String nr, int p, boolean bp) {
        numer = nr;
        pietro = p;
        bezPietra = bp;
        id = 0;
    }

    /**
     * Finds a room in a Przychodnia database identified by id.
     * Returns a String which contains room's number and the floor. If no floor is present, dash is written.
     *
     * @param database database that will be searched through, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param id    id of the room
     * @return      String which contains room's number and the floor
     */
    public static String znajdzPomieszczenieDoTabelki(Database database, int id) {
        AtomicBoolean result = new AtomicBoolean(false);
        Pomieszczenie pomieszczenie = new Pomieszczenie();
        ResultSet res = pomieszczenie.search(database, id, result);
        if (!result.get()) {
            JOptionPane.showMessageDialog(null,"B\u0142\u0105 szukania pomieszczenia: " + Integer.toString(id) + " do tabelki!",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return "";
        }
        String s = "", s2 = "";
        try {
            while (res.next()) {
                s2 = res.getString(3);
                if (s2 == null)
                    s2 = new String("--");
                else if (s2.isEmpty())
                    s2 = "--";
                else if (s2 == "null" || s2 == "NULL")
                    s2 = "--";
                s = res.getString(2) + ", pietro: " +  s2;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"B\u0142\u0105 szukania pomieszczenia: " + Integer.toString(id) + " do tabelki",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return "";
        }
        return s;
    }

    /**
     * Finds data about the particular room (number and the floor) identified by its id.
     *
     * @param database  database that will be searched through, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param id    id of the room
     * @param result    whether searching operation was successful or not
     * @return      dataset in ResultSet format.
     */
    @Override
    public ResultSet search(Database database, int id, AtomicBoolean result) {
        String table = "Pomieszczenia";
        String what = "id_pomieszczenie, numer, pietro";
        String condition = "id_pomieszczenie = " + Integer.toString(id);
        return (database.select(what, table, condition, result));
    }

    /**
     * Finds data (id, number and floor) about all rooms in the database.
     *
     * @param database  database that will be searched through, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param result    whether the operation succeeded or not
     * @return      dataset in ResultSet format
     */
    @Override
    public ResultSet search(Database database, AtomicBoolean result) {
        String table = "Pomieszczenia";
        String what = "id_pomieszczenie, numer, pietro";
        return (database.select(what, table, result));
    }

    /**
     * Adds a new room to the database.
     * Room's data are placed in the object's fields before (upon creation of the object).
     *
     * @param database  database, in which the room will be added, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @return      whether the operation was successful or not
     */
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

    /**
     * Finds the rooms that match the given criteria.
     * The criteria are placed in the object before calling this method (upon creating the object).
     * The criteria are ANDed.
     *
     * @param database  database that will be searched through, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param jestPietro    whether the floor is in the criteria list or not
     * @param result    whether the operation succeeded or not
     * @return          dataset (room number, floor and id) in the ResultSet format
     */
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

    /**
     * Removes the room from the database.
     * The room is identified by id, which is a field of the previously created Pomieszczenie object.
     *
     * @param database  database from which the room will be removed, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @return  whether the operation succeeded or not
     */
    public boolean removeFromDatabase(Database database) {
        String table = "Pomieszczenia";
        String where = "id_pomieszczenie = " + Integer.toString(id);
        return (database.delete(table, where));
    }

    /**
     * Updates data about the room in a database.
     * The room is identified by its id, which is provided as an object's field upon its creation.
     * The new parameters are provided as fields too.
     *
     * @param database  database that will be modified, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @return  whether the operation succeeded or not
     */
    public boolean modifyInDatabase(Database database) {
        String table = "Pomieszczenia";
        if (!checkNumerAndPietro())
            return false;
        String numerPart = "numer = " + Database.addCommas(numer);
        String pietroPart;
        if (!bezPietra)
            pietroPart = "pietro = " + Integer.toString(pietro);
        else {
            pietroPart = "pietro = NULL";
        }
        String data = numerPart + ", " + pietroPart;
        String condition = "id_pomieszczenie = " + Integer.toString(id);
        return (database.update(table, data, condition));
    }

    /**
     * A setter which sets bezPietra field.
     * bezPietra field indicates whether the room contains information about the floor or not.
     *
     * @param ifx   whether the room contains information about the floor or not
     */
    public void setIfBezPietra(boolean ifx) {
        bezPietra = ifx;
    }
    private String numer;
    private int pietro, id;
    private boolean bezPietra;

    /**
     * Checks whether number and floor contain valid data.
     * Calls {@link net.sawannaniz.databaseclient.dbutils.Database#checkStringsForProperContent(String)} static method.
     *
     * @return whether number and floor data are valid
     */
    private boolean checkNumerAndPietro() {
        Vector<String> strTable = new Vector<String>();
        strTable.add(numer);
        strTable.add(Integer.toString(pietro));
        return (Database.checkStringsForProperContent(strTable));
    }
}
