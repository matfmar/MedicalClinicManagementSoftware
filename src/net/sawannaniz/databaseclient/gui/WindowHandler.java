package net.sawannaniz.databaseclient.gui;

import java.awt.event.WindowAdapter;
import javax.swing.*;
import java.awt.event.*;
import net.sawannaniz.databaseclient.dbutils.*;

/**
 * Responsible for closing the application with a preceding prompt.
 *
 * @author Mateusz Marzec
 * @version 1.0
 * @since 2023-04-09
 */
public class WindowHandler extends WindowAdapter {
    /**
     * Creates the object.
     *
     * @param fr JFrame object that will be closed at last
     * @param db database with opened connection which will be closed, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     */
    public WindowHandler(JFrame fr, Database db) {
        frame = fr;
        database = db;
    }

    /**
     * Action upon window closing.
     * Provides prompt, closes the database and closes the application.
     *
     * @param event the event to be processed
     */
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
