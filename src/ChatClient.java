import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "jalauddintaj-58689.portmap.io";
    private static final int PORT = 58689;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
            System.out.println("Connected to the chat server.");

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            Thread readThread = new Thread(() -> {
                try {
                    Object message;
                    while ((message = in.readObject()) != null) {
                        if (message instanceof String) {
                            System.out.println((String) message);
                        } else if (message instanceof FileWrapper) {
                            saveFile((FileWrapper) message);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Connection closed.");
                }
            });

            Thread writeThread = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                try {
                    System.out.println((String) in.readObject());
                    String username = scanner.nextLine();
                    out.writeObject(username);

                    while (true) {
                        String input = scanner.nextLine();
                        if (input.startsWith("/sendfile")) {
                            String[] parts = input.split(" ", 3);
                            if (parts.length == 3) {
                                sendFile(parts[1], parts[2], out);
                            } else {
                                System.out.println("Usage: /sendfile recipient filepath");
                            }
                        } else {
                            out.writeObject(input);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });

            readThread.start();
            writeThread.start();

            readThread.join();
            writeThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void sendFile(String recipient, String filepath, ObjectOutputStream out) {
        try {
            File file = new File(filepath);
            byte[] content = new byte[(int) file.length()];
            try (FileInputStream fis = new FileInputStream(file)) {
                fis.read(content);
            }
            FileWrapper fileWrapper = new FileWrapper(recipient, file.getName(), content);
            out.writeObject(fileWrapper);
            System.out.println("File sent to " + recipient);
        } catch (IOException e) {
            System.out.println("Error sending file: " + e.getMessage());
        }
    }

    private static void saveFile(FileWrapper fileWrapper) {
        try {
            File file = new File("received_" + fileWrapper.getFilename());
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileWrapper.getContent());
            }
            System.out.println("File received: " + file.getName());
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
}
