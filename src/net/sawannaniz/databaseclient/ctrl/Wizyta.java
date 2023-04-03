package net.sawannaniz.databaseclient.ctrl;

import net.sawannaniz.databaseclient.ctrl.*;
import net.sawannaniz.databaseclient.dbutils.Database;

import javax.swing.*;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Wizyta extends ImplicitSearchingClass implements SaveableToPrzychodnia {
    public Wizyta(String pesel, int id_lek, int id_pom, String d) {
        nazwiskoPacjent = "";
        peselPacjent = pesel;
        data = d;
        id_lekarz = id_lek;
        id_pomieszczenie = id_pom;
        id_pacjent = -1;
    }
    public Wizyta(String nazwisko, int id_lek, int id_pom) {
        nazwiskoPacjent = nazwisko;
        peselPacjent = "";
        data = "";
        id_lekarz = id_lek;
        id_pomieszczenie = id_pom;
        id_pacjent = -1;
    }
    @Override
    public ResultSet search(Database database, AtomicBoolean result) {
        return null;
    }
    @Override
    public ResultSet search(Database database, int id, AtomicBoolean result) {
        return null;
    }
    public ResultSet searchDatabase(Database database, String dataOd, String dataDo, AtomicBoolean result) {
        if ((!checkInputData()) || ((!checkDatetime(dataOd)) || (!checkDatetime(dataDo)))) {
            result.set(false);
            return null;
        }
        String realDataOd = "STR_TO_DATE(" + addCommas(dataOd) + ", " + addCommas("%Y-%m-%d %h:%i") + ")";
        String realDataDo = "STR_TO_DATE(" + addCommas(dataDo) + ", " + addCommas("%Y-%m-%d %h:%i") + ")";
        String what = "Wizyty.data, Pacjenci.nazwisko, Pacjenci.imie, Pacjenci.pesel, Wizyty.id_lekarz, Wizyty.id_pomieszczenie, Wizyty.id_wizyta";
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

        ///////////////////////
        if (!checkInputData())
            return false;
        String table = "Pacjenci";
        String columns = "imie, nazwisko, pesel, id_lekarz, telefon, adres, osoby_upowaznione, flagi";
        String imiePart = addCommas(imie);
        String nazwiskoPart = addCommas(nazwisko);
        String peselPart = addCommas(pesel);
        String lekarzPart, telefonPart, adresPart, upowaznieniaPart, flagiPart;
        if (lekarzProwadzacy > 0) lekarzPart = Integer.toString(lekarzProwadzacy); else lekarzPart = "NULL";
        if (telefon.isEmpty()) telefonPart = "NULL"; else telefonPart = addCommas(telefon);
        if (adres.isEmpty()) adresPart = "NULL"; else adresPart = addCommas(adres);
        if (upowaznienia.isEmpty()) upowaznieniaPart = "NULL"; else upowaznieniaPart = addCommas(upowaznienia);
        if (flagi.isEmpty()) flagiPart = "NULL"; else flagiPart = addCommas(flagi);
        String params = imiePart + "," + nazwiskoPart + "," + peselPart + "," +
                lekarzPart + "," +
                telefonPart + "," + adresPart + "," + upowaznieniaPart + "," + flagiPart;
        return (database.insert(table, columns, params));;
    }
    public boolean removeFromDatabase(Database database) {
        return false;
    }
    public boolean modifyInDatabase(Database database) {
        return false;
    }
    private int id_lekarz, id_pomieszczenie, id_pacjent;
    private String nazwiskoPacjent, data, peselPacjent;
    private boolean checkInputData() {
        Vector<String> strarr = new Vector<String>();
        strarr.add(nazwiskoPacjent);
        return (Database.checkStringsForProperContent(strarr));
    }
    private boolean checkDatetime(String s) {
        return (Database.checkStringForProperDatetime(s));
    }
    private String addCommas(String s) {
        return Database.addCommas(s);
    }
}
