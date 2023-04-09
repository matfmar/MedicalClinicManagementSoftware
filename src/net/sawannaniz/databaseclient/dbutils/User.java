package net.sawannaniz.databaseclient.dbutils;

/**
 * Represents a user logged in to a database.
 */
public class User {
    /**
     * Creates an empty (default) user.
     */
    public User() {
        login = "";
        role = Database.Role.NO_ROLE;
    }

    /**
     * Creates a user based on actual login and role
     *
     * @param s user's login
     * @param r user's role {@link Database.Role}
     */
    public User(String s, Database.Role r) {
        login = s;
        if (r == null)
            role = Database.Role.NO_ROLE;
        else
            role = r;
    }

    /**
     * Returns the user's role.
     *
     * @return user's role
     */
    public Database.Role getRole() {
        return role;
    }

    /**
     * Returns the user's login
     *
     * @return user's login
     */
    public String getLogin() {
        return login;
    }
    private String login;
    private Database.Role role;
}
