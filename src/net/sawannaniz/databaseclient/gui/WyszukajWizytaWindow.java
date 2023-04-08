package net.sawannaniz.databaseclient.gui;

import javax.swing.*;
import javax.swing.table.*;
import javax.tools.Tool;

import net.sawannaniz.databaseclient.ctrl.*;
import net.sawannaniz.databaseclient.dbutils.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class WyszukajWizytaWindow extends JFrame {
    public WyszukajWizytaWindow(Database db, boolean cd, User us) {
        super("Wyszukaj wizyt\u0119");
        database = db;
        vtIdLekarze = new Vector<Integer>();
        vtIdPomieszczenia = new Vector<Integer>();
        vtIdPacjenci = new Vector<Integer>();
        vtIdWizyty = new Vector<Integer>();
        id_cb_lek = -1;
        id_cb_pom = -1;
        id_cb_pac = -1;
        czyDzisiejsze = cd;
        user = us;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        AtomicBoolean result = new AtomicBoolean(false);
        //COMPONENTS
        JLabel label1 = new JLabel("Nazwisko pacjenta: ");
        JTextField pacjentTextField = new JTextField(10);
        JLabel label2 = new JLabel("Lekarz: ");
        JComboBox cbLekarz = createComboBoxLekarz(result);
        id_cb_lek = comboboxCheckInfo(cbLekarz);

        //ponizsze tylko dla wyszukiwania historycznego
        JLabel label3, label4, label5;
        JComboBox cbPomieszczenie;
        JTextField dataOdTextField, dataDoTextField;
        if (!czyDzisiejsze) {
            label3 = new JLabel("Pomieszczenie: ");
            cbPomieszczenie = createComboBoxPomieszczenie(result);
            id_cb_pom = comboboxCheckInfo(cbPomieszczenie);
            label4 = new JLabel("data od: ");
            label5 = new JLabel("data do: ");
            dataOdTextField = new JTextField("RRRR-MM-DD HH:MM");
            dataDoTextField = new JTextField("RRRR-MM-DD HH:MM");
        }
        else {
            label3 = null; label4 = null; label5 = null;
            cbPomieszczenie = null;
            dataOdTextField = null; dataDoTextField = null;
        }

        JButton szukajButton = new JButton("Szukaj");
        DefaultTableModel dtm = new DefaultTableModel();
        JTable table = new JTable(dtm);
        dtm.addColumn("Data");
        dtm.addColumn("Nazwisko pacjenta");
        dtm.addColumn("Imi\u0119 pacjenta");
        dtm.addColumn("PESEL pacjenta");
        dtm.addColumn("Lekarz");
        dtm.addColumn("Pomieszczenie");
        dtm.addColumn("Zrealizowana");
        JScrollPane scrTable = new JScrollPane(table);
        //PANELS
        JPanel panelPacjent = new JPanel(); panelPacjent.add(label1); panelPacjent.add(pacjentTextField);
        JPanel panelLekarz = new JPanel();
        if (cbLekarz != null) {
            panelLekarz.add(label2);
            panelLekarz.add(cbLekarz);
        }
        JPanel panelPomieszczenie = new JPanel();
        if (cbPomieszczenie != null) {
            panelPomieszczenie.add(label3);
            panelPomieszczenie.add(cbPomieszczenie);
        }
        JPanel panelData = new JPanel();
        panelData.setLayout(new BoxLayout(panelData, BoxLayout.X_AXIS));
        if (!czyDzisiejsze) {
            panelData.add(label4);
            panelData.add(dataOdTextField);
            panelData.add(label5);
            panelData.add(dataDoTextField);
        }
        //SETUP EVENTS
        if (cbLekarz != null) {
            cbLekarz.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    id_cb_lek = cbLekarz.getSelectedIndex();
                }
            });
        }
        if (cbPomieszczenie != null) {
            cbPomieszczenie.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    id_cb_pom = cbPomieszczenie.getSelectedIndex();
                }
            });
        }
        if (dataOdTextField != null) {
            dataOdTextField.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    dataOdTextField.setText("");
                }
            });
        }
        if (dataDoTextField != null) {
            dataDoTextField.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    dataDoTextField.setText("");
                }
            });
        }
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (SwingUtilities.isRightMouseButton(e)) {
                    WyszukajWizytaWindow.PopUp menu = new WyszukajWizytaWindow.PopUp(table, dtm);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        szukajButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dtm.setRowCount(0);
                vtIdWizyty.clear();
                String dataOd, dataDo;
                String nazwisko = pacjentTextField.getText(); nazwisko = nazwisko.trim();
                int id_lekarz = -1, id_pomieszczenie = -1;
                if (id_cb_lek != -1) id_lekarz = vtIdLekarze.get(id_cb_lek);
                if (id_cb_pom != -1) id_pomieszczenie = vtIdPomieszczenia.get(id_cb_pom);
                if (!czyDzisiejsze) {
                    dataOd = dataOdTextField.getText();
                    dataOd = dataOd.trim();
                    if (!dataOd.isEmpty()) dataOd = dataOd + ":00";
                    dataDo = dataDoTextField.getText();
                    dataDo = dataDo.trim();
                    if (!dataDo.isEmpty()) dataDo = dataDo + ":00";
                }
                else {
                    dataOd = Database.getCurrentDate() + " 00:00:00";
                    dataDo = Database.getCurrentDate() + " 23:59:59";
                }
                Wizyta wizytaDoZnalezienia = new Wizyta(nazwisko, id_lekarz, id_pomieszczenie);
                AtomicBoolean result = new AtomicBoolean(false);
                ResultSet wynikiWyszukiwania = wizytaDoZnalezienia.searchDatabase(database, dataOd, dataDo, result);
                if (!result.get()) {
                    JOptionPane.showMessageDialog(null, "Nie uda\u0142o si\u0119 odczyta\u0107 danych!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String dataRes, nazwiskoRes, imieRes, peselRes, lekarzTotal, pomieszczenieTotal, zrealizowanaTekst;
                int id_lekarzRes, id_pomieszczenieRes, id_wizytaRes, zrealizowana;
                try {
                    while (wynikiWyszukiwania.next()) {
                        dataRes = Database.removeSecondsFromDatetime(wynikiWyszukiwania.getString(1));
                        nazwiskoRes = wynikiWyszukiwania.getString(2);
                        imieRes = wynikiWyszukiwania.getString(3);
                        peselRes = wynikiWyszukiwania.getString(4);
                        id_lekarzRes = wynikiWyszukiwania.getInt(5);
                        id_pomieszczenieRes = wynikiWyszukiwania.getInt(6);
                        id_wizytaRes = wynikiWyszukiwania.getInt(7);
                        zrealizowana = wynikiWyszukiwania.getInt(8);
                        lekarzTotal = Lekarz.znajdzLekarzaDoTabelki(database, id_lekarzRes);
                        pomieszczenieTotal = Pomieszczenie.znajdzPomieszczenieDoTabelki(database, id_pomieszczenieRes);
                        if (zrealizowana == 1) zrealizowanaTekst = "TAK";
                        else zrealizowanaTekst = "NIE";
                        vtIdWizyty.add(id_wizytaRes);
                        dtm.addRow(new Object[] {dataRes, nazwiskoRes, imieRes, peselRes,
                                lekarzTotal, pomieszczenieTotal, zrealizowanaTekst});
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,"Nie uda\u0142o si\u0119 odczyta\u0107 danych!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

                TableColumnModel tcm = table.getColumnModel();
                TableColumn tm = tcm.getColumn(6);
                tm.setCellRenderer(new ColoredTableCellRenderer());
            }
        });

        //GET EVERYTHING TOGETHER
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panelPacjent);
        getContentPane().add(panelLekarz);
        getContentPane().add(panelPomieszczenie);
        getContentPane().add(panelData);
        getContentPane().add(szukajButton);
        getContentPane().add(scrTable);
        pack();
        Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dimFrame = new Dimension(dimScreen.width/2, dimScreen.height/2);
        setSize(dimFrame);
        setLocationRelativeTo(null);
        setVisible(true);
        if (czyDzisiejsze)
            szukajButton.doClick();
    }
    private Database database;
    private Vector<Integer> vtIdLekarze, vtIdPomieszczenia, vtIdPacjenci, vtIdWizyty;
    private int id_cb_lek, id_cb_pom, id_cb_pac;
    private User user;
    private boolean czyDzisiejsze;
    private int comboboxCheckInfo(JComboBox cb) {
        if (cb != null) {
            return cb.getSelectedIndex();
        }
        else {
            JOptionPane.showMessageDialog(null,"Nie uda\u0142o si\u0119 odczyta\u0107 danych!", "ERROR",JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }
    private JComboBox createComboBoxLekarz(AtomicBoolean result) {
        Lekarz lekarz = new Lekarz();
        ResultSet resultSet = lekarz.search(database, result);
        if (!result.get())
            return null;
        Vector<String> vtCombo = new Vector<String>();
        vtCombo.add("");    //pierwszy wiersz [0] pusty
        vtIdLekarze.add(-1);  //tak samo - analogicznie
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
                vtIdLekarze.add(id_lekarz);
            }
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie uda\u0142o si\u0119 odczyta\u0107 danych!", "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        JComboBox cb = new JComboBox(vtCombo);
        //get selected by current user if a physician and wizyty sa szukane tylko dzisiejsze
        if (czyDzisiejsze && user.getRole() == Database.Role.LEKARZ) {
            String pwz_user = user.getLogin().substring(1);
            int id_lekarz_user = Lekarz.znajdzLekarzaPoPWZ(database, pwz_user);
            if (id_lekarz_user > 0) {
                int i = 0;
                for (int id : vtIdLekarze) {
                    if (id == id_lekarz_user)
                        break;
                    i++;
                }
                cb.setSelectedIndex(i);
            }
        }
        return cb;
    }
    private JComboBox createComboBoxPomieszczenie(AtomicBoolean result) {
        Pomieszczenie pomieszczenie = new Pomieszczenie();
        ResultSet resultSet = pomieszczenie.search(database, result);
        if (!result.get())
            return null;
        Vector<String> vtCombo = new Vector<String>();
        vtCombo.add("");    //pierwszy wiersz [0] pusty
        vtIdPomieszczenia.add(-1);  //tak samo - analogicznie
        String numer, total;
        int pietro;
        int id_pomieszczenie = 0;
        try {
            while (resultSet.next()) {
                id_pomieszczenie = resultSet.getInt(1);
                numer = resultSet.getString(2);
                pietro = resultSet.getInt(3);
                total = numer + " " + Integer.toString(pietro);
                vtCombo.add(total);
                vtIdPomieszczenia.add(id_pomieszczenie);
            }
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie uda\u0142o si\u0119 odczyta\u0107 danych!", "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        JComboBox cb = new JComboBox(vtCombo);
        return cb;
    }
    private class PopUp extends JPopupMenu {
        public PopUp(JTable tab, DefaultTableModel d) {
            table = tab;
            dtm = d;
            JMenuItem deleteMenuItem = new JMenuItem("Usu\u0144");
            JMenuItem realizujMenuItem = new JMenuItem("Realizuj wizyt\u0119");
            JMenuItem przegladajWizyte = new JMenuItem("Przegl\u0105daj");
            if (czyDzisiejsze) {
                add(realizujMenuItem);
            }
            else {
                add(deleteMenuItem);
            }
            add(przegladajWizyte);
            deleteMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    int decision = JOptionPane.showConfirmDialog(null,
                            "Czy na pewno chcesz usun\u0105\u0107 t\u0105 wizyt\u0119 z systemu?",
                            "PYTANIE", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (decision == JOptionPane.YES_OPTION) {
                        deleteWizyta();
                    }
                }
            });
            realizujMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    RealizujWizyteWindow realizujWizyteWindow = new RealizujWizyteWindow(database, dtm, table, vtIdWizyty, user);
                }
            });
            przegladajWizyte.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    PrzegladajWizyteWindow przegladajWizyteWindow = new PrzegladajWizyteWindow(database, dtm, table, vtIdWizyty, user);
                }
            });
        }
        private JTable table;
        private DefaultTableModel dtm;
        private void deleteWizyta() {
            int idSelected = table.getSelectedRow();
            if (idSelected == -1) {
                JOptionPane.showMessageDialog(null,"Nie wybrano wizyty!","ERROR",JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int idWizyta = vtIdWizyty.get(idSelected);
            Wizyta wizytaDoUsuniecia = new Wizyta(idWizyta);
            if (wizytaDoUsuniecia.removeFromDatabase(database)) {
                JOptionPane.showMessageDialog(null,"Usuni\u0119to wizyt\u0119.", "INFO",JOptionPane.INFORMATION_MESSAGE);
                dtm.removeRow(idSelected);
                vtIdWizyty.remove(idSelected);
            }
            else {
                JOptionPane.showMessageDialog(null,"B\u0142\u0105d w usuwaniu wizyty!","ERROR",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

class ColoredTableCellRenderer extends DefaultTableCellRenderer
{
    public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column) {
        final Color mygreen = new Color(0, 255, 0);
        final Color myred = new Color(255, 0, 0);
        setEnabled(table == null || table.isEnabled());
        if (table.getValueAt(row, 6) == "TAK")
            setBackground(mygreen);
        else
            setBackground(myred);
        super.getTableCellRendererComponent(table, value, selected, focused, row, column);
        return this;
    }
}
