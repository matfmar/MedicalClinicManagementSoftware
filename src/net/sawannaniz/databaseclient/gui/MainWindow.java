package net.sawannaniz.databaseclient.gui;

import net.sawannaniz.databaseclient.dbutils.*;
import javax.swing.*;

public class MainWindow extends JFrame {
    public MainWindow(Database db) {
        super("Main Window");
        database = db;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        menuPomoc.add(menuItemOProgramie);

        JLabel label1 = new JLabel("To jest glowne okno programu", JLabel.CENTER);
        add(label1);

        pack();
        setSize(400,400);
        setVisible(true);
    }
    private Database database;
}
