package net.sawannaniz.databaseclient.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import net.sawannaniz.databaseclient.ctrl.Lekarz;
import net.sawannaniz.databaseclient.ctrl.Pacjent;
import net.sawannaniz.databaseclient.dbutils.*;

public class UpdatePacjentWindow extends JFrame {
    public UpdatePacjentWindow(Database db, DefaultTableModel dt, JTable tab, Vector<Integer> v) {
        super("Aktualizuj dane pacjenta");
        database = db;
        dtm = dt;
        table = tab;
        vtIdPacjenci = v;
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
        JTextField teefonTextField = new JTextField(10);
        JTextField adresTextField = new JTextField(15);
        JTextField upowaznieniaTextField = new JTextField(15);
        JTextField adnotacjeTextField = new JTextField(15);
        JButton updateButton = new JButton("Aktualizuj");
        JButton zamknijButton = new JButton("Zamknij");
        AtomicBoolean result = new AtomicBoolean(false);
        JComboBox cb = createComboBox(result);
        if (!result.get()) {
            JOptionPane.showMessageDialog(null, "Failed to load comboBox","Error",JOptionPane.ERROR_MESSAGE);
        }
        if (cb != null)
            id_cb = cb.getSelectedIndex();
        else
            id_cb = -1;
        //PANELS
        JPanel panelImie = new JPanel();
        JPanel panelNaziwsko = new JPanel();
        JPanel panelPesel = new JPanel();
        JPanel panelTelefon = new JPanel();
        JPanel panelAdres = new JPanel();
        JPanel panelUpowaznienia = new JPanel();
        JPanel panelAdnotacje = new JPanel();
        JPanel panelLekarz = new JPanel();
        JPanel panelButtons = new JPanel();
        panelImie.add(label1); panelImie.add(imieTextField);
        panelNaziwsko.add(label2); panelNaziwsko.add(nazwiskoTextField);
        panelPesel.add(label3); panelPesel.add(peselTextField);
        panelTelefon.add(label4); panelTelefon.add(teefonTextField);
        panelAdres.add(label5); panelAdres.add(adresTextField);
        panelUpowaznienia.add(label6); panelUpowaznienia.add(upowaznieniaTextField);
        panelAdnotacje.add(label8); panelAdnotacje.add(adnotacjeTextField);
        if (cb != null) {
            panelLekarz.add(label7);
            panelLekarz.add(cb);
        }
        panelButtons.add(updateButton); panelButtons.add(zamknijButton);

        //SETTING UP DATA
        int idSelected = table.getSelectedRow();
        int idPacjent = vtIdPacjenci.get(idSelected);
        Pacjent pacjentDoZmiany = new Pacjent(idPacjent);
        AtomicBoolean resultX = new AtomicBoolean(false);
        ResultSet danePacjenta = pacjentDoZmiany.search(database, idPacjent, resultX);
        if (!resultX.get()) {
            JOptionPane.showMessageDialog(null,"Failed to get data","ERROR",JOptionPane.ERROR_MESSAGE);
            updateButton.setEnabled(false);
        }
        else {
            try {
                while (danePacjenta.next()) {
                    imieTextField.setText(danePacjenta.getString(2));
                    nazwiskoTextField.setText(danePacjenta.getString(3));
                    peselTextField.setText(danePacjenta.getString(4));
                    int idLekarz = danePacjenta.getInt(5);
                    teefonTextField.setText(danePacjenta.getString(6));
                    adresTextField.setText(danePacjenta.getString(7));
                    upowaznieniaTextField.setText(danePacjenta.getString(8));
                    adnotacjeTextField.setText(danePacjenta.getString(9));
                    if (cb != null) {
                        int idToBeSelected = fillComboBox(idLekarz);
                        cb.setSelectedIndex(idToBeSelected);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,"Failed to read in filling combobox","ERROR",JOptionPane.ERROR_MESSAGE);
                updateButton.setEnabled(false);
            }
        }


        //EVENTS SETUP
        if (cb != null) {
            cb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    id_cb = cb.getSelectedIndex();
                }
            });
        }
        zamknijButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                close();
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String imie, nazwisko, pesel, telefon, adres, upowaznienia, adnotacje;
                int id_lekarz = -1;
                imie = imieTextField.getText(); imie = imie.trim();
                nazwisko = nazwiskoTextField.getText(); nazwisko = nazwisko.trim();
                pesel = peselTextField.getText(); pesel = pesel.trim();
                telefon = teefonTextField.getText(); telefon = telefon.trim();
                adres = adresTextField.getText(); adres = adres.trim();
                upowaznienia = upowaznieniaTextField.getText(); upowaznienia = upowaznienia.trim();
                adnotacje = adnotacjeTextField.getText(); adnotacje = adnotacje.trim();
                if (id_cb >= 0)
                    id_lekarz = vtIndexes.get(id_cb);   //powinno sie zgadzac
                else
                    id_lekarz = -1;
                if (imie.isEmpty() || (nazwisko.isEmpty() || pesel.isEmpty())) {
                    JOptionPane.showMessageDialog(null,"Za malo danych", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Pacjent pacjentDoZmiany = new Pacjent(imie, nazwisko, pesel, telefon, adres, adnotacje, upowaznienia, id_lekarz);
                if (pacjentDoZmiany.modifyInDatabase(database)) {
                    JOptionPane.showMessageDialog(null,"Zaktualizowano pacjenta", "OK", JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(null, "Blad aktualizacji pacjenta", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String znalezionyLekarz;
                if (id_cb >= 0) {
                    znalezionyLekarz = cb.getSelectedItem().toString();
                }
                else {
                    znalezionyLekarz = "";
                }
                dtm.setValueAt(imie, idSelected, 0);
                dtm.setValueAt(nazwisko, idSelected, 1);
                dtm.setValueAt(pesel, idSelected, 2);
                dtm.setValueAt(adres, idSelected, 3);
                dtm.setValueAt(telefon, idSelected, 4);
                dtm.setValueAt(upowaznienia, idSelected, 6);
                dtm.setValueAt(adnotacje, idSelected, 7);
                dtm.setValueAt(znalezionyLekarz, idSelected, 5);
            }
        });
        //PACKING EVERYTHING
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panelImie);
        getContentPane().add(panelNaziwsko);
        getContentPane().add(panelPesel);
        getContentPane().add(panelTelefon);
        getContentPane().add(panelAdres);
        getContentPane().add(panelUpowaznienia);
        getContentPane().add(panelAdnotacje);
        getContentPane().add(panelLekarz);
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
    private Vector<Integer> vtIdPacjenci;
    private int id_cb;
    private Vector<Integer> vtIndexes;
    private JComboBox createComboBox(AtomicBoolean result) {
        Lekarz lekarz = new Lekarz();
        ResultSet resultSet = lekarz.search(database, result);
        if (!result.get())
            return null;
        Vector<String> vtCombo = new Vector<String>();
        String imie, nazwisko, pwz, total;
        int id_lekarz = 0;
        vtCombo.add("");    //lekarz is not obligatory
        vtIndexes.add(-1);  //as above
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
    private int fillComboBox(int id_lekarz) {
        int i = 0;
        for (int el : vtIndexes) {
            if (el == id_lekarz)
                return i;
            i++;
        }
        return 0;
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
