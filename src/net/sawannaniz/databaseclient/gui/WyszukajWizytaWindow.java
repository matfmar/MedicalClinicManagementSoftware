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

public class WyszukajWizytaWindow extends JFrame {
    public WyszukajWizytaWindow(Database db) {
        super("Wyszukiwanie wizyty...");
        database = db;
        vtIdLekarze = new Vector<Integer>();
        vtIdPomieszczenia = new Vector<Integer>();
        vtIdPacjenci = new Vector<Integer>();
        id_cb_lek = -1;
        id_cb_pom = -1;
        id_cb_pac = -1;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        AtomicBoolean result = new AtomicBoolean(false);
        //COMPONENTS
        JLabel label1 = new JLabel("Nazwisko pacjenta: ");
        JTextField pacjentTextField = new JTextField(10);
        JLabel label2 = new JLabel("Lekarz: ");
        JComboBox cbLekarz = createComboBoxLekarz(result);
        id_cb_lek = comboboxCheckInfo(cbLekarz);
        JLabel label3 = new JLabel("Pomieszczenie: ");
        JComboBox cbPomieszczenie = createComboBoxPomieszczenie(result);
        id_cb_pom = comboboxCheckInfo(cbPomieszczenie);
        JLabel label4 = new JLabel("data od: ");
        JLabel label5 = new JLabel("data do: ");
        JTextField dataOdTextField = new JTextField("RRRR-MM-DD");
        JTextField dataDoTextField = new JTextField("RRRR-MM-DD");
        JButton szukajButton = new JButton("Szukaj");
        DefaultTableModel dtm = new DefaultTableModel();
        JTable table = new JTable(dtm);
        dtm.addColumn("Data");
        dtm.addColumn("Nazwisko pacjenta");
        dtm.addColumn("Imie pacjenta");
        dtm.addColumn("PESEL pacjenta");
        dtm.addColumn("Lekarz");
        dtm.addColumn("Pomieszczenie");
        JScrollPane scrTable = new JScrollPane(table);
        //PANELS
        JPanel panelPacjent = new JPanel(); panelPacjent.add(label1); panelPacjent.add(pacjentTextField);
        JPanel panelLekarz = new JPanel(); panelLekarz.add(label2);
        if (cbLekarz != null) panelLekarz.add(cbLekarz);
        JPanel panelPomieszczenie = new JPanel(); panelPomieszczenie.add(label3);
        if (cbPomieszczenie != null) panelPomieszczenie.add(cbPomieszczenie);
        JPanel panelData = new JPanel();
        panelData.setLayout(new BoxLayout(panelData, BoxLayout.X_AXIS));
        panelData.add(label4); panelData.add(dataOdTextField); panelData.add(label5); panelData.add(dataDoTextField);
        //SETUP EVENTS
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
        szukajButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nazwisko = pacjentTextField.getText(); nazwisko = nazwisko.trim();
                int id_lekarz, id_pomieszczenie;
                if (id_cb_lek != -1) id_lekarz = vtIdLekarze.get(id_cb_lek);
                if (id_cb_pom != -1) id_pomieszczenie = vtIdPomieszczenia.get(id_cb_pom);
                String dataOd = dataOdTextField.getText(); dataOd = dataOd.trim();
                String dataDo = dataDoTextField.getText(); dataDo = dataDo.trim();

            }
        });


        //GET EVERYTHING TOGETHER
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panelPacjent);
        getContentPane().add(panelLekarz);
        getContentPane().add(panelPomieszczenie);
        getContentPane().add(panelData);
        getContentPane().add(szukajButton);
        getContentPane().add(scrTable);
        pack();
        setVisible(true);
    }
    private Database database;
    private Vector<Integer> vtIdLekarze, vtIdPomieszczenia, vtIdPacjenci;
    private int id_cb_lek, id_cb_pom, id_cb_pac;
    private int comboboxCheckInfo(JComboBox cb) {
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
        vtCombo.add("");    //pierwszy wiersz [0] pusty
        vtIdLekarze.add(-1);  //tak samo - analogicznie
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
                vtIdLekarze.add(id_lekarz);
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
        vtIdPomieszczenia.add(-1);  //tak samo - analogicznie
        String numer, total;
        int pietro;
        int id_pomieszczenie = 0;
        try {
            while (resultSet.next()) {
                id_pomieszczenie = resultSet.getInt(1);
                numer = resultSet.getString(2);
                pietro = resultSet.getInt(3);
                total = numer + " " + Integer.toString(pietro);
                vtCombo.add(total);
                vtIdPomieszczenia.add(id_pomieszczenie);
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
