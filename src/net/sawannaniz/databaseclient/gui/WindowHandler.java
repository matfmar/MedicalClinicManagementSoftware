package net.sawannaniz.databaseclient.gui;

import java.awt.event.WindowAdapter;
import javax.swing.*;
import java.awt.event.*;
import net.sawannaniz.databaseclient.dbutils.*;

public class WindowHandler extends WindowAdapter {
    public WindowHandler(JFrame fr, Database db) {
        frame = fr;
        database = db;
    }
    @Override
    public void windowClosing(WindowEvent event) {
        if (JOptionPane.showConfirmDialog(frame,
                "Czy zako\u0144czy\u0107 program?",
                "PYTANIE",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            database.close();
            System.exit(0);
        }
    }
    private JFrame frame;
    private Database database;
}
