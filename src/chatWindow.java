import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ChatWindow extends JFrame implements ChatClient.MessageListener {
    // ... [previous code remains the same until onFileReceived method] ...

    @Override
    public void onFileReceived(FileWrapper file) {
        SwingUtilities.invokeLater(() -> {
            String sender = file.getRecipient(); // Using recipient as sender since that's how it's implemented
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(file.getFilename()));
            fileChooser.setDialogTitle("Save Received File");
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();
                try {
                    try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                        fos.write(file.getContent());
                    }
                    appendMessage(sender, "System", 
                        "Received file: " + file.getFilename() + "\nSaved as: " + saveFile.getName(), 
                        false);
                } catch (IOException e) {
                    appendMessage(sender, "System", 
                        "Error saving file: " + e.getMessage(), 
                        false);
                }
            }
        });
    }

    // ... [rest of the code remains the same] ...
}
