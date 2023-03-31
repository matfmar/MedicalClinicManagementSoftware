package net.sawannaniz.databaseclient.gui;

import net.sawannaniz.databaseclient.ctrl.Lekarz;
import net.sawannaniz.databaseclient.dbutils.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class WyszukajLekarzaWindow extends JFrame {
    public WyszukajLekarzaWindow(Database db) {
        super("Wyszukaj lekarza");
        database = db;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //COMPONENTS
        JLabel label1 = new JLabel("Imie: ");
        JLabel label2 = new JLabel("Nazwisko: ");
        JLabel label3 = new JLabel("Numer PWZ: ");
        JLabel label4 = new JLabel("Specjalizacja: ");
        JTextField imieTextField = new JTextField(10);
        JTextField nazwiskoTextField = new JTextField(10);
        JTextField pwzTextField = new JTextField(10);
        JTextField specjalizacjaTextField = new JTextField(10);
        JButton wyszukajButton = new JButton("Wyszukaj");
        //TABLE
        DefaultTableModel dtm = new DefaultTableModel();
        JTable table = new JTable(dtm);
        dtm.addColumn("Imie");
        dtm.addColumn("Nazwisko");
        dtm.addColumn("PWZ");
        dtm.addColumn("Telefon");
        dtm.addColumn("Specjalizacje");
        JScrollPane scrTable = new JScrollPane(table);
        //PANELS
        JPanel panelImie = new JPanel();
        JPanel panelNazwisko = new JPanel();
        JPanel panelPWZ = new JPanel();
        JPanel panelSpecjalizacja = new JPanel();
        JPanel panelTable = new JPanel();
        panelImie.add(label1); panelImie.add(imieTextField);
        panelNazwisko.add(label2); panelNazwisko.add(nazwiskoTextField);
        panelPWZ.add(label3); panelPWZ.add(pwzTextField);
        panelSpecjalizacja.add(label4); panelSpecjalizacja.add(specjalizacjaTextField);
        panelTable.add(scrTable);
        //EVENTS SETUP
        wyszukajButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dtm.setRowCount(0);
                String imie = imieTextField.getText(); imie = imie.trim();
                String nazwisko = nazwiskoTextField.getText(); nazwisko = nazwisko.trim();
                String pwz = pwzTextField.getText(); pwz = pwz.trim();
                String specjalizacja = specjalizacjaTextField.getText(); specjalizacja = specjalizacja.trim();
                Lekarz lekarzDoWyszukania = new Lekarz(imie, nazwisko, pwz, "", specjalizacja);
                AtomicBoolean result = new AtomicBoolean(false);
                ResultSet wynikiWyszukiwania = lekarzDoWyszukania.searchDatabase(database, result);
                if (!result.get()) {
                    JOptionPane.showMessageDialog(null, "Nie udalo sie odczytac danych", "Blad", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String imieRes, nazwiskoRes, pwzRes, telefonRes, specjalizacjeRes;
                try {
                    while (wynikiWyszukiwania.next()) {
                        imieRes = wynikiWyszukiwania.getString(1);
                        nazwiskoRes = wynikiWyszukiwania.getString(2);
                        pwzRes = wynikiWyszukiwania.getString(3);
                        telefonRes = wynikiWyszukiwania.getString(4);
                        specjalizacjeRes = wynikiWyszukiwania.getString(5);
                        dtm.addRow(new Object[] {imieRes, nazwiskoRes, pwzRes, telefonRes, specjalizacjeRes});
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,"Blad odczytu kursora", "Blad", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        //PACK EVERYTHING TOGETHER
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panelImie);
        getContentPane().add(panelNazwisko);
        getContentPane().add(panelPWZ);
        getContentPane().add(panelSpecjalizacja);
        getContentPane().add(wyszukajButton);
        getContentPane().add(panelTable);
        pack();
        setVisible(true);

    }
    private Database database;
}
