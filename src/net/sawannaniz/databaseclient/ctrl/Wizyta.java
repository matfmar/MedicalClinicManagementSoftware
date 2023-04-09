package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.dbutils.Database;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class that represents an appointment in a clinic, as stored in the Przychodnia database.
 */
public class Wizyta extends Searching implements SaveableToPrzychodnia {
    /**
     * Creates an appointment based on its id.
     *
     * @param id    id of the appointment
     */
    public Wizyta(int id) {
        nazwiskoPacjent = "";
        peselPacjent = "";
        data = "";
        id_lekarz = -1;
        id_pomieszczenie = -1;
        id_pacjent = -1;
        id_wizyta = id;
        wpis = "";
        icd10 = "";
    }

    /**
     * Creates an appointment based on its id, the description and icd-10 code.
     * Used when making the appointment's realization.
     *
     * @param w the description of the appointment
     * @param i icd-10 code
     * @param id_w  appointment's id
     */
    public Wizyta(String w, String i, int id_w) {
        nazwiskoPacjent = "";
        peselPacjent = "";
        data = "";
        id_lekarz = -1;
        id_pomieszczenie = -1;
        id_pacjent = -1;
        id_wizyta = id_w;
        wpis = w;
        icd10 = i;
    }

    /**
     * Creates an appointment based on the patient's PESEL, physician's id, date, room's id and patient's id.
     *
     * @param pesel patient's PESEL number
     * @param id_lek    id of a physician involved in the appointment
     * @param id_pom    id of a room where the appointment will take place
     * @param d     date of the appointment in 'RRRR-MM-DD HH:MM' format
     * @param id_pac    id of the patient involved in the appointment
     */
    public Wizyta(String pesel, int id_lek, int id_pom, String d, int id_pac) {
        nazwiskoPacjent = "";
        peselPacjent = pesel;
        data = d;
        id_lekarz = id_lek;
        id_pomieszczenie = id_pom;
        id_pacjent = id_pac;
        id_wizyta = -1;
        wpis = "";
        icd10 = "";
    }

    /**
     * Creates an appointment based on patient's surname, physician's id and room's id.
     *
     * @param nazwisko  patient's surname
     * @param id_lek    physician's id
     * @param id_pom    room's id
     */
    public Wizyta(String nazwisko, int id_lek, int id_pom) {
        nazwiskoPacjent = nazwisko;
        peselPacjent = "";
        data = "";
        id_lekarz = id_lek;
        id_pomieszczenie = id_pom;
        id_pacjent = -1;
        id_wizyta = -1;
        wpis = "";
        icd10 = "";
    }

    /**
     * @deprecated
     * Returns all appointments in the database.
     * In the current version there was no need for such a function, so it returns nothing.
     * Defined for the sake of completeness.
     * Don't use it, because it does not do anything.
     *
     * @param database  database that will be searched through, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param result    whether the operation succeeded or not
     * @return  null
     */
    @Override
    public ResultSet search(Database database, AtomicBoolean result) {
        return null;
    }

    /**
     * Finds an appointment identified by id.
     *
     * @param database database that will be searched through, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param id    id of the object, as stored in the database
     * @param result    whether the operation succeeded or not
     * @return  dataset in ResultSet format
     */
    @Override
    public ResultSet search(Database database, int id, AtomicBoolean result) {
        String table = "Wizyty";
        String what = "opis_wizyty, icd10, id_lekarz, zrealizowana";
        String condition = "id_wizyta = " + id_wizyta;
        return (database.select(what, table, condition, result));
    }

    /**
     * Finds a set of appointments that meet the given criteria.
     * The criteria are partially given as parameters, and partially as fields of the object (provided upon its creation).
     * The criteria are ANDed.
     * The query joins two tables of the Przychodnia database and combines the data together.
     *
     * @param database  database that will be searched through, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param dataOd    the earliest date of the appointment
     * @param dataDo    the latest date of the appointment
     * @param result    whether the operation was successful or not
     * @return          dataset in ResultSet format.
     */
    public ResultSet searchDatabase(Database database, String dataOd, String dataDo, AtomicBoolean result) {
        if ((!checkInputData()) || ((!checkDatetime(dataOd)) || (!checkDatetime(dataDo)))) {
            result.set(false);
            return null;
        }
        String realDataOd = addCommas(dataOd);
        String realDataDo = addCommas(dataDo);
        String what = "Wizyty.data, Pacjenci.nazwisko, Pacjenci.imie, Pacjenci.pesel, Wizyty.id_lekarz, Wizyty.id_pomieszczenie, Wizyty.id_wizyta, Wizyty.zrealizowana";
        String table = "Wizyty INNER JOIN Pacjenci ON Wizyty.id_pacjent = Pacjenci.id_pacjent";
        String nazwiskoPart, idLekarzPart, idPomieszczeniePart, dataPart;
        if (nazwiskoPacjent.isEmpty()) nazwiskoPart = "Pacjenci.nazwisko LIKE \'%\'";
            else nazwiskoPart = "Pacjenci.nazwisko = " + addCommas(nazwiskoPacjent);
        if (id_lekarz < 0) idLekarzPart = "(Wizyty.id_lekarz LIKE \'%\' OR Wizyty.id_lekarz IS NULL)";
            else idLekarzPart = "Wizyty.id_lekarz = " + Integer.toString(id_lekarz);
        if (id_pomieszczenie < 0) idPomieszczeniePart = "(Wizyty.id_pomieszczenie LIKE \'%\' OR Wizyty.id_pomieszczenie IS NULL)";
            else idPomieszczeniePart = "Wizyty.id_pomieszczenie = " + Integer.toString(id_pomieszczenie);
        if (dataOd.isEmpty() && dataDo.isEmpty()) dataPart = "(Wizyty.data LIKE \'%\' OR Wizyty.data IS NULL)";
        else if (!dataOd.isEmpty() && !dataDo.isEmpty()) dataPart = "(Wizyty.data BETWEEN " + realDataOd + " AND " + realDataDo + ")";
        else if (!dataOd.isEmpty() && dataDo.isEmpty()) dataPart = "Wizyty.data > " + realDataOd;
        else dataPart = "Wizyty.data < " + realDataDo;
        String where = nazwiskoPart + " AND " + idLekarzPart + " AND " + idPomieszczeniePart + " AND " + dataPart;
        return (database.select(what, table, where, result));
    }

    /**
     * Adds a new appointment into database.
     * The parameters are provided as object's fields upon creation of the object.
     *
     * @param database database that will be used, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @return  whether the operation was successful or not
     */
    public boolean insertToDatabase(Database database) {
        if (!checkInputData() || !checkDatetime(data))
            return false;
        String table = "Wizyty";
        String columns = "id_pacjent, id_lekarz, id_pomieszczenie, data";
        String id_pacjentPart = Integer.toString(id_pacjent);
        String id_lekarzPart = Integer.toString(id_lekarz);
        //String dataPart = Database.convertStringToDatetime(data);
        String dataPart = addCommas(data);
        String id_pomieszczeniePart;
        if (id_pomieszczenie > 0)
            id_pomieszczeniePart = Integer.toString(id_pomieszczenie);
        else
            id_pomieszczeniePart = "NULL";
        String params = id_pacjentPart + ", " + id_lekarzPart + ", " + id_pomieszczeniePart + ", " + dataPart;
        return (database.insert(table, columns, params));
    }

    /**
     * Makes realization of an appointment.
     * In fact, it updates the database on the description, icd-10 code, date-of-change and checks that the appointment was done.
     *
     * @param database  database that will be modified, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @return  whether the operation was successful or not
     */
    public boolean realize(Database database) {
        if (!Database.checkStringForICD10(icd10))
            return false;
        String command = "UPDATE Wizyty SET opis_wizyty = ?, icd10 = ?, data_modyfikacji = now(), zrealizowana = 1 WHERE id_wizyta = ?";
        return (database.update(command, wpis, icd10, id_wizyta));
    }

    /**
     * Updates data about the appointment.
     * The appointment has to be already realized. Otherwise, it will fail.
     *
     * @param database database that will be modified
     * @return  whether the operation was successful or not
     */
    public boolean edit(Database database) {
        if (!Database.checkStringForICD10(icd10))
            return false;
        String command = "UPDATE Wizyty SET opis_wizyty = ?, icd10 = ?, data_modyfikacji = now() WHERE id_wizyta = ?";
        return (database.update(command, wpis, icd10, id_wizyta));
    }

    /**
     * Removes an appointment from the database.
     * Parameters are provided as the object's fields upon its creation.
     *
     * @param database  database that will be used, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @return  whether the operation was successful or not
     */
    public boolean removeFromDatabase(Database database) {
        String table = "Wizyty";
        String where = "id_wizyta = " + Integer.toString(id_wizyta);
        return (database.delete(table, where));
    }

    /**
     * @deprecated
     * This method is provided just for the sake of completeness.
     * It does nothing, because it is not necessary for the application to allow modification of appointments that have not been realized yet.
     * Don't use it.
     *
     * @param database database that will be used, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @return  false
     */
    public boolean modifyInDatabase(Database database) {
        return false;
    }
    private int id_lekarz, id_pomieszczenie, id_pacjent, id_wizyta;
    private String nazwiskoPacjent, data, peselPacjent, wpis, icd10;

    /**
     * Checks the patient's surname and patient's PESEL number whether the data is valid.
     * Calls {@link net.sawannaniz.databaseclient.dbutils.Database#checkStringsForProperContent(String)} method.
     *
     * @return whether the mentioned data are valid or not
     */
    private boolean checkInputData() {
        Vector<String> strarr = new Vector<String>();
        strarr.add(nazwiskoPacjent);
        strarr.add(peselPacjent);
        return (Database.checkStringsForProperContent(strarr));
    }

    /**
     * Checks whether the provided String is written in a correct datetime format.
     * Calls {@link net.sawannaniz.databaseclient.dbutils.Database#checkStringForProperDatetime(String)} method.
     *
     * @param s String that will be checked for fulfilling datetime format
     * @return  whether the format is correct or not
     */
    private boolean checkDatetime(String s) {
        return (Database.checkStringForProperDatetime(s));
    }

    /**
     * Adds parentheses (') before and after the String.
     * Makes writing SQL queries easier.
     *
     * @param s     String to be modified
     * @return      String after modification
     */
    private String addCommas(String s) {
        return Database.addCommas(s);
    }
}
