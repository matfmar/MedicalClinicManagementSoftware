package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.ctrl.*;
import net.sawannaniz.databaseclient.dbutils.Database;

import javax.swing.*;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Wizyta extends ImplicitSearchingClass implements SaveableToPrzychodnia {
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
    @Override
    public ResultSet search(Database database, AtomicBoolean result) {
        return null;
    }
    @Override
    public ResultSet search(Database database, int id, AtomicBoolean result) {
        String table = "Wizyty";
        String what = "opis_wizyty, icd10, id_lekarz, zrealizowana";
        String condition = "id_wizyta = " + id_wizyta;
        return (database.select(what, table, condition, result));
    }
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
    public boolean realize(Database database) {
        if (!Database.checkStringForICD10(icd10))
            return false;
        String command = "UPDATE Wizyty SET opis_wizyty = ?, icd10 = ?, data_modyfikacji = now(), zrealizowana = 1 WHERE id_wizyta = ?";
        return (database.update(command, wpis, icd10, id_wizyta));
    }
    public boolean edit(Database database) {
        if (!Database.checkStringForICD10(icd10))
            return false;
        String command = "UPDATE Wizyty SET opis_wizyty = ?, icd10 = ?, data_modyfikacji = now() WHERE id_wizyta = ?";
        return (database.update(command, wpis, icd10, id_wizyta));
    }
    public boolean removeFromDatabase(Database database) {
        String table = "Wizyty";
        String where = "id_wizyta = " + Integer.toString(id_wizyta);
        return (database.delete(table, where));
    }
    public boolean modifyInDatabase(Database database) {
        return false;
    }
    private int id_lekarz, id_pomieszczenie, id_pacjent, id_wizyta;
    private String nazwiskoPacjent, data, peselPacjent, wpis, icd10;
    private boolean checkInputData() {
        Vector<String> strarr = new Vector<String>();
        strarr.add(nazwiskoPacjent);
        strarr.add(peselPacjent);
        return (Database.checkStringsForProperContent(strarr));
    }
    private boolean checkDatetime(String s) {
        return (Database.checkStringForProperDatetime(s));
    }
    private String addCommas(String s) {
        return Database.addCommas(s);
    }
}
