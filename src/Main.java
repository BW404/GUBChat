import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.out.println("Preparing to open login window"); // Debugging output
        SwingUtilities.invokeLater(() -> {
            System.out.println("Inside invokeLater, opening login window"); // Debugging output
            new loginWindow(); // Open the login window
        });
    }
}
