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

public class UpdatePomieszczenieWindow extends JFrame {
    public UpdatePomieszczenieWindow(Database db, DefaultTableModel dt, JTable t, Vector<Integer> vtIdPomieszczenia) {
        super("Aktualizuj pomieszczenie");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        database = db;
        dtm = dt;
        table = t;
        vtId = vtIdPomieszczenia;
        //COMPONENTS
        JLabel label1 = new JLabel("pietro: ");
        JLabel label2 = new JLabel("numer pomieszczenia: ");
        JTextField pietroTextField = new JTextField(5);
        JTextField numerTextField = new JTextField(5);
        JButton buttonUpdate = new JButton("Aktualizuj !");
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
        int idSelected = table.getSelectedRow();
        int idPomieszczenie = vtId.get(idSelected);
        Pomieszczenie pomieszczenieDoZmiany = new Pomieszczenie(idPomieszczenie);
        AtomicBoolean result = new AtomicBoolean(false);
        ResultSet danePomieszczenia = pomieszczenieDoZmiany.search(database, idPomieszczenie, result);
        if (!result.get()) {
            JOptionPane.showMessageDialog(null,"Failed to get data","ERROR",JOptionPane.ERROR_MESSAGE);
            buttonUpdate.setEnabled(false);
        }
        else {
            try {
                while (danePomieszczenia.next()) {
                    pietroTextField.setText(Integer.toString(danePomieszczenia.getInt(3)));
                    numerTextField.setText(danePomieszczenia.getString(2));
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,"Failed to read cursor","ERROR",JOptionPane.ERROR_MESSAGE);
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
                int pietro = -1;
                try {
                    pietro= Integer.parseInt(pietroStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null,
                            "Conversion problem",
                            "Conversion problem",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Pomieszczenie pomieszczenieDoZmiany = new Pomieszczenie(idPomieszczenie, numer, pietro);
                if (pomieszczenieDoZmiany.modifyInDatabase(database)) {
                    JOptionPane.showMessageDialog(null,"Zaktualizowano pomieszczenie", "OK", JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(null, "Blad aktualizacji pomieszczenia", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                dtm.setValueAt(numer, idSelected, 0);
                dtm.setValueAt(pietro, idSelected, 1);
            }
        });

        //PACKING EVERYTHING
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panelNumer);
        getContentPane().add(panelPietro);
        getContentPane().add(panelButtons);
        pack();
        setVisible(true);
    }
    public void close() {
        dispose();
    }
    private Database database;
    private DefaultTableModel dtm;
    private JTable table;
    private Vector<Integer> vtId;

}
