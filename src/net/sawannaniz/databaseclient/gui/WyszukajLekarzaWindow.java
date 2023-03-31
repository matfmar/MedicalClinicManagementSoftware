package net.sawannaniz.databaseclient.gui;

import net.sawannaniz.databaseclient.dbutils.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class WyszukajLekarzaWindow extends JFrame {
    public WyszukajLekarzaWindow(Database db) {
        super("Wyszukaj lekarza");
        database = db;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //COMPONENTS
        JLabel label1 = new JLabel("Imie: ");
        JLabel label2 = new JLabel("Nazwisko: ");
        JLabel label3 = new JLabel("Numer PWZ: ");
        JLabel label4 = new JLabel("Specjalizacja: ");
        JTextField imieTextField = new JTextField(10);
        JTextField nazwiskoTextField = new JTextField(10);
        JTextField pwzTextField = new JTextField(10);
        JTextField specjalizacjaTextField = new JTextField(10);
        JButton wyszukajButton = new JButton("Wyszukaj");
        //TABLE
        DefaultTableModel dtm = new DefaultTableModel();
        JTable table = new JTable(dtm);
        dtm.addColumn("Imie");
        dtm.addColumn("Nazwisko");
        dtm.addColumn("PWZ");
        dtm.addColumn("Telefon");
        dtm.addColumn("Specjalizacje");
        JScrollPane scrTable = new JScrollPane(table);
        //PANELS
        JPanel panelImie = new JPanel();
        JPanel panelNazwisko = new JPanel();
        JPanel panelPWZ = new JPanel();
        JPanel panelSpecjalizacja = new JPanel();
        JPanel panelTable = new JPanel();
        panelImie.add(label1); panelImie.add(imieTextField);
        panelNazwisko.add(label2); panelNazwisko.add(nazwiskoTextField);
        panelPWZ.add(label3); panelPWZ.add(pwzTextField);
        panelSpecjalizacja.add(label4); panelSpecjalizacja.add(specjalizacjaTextField);
        panelTable.add(scrTable);
        //PACK EVERYTHING TOGETHER
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panelImie);
        getContentPane().add(panelNazwisko);
        getContentPane().add(panelPWZ);
        getContentPane().add(panelSpecjalizacja);
        getContentPane().add(wyszukajButton);
        getContentPane().add(panelTable);
        pack();
        setVisible(true);

    }
    private Database database;
}
