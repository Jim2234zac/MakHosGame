import javax.swing.SwingUtilities;

/**
 * Main.java
 * Starts the application by launching the Main Menu.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }
}