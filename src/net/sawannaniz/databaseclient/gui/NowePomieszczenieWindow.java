package net.sawannaniz.databaseclient.gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import net.sawannaniz.databaseclient.dbutils.*;

public class NowePomieszczenieWindow extends JFrame {
    public NowePomieszczenieWindow(Database db) {
        super("Dodaj nowe pomieszczenie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        database = db;
        //COMPONENTS
        JLabel label1 = new JLabel("pietro: ");
        JLabel label2 = new JLabel("numer pomieszczenia: ");
        JTextField pietroTextField = new JTextField(5);
        JTextField numerTextField = new JTextField(5);
        JButton buttonDodaj = new JButton("Dodaj !");
        JButton buttonZamknij = new JButton("Zamknij");
        //PANELS
        JPanel panelPietro = new JPanel();
        panelPietro.add(label1);
        panelPietro.add(pietroTextField);
        JPanel panelNumer = new JPanel();
        panelNumer.add(label2);
        panelNumer.add(numerTextField);
        JPanel panelButtons = new JPanel();
        panelButtons.add(buttonDodaj);
        panelButtons.add(buttonZamknij);

        //DISABLING/ENABLING DODAJ
        buttonDodaj.setEnabled(false);
        numerTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                buttonDodaj.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                if (numerTextField.getText().isEmpty()) {
                    buttonDodaj.setEnabled(false);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {}
        });
        //BUTTON ACTIONS
        buttonZamknij.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                close();
            }
        });
        buttonDodaj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //TUTAJ BEDZIE DODAWANIE NOWEGO POMIESZCZENIA DO BAZY
            }
        });

        //PACKING EVERYTHING
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panelNumer);
        getContentPane().add(panelPietro);
        getContentPane().add(panelButtons);
        pack();
        setVisible(true);
    }
    public void close() {
        dispose();
    }
    private Database database;
}
