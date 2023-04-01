package net.sawannaniz.databaseclient.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import net.sawannaniz.databaseclient.ctrl.*;
import net.sawannaniz.databaseclient.dbutils.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class WyszukajPacjentaWindow extends JFrame {
    public WyszukajPacjentaWindow(Database db) {
        super("Znajdz pacjenta");
        database = db;
        id_cb = -1;
        vtIndexes = new Vector<Integer>();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //COMPONENTS
        JLabel label1 = new JLabel("Imie: ");
        JLabel label2 = new JLabel("Nazwisko: ");
        JLabel label3 = new JLabel("PESEL: ");
        JLabel label4 = new JLabel("Telefon: ");
        JLabel label5 = new JLabel("Adres: ");
        JLabel label6 = new JLabel("Osoby upowaznione: ");
        JLabel label7 = new JLabel("Lekarz prowadzacy: ");
        JLabel label8 = new JLabel("Adnotacje specjalne: ");
        JTextField imieTextField = new JTextField(10);
        JTextField nazwiskoTextField = new JTextField(10);
        JTextField peselTextField = new JTextField(10);
        JTextField telefonTextField = new JTextField(10);
        JTextField adresTextField = new JTextField(15);
        JTextField upowaznieniaTextField = new JTextField(15);
        JTextField adnotacjeTextField = new JTextField(15);
        JButton znajdzButton = new JButton("Znajdz");
        AtomicBoolean result = new AtomicBoolean(false);
        JComboBox cb = createComboBox(result);
        if (!result.get()) {
            JOptionPane.showMessageDialog(null, "Failed to load comboBox","Error",JOptionPane.ERROR_MESSAGE);
            znajdzButton.setEnabled(false);
            return;
        }
        else {
            znajdzButton.setEnabled(true);
        }
        id_cb = cb.getSelectedIndex();
        DefaultTableModel dtm = new DefaultTableModel();
        JTable table = new JTable(dtm);
        dtm.addColumn("Imie");
        dtm.addColumn("Nazwisko");
        dtm.addColumn("PESEL");
        dtm.addColumn("Adres");
        dtm.addColumn("Telefon");
        dtm.addColumn("Lekarz prowadzący");
        dtm.addColumn("Upowaznienia");
        dtm.addColumn("Adnotacje");
        JScrollPane scrTable = new JScrollPane(table);
        //PANELS
        JPanel panelImie = new JPanel();
        JPanel panelNazwisko = new JPanel();
        JPanel panelPesel = new JPanel();
        JPanel panelTelefon = new JPanel();
        JPanel panelAdres = new JPanel();
        JPanel panelUpowaznienia = new JPanel();
        JPanel panelAdnotacje = new JPanel();
        JPanel panelLekarz = new JPanel();
        panelImie.add(label1); panelImie.add(imieTextField);
        panelNazwisko.add(label2); panelNazwisko.add(nazwiskoTextField);
        panelPesel.add(label3); panelPesel.add(peselTextField);
        panelTelefon.add(label4); panelTelefon.add(telefonTextField);
        panelAdres.add(label5); panelAdres.add(adresTextField);
        panelUpowaznienia.add(label6); panelUpowaznienia.add(upowaznieniaTextField);
        panelAdnotacje.add(label8); panelAdnotacje.add(adnotacjeTextField);
        panelLekarz.add(label7); panelLekarz.add(cb);
        //SETUP EVENTS
        cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                id_cb = cb.getSelectedIndex();
            }
        });
        znajdzButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dtm.setRowCount(0);
                String imie, nazwisko, pesel, telefon, adres, upowaznienia, adnotacje;
                int id_lekarz = -1;
                if (id_cb < 0)
                    return;
                imie = imieTextField.getText(); imie = imie.trim();
                nazwisko = nazwiskoTextField.getText(); nazwisko = nazwisko.trim();
                pesel = peselTextField.getText(); pesel = pesel.trim();
                telefon = telefonTextField.getText(); telefon = telefon.trim();
                adres = adresTextField.getText(); adres = adres.trim();
                upowaznienia = upowaznieniaTextField.getText(); upowaznienia = upowaznienia.trim();
                adnotacje = adnotacjeTextField.getText(); adnotacje = adnotacje.trim();
                id_lekarz = vtIndexes.get(id_cb);   //powinno sie zgadzac
                Pacjent pacjentDoWyszukania = new Pacjent(imie, nazwisko, pesel, telefon, adres, adnotacje, upowaznienia, id_lekarz);
                AtomicBoolean result = new AtomicBoolean(false);
                ResultSet wynikiWyszukiwania = pacjentDoWyszukania.searchDatabase(database, result);
                if (!result.get()) {
                    JOptionPane.showMessageDialog(null, "Nie udalo sie odczytac danych", "Blad", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String imieRes, nazwiskoRes, peselRes, adresRes, telefonRes, upowaznieniaRes, flagiRes, lekarz;
                int id_lekarzRes;
                try {
                    while (wynikiWyszukiwania.next()) {
                        imieRes = wynikiWyszukiwania.getString(1);
                        nazwiskoRes = wynikiWyszukiwania.getString(2);
                        peselRes = wynikiWyszukiwania.getString(3);
                        adresRes = wynikiWyszukiwania.getString(4);
                        telefonRes = wynikiWyszukiwania.getString(5);
                        id_lekarzRes = wynikiWyszukiwania.getInt(6);
                        upowaznieniaRes = wynikiWyszukiwania.getString(7);
                        flagiRes = wynikiWyszukiwania.getString(8);
                        lekarz = znajdzLekarzaDoTabelki(id_lekarzRes);
                        dtm.addRow(new Object[] {imieRes, nazwiskoRes, peselRes,
                                adresRes, telefonRes, lekarz,
                                upowaznieniaRes, flagiRes});
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,"Blad odczytu kursora", "Blad", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        //GET EVERYTHING TOGETHER
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panelImie);
        getContentPane().add(panelNazwisko);
        getContentPane().add(panelPesel);
        getContentPane().add(panelTelefon);
        getContentPane().add(panelAdres);
        getContentPane().add(panelUpowaznienia);
        getContentPane().add(panelAdnotacje);
        getContentPane().add(panelLekarz);
        getContentPane().add(znajdzButton);
        getContentPane().add(scrTable);
        pack();
        setVisible(true);
    }
    private Database database;
    private int id_cb;
    private Vector<Integer> vtIndexes;
    private JComboBox createComboBox(AtomicBoolean result) {
        Lekarz lekarz = new Lekarz();
        ResultSet resultSet = lekarz.search(database, result);
        if (!result.get())
            return null;
        Vector<String> vtCombo = new Vector<String>();
        vtCombo.add("");    //pierwszy wiersz [0] pusty
        vtIndexes.add(-1);  //tak samo - analogicznie
        String imie, nazwisko, pwz, total;
        int id_lekarz = 0;
        try {
            while (resultSet.next()) {
                id_lekarz = resultSet.getInt(1);
                imie = resultSet.getString(2);
                nazwisko = resultSet.getString(3);
                pwz = resultSet.getString(4);
                total = imie + " " + nazwisko + ", " + pwz;
                vtCombo.add(total);
                vtIndexes.add(id_lekarz);
            }
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Fail to read cursor", "Blad", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        JComboBox cb = new JComboBox(vtCombo);
        return cb;
    }
    private String znajdzLekarzaDoTabelki(int id) {
        AtomicBoolean result = new AtomicBoolean(false);
        Lekarz lekarz = new Lekarz();
        ResultSet res = lekarz.search(database, id, result);
        if (!result.get()) {
            JOptionPane.showMessageDialog(null,"Blad szukania lekarza: " + Integer.toString(id) + " do tabelki",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return "";
        }
        String s = "";
        try {
            while (res.next()) {
                s = res.getString(2) + " " + res.getString(3);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"Blad kursora lekarza: " + Integer.toString(id) + " do tabelki",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return "";
        }
        return s;
    }
}
