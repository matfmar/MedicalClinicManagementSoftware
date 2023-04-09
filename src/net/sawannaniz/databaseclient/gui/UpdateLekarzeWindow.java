package net.sawannaniz.databaseclient.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import net.sawannaniz.databaseclient.dbutils.*;
import net.sawannaniz.databaseclient.ctrl.Lekarz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Responsible for a window which enables updating data about physicians.
 *
 * @author Mateusz Marzec
 * @version 1.0
 * @since 2023-04-09
 */
public class UpdateLekarzeWindow extends JFrame {
    /**
     * Creates a window.
     *
     * @param d database with opened connection, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param dt DefaultTableModel, a starting point from which physician was selected
     * @param tab JTable object, a starting point from which physician was selected
     * @param v vector of integers representing ids of physicians.
     */
    public UpdateLekarzeWindow(Database d, DefaultTableModel dt, JTable tab, Vector<Integer> v) {
        super("Edycja danych");
        database = d;
        dtm = dt;
        table = tab;
        vtId = v;
        //necessary
        int idSelected = table.getSelectedRow();
        if (idSelected == -1) {
            JOptionPane.showMessageDialog(null,"Nic nie wybrano!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //COMPONENTS
        JLabel label1 = new JLabel("Imi\u0119: ");
        JLabel label2 = new JLabel("Nazwisko: ");
        JLabel label3 = new JLabel("Numer PWZ: ");
        JLabel label4 = new JLabel("Telefon: ");
        JLabel label5 = new JLabel("Specjalizacje: ");
        JTextField imieTextField = new JTextField(10);
        JTextField nazwiskoTextField = new JTextField(10);
        JTextField pwzTextField = new JTextField(10);
        JTextField telefonTextField = new JTextField(10);
        JTextArea specjalizacjeTextArea = new JTextArea(5, 10);
        JButton updateButton = new JButton("Aktualizuj");
        JButton zamknijButton = new JButton("Zamknij");
        //PANELS
        JPanel panelImie = new JPanel();
        JPanel panelNazwisko = new JPanel();
        JPanel panelPWZ = new JPanel();
        JPanel panelTelefon = new JPanel();
        JPanel panelSpecjalizacje = new JPanel();
        JPanel panelButtons = new JPanel();
        panelImie.add(label1); panelImie.add(imieTextField);
        panelNazwisko.add(label2); panelNazwisko.add(nazwiskoTextField);
        panelPWZ.add(label3); panelPWZ.add(pwzTextField);
        panelTelefon.add(label4); panelTelefon.add(telefonTextField);
        JScrollPane scrSpecjalizacje = new JScrollPane(specjalizacjeTextArea);
        panelSpecjalizacje.add(label5); panelSpecjalizacje.add(scrSpecjalizacje);
        panelButtons.add(updateButton); panelButtons.add(zamknijButton);
        //SETTING UP DATA
        int idLekarz = vtId.get(idSelected);
        Lekarz lekarzDoZmiany = new Lekarz(idLekarz);
        AtomicBoolean result = new AtomicBoolean(false);
        ResultSet daneLekarza = lekarzDoZmiany.search(database, idLekarz, result);
        if (!result.get()) {
            JOptionPane.showMessageDialog(null,"B\u0142\u0105d szukania danych!","ERROR",JOptionPane.ERROR_MESSAGE);
            updateButton.setEnabled(false);
        }
        else {
            try {
                while (daneLekarza.next()) {
                    imieTextField.setText(daneLekarza.getString(2));
                    nazwiskoTextField.setText(daneLekarza.getString(3));
                    pwzTextField.setText(daneLekarza.getString(4));
                    telefonTextField.setText(daneLekarza.getString(5));
                    specjalizacjeTextArea.setText(daneLekarza.getString(6));
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,"Błąd szukania danych!","ERROR",JOptionPane.ERROR_MESSAGE);
                updateButton.setEnabled(false);
            }
        }
        //SETUP EVENTS
        zamknijButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                close();
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String imie = imieTextField.getText();
                String nazwisko = nazwiskoTextField.getText();
                String pwz = pwzTextField.getText();
                String telefon = telefonTextField.getText();
                String specjalizacje = specjalizacjeTextArea.getText();
                imie = imie.trim();
                nazwisko = nazwisko.trim();
                pwz = pwz.trim();
                telefon = telefon.trim();
                specjalizacje = specjalizacje.trim();
                if ((imie.isEmpty() || nazwisko.isEmpty()) || pwz.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Za malo danych!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                specjalizacje = specjalizacje.replace("\r\n", " ");
                specjalizacje = specjalizacje.replace("\n", " ");
                specjalizacje = specjalizacje.trim();
                Lekarz lekarzDoZmiany = new Lekarz(idLekarz, imie, nazwisko, pwz, telefon, specjalizacje);
                if (lekarzDoZmiany.modifyInDatabase(database)) {
                    JOptionPane.showMessageDialog(null,"Zaktualizowano dane lekarza", "INFO", JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(null, "B\u0142\u0105d aktualizacji danych lekarza!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                dtm.setValueAt(imie, idSelected, 0);
                dtm.setValueAt(nazwisko, idSelected, 1);
                dtm.setValueAt(pwz, idSelected, 2);
                dtm.setValueAt(telefon, idSelected, 3);
                dtm.setValueAt(specjalizacje, idSelected, 4);
            }
        });

        //PACKING EVERYTHING
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panelImie);
        getContentPane().add(panelNazwisko);
        getContentPane().add(panelPWZ);
        getContentPane().add(panelTelefon);
        getContentPane().add(panelSpecjalizacje);
        getContentPane().add(panelButtons);
        setSize(350,300);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Closes the window
     */
    public void close() {
        dispose();
    }
    private Database database;
    private DefaultTableModel dtm;
    private JTable table;
    private Vector<Integer> vtId;
}
