package net.sawannaniz.databaseclient.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import net.sawannaniz.databaseclient.ctrl.*;
import net.sawannaniz.databaseclient.dbutils.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class WyszukajPacjentaWindow extends JFrame {
    public WyszukajPacjentaWindow(Database db, JTextField pesel2TextField, boolean peselReturn) {
        super("Wyszukaj pacjenta");
        database = db;
        id_cb = -1;
        vtIndexes = new Vector<Integer>();
        vtIdPacjenci = new Vector<Integer>();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //COMPONENTS
        JLabel label1 = new JLabel("Imi\u0119: ");
        JLabel label2 = new JLabel("Nazwisko: ");
        JLabel label3 = new JLabel("PESEL: ");
        JLabel label4 = new JLabel("Telefon: ");
        JLabel label5 = new JLabel("Adres: ");
        JLabel label6 = new JLabel("Osoby upowa\u017cnione: ");
        JLabel label7 = new JLabel("Lekarz prowadz\u0105cy: ");
        JLabel label8 = new JLabel("Adnotacje specjalne: ");
        JTextField imieTextField = new JTextField(10);
        JTextField nazwiskoTextField = new JTextField(10);
        JTextField peselTextField = new JTextField(10);
        JTextField telefonTextField = new JTextField(10);
        JTextField adresTextField = new JTextField(15);
        JTextField upowaznieniaTextField = new JTextField(15);
        JTextField adnotacjeTextField = new JTextField(15);
        JButton znajdzButton = new JButton("Szukaj");
        JButton zobaczUpowaznieniaButton = new JButton("Zobacz upowa\u017cnienia");
        JButton zobaczAdnotacjeButton = new JButton("Zobacz adnotacje");
        AtomicBoolean result = new AtomicBoolean(false);
        JComboBox cb = createComboBox(result);
        if (!result.get()) {
            JOptionPane.showMessageDialog(null, "B\u0142\u0105d znalezienia danych lekarzy!","ERROR",JOptionPane.ERROR_MESSAGE);
        }
        if (cb != null) {
            id_cb = cb.getSelectedIndex();
        }
        DefaultTableModel dtm = new DefaultTableModel();
        JTable table = new JTable(dtm);
        dtm.addColumn("Imi\u0119");
        dtm.addColumn("Nazwisko");
        dtm.addColumn("PESEL");
        dtm.addColumn("Adres");
        dtm.addColumn("Telefon");
        dtm.addColumn("Lekarz prowadz\u0105cy");
        dtm.addColumn("Upowa\u017cnienia");
        dtm.addColumn("Adnotacje");
        JScrollPane scrTable = new JScrollPane(table);
        JButton zwrocPeselButton = null;
        //w przypadku zwracania PESEL dla potrzeb nowej wizyty
        if (peselReturn)
            zwrocPeselButton = new JButton("Wybierz PESEL zaznaczonego pacjenta");
        //PANELS
        JPanel panelImie = new JPanel();
        JPanel panelNazwisko = new JPanel();
        JPanel panelPesel = new JPanel();
        JPanel panelTelefon = new JPanel();
        JPanel panelAdres = new JPanel();
        JPanel panelUpowaznienia = new JPanel();
        JPanel panelAdnotacje = new JPanel();
        JPanel panelLekarz = new JPanel();
        JPanel panelButtonSzukaj = new JPanel();
        JPanel panelButtonPodaj = new JPanel();
        JPanel panelZobaczButtons = new JPanel();
        panelImie.add(label1); panelImie.add(imieTextField);
        panelNazwisko.add(label2); panelNazwisko.add(nazwiskoTextField);
        panelPesel.add(label3); panelPesel.add(peselTextField);
        panelTelefon.add(label4); panelTelefon.add(telefonTextField);
        panelAdres.add(label5); panelAdres.add(adresTextField);
        panelUpowaznienia.add(label6); panelUpowaznienia.add(upowaznieniaTextField);
        panelAdnotacje.add(label8); panelAdnotacje.add(adnotacjeTextField);
        panelButtonSzukaj.add(znajdzButton);
        panelZobaczButtons.add(zobaczUpowaznieniaButton); panelZobaczButtons.add(zobaczAdnotacjeButton);
        if (peselReturn)
            panelButtonPodaj.add(zwrocPeselButton);
        if (cb != null) {
            panelLekarz.add(label7);
            panelLekarz.add(cb);
        }
        //SETUP EVENTS
        if (cb != null) {
            cb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    id_cb = cb.getSelectedIndex();
                }
            });
        }
        znajdzButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dtm.setRowCount(0);
                vtIdPacjenci.clear();
                String imie, nazwisko, pesel, telefon, adres, upowaznienia, adnotacje;
                int id_lekarz = -1;
                imie = imieTextField.getText(); imie = imie.trim();
                nazwisko = nazwiskoTextField.getText(); nazwisko = nazwisko.trim();
                pesel = peselTextField.getText(); pesel = pesel.trim();
                telefon = telefonTextField.getText(); telefon = telefon.trim();
                adres = adresTextField.getText(); adres = adres.trim();
                upowaznienia = upowaznieniaTextField.getText(); upowaznienia = upowaznienia.trim();
                adnotacje = adnotacjeTextField.getText(); adnotacje = adnotacje.trim();
                if (id_cb >= 0)
                    id_lekarz = vtIndexes.get(id_cb);   //powinno sie zgadzac
                Pacjent pacjentDoWyszukania = new Pacjent(imie, nazwisko, pesel, telefon, adres, adnotacje, upowaznienia, id_lekarz);
                AtomicBoolean result = new AtomicBoolean(false);
                ResultSet wynikiWyszukiwania = pacjentDoWyszukania.searchDatabase(database, result);
                if (!result.get()) {
                    JOptionPane.showMessageDialog(null, "Nie udalo si\u0119 odczyta\u0107 danych", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String imieRes, nazwiskoRes, peselRes, adresRes, telefonRes, upowaznieniaRes, flagiRes, lekarz;
                int id_lekarzRes;
                try {
                    while (wynikiWyszukiwania.next()) {
                        imieRes = wynikiWyszukiwania.getString(1);
                        nazwiskoRes = wynikiWyszukiwania.getString(2);
                        peselRes = wynikiWyszukiwania.getString(3);
                        adresRes = wynikiWyszukiwania.getString(4);
                        telefonRes = wynikiWyszukiwania.getString(5);
                        id_lekarzRes = wynikiWyszukiwania.getInt(6);
                        upowaznieniaRes = wynikiWyszukiwania.getString(7);
                        flagiRes = wynikiWyszukiwania.getString(8);
                        lekarz = Lekarz.znajdzLekarzaDoTabelki(database, id_lekarzRes);
                        vtIdPacjenci.add(wynikiWyszukiwania.getInt(9));
                        dtm.addRow(new Object[] {imieRes, nazwiskoRes, peselRes,
                                adresRes, telefonRes, lekarz,
                                upowaznieniaRes, flagiRes});
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,"B\u0142\u0105d odczytu danych!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });
        if (!peselReturn) {
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if (SwingUtilities.isRightMouseButton(e)) {
                        WyszukajPacjentaWindow.PopUp menu = new WyszukajPacjentaWindow.PopUp(table, dtm);
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
        }
        else {
            zwrocPeselButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    int id_selected = table.getSelectedRow();
                    if (id_selected == -1) {
                        JOptionPane.showMessageDialog(null,"Nic nie wybrano!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    String pesel = table.getValueAt(id_selected, 2).toString();
                    pesel2TextField.setText(pesel);
                    close();
                }
            });
        }
        zobaczUpowaznieniaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int idSelected = table.getSelectedRow();
                if (idSelected == -1) {
                    JOptionPane.showMessageDialog(null, "Nie nie zaznaczono!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String spec;
                try {
                    spec = table.getValueAt(idSelected, 6).toString();
                } catch (NullPointerException ex) {
                    spec = "";
                }
                JOptionPane.showMessageDialog(null, spec, "UPOWA\u017bNIENIA", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        zobaczAdnotacjeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int idSelected = table.getSelectedRow();
                if (idSelected == -1) {
                    JOptionPane.showMessageDialog(null, "Nie nie zaznaczono!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String spec;
                try {
                    spec = table.getValueAt(idSelected, 7).toString();
                } catch (NullPointerException ex) {
                    spec = "";
                }
                JOptionPane.showMessageDialog(null, spec, "ADNOTACJE", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        //GET EVERYTHING TOGETHER
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panelImie);
        getContentPane().add(panelNazwisko);
        getContentPane().add(panelPesel);
        getContentPane().add(panelTelefon);
        getContentPane().add(panelAdres);
        getContentPane().add(panelUpowaznienia);
        getContentPane().add(panelAdnotacje);
        getContentPane().add(panelLekarz);
        getContentPane().add(panelButtonSzukaj);
        getContentPane().add(scrTable);
        getContentPane().add(panelZobaczButtons);
        if (peselReturn)
            getContentPane().add(panelButtonPodaj);
        pack();
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public void close() {
        dispose();
    }
    private Database database;
    private int id_cb;
    private Vector<Integer> vtIndexes;
    private Vector<Integer> vtIdPacjenci;
    private JComboBox createComboBox(AtomicBoolean result) {
        Lekarz lekarz = new Lekarz();
        ResultSet resultSet = lekarz.search(database, result);
        if (!result.get())
            return null;
        Vector<String> vtCombo = new Vector<String>();
        vtCombo.add("");    //pierwszy wiersz [0] pusty
        vtIndexes.add(-1);  //tak samo - analogicznie
        String imie, nazwisko, pwz, total;
        int id_lekarz = 0;
        try {
            while (resultSet.next()) {
                id_lekarz = resultSet.getInt(1);
                imie = resultSet.getString(2);
                nazwisko = resultSet.getString(3);
                pwz = resultSet.getString(4);
                total = imie + " " + nazwisko + ", " + pwz;
                vtCombo.add(total);
                vtIndexes.add(id_lekarz);
            }
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "B\u0142\u0105d odczytu kursora", "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        JComboBox cb = new JComboBox(vtCombo);
        return cb;
    }
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
                    UpdatePacjentWindow updatePacjentWindow = new UpdatePacjentWindow(database, dtm, table, vtIdPacjenci);
                }
            });
            deleteMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    int decision = JOptionPane.showConfirmDialog(null,
                            "Czy na pewno chcesz usun\u0105\u0107 ten wpis?",
                            "INFO", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (decision == JOptionPane.YES_OPTION) {
                        deletePacjent();
                    }
                }
            });
        }
        private JTable table;
        private DefaultTableModel dtm;
        private void deletePacjent() {
            int idSelected = table.getSelectedRow();
            if (idSelected == -1) {
                JOptionPane.showMessageDialog(null,"Nie wybrano pacjenta!","ERROR",JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int idPacjent = vtIdPacjenci.get(idSelected);
            Pacjent pacjentDoUsuniecia = new Pacjent(idPacjent);
            if (pacjentDoUsuniecia.removeFromDatabase(database)) {
                JOptionPane.showMessageDialog(null,"Usuni\u0119to pacjenta.", "INFO",JOptionPane.INFORMATION_MESSAGE);
                dtm.removeRow(idSelected);
                vtIdPacjenci.remove(idSelected);
            }
            else {
                JOptionPane.showMessageDialog(null,"B\u0142\u0105d w usuwaniu pacjenta !","ERROR",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
