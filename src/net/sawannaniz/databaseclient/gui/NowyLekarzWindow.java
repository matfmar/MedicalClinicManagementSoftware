package net.sawannaniz.databaseclient.gui;

import javax.swing.*;

import net.sawannaniz.databaseclient.ctrl.Lekarz;
import net.sawannaniz.databaseclient.dbutils.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is responsible for a window which enables the user to add new physicians to the database.
 *
 * @author Mateusz Marzec
 * @version 1.0
 * @since 2023-04-09
 */
public class NowyLekarzWindow extends JFrame {
    /**
     * Creates a new window.
     *
     * @param db database with opened connection, see  {@link net.sawannaniz.databaseclient.dbutils.Database Database}
     */
    public NowyLekarzWindow(Database db) {
        super("Dodawanie nowego lekarza");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        database = db;
        //COMPONENTS
        JLabel label1 = new JLabel("Imi\u0119: ");
        JLabel label2 = new JLabel("Nazwisko: ");
        JLabel label3 = new JLabel("Numer PWZ: ");
        JLabel label4 = new JLabel("Specjalizacje: ");
        JLabel label5 = new JLabel("Telefon: ");
        JTextField imieTextField = new JTextField(10);
        JTextField nazwiskoTextField = new JTextField(10);
        JTextField pwzTextField = new JTextField(10);
        JTextField telefonTextField = new JTextField(10);
        JTextArea specjalizacjeTextArea = new JTextArea(5, 10);
        JButton dodajButton = new JButton("Dodaj");
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
        panelTelefon.add(label5); panelTelefon.add(telefonTextField);
        JScrollPane scrSpecjalizacje = new JScrollPane(specjalizacjeTextArea);
        panelSpecjalizacje.add(label4); panelSpecjalizacje.add(scrSpecjalizacje);
        panelButtons.add(dodajButton); panelButtons.add(zamknijButton);
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
                    JOptionPane.showMessageDialog(null, "Zbyt ma\u0142o danych!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                specjalizacje = specjalizacje.replace("\r\n", " ");
                specjalizacje = specjalizacje.replace("\n", " ");
                specjalizacje = specjalizacje.trim();
                Lekarz lekarz = new Lekarz(imie, nazwisko, pwz, telefon, specjalizacje);
                if (lekarz.insertToDatabase(database)) {
                    JOptionPane.showMessageDialog(null,"Dodano nowego lekarza.","INFO",JOptionPane.INFORMATION_MESSAGE);
                    imieTextField.setText("");
                    nazwiskoTextField.setText("");
                    pwzTextField.setText("");
                    telefonTextField.setText("");
                    specjalizacjeTextArea.setText("");
                }
                else {
                    JOptionPane.showMessageDialog(null, "B\u0142\u0105d w dodawaniu lekarza do bazy!", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
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
        pack();
        setSize(350, 300);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Closes the window.
     */
    public void close() {
        dispose();
    }
    private Database database;
}
