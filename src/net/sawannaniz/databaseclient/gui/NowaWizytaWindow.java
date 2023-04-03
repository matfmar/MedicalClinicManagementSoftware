package net.sawannaniz.databaseclient.gui;

import javax.swing.*;

import net.sawannaniz.databaseclient.ctrl.Lekarz;
import net.sawannaniz.databaseclient.ctrl.Pacjent;
import net.sawannaniz.databaseclient.ctrl.Pomieszczenie;
import net.sawannaniz.databaseclient.ctrl.Wizyta;
import net.sawannaniz.databaseclient.dbutils.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class NowaWizytaWindow extends JFrame {
    public NowaWizytaWindow(Database db) {
        super("Tworzenie nowej wizyty");
        database = db;
        id_cb_lek = -1;
        id_cb_pom = -1;
        vt_id_lek = new Vector<Integer>();
        vt_id_pom = new Vector<Integer>();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //COMPONENTS
        JLabel label1 = new JLabel("PESEL pacjenta: ");
        JTextField peselTextField = new JTextField(10);
        JButton peselButton = new JButton("Znajdz PESEL pacjenta");
        JLabel label2 = new JLabel("Lekarz: ");
        AtomicBoolean result = new AtomicBoolean(false);
        JComboBox cbLekarz = createComboBoxLekarz(result);
        id_cb_lek = comboboxCheckInfo(cbLekarz, result);
        JLabel label3 = new JLabel("Pomieszczenie: ");
        JComboBox cbPomieszczenie = createComboBoxPomieszczenie(result);
        id_cb_pom = comboboxCheckInfo(cbPomieszczenie, result);
        JLabel label4 = new JLabel("Data: ");
        JTextField dataTextField = new JTextField("RRRR-MM-DD HH:MM");
        JButton dodajButton = new JButton("Dodaj");
        JButton zamknijButton = new JButton("Zamknij");
        //PANELS
        JPanel panelPacjent = new JPanel(); panelPacjent.add(label1); panelPacjent.add(peselTextField); panelPacjent.add(peselButton);
        JPanel panelLekarz = new JPanel();
        if (cbLekarz != null) { panelLekarz.add(label2); panelLekarz.add(cbLekarz); }
        JPanel panelPomieszczenie = new JPanel();
        if (cbPomieszczenie != null) { panelPomieszczenie.add(label3); panelPomieszczenie.add(cbPomieszczenie); }
        JPanel panelData = new JPanel(); panelData.add(label4); panelData.add(dataTextField);
        JPanel panelButtons = new JPanel(); panelButtons.add(dodajButton); panelButtons.add(zamknijButton);

        //EVENTS SETUP
        zamknijButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                close();
            }
        });
        peselButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                WyszukajPacjentaWindow wyszukajPacjentaWindow = new WyszukajPacjentaWindow(database, peselTextField, true);
            }
        });
        if (cbLekarz != null) {
            cbLekarz.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    id_cb_lek = cbLekarz.getSelectedIndex();
                }
            });
        }
        if (cbPomieszczenie != null) {
            cbPomieszczenie.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    id_cb_pom = cbPomieszczenie.getSelectedIndex();
                }
            });
        }
        dodajButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String pesel, data;
                int id_lekarz = -1, id_pomieszczenie = -1;
                pesel = peselTextField.getText(); pesel = pesel.trim();
                data = dataTextField.getText(); data = data.trim();
                if (id_cb_lek >= 0)
                    id_lekarz = vt_id_lek.get(id_cb_lek);   //powinno sie zgadzac
                else
                    id_lekarz = -1;
                if (id_cb_pom >= 0)
                    id_pomieszczenie = vt_id_pom.get(id_cb_pom);
                else
                    id_pomieszczenie = -1;
                if (data.isEmpty() || (id_lekarz <= 0 || pesel.isEmpty())) {
                    JOptionPane.showMessageDialog(null,"Za malo danych", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Wizyta nowaWizyta = new Wizyta(pesel, id_lekarz, id_pomieszczenie, data);
                if (nowaWizyta.insertToDatabase(database)) {
                    ////////////
                    JOptionPane.showMessageDialog(null, "Udalo sie dodac do bazy", "OK", JOptionPane.INFORMATION_MESSAGE);
                    imieTextField.setText("");
                    nazwiskoTextField.setText("");
                    peselTextField.setText("");
                    adresTextField.setText("");
                    teefonTextField.setText("");
                    adnotacjeTextField.setText("");
                    upowaznieniaTextField.setText("");
                    id_lekarz = -1;
                    if (cb != null)
                        cb.setSelectedIndex(0);
                }
                else {
                    JOptionPane.showMessageDialog(null,"Nie udalo sie dodac do bazy", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //PACKING EVERYTHING
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panelPacjent);
        getContentPane().add(panelLekarz);
        getContentPane().add(panelPomieszczenie);
        getContentPane().add(panelData);
        getContentPane().add(panelButtons);
        pack();
        setVisible(true);

    }
    public void close() {
        dispose();
    }
    private Database database;
    private Vector<Integer> vt_id_lek, vt_id_pom;
    private int id_cb_lek, id_cb_pom;
    private int comboboxCheckInfo(JComboBox cb, AtomicBoolean result) {
        if (!result.get()) {
            JOptionPane.showMessageDialog(null,"fail to load combobox", "error",JOptionPane.ERROR_MESSAGE);
            return -1;
        }
        if (cb != null) {
            return cb.getSelectedIndex();
        }
        else {
            JOptionPane.showMessageDialog(null,"fail to load combobox", "error",JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }
    private JComboBox createComboBoxLekarz(AtomicBoolean result) {
        Lekarz lekarz = new Lekarz();
        ResultSet resultSet = lekarz.search(database, result);
        if (!result.get())
            return null;
        Vector<String> vtCombo = new Vector<String>();
        String imie, nazwisko, pwz, total;
        int id_lekarz = 0;
        vtCombo.add("");    //lekarz is not obligatory
        vt_id_lek.add(-1);  //as above
        try {
            while (resultSet.next()) {
                id_lekarz = resultSet.getInt(1);
                imie = resultSet.getString(2);
                nazwisko = resultSet.getString(3);
                pwz = resultSet.getString(4);
                total = imie + " " + nazwisko + ", " + pwz;
                vtCombo.add(total);
                vt_id_lek.add(id_lekarz);
            }
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Fail to read cursor", "Blad", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        JComboBox cb = new JComboBox(vtCombo);
        return cb;
    }
    private JComboBox createComboBoxPomieszczenie(AtomicBoolean result) {
        Pomieszczenie pomieszczenie = new Pomieszczenie();
        ResultSet resultSet = pomieszczenie.search(database, result);
        if (!result.get())
            return null;
        Vector<String> vtCombo = new Vector<String>();
        vtCombo.add("");    //pierwszy wiersz [0] pusty
        vt_id_pom.add(-1);  //tak samo - analogicznie
        String numer, total;
        int pietro;
        int id_pomieszczenie = 0;
        try {
            while (resultSet.next()) {
                id_pomieszczenie = resultSet.getInt(1);
                numer = resultSet.getString(2);
                pietro = resultSet.getInt(3);
                total = numer + ", p. " + Integer.toString(pietro);
                vtCombo.add(total);
                vt_id_pom.add(id_pomieszczenie);
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
