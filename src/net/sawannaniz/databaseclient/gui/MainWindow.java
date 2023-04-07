package net.sawannaniz.databaseclient.gui;

import net.sawannaniz.databaseclient.dbutils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainWindow extends JFrame {
    public MainWindow(Database db, String loginName) {
        super("Przychodnia Crusher v1.0");
        database = db;
        user = null;
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
        JMenuItem menuItemDodajWizyte = new JMenuItem("Dodaj now\u0105 wizyt\u0119");
        JMenuItem menuItemZnajdzWizyte = new JMenuItem("Wyszukaj wizyt\u0119...");
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
        this.add(new JLabel(new ImageIcon("pic.png")));


        //EVENT SETUP
        //MENU POMOC
        menuItemZamknij.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame frame = new JFrame();
                if (JOptionPane.showConfirmDialog(frame,
                        "Czy zako\u0144czy\u0119 program?",
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
                        "Czy chcesz si\u0119 wylogowa\u0107?",
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
                JOptionPane.showMessageDialog(frame, "Przychodnia Crusher v1.0", "INFO", JOptionPane.INFORMATION_MESSAGE);
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
        //MENU LEKARZE
        menuItemDodajLekarza.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                NowyLekarzWindow nowyLekarzWindow = new NowyLekarzWindow(database);
            }
        });
        menuItemZnajdzLekarza.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                WyszukajLekarzaWindow wyszukajLekarzaWindow = new WyszukajLekarzaWindow(database);
            }
        });
        //MENU PACJENCI
        menuItemDodajPacjenta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                NowyPacjentWindow nowyPacjentWindow = new NowyPacjentWindow(database);
            }
        });
        menuItemZnajdzPacjenta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                WyszukajPacjentaWindow wyszukajPacjentaWindow = new WyszukajPacjentaWindow(database, null, false);
            }
        });
        //MENU WIZYTY
        menuItemZnajdzWizyte.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WyszukajWizytaWindow wyszukajWizytaWindow = new WyszukajWizytaWindow(database, false, user);
            }
        });
        menuItemDodajWizyte.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                NowaWizytaWindow nowaWizytaWindow = new NowaWizytaWindow(database);
            }
        });
        menuItemWizytyDzisiejsze.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                WyszukajWizytaWindow dzisiejszeWizytyWindow = new WyszukajWizytaWindow(database, true, user);
            }
        });
        //CREATE USER OF A PROGRAM
        AtomicBoolean result1 = new AtomicBoolean(false);
        user = new User(loginName, database.determineCurrentRole(result1));
        if (!result1.get()) {
            JOptionPane.showMessageDialog(null,"Nie mog\u0119 okresli\u0107 u\u017cytkownika!","ERROR",JOptionPane.ERROR_MESSAGE);
            menuPacjenci.setEnabled(false);
            menuWizyty.setEnabled(false);
            menuLekarze.setEnabled(false);
            menuPomieszczenia.setEnabled(false);
        }
        else {
            String message = "Twoja rola: " + user.getRole().toString();
            JOptionPane.showMessageDialog(null, message, "INFO", JOptionPane.INFORMATION_MESSAGE);
        }
        pack();
        setSize(400, 400);
        setResizable(false);
        Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimScreen.width/6, dimScreen.height/6);
        setVisible(true);
    }
    public void close() {
        dispose();
    }
    private Database database;
    private User user;
}
