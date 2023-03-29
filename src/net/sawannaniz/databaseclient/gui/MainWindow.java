package net.sawannaniz.databaseclient.gui;

import net.sawannaniz.databaseclient.dbutils.*;
import javax.swing.*;
import java.awt.event.*;

public class MainWindow extends JFrame {
    public MainWindow(Database db) {
        super("Main Window");
        database = db;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowHandler(this, database));

        //PASEK MENU
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu menuWizyty = new JMenu("Wizyty");
        JMenu menuPacjenci = new JMenu("Pacjenci");
        JMenu menuLekarze = new JMenu("Lekarze");
        JMenu menuPomieszczenia = new JMenu("Pomieszczenia");
        JMenu menuPomoc = new JMenu("Pomoc");
        menuBar.add(menuWizyty);
        menuBar.add(menuPacjenci);
        menuBar.add(menuLekarze);
        menuBar.add(menuPomieszczenia);
        menuBar.add(menuPomoc);
        //MENU: WIZYTY
        JMenuItem menuItemWizytyDzisiejsze = new JMenuItem("Dzisiejsze wizyty");
        JMenuItem menuItemDodajWizyte = new JMenuItem("Dodaj nowa wizyte");
        JMenuItem menuItemZnajdzWizyte = new JMenuItem("Wyszukaj wizyte...");
        menuWizyty.add(menuItemWizytyDzisiejsze);
        menuWizyty.add(menuItemDodajWizyte);
        menuWizyty.add(menuItemZnajdzWizyte);
        //MENU: PACJENCI
        JMenuItem menuItemDodajPacjenta = new JMenuItem("Dodaj nowego pacjenta");
        JMenuItem menuItemZnajdzPacjenta = new JMenuItem("Wyszukaj pacjenta...");
        menuPacjenci.add(menuItemDodajPacjenta);
        menuPacjenci.add(menuItemZnajdzPacjenta);
        //MENU LEKARZE
        JMenuItem menuItemDodajLekarza = new JMenuItem("Dodaj nowego lekarza");
        JMenuItem menuItemZnajdzLekarza = new JMenuItem("Wyszukaj lekarza...");
        menuLekarze.add(menuItemDodajLekarza);
        menuLekarze.add(menuItemZnajdzLekarza);
        //MENU POMIESZCZENIA
        JMenuItem menuItemDodajPomieszczenie = new JMenuItem("Dodaj nowe pomieszczenie");
        JMenuItem menuItemZnajdzPomieszczenie = new JMenuItem("Wyszukaj pomieszczenie...");
        menuPomieszczenia.add(menuItemDodajPomieszczenie);
        menuPomieszczenia.add(menuItemZnajdzPomieszczenie);
        //MENU POMOC
        JMenuItem menuItemOProgramie = new JMenuItem("O programie");
        JMenuItem menuItemWyloguj = new JMenuItem("Wyloguj");
        JMenuItem menuItemZamknij = new JMenuItem("Zamknij");
        menuPomoc.add(menuItemOProgramie);
        menuPomoc.add(menuItemWyloguj);
        menuPomoc.add(menuItemZamknij);
        //OTHER COMPONENTS
        JLabel label1 = new JLabel("To jest glowne okno programu", JLabel.CENTER);
        add(label1);

        //EVENT SETUP
        //MENU POMOC
        menuItemZamknij.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame frame = new JFrame();
                if (JOptionPane.showConfirmDialog(frame,
                        "Czy zakonczyc program?",
                        "Pytanie",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    database.close();
                    System.exit(0);
                }
            }
        });
        menuItemWyloguj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame frame = new JFrame();
                if (JOptionPane.showConfirmDialog(frame,
                        "Czy chcesz sie wylogowac?",
                        "Pytanie",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    database.close();
                    LoginWindow loginWindow = new LoginWindow();
                    close();
                }
            }
        });
        menuItemOProgramie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame, "DatabaseClient v1.0", "HAHAHA !!!", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        //MENU POMIESZCZENIA
        menuItemDodajPomieszczenie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                NowePomieszczenieWindow nowePomieszczenieWindow = new NowePomieszczenieWindow(database);
            }
        });
        menuItemZnajdzPomieszczenie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                WyszukajPomieszczenieWindow wyszukajPomieszczenieWindow = new WyszukajPomieszczenieWindow(database);
            }
        });

        pack();
        setSize(400,400);
        setVisible(true);
    }
    public void close() {
        dispose();
    }
    private Database database;
}
