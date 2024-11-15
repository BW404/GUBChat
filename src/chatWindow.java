import java.awt.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;



public class ChatWindow extends JFrame {

    private JList<String> contactList;
    private DefaultListModel<String> contactListModel;
    private JTextPane messageArea;
    private JTextField writeMessageField;

    public ChatWindow() {
        setTitle("Chat Application");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);  // Center the window

        // Left Section: Contact List
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(0x26272D)); // Light grey background
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, getHeight()));

        // Search Bar
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(0x26272D));
        searchPanel.setLayout(new BorderLayout());
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBackground(new Color(0xF0F0F0));
        searchField.setForeground(Color.BLACK);
        searchField.setText("Search");
        searchPanel.add(searchField, BorderLayout.CENTER);

        leftPanel.add(searchPanel, BorderLayout.NORTH);

        // Contact List
        contactListModel = new DefaultListModel<>();
        contactListModel.addElement("John Doe");
        contactListModel.addElement("Jane Smith");
        contactListModel.addElement("Alice Johnson");
        contactList = new JList<>(contactListModel);
        contactList.setBackground(new Color(0x1C1D22));
        contactList.setForeground(Color.WHITE);
        contactList.setSelectionBackground(new Color(0xD0D0D0));
        contactList.setSelectionForeground(Color.WHITE);
        contactList.setFont(new Font("Roboto", Font.PLAIN, 14));

        // Set a custom cell renderer for the contact list
        contactList.setCellRenderer(new CustomListCellRenderer());

        // Add the contact list to a scroll pane
        JScrollPane contactScrollPane = new JScrollPane(contactList);
        leftPanel.add(contactScrollPane, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        // Right Section: Chat Area
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(new Color(0x1C1D22));
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Chat Header
        JPanel chatHeader = new JPanel();
        chatHeader.setBackground(new Color(0x1C1D22));
        chatHeader.setLayout(new BorderLayout());
        chatHeader.setPreferredSize(new Dimension(getWidth(), 60));
        chatHeader.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel chatHeaderLabel = new JLabel("Chat with John Doe"); // Rshould get the name from the chat name
        chatHeaderLabel.setForeground(Color.WHITE);
        chatHeaderLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        chatHeader.add(chatHeaderLabel, BorderLayout.CENTER);
        rightPanel.add(chatHeader, BorderLayout.NORTH);

        // Message Area
        messageArea = new JTextPane();
        messageArea.setContentType("text/html");
        messageArea.setEditable(false);
        messageArea.setBackground(new Color(0x141416));
        messageArea.setForeground(Color.WHITE);
        messageArea.setFont(new Font("Lucida Handwriting", Font.PLAIN, 18));
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        rightPanel.add(messageScrollPane, BorderLayout.CENTER);

        // Add some dummy chat messages
        appendMessage("John Doe", "Hi there!", false); 
        appendMessage("You", "Hello! How are you?", true); 
        appendMessage("John Doe", "I'm good, thanks! How about you?", false);
        appendMessage("You", "I'm doing well, thank you.", true);

        // Message Input Field
        JPanel messageInputPanel = new JPanel();
        messageInputPanel.setLayout(new BorderLayout());
        messageInputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        messageInputPanel.setBackground(new Color(0x26272D));

        writeMessageField = new JTextField();
        writeMessageField.setPreferredSize(new Dimension(200, 30));
        writeMessageField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        writeMessageField.setFont(new Font("Arial", Font.PLAIN, 14));
        writeMessageField.setBackground(new Color(0xE0E0E0));
        writeMessageField.setForeground(Color.BLACK);
        messageInputPanel.add(writeMessageField, BorderLayout.CENTER);

        JButton sendButton = new JButton(new ImageIcon("src\\img\\send.png")); //send button icon
        sendButton.setBackground(new Color(0x128C7E));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        messageInputPanel.add(sendButton, BorderLayout.EAST);

        rightPanel.add(messageInputPanel, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.CENTER);
    }
    private void appendMessage(String sender, String message, boolean isRight) {
        String alignment = isRight ? "right" : "left";
        String colorHex = "#9F33FF"; // Set the message background color to #9F33FF
    
        String htmlMessage = String.format(
            "<div style='text-align: %s; margin: 5px;'>"
            + "<p style='background-color: %s; padding: 5px 15px; display: inline-block; max-width: 50%%; margin-%s: 150px;'>"
            + "<b>%s:</b> %s</p></div>",
            alignment, colorHex, isRight ? "left" : "right", sender, message
        );
    
        try {
            HTMLDocument doc = (HTMLDocument) messageArea.getDocument();
            HTMLEditorKit kit = (HTMLEditorKit) messageArea.getEditorKit();
            kit.insertHTML(doc, doc.getLength(), htmlMessage, 0, 0, null);
        } catch (BadLocationException | IOException e) {
            e.printStackTrace();
        }
    }


// Custom List Cell Renderer
class CustomListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setOpaque(true); // Make sure the label is opaque to show the background color


        // Set background color based on selection state
        if (isSelected) {
            label.setBackground(new Color(0xD0D0D0)); // Background color for selected items
            label.setForeground(Color.BLACK); // Set text color for selected items
        } else {
            label.setBackground(new Color(0x2C2D32)); // Background color for non-selected items
            label.setForeground(Color.WHITE); // Set text color for non-selected items
        }

        // Set a visible border
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1), // Outer border
            BorderFactory.createEmptyBorder(20, 30, 20, 5) // Inner padding
        ));
        label.setHorizontalAlignment(SwingConstants.LEFT);

        return label;
    }
}


    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatWindow chatWindow = new ChatWindow();
            chatWindow.setVisible(true);
        });
    }
}