package net.sawannaniz.databaseclient.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import net.sawannaniz.databaseclient.dbutils.*;

public class LoginWindow extends JFrame {
    public LoginWindow() {
        super("Logowanie");
        db = null;
        vt = null;
        used = new AtomicBoolean(false);
        no_login = 0;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //MAIN COMPONENTS SETUP
        JLabel label1 = new JLabel("Zaloguj si\u0119 do bazy danych: ");
        JLabel label2 = new JLabel("Login: ");
        JTextField loginTextField = new JTextField(10);
        loginTextField.setText("L3121126");
        JLabel label3 = new JLabel("Has\u0142o: ");
        JPasswordField passwordField = new JPasswordField(10);
        passwordField.setText("12345678");
        JButton buttonOK = new JButton("OK");
        JButton buttonClose = new JButton("Zamknij");
        JButton buttonSettings = new JButton("Ustawienia po\u0142\u0105czenia");
        JLabel label4 = new JLabel("Copyright\u00a9 by Mateusz Marzec");
        //PANELS SETUP
        JPanel labelPanel = new JPanel(); labelPanel.add(label1);
        labelPanel.setBorder(BorderFactory.createEmptyBorder());
        JPanel loginPanel = new JPanel();
        loginPanel.add(label2);
        loginPanel.add(loginTextField);
        JPanel passwordPanel = new JPanel();
        passwordPanel.add(label3);
        passwordPanel.add(passwordField);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(buttonOK);
        buttonsPanel.add(buttonClose);
        JPanel ustawieniaPanel = new JPanel();
        ustawieniaPanel.add(buttonSettings);
        ustawieniaPanel.setBorder(BorderFactory.createEmptyBorder());
        JPanel copyPanel = new JPanel();
        copyPanel.add(label4);
        copyPanel.setBorder(BorderFactory.createEmptyBorder());
        //EVENTS SETUP
        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String user = new String(loginTextField.getText());
                String password = new String(passwordField.getPassword());
                if (!used.get())
                    db = new Database(user, password);
                else
                    db = new Database(user, password, vt.get(0), vt.get(1), vt.get(2), vt.get(3), vt.get(4));
                if (!db.connect()) {
                    JOptionPane.showMessageDialog(null, "B\u0142\u0105d logowania!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    no_login++;
                    if (no_login == MAX_LOGIN)
                        System.exit(-1);
                }
                else {
                    MainWindow mainWindow = new MainWindow(db, user);
                    close();
                }
            }
        });
        buttonClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                realClose();
            }
        });
        buttonSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                vt = new Vector<String>();
                LoginSettingsWindow loginSettingsWindow = new LoginSettingsWindow(vt, used);
            }
        });
        //LAYOUT SETUP
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(labelPanel);
        getContentPane().add(loginPanel);
        getContentPane().add(passwordPanel);
        getContentPane().add(buttonsPanel);
        getContentPane().add(ustawieniaPanel);
        getContentPane().add(copyPanel);
        pack();
        setSize(300, 250);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public void close() {
        dispose();
    }
    public void realClose() {
        System.exit(0);
    }
    private Database db;
    private Vector<String> vt;
    private AtomicBoolean used;
    private final int MAX_LOGIN = 3;
    private int no_login;
}
