package net.sawannaniz.databaseclient.gui;

import net.sawannaniz.databaseclient.ctrl.Lekarz;
import net.sawannaniz.databaseclient.ctrl.Pomieszczenie;
import net.sawannaniz.databaseclient.dbutils.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class WyszukajLekarzaWindow extends JFrame {
    public WyszukajLekarzaWindow(Database db) {
        super("Wyszukaj lekarza");
        database = db;
        vtIdLekarze = new Vector<Integer>();
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
        JButton zobaczSpecjalizacje = new JButton("Zobacz specjalizacje zaznaczonego lekarza");
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
        JPanel panelButtons = new JPanel(); panelButtons.add(wyszukajButton);
        JPanel panelButtons2 = new JPanel(); panelButtons2.add(zobaczSpecjalizacje);
        panelImie.add(label1); panelImie.add(imieTextField);
        panelNazwisko.add(label2); panelNazwisko.add(nazwiskoTextField);
        panelPWZ.add(label3); panelPWZ.add(pwzTextField);
        panelSpecjalizacja.add(label4); panelSpecjalizacja.add(specjalizacjaTextField);
        //EVENTS SETUP

        wyszukajButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dtm.setRowCount(0);
                vtIdLekarze.clear();
                String imie = imieTextField.getText(); imie = imie.trim();
                String nazwisko = nazwiskoTextField.getText(); nazwisko = nazwisko.trim();
                String pwz = pwzTextField.getText(); pwz = pwz.trim();
                String specjalizacja = specjalizacjaTextField.getText(); specjalizacja = specjalizacja.trim();
                Lekarz lekarzDoWyszukania = new Lekarz(imie, nazwisko, pwz, "", specjalizacja);
                AtomicBoolean result = new AtomicBoolean(false);
                ResultSet wynikiWyszukiwania = lekarzDoWyszukania.searchDatabase(database, result);
                if (!result.get()) {
                    JOptionPane.showMessageDialog(null, "Nie uda\u0142o si\u0119 odczyta\u0107 danych!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String imieRes, nazwiskoRes, pwzRes, telefonRes, specjalizacjeRes;
                try {
                    while (wynikiWyszukiwania.next()) {
                        imieRes = wynikiWyszukiwania.getString(1);
                        nazwiskoRes = wynikiWyszukiwania.getString(2);
                        pwzRes = wynikiWyszukiwania.getString(3);
                        telefonRes = wynikiWyszukiwania.getString(4);
                        specjalizacjeRes = wynikiWyszukiwania.getString(5);
                        vtIdLekarze.add(wynikiWyszukiwania.getInt(6));
                        dtm.addRow(new Object[] {imieRes, nazwiskoRes, pwzRes, telefonRes, specjalizacjeRes});
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,"B\u0142\u0105d odczytu kursora!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (SwingUtilities.isRightMouseButton(e)) {
                    WyszukajLekarzaWindow.PopUp menu = new WyszukajLekarzaWindow.PopUp(table, dtm);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        zobaczSpecjalizacje.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int idSelected = table.getSelectedRow();
                if (idSelected == -1) {
                    JOptionPane.showMessageDialog(null, "Nie nie zaznaczono!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String spec = table.getValueAt(idSelected, 4).toString();
                JOptionPane.showMessageDialog(null, spec, "SPECJALIZACJE", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        //PACK EVERYTHING TOGETHER
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panelImie);
        getContentPane().add(panelNazwisko);
        getContentPane().add(panelPWZ);
        getContentPane().add(panelSpecjalizacja);
        getContentPane().add(panelButtons);
        getContentPane().add(scrTable);
        getContentPane().add(panelButtons2);
        pack();
        setSize(700,400);
        setLocationRelativeTo(null);
        setVisible(true);

    }
    private Database database;
    private Vector<Integer> vtIdLekarze;
    private class PopUp extends JPopupMenu {
        public PopUp(JTable tab, DefaultTableModel d) {
            table = tab;
            dtm = d;
            JMenuItem updateMenuItem = new JMenuItem("Modyfikuj");
            JMenuItem deleteMenuItem = new JMenuItem("Usu\u0144");
            add(updateMenuItem);
            add(deleteMenuItem);
            updateMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    UpdateLekarzeWindow updateLekarzeWindow = new UpdateLekarzeWindow(database, dtm, table, vtIdLekarze);
                }
            });
            deleteMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    int decision = JOptionPane.showConfirmDialog(null,
                            "Czy na pewno chcesz usun\u0105\u0107 ten wpis?",
                            "PYTANIE", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (decision == JOptionPane.YES_OPTION) {
                        deleteLekarz();
                    }
                }
            });
        }
        private JTable table;
        private DefaultTableModel dtm;
        private void deleteLekarz() {
            int idSelected = table.getSelectedRow();
            if (idSelected == -1) {
                JOptionPane.showMessageDialog(null,"Nie wybrano lekarza!","ERROR",JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int idLekarz = vtIdLekarze.get(idSelected);
            Lekarz lekarzDoUsuniecia = new Lekarz(idLekarz);
            if (lekarzDoUsuniecia.removeFromDatabase(database)) {
                JOptionPane.showMessageDialog(null,"Pomy\u015blnie Usuni\u0119to lekarza.", "INFO",JOptionPane.INFORMATION_MESSAGE);
                dtm.removeRow(idSelected);
                vtIdLekarze.remove(idSelected);
            }
            else {
                JOptionPane.showMessageDialog(null,"B\u0142\u0105d w usuwaniu lekarza !","ERROR",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
