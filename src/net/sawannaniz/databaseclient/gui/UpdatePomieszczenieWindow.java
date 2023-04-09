package net.sawannaniz.databaseclient.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import net.sawannaniz.databaseclient.dbutils.*;
import net.sawannaniz.databaseclient.ctrl.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Responsible for a window which enables updating information about rooms.
 *
 * @author Mateusz Marzec
 * @version 1.0
 * @since 2023-04-09
 */
public class UpdatePomieszczenieWindow extends JFrame {
    /**
     * Creates the window.
     *
     * @param db database with opened connection, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param dt DefaultTableModel object, a starting point from which room was selected
     * @param t JTable object, from which room was selected
     * @param vtIdPomieszczenia a vector of Integers - ids of rooms
     */
    public UpdatePomieszczenieWindow(Database db, DefaultTableModel dt, JTable t, Vector<Integer> vtIdPomieszczenia) {
        super("Edycja danych");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        database = db;
        dtm = dt;
        table = t;
        vtId = vtIdPomieszczenia;
        //BASIC TEST
        int idSelected = table.getSelectedRow();
        if (idSelected == -1) {
            JOptionPane.showMessageDialog(null, "Nie wybrano nic!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        //COMPONENTS
        JLabel label1 = new JLabel("Pi\u0119tro: ");
        JLabel label2 = new JLabel("Numer: ");
        JTextField pietroTextField = new JTextField(5);
        JTextField numerTextField = new JTextField(5);
        JButton buttonUpdate = new JButton("Aktualizuj");
        JButton buttonZamknij = new JButton("Zamknij");
        //PANELS
        JPanel panelPietro = new JPanel();
        panelPietro.add(label1);
        panelPietro.add(pietroTextField);
        JPanel panelNumer = new JPanel();
        panelNumer.add(label2);
        panelNumer.add(numerTextField);
        JPanel panelButtons = new JPanel();
        panelButtons.add(buttonUpdate);
        panelButtons.add(buttonZamknij);
        //SETTING UP DATA
        int idPomieszczenie = vtId.get(idSelected);
        Pomieszczenie pomieszczenieDoZmiany = new Pomieszczenie(idPomieszczenie);
        AtomicBoolean result = new AtomicBoolean(false);
        ResultSet danePomieszczenia = pomieszczenieDoZmiany.search(database, idPomieszczenie, result);
        if (!result.get()) {
            JOptionPane.showMessageDialog(null,"B\u0142\u0105d dodawania danych!","ERROR",JOptionPane.ERROR_MESSAGE);
            buttonUpdate.setEnabled(false);
        }
        else {
            try {
                while (danePomieszczenia.next()) {
                    pietroTextField.setText(danePomieszczenia.getString(3));
                    numerTextField.setText(danePomieszczenia.getString(2));
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,"B\u0142\u0105d dodawania danych!","ERROR",JOptionPane.ERROR_MESSAGE);
                buttonUpdate.setEnabled(false);
            }
        }
        //EVENTS SETUP
        buttonZamknij.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                close();
            }
        });
        buttonUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String numer = numerTextField.getText(); numer = numer.trim();
                if (numer.isEmpty()) {
                    JOptionPane.showMessageDialog(null,"Za malo danych!", "ERROR",JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String pietroStr = pietroTextField.getText(); pietroStr = pietroStr.trim();
                int pietro = 9876;
                boolean bezPietra = true;
                if (!pietroStr.isEmpty()) {
                    try {
                        pietro = Integer.parseInt(pietroStr);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null,
                                "Chyba wpisano z\u0142\u0105 liczb\u0119.",
                                "ERROR",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    bezPietra = false;
                }
                Pomieszczenie pomieszczenieDoZmiany = new Pomieszczenie(idPomieszczenie, numer, pietro);
                pomieszczenieDoZmiany.setIfBezPietra(bezPietra);
                if (pomieszczenieDoZmiany.modifyInDatabase(database)) {
                    JOptionPane.showMessageDialog(null,"Zaktualizowano pomieszczenie", "INFO", JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(null, "B\u0142\u0105d aktualizacji pomieszczenia!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                dtm.setValueAt(numer, idSelected, 0);
                if (!bezPietra)
                    dtm.setValueAt(pietro, idSelected, 1);
                else
                    dtm.setValueAt("", idSelected, 1);
            }
        });

        //PACKING EVERYTHING
        setSize(550, 150);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panelNumer);
        getContentPane().add(panelPietro);
        getContentPane().add(panelButtons);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * CLoses the window.
     */
    public void close() {
        dispose();
    }
    private Database database;
    private DefaultTableModel dtm;
    private JTable table;
    private Vector<Integer> vtId;

}
