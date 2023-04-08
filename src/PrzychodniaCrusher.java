import net.sawannaniz.databaseclient.gui.*;
import javax.swing.*;

/**
 * The Przychodnia Crusher program is a client application for a database server, with which it forms the complete system of data storage for a small medical clinic.
 *
 * @author Mateusz Marzec
 * @version 1.0
 * @since 2023-04-08
 */
public class PrzychodniaCrusher extends JFrame {
    public static void main(String args[]) {
        System.out.println("Start programu");
        JFrame.setDefaultLookAndFeelDecorated(true);
        LoginWindow loginWindow = new LoginWindow();
    }
}
