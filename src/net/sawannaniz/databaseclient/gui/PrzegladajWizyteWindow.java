package net.sawannaniz.databaseclient.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import net.sawannaniz.databaseclient.ctrl.Lekarz;
import net.sawannaniz.databaseclient.ctrl.Wizyta;
import net.sawannaniz.databaseclient.dbutils.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class PrzegladajWizyteWindow extends JFrame {
    public PrzegladajWizyteWindow(Database db, DefaultTableModel dt, JTable t, Vector<Integer> vtWiz, User us) {
        super("Przegladanie wizyty");
        database = db;
        dtm = dt;
        table = t;
        vt_id_wiz= vtWiz;
        user = us;
        editable = false;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //BASIC TEST
        int idSelected = table.getSelectedRow();
        if (idSelected == -1) {
            JOptionPane.showMessageDialog(null, "Nie wybrano nic", "error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        id_wizyta = vt_id_wiz.get(idSelected);
        //TEST FOR USER - if Wizyta is editable
        editable = (user.getRole() == Database.Role.LEKARZ);
        if (editable) {
            editable = (verifyIfGoodPhysician(user.getLogin().substring(1)));
        }
        //COMPONENTS
        JLabel label1 = new JLabel("Pacjent: ");
        JLabel labelPacjent = new JLabel();
        JLabel label2 = new JLabel("PESEL: ");
        JLabel labelPesel = new JLabel();
        JLabel label3 = new JLabel("Lekarz: ");
        JLabel labelLekarz = new JLabel();
        JLabel label4 = new JLabel("Opis wizyty: ");
        JTextArea wizytaTextArea = new JTextArea(10, 10);
        wizytaTextArea.setEditable(false);
        JScrollPane scrWizyta = new JScrollPane(wizytaTextArea);
        JLabel label5 = new JLabel("ICD-10: ");
        JTextField icdTextField = new JTextField(10);
        icdTextField.setEditable(false);
        JButton edytujButton = new JButton("Edytuj");
        edytujButton.setEnabled(editable);
        JButton zapiszButton = new JButton("Zapisz");
        zapiszButton.setEnabled(false);
        JButton zamknijButton = new JButton("Zamknij");
        //PANELS
        JPanel panelPacjent = new JPanel(); panelPacjent.add(label1); panelPacjent.add(labelPacjent);
        JPanel panelPesel = new JPanel(); panelPesel.add(label2); panelPesel.add(labelPesel);
        JPanel panelLekarz = new JPanel(); panelLekarz.add(label3); panelLekarz.add(labelLekarz);
        JPanel panelIcd = new JPanel(); panelIcd.add(label5); panelIcd.add(icdTextField);
        JPanel panelButtons = new JPanel(); panelButtons.add(edytujButton); panelButtons.add(zapiszButton); panelButtons.add(zamknijButton);
        //DATA
        String labPacjent = table.getValueAt(idSelected, 1).toString() + " " + table.getValueAt(idSelected, 2).toString();
        String labLekarz = table.getValueAt(idSelected, 4).toString();
        String labPesel = table.getValueAt(idSelected,3).toString();
        labelPacjent.setText(labPacjent);
        labelPesel.setText(labPesel);
        labelLekarz.setText(labLekarz);
        if (!getWpisAndIcd(wizytaTextArea, icdTextField)) {
            JOptionPane.showMessageDialog(null, "nie udalo sie uzyskac wpisu", "error", JOptionPane.ERROR_MESSAGE);
            edytujButton.setEnabled(false);
        }
        //EVENTS
        zamknijButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                close();
            }
        });
        zapiszButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int decision = JOptionPane.showConfirmDialog(null,
                        "Czy na pewno chcesz dodac wpis z wizyty w takiej postaci?",
                        "Dodawanie wpisu z wizyty", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (decision == JOptionPane.NO_OPTION) {
                    return;
                }
                String wpis = wizytaTextArea.getText(); wpis = wpis.trim();
                if (wpis.isEmpty()) {
                    JOptionPane.showMessageDialog(null,"Nic nie wpisano!", "error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String icd10 = icdTextField.getText(); icd10 = icd10.trim();
                Wizyta wizyta = new Wizyta(wpis, icd10, id_wizyta);
                if (!wizyta.edit(database)) {
                    JOptionPane.showMessageDialog(null,"Nie mozna zapisac edytowanej wpisu. Blad.", "error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(null,"Poprawnie edytowano wpis z wizyty.","OK",JOptionPane.INFORMATION_MESSAGE);
                zapiszButton.setEnabled(false);
                wizytaTextArea.setEditable(false);
                icdTextField.setEditable(false);
                edytujButton.setEnabled(editable);
                close();
            }
        });
        edytujButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                edytujButton.setEnabled(false);
                zapiszButton.setEnabled(true);
                wizytaTextArea.setEditable(true);
                icdTextField.setEditable(true);
            }
        });

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panelPacjent);
        getContentPane().add(panelPesel);
        getContentPane().add(panelLekarz);
        getContentPane().add(label4);
        getContentPane().add(scrWizyta);
        getContentPane().add(panelIcd);
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
    private Vector<Integer> vt_id_wiz;
    private User user;
    private int id_wizyta;
    private boolean editable;
    private boolean getWpisAndIcd(JTextArea wpisAream, JTextField icdField) {
        Wizyta wizyta = new Wizyta(id_wizyta);
        AtomicBoolean result = new AtomicBoolean(false);
        ResultSet resSet = wizyta.search(database, id_wizyta, result);
        if (!result.get()) {
            JOptionPane.showMessageDialog(null, "fail", "error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String wpis = "",icd = "";
        try {
            while (resSet.next()) {
                wpis = resSet.getString(1);
                icd = resSet.getString(2);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Failed to read cursos", "error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        wpisAream.setText(wpis);
        icdField.setText(icd);
        return true;
    }
    private boolean verifyIfGoodPhysician(String lekarzPWZ) {
        Wizyta wizytaDoWeryfikacji = new Wizyta(id_wizyta);
        AtomicBoolean result = new AtomicBoolean(false);
        ResultSet resultSet = wizytaDoWeryfikacji.search(database, id_wizyta, result);
        if (!result.get())
            return false;
        int id_lek_shouldBe = -1;
        try {
            while (resultSet.next()) {
                id_lek_shouldBe = resultSet.getInt(3);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "fail to read cursor", "error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        int id_lek_aktualne = Lekarz.znajdzLekarzaPoPWZ(database, lekarzPWZ);
        return (id_lek_aktualne == id_lek_shouldBe);
    }
}
