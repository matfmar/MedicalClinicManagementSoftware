package net.sawannaniz.databaseclient.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import net.sawannaniz.databaseclient.ctrl.Pomieszczenie;
import net.sawannaniz.databaseclient.dbutils.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.sql.ResultSet;
public class WyszukajPomieszczenieWindow extends JFrame {
    public WyszukajPomieszczenieWindow(Database db) {
        super("Znajdz pomieszczenie");
        database = db;
        vtIdPomieszczenia = new Vector<Integer>();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //COMPONENTS
        JLabel label1 = new JLabel("numer: ");
        JLabel label2 = new JLabel("pietro: ");
        JTextField numerTextField = new JTextField(5);
        JTextField pietroTextField = new JTextField(5);
        JButton buttonSzukaj = new JButton("Szukaj");
        //TABLE
        DefaultTableModel dtm = new DefaultTableModel();
        JTable table = new JTable(dtm);
        dtm.addColumn("numer");
        dtm.addColumn("pietro");
        JScrollPane scrollPane1 = new JScrollPane(table);
        //PANELS
        JPanel panel1 = new JPanel();
        panel1.add(label1);
        panel1.add(numerTextField);
        JPanel panel2 = new JPanel();
        panel2.add(label2);
        panel2.add(pietroTextField);

        //EVENTS SETUP
        buttonSzukaj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dtm.setRowCount(0);     //clears table
                String numerPomieszczenia = numerTextField.getText();
                String pietroPomieszczeniaStr = pietroTextField.getText();
                pietroPomieszczeniaStr = pietroPomieszczeniaStr.trim();
                numerPomieszczenia = numerPomieszczenia.trim();
                boolean jestPietro;
                int pietroPomieszczenia = 0;
                if (!(pietroPomieszczeniaStr.isEmpty())) {
                    try {
                        pietroPomieszczenia = Integer.parseInt(pietroPomieszczeniaStr);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null,
                                "Conversion problem",
                                "Conversion problem",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    jestPietro = true;
                }
                else {
                    jestPietro = false;
                }
                Pomieszczenie pomieszczenieDoWyszukania = new Pomieszczenie(numerPomieszczenia, pietroPomieszczenia);
                AtomicBoolean result = new AtomicBoolean(true);
                ResultSet wynikiWyszukiwania = pomieszczenieDoWyszukania.searchDatabase(database, jestPietro, result);
                if (!result.get()) {
                    JOptionPane.showMessageDialog(null,"Nie udalo sie odczytac danych", "Blad", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String pietroRes="", numerRes="";
                try {
                    while (wynikiWyszukiwania.next()) {
                        numerRes = wynikiWyszukiwania.getString(1);
                        pietroRes = wynikiWyszukiwania.getString(2);
                        vtIdPomieszczenia.add(wynikiWyszukiwania.getInt(3));
                        dtm.addRow(new Object[] {numerRes, pietroRes});
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Blad odczytu z kursora", "Blad", JOptionPane.ERROR_MESSAGE);
                    return;
                }

            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (SwingUtilities.isRightMouseButton(e)) {
                    PopUp menu = new PopUp(table, dtm);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
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
    private Vector<Integer> vtIdPomieszczenia;
    private class PopUp extends JPopupMenu {
        public PopUp(JTable tab, DefaultTableModel d) {
            table = tab;
            dtm = d;
            JMenuItem updateMenuItem = new JMenuItem("Modyfikuj");
            JMenuItem deleteMenuItem = new JMenuItem("Usun");
            add(updateMenuItem);
            add(deleteMenuItem);
            updateMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    UpdatePomieszczenieWindow updatePomieszczenieWindow = new UpdatePomieszczenieWindow(database);
                }
            });
            deleteMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    int decision = JOptionPane.showConfirmDialog(null,
                            "Czy na pewno chcesz usunac ten wpis?",
                            "Usuwanie wpisu", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (decision == JOptionPane.YES_OPTION) {
                        deletePomieszczenie();
                    }
                }
            });
        }
        private JTable table;
        private DefaultTableModel dtm;
        private void deletePomieszczenie() {
            int idSelected = table.getSelectedRow();
            if (idSelected == -1) {
                JOptionPane.showMessageDialog(null,"Nie wybrano pomieszczenia","ERROR",JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int idPomieszczenia = vtIdPomieszczenia.get(idSelected);
            Pomieszczenie pomieszczenieDoUsuniecia = new Pomieszczenie(idPomieszczenia);
            if (pomieszczenieDoUsuniecia.removeFromDatabase(database)) {
                JOptionPane.showMessageDialog(null,"Usunieto pomieszczenie", "OK",JOptionPane.INFORMATION_MESSAGE);
                dtm.removeRow(idSelected);
                vtIdPomieszczenia.remove(idSelected);
            }
            else {
                JOptionPane.showMessageDialog(null,"Blad w usuwaniu pomieszczenia !","ERROR",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
