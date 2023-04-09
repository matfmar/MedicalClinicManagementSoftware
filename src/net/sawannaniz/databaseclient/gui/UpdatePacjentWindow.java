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

/**
 * Responsible for a window which enables updating patient's data.
 *
 * @author Mateusz Marzec
 * @version 1.0
 * @since 2023-04-09
*/
public class UpdatePacjentWindow extends JFrame {
    /**
     * Creates a window.
     *
     * @param db database with opened connection, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param dt DefaultTableModel, a starting point from which patient was selected
     * @param tab   JTable object, a starting point from which patient was selected
     * @param v vector of integers - ids of patients.
     */
    public UpdatePacjentWindow(Database db, DefaultTableModel dt, JTable tab, Vector<Integer> v) {
        super("Edycja danych");
        database = db;
        dtm = dt;
        table = tab;
        vtIdPacjenci = v;
        vtIndexes = new Vector<Integer>();
        //necessary
        int idSelected = table.getSelectedRow();
        if (idSelected == -1) {
            JOptionPane.showMessageDialog(null,"Nic nie wybrano!","ERROR",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //COMPONENTS
        JLabel label1 = new JLabel("Imi\u0107: ");
        JLabel label2 = new JLabel("Nazwisko: ");
        JLabel label3 = new JLabel("PESEL: ");
        JLabel label4 = new JLabel("Telefon: ");
        JLabel label5 = new JLabel("Adres: ");
        JLabel label6 = new JLabel("Osoby upowa\u017cnione: ");
        JLabel label7 = new JLabel("Lekarz prowadz\u0105cy: ");
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
            JOptionPane.showMessageDialog(null, "B\u0142\u0105d szukania danych!","ERROR",JOptionPane.ERROR_MESSAGE);
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
        int idPacjent = vtIdPacjenci.get(idSelected);
        Pacjent pacjentDoZmiany = new Pacjent(idPacjent);
        AtomicBoolean resultX = new AtomicBoolean(false);
        ResultSet danePacjenta = pacjentDoZmiany.search(database, idPacjent, resultX);
        if (!resultX.get()) {
            JOptionPane.showMessageDialog(null,"B\u0142\u0105d szukania danych!","ERROR",JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(null,"B\u0142\u0105d szukania danych!","ERROR",JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(null,"Za ma\u0142o danych!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Pacjent pacjentDoZmiany = new Pacjent(imie, nazwisko, pesel, telefon, adres, adnotacje, upowaznienia, id_lekarz, idPacjent);
                if (pacjentDoZmiany.modifyInDatabase(database)) {
                    JOptionPane.showMessageDialog(null,"Zaktualizowano dane pacjenta.", "INFO", JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(null, "B\u0142\u0105d aktualizacji danych pacjenta!", "ERROR", JOptionPane.ERROR_MESSAGE);
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
        setSize(600,400);
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
    private Vector<Integer> vtIdPacjenci;
    private int id_cb;
    private Vector<Integer> vtIndexes;

    /**
     * Creates JComboBox object filled with phsycians' data to choose from.
     *
     * @param result result of the operation is stored here
     * @return  JComboBox object filled with relevant data
     */
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
            JOptionPane.showMessageDialog(null, "B\u0142\u0105d szukania danych!", "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        JComboBox cb = new JComboBox(vtCombo);
        return cb;
    }

    /**
     * Finds an id occupied by a specific physician identified by his id.
     *
     * @param id_lekarz id of a physician
     * @return id of a JComboBox.
     */
    private int fillComboBox(int id_lekarz) {
        int i = 0;
        for (int el : vtIndexes) {
            if (el == id_lekarz)
                return i;
            i++;
        }
        return 0;
    }
}
