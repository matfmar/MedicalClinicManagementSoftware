package net.sawannaniz.databaseclient.dbutils;

import javax.xml.crypto.Data;

public class User {
    public User() {
        login = "";
        role = Database.Role.NO_ROLE;
    }
    public User(String s, Database.Role r) {
        login = s;
        if (r == null)
            role = Database.Role.NO_ROLE;
        else
            role = r;
    }
    public Database.Role getRole() {
        return role;
    }
    public String getLogin() {
        return login;
    }
    private String login;
    private Database.Role role;
}
