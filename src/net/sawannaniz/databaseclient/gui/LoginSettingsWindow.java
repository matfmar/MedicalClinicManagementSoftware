package net.sawannaniz.databaseclient.gui;

import javax.swing.*;
import javax.xml.crypto.Data;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginSettingsWindow  extends JFrame {
    public LoginSettingsWindow(Vector<String> v, AtomicBoolean u) {
        super("Ustawienia po\u0142\u0105czenia");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        vt = v;
        used = u;
        //COMPONENTS
        JLabel label1 = new JLabel("Adres hosta: ");
        JLabel label2 = new JLabel("Port: ");
        JLabel label3 = new JLabel("Baza danych: ");
        JLabel label4 = new JLabel("Has\u0142o zmiany: ");
        JTextField adresTextField = new JTextField("172.106.0.62", 15);
        JTextField portTextField = new JTextField("18601", 15);
        JTextField bazaTextField = new JTextField("Przychodnia", 15);
        JPasswordField hasloPasswordField = new JPasswordField("password", 15);
        JCheckBox sslCheckBox = new JCheckBox("SSL/TLS", true);
        JButton okButton = new JButton("OK");
        JButton closeButton = new JButton("Zamknij");
        JPanel panelAdres = new JPanel();
        JPanel panelPort = new JPanel();
        JPanel panelBaza = new JPanel();
        panelAdres.add(label1); panelAdres.add(adresTextField);
        panelPort.add(label2); panelPort.add(portTextField);
        panelBaza.add(label3); panelBaza.add(bazaTextField);
        JPanel panelButtons = new JPanel();
        panelButtons.add(okButton); panelButtons.add(closeButton);
        JPanel panelCheck = new JPanel(); panelCheck.add(sslCheckBox);
        JPanel panelPassword = new JPanel(); panelPassword.add(label4); panelPassword.add(hasloPasswordField);
        //EVENTS
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                used.set(false);
                close();
            }
        });
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String adres = adresTextField.getText(); adres = adres.trim();
                String port = portTextField.getText(); port = port.trim();
                String baza = bazaTextField.getText(); baza = baza.trim();
                String password = new String(hasloPasswordField.getPassword());
                boolean ssl = sslCheckBox.isSelected();
                String sslStr = "";
                if (ssl)
                    sslStr = "YES";
                else
                    sslStr = "NO";
                if (adres.isEmpty() || (port.isEmpty() || baza.isEmpty())) {
                    JOptionPane.showMessageDialog(null, "Za ma\u0142o danych", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!ssl)
                    JOptionPane.showMessageDialog(null,"Odradzam \u0142\u0105czenie bez SSL !", "UWAGA", JOptionPane.WARNING_MESSAGE);
                if (!passwordVerification(password)) {
                    JOptionPane.showMessageDialog(null,"Z\u0142e has\u0142o!");
                    return;
                }
                vt.add(adres);
                vt.add(port);
                vt.add(baza);
                vt.add(sslStr);
                vt.add(password);
                used.set(true);
                close();
            }
        });
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panelAdres);
        getContentPane().add(panelPort);
        getContentPane().add(panelBaza);
        getContentPane().add(panelCheck);
        getContentPane().add(panelPassword);
        getContentPane().add(panelButtons);
        pack();
        setSize(500, 270);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public void close() {
        dispose();
    }
    private Vector<String> vt;
    private AtomicBoolean used;
    private int HASH = 1216985755;
    private boolean passwordVerification(String s) {
        String stringToHash = s;
        int hash = 0;
        hash = stringToHash.hashCode();
        return (hash == HASH);
    }
}
