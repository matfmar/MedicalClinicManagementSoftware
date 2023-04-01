package net.sawannaniz.databaseclient.gui;

import javax.swing.*;
import net.sawannaniz.databaseclient.dbutils.*;
import net.sawannaniz.databaseclient.ctrl.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class NowyPacjentWindow extends JFrame {
    public NowyPacjentWindow(Database db) {
        super("Dodaj nowego pacjenta");
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
        JTextField teefonTextField = new JTextField(10);
        JTextField adresTextField = new JTextField(15);
        JTextField upowaznieniaTextField = new JTextField(15);
        JTextField adnotacjeTextField = new JTextField(15);
        JButton dodajButton = new JButton("Dodaj");
        JButton zamknijButton = new JButton("Zamknij");
        dodajButton.setEnabled(false);
        AtomicBoolean result = new AtomicBoolean(false);
        JComboBox cb = createComboBox(result);
        if (!result.get()) {
            JOptionPane.showMessageDialog(null, "Failed to load comboBox","Error",JOptionPane.ERROR_MESSAGE);
            dodajButton.setEnabled(false);
            return;
        }
        else {
            dodajButton.setEnabled(true);
        }
        id_cb = cb.getSelectedIndex();
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
        panelLekarz.add(label7); panelLekarz.add(cb);
        panelButtons.add(dodajButton); panelButtons.add(zamknijButton);
        //EVENTS SETUP
        cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                id_cb = cb.getSelectedIndex();
            }
        });
        zamknijButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                close();
            }
        });
        dodajButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String imie, nazwisko, pesel, telefon, adres, upowaznienia, adnotacje;
                int id_lekarz = -1;
                if (id_cb < 0)
                    return;
                imie = imieTextField.getText();
                nazwisko = nazwiskoTextField.getText();
                pesel = peselTextField.getText();
                telefon = teefonTextField.getText();
                adres = adresTextField.getText();
                upowaznienia = upowaznieniaTextField.getText();
                adnotacje = adnotacjeTextField.getText();
                id_lekarz = vtIndexes.get(id_cb);   //powinno sie zgadzac
                if (imie.isEmpty() || (nazwisko.isEmpty() || pesel.isEmpty())) {
                    JOptionPane.showMessageDialog(null,"Za malo danych", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Pacjent nowyPacjent = new Pacjent(imie, nazwisko, pesel, telefon, adres, adnotacje, upowaznienia, id_lekarz);
                if (nowyPacjent.insertToDatabase(database)) {
                    JOptionPane.showMessageDialog(null, "Udalo sie dodac do bazy", "OK", JOptionPane.INFORMATION_MESSAGE);
                    imieTextField.setText("");
                    nazwiskoTextField.setText("");
                    peselTextField.setText("");
                    adresTextField.setText("");
                    teefonTextField.setText("");
                    adnotacjeTextField.setText("");
                    upowaznieniaTextField.setText("");
                    id_lekarz = -1;
                    cb.setSelectedIndex(0);
                }
                else {
                    JOptionPane.showMessageDialog(null,"Nie udalo sie dodac do bazy", "Error", JOptionPane.ERROR_MESSAGE);
                }
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
}
