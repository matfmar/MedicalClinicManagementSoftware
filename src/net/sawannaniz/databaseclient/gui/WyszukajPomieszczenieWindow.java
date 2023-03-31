package net.sawannaniz.databaseclient.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import net.sawannaniz.databaseclient.ctrl.Pomieszczenie;
import net.sawannaniz.databaseclient.dbutils.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.sql.ResultSet;
public class WyszukajPomieszczenieWindow extends JFrame {
    public WyszukajPomieszczenieWindow(Database db) {
        super("Znajdz pomieszczenie");
        database = db;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //COMPONENTS
        JLabel label1 = new JLabel("numer: ");
        JLabel label2 = new JLabel("pietro: ");
        JTextField numerTextField = new JTextField(5);
        JTextField pietroTextField = new JTextField(5);
        JButton buttonSzukaj = new JButton("Szukaj");
        //TABLE
        DefaultTableModel dtm = new DefaultTableModel();
        JTable table = new JTable(dtm);
        dtm.addColumn("numer");
        dtm.addColumn("pietro");
        JScrollPane scrollPane1 = new JScrollPane(table);
        //PANELS
        JPanel panel1 = new JPanel();
        panel1.add(label1);
        panel1.add(numerTextField);
        JPanel panel2 = new JPanel();
        panel2.add(label2);
        panel2.add(pietroTextField);

        //EVENTS SETUP
        buttonSzukaj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dtm.setRowCount(0);     //clears table
                String numerPomieszczenia = numerTextField.getText();
                String pietroPomieszczeniaStr = pietroTextField.getText();
                pietroPomieszczeniaStr = pietroPomieszczeniaStr.trim();
                numerPomieszczenia = numerPomieszczenia.trim();
                boolean jestPietro;
                int pietroPomieszczenia = 0;
                if (!(pietroPomieszczeniaStr.isEmpty())) {
                    try {
                        pietroPomieszczenia = Integer.parseInt(pietroPomieszczeniaStr);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null,
                                "Conversion problem",
                                "Conversion problem",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    jestPietro = true;
                }
                else {
                    jestPietro = false;
                }
                Pomieszczenie pomieszczenieDoWyszukania = new Pomieszczenie(numerPomieszczenia, pietroPomieszczenia);
                AtomicBoolean result = new AtomicBoolean(true);
                ResultSet wynikiWyszukiwania = pomieszczenieDoWyszukania.searchDatabase(database, jestPietro, result);
                if (!result.get()) {
                    JOptionPane.showMessageDialog(null,"Nie udalo sie odczytac danych", "Blad", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String pietroRes="", numerRes="";
                try {
                    while (wynikiWyszukiwania.next()) {
                        numerRes = wynikiWyszukiwania.getString(1);
                        pietroRes = wynikiWyszukiwania.getString(2);
                        dtm.addRow(new Object[] {numerRes, pietroRes});
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Blad odczytu z kursora", "Blad", JOptionPane.ERROR_MESSAGE);
                    return;
                }

            }
        });

        //PACKING EVERYTHING
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panel1);
        getContentPane().add(panel2);
        getContentPane().add(buttonSzukaj);
        getContentPane().add(scrollPane1);
        pack();
        setVisible(true);
    }
    private Database database;
}
