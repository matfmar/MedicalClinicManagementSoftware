import javax.swing.*;

public class DatabaseClient extends JFrame {
    public static void main(String args[]) {
        System.out.println("Klient rozpoczal dzialanie");
        JFrame.setDefaultLookAndFeelDecorated(true);
        LoginWindow loginWindow = new LoginWindow();
    }
}
