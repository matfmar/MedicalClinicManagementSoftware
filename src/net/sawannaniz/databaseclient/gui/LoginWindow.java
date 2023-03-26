package net.sawannaniz.databaseclient.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import net.sawannaniz.databaseclient.dbutils.*;

public class LoginWindow extends JFrame {
    public LoginWindow() {
        super("Logowanie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //MAIN COMPONENTS SETUP
        JLabel label1 = new JLabel("Zaloguj sie do bazy danych: ", JLabel.LEFT);
        JLabel label2 = new JLabel("Login:", JLabel.RIGHT);
        JTextField loginTextField = new JTextField(10);
        JLabel label3 = new JLabel("Haslo:", JLabel.RIGHT);
        JPasswordField passwordField = new JPasswordField(10);
        JButton buttonOK = new JButton("OK");
        JButton buttonClose = new JButton("Zamknij");
        JLabel label4 = new JLabel("Copyright by Mateusz Marzec");
        //PANELS SETUP
        JPanel loginPanel = new JPanel();
        loginPanel.add(label2);
        loginPanel.add(loginTextField);
        JPanel passwordPanel = new JPanel();
        passwordPanel.add(label3);
        passwordPanel.add(passwordField);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(buttonOK);
        buttonsPanel.add(buttonClose);
        //EVENTS SETUP
        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String user = new String(loginTextField.getText());
                String password = new String(passwordField.getPassword());
                Database db = new Database(user, password);
                db.Connect();
            }
        });
        buttonClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                close();
            }
        });
        //LAYOUT SETUP
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(label1);
        getContentPane().add(loginPanel);
        getContentPane().add(passwordPanel);
        getContentPane().add(buttonsPanel);
        getContentPane().add(label4);

        pack();
        setVisible(true);
    }
    public void close() {
        this.dispose();
    }
}
