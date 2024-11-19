import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int PORT = 8000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
            System.out.println("Connected to the chat server.");

            Thread readThread = new Thread(new ReadHandler(socket));
            Thread writeThread = new Thread(new WriteHandler(socket));

            readThread.start();
            writeThread.start();

            // Wait for threads to finish before exiting
            readThread.join();
            writeThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class ReadHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;

        public ReadHandler(Socket socket) {
            this.socket = socket;
            try {
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                System.out.println("Error initializing input stream: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.out.println("Connection closed: " + e.getMessage());
            } finally {
                closeResources();
            }
        }

        private void closeResources() {
            try {
                if (in != null) {
                    in.close();
                }
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    static class WriteHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private Scanner scanner;

        public WriteHandler(Socket socket) {
            this.socket = socket;
            try {
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.scanner = new Scanner(System.in);
            } catch (IOException e) {
                System.out.println("Error initializing output stream: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = scanner.nextLine();
                    if (message.equalsIgnoreCase("/quit")) {
                        out.println("Client has disconnected.");
                        break;
                    }
                    out.println(message);
                }
            } finally {
                closeResources();
            }
        }

        private void closeResources() {
            try {
                if (out != null) {
                    out.close();
                }
                if (!socket.isClosed()) {
                    socket.close();
                }
                if (scanner != null) {
                    scanner.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
