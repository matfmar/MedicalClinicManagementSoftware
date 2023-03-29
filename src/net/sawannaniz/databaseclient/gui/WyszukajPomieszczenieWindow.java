package net.sawannaniz.databaseclient.gui;

import javax.swing.*;
import net.sawannaniz.databaseclient.dbutils.*;
public class WyszukajPomieszczenieWindow extends JFrame {
    public WyszukajPomieszczenieWindow(Database db) {
        super("Znajdz pomieszczenie");
        database = db;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //COMPONENTS
        JLabel label1 = new JLabel("numer: ");
        JLabel label2 = new JLabel("pietro: ");
        JTextField numerTextField = new JTextField(5);
        JTextField pietroTextField = new JTextField(5);
        JButton buttonSzukaj = new JButton("Szukaj");
        //TABLE
        String[] columns = {"NUMER", "PIETRO"};
        String[][] data = {{"", ""}};
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane1 = new JScrollPane(table);
        //PANELS
        JPanel panel1 = new JPanel();
        panel1.add(label1);
        panel1.add(numerTextField);
        JPanel panel2 = new JPanel();
        panel2.add(label2);
        panel2.add(pietroTextField);
        //PACKING EVERYTHING
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panel1);
        getContentPane().add(panel2);
        getContentPane().add(buttonSzukaj);
        getContentPane().add(scrollPane1);
        pack();
        setVisible(true);
    }
    private Database database;
}
