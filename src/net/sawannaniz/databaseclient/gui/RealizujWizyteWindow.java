package net.sawannaniz.databaseclient.gui;

import net.sawannaniz.databaseclient.ctrl.Lekarz;
import net.sawannaniz.databaseclient.ctrl.Wizyta;
import net.sawannaniz.databaseclient.dbutils.Database;
import net.sawannaniz.databaseclient.dbutils.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Responsible for a window which enables realization of appointments by physicians.
 * Only a physician who is mentioned in the appointment can realize this appointment.
 * When the appointment is tried to be realized many times, a prompt appears which warns that there is some data already written.
 *
 * @author Mateusz Marzec
 * @version 1.0
 * @since 2023-04-09
 */
public class RealizujWizyteWindow extends JFrame {
    /**
     * Creates the window.
     *
     * @param db database with opened connection, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     * @param dt DefaultTableModel object, as a starting point for a selected appointment to realize
     * @param t JTable object, as a starting point for a selected appointment to realize
     * @param vtWiz vector of integers representing ids of appointments
     * @param us    user viewing the data (opening the window)
     */
    public RealizujWizyteWindow(Database db, DefaultTableModel dt, JTable t, Vector<Integer> vtWiz, User us) {
        super("Realizacja wizyty");
        database = db;
        dtm = dt;
        table = t;
        vt_id_wiz = vtWiz;
        user = us;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //BASIC TEST
        int idSelected = table.getSelectedRow();
        if (idSelected == -1) {
            JOptionPane.showMessageDialog(null, "Nie wybrano nic!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        id_wizyta = vt_id_wiz.get(idSelected);
        //TEST FOR USER
        if (user.getRole() != Database.Role.LEKARZ) {
            JOptionPane.showMessageDialog(null, "Brak uprawnie\u0144 do realizacji wizyt! ", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!verifyIfGoodPhysician(user.getLogin().substring(1))) {
            JOptionPane.showMessageDialog(null, "Nie jeste\u015b lekarzem od tej wizyty !", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
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
        JScrollPane scrWizyta = new JScrollPane(wizytaTextArea);
        JLabel label5 = new JLabel("ICD-10: ");
        JTextField icdTextField = new JTextField(10);
        JButton dodajButton = new JButton("OK");
        JButton zamknijButton = new JButton("Zamknij");
        //PANELS
        JPanel panelPacjent = new JPanel(); panelPacjent.add(label1); panelPacjent.add(labelPacjent);
        JPanel panelPesel = new JPanel(); panelPesel.add(label2); panelPesel.add(labelPesel);
        JPanel panelLekarz = new JPanel(); panelLekarz.add(label3); panelLekarz.add(labelLekarz);
        JPanel panelIcd = new JPanel(); panelIcd.add(label5); panelIcd.add(icdTextField);
        JPanel panelButtons = new JPanel(); panelButtons.add(dodajButton); panelButtons.add(zamknijButton);
        //DATA
        String labPacjent = table.getValueAt(idSelected, 1).toString() + " " + table.getValueAt(idSelected, 2).toString();
        String labLekarz = table.getValueAt(idSelected, 4).toString();
        String labPesel = table.getValueAt(idSelected,3).toString();
        labelPacjent.setText(labPacjent);
        labelPesel.setText(labPesel);
        labelLekarz.setText(labLekarz);
        //ANOTHER TEST
        AtomicBoolean resultX = new AtomicBoolean(false);
        Vector<String> wizytyResults;
        wizytyResults = checkIfWasRealizedBefore(resultX);
        if (!resultX.get()) {
            return;
        }
        if (wizytyResults.get(0) != null && wizytyResults.get(1) != null) {
            if (!wizytyResults.get(0).isEmpty() || !wizytyResults.get(1).isEmpty()) {
                int decision = JOptionPane.showConfirmDialog(null,
                        "Wpis ju\u017c jest w bazie. Czy chcesz go edytowa\u0107?",
                        "PYTANIE", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (decision == JOptionPane.NO_OPTION) {
                    return;
                }
                wizytaTextArea.setText(wizytyResults.get(0));
                icdTextField.setText(wizytyResults.get(1));
            }
        }
        //EVENTS SETUP
        zamknijButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                close();
            }
        });
        dodajButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int decision = JOptionPane.showConfirmDialog(null,
                        "Czy na pewno chcesz doda\u0107 wpis z wizyty w takiej postaci?",
                        "PYTANIE", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (decision == JOptionPane.NO_OPTION) {
                    return;
                }
                String wpis = wizytaTextArea.getText(); wpis = wpis.trim();
                if (wpis.isEmpty()) {
                    JOptionPane.showMessageDialog(null,"Nic nie wpisano!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String icd10 = icdTextField.getText(); icd10 = icd10.trim();
                Wizyta wizyta = new Wizyta(wpis, icd10, id_wizyta);
                if (!wizyta.realize(database)) {
                    JOptionPane.showMessageDialog(null,"Nie mo\u017cna doda\u0107 wpisu!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(null,"Poprawnie dodano wpis z wizyty.","INFO",JOptionPane.INFORMATION_MESSAGE);
                table.setValueAt("TAK", idSelected, 6);
                close();
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
        Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dimFrame = new Dimension(dimScreen.width/3, dimScreen.height/3);
        setSize(dimFrame);
        setLocation(dimScreen.width/4, dimScreen.height/4);
        //setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Closes the window.
     */
    public void close() {
        dispose();
    }
    private Database database;
    private User user;
    private DefaultTableModel dtm;
    private JTable table;
    private Vector<Integer> vt_id_wiz;
    private int id_wizyta;

    /**
     * Checks whether the selected appointment has already been realized.
     *
     * @param resultX the result of the operation is stored here
     * @return  when yes, appointment's description and icd-10 code are placed to this vector of Strings.
     */
    private Vector<String> checkIfWasRealizedBefore(AtomicBoolean resultX) {    //jak false to ok
        Wizyta wizytaDoSprawdzenia = new Wizyta(id_wizyta);
        AtomicBoolean result = new AtomicBoolean(false);
        ResultSet resultSet = wizytaDoSprawdzenia.search(database, id_wizyta, result);
        if (!result.get()) {
            JOptionPane.showMessageDialog(null, "Nie mozna sprawdzi\u0107 wizyty !", "ERROR", JOptionPane.ERROR_MESSAGE);
            resultX.set(false);
            return null;
        }
        String s1 = "", s2 = "";
        try {
            while (resultSet.next()) {
                s1 = resultSet.getString(1);
                s2 = resultSet.getString(2);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "B\u0142\u0105d szukania danych!", "ERROR",JOptionPane.ERROR_MESSAGE);
            resultX.set(false);
            return null;
        }
        Vector<String> v = new Vector<String>();
        v.add(s1);
        v.add(s2);
        resultX.set(true);
        return v;
    }

    /**
     * Checks if a user which tries to realize the appointment:
     * <ul>
     *     <li>Is a physician (i.e., has a role of physicians)</li>
     *     <li>If a physician, whether the correct one, i.e., the one responsible for the appointment</li>
     * </ul>
     *
     * @param lekarzPWZ licence number of a physician who tries to realize the appointment
     * @return  whether the user is a valid physician or not
     */
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
            JOptionPane.showMessageDialog(null, "B\u0142\u0105d szukania danych!", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        int id_lek_aktualne = Lekarz.znajdzLekarzaPoPWZ(database, lekarzPWZ);
        return (id_lek_aktualne == id_lek_shouldBe);
    }
}
