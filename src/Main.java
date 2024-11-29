public class Main {
    public static void main(String[] args) {
        // Start the chat server in a separate thread
        new Thread(() -> {
            ChatServer.main(new String[]{});
        }).start();

        // Give the server a moment to start
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Launch the login window
        SwingUtilities.invokeLater(() -> {
            new loginWindow();
        });
    }
}
