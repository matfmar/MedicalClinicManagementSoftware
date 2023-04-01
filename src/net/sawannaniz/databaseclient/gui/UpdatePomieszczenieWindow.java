package net.sawannaniz.databaseclient.gui;

import javax.swing.*;
import net.sawannaniz.databaseclient.dbutils.*;
public class UpdatePomieszczenieWindow extends JFrame {
    public UpdatePomieszczenieWindow(Database db) {
        super("Aktualizuj pomieszczenie");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        database = db;
    }
    private Database database;
}
