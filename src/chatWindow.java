import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ChatWindow extends JFrame {
    private JList<String> contactList;
    private DefaultListModel<String> contactListModel;
    private JTextPane messageArea;
    private JTextField writeMessageField;
    private ChatClient chatClient; // Add ChatClient instance

    public ChatWindow(ChatClient chatClient) {
        this.chatClient = chatClient; // Initialize ChatClient
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
        searchPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
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

        JLabel chatHeaderLabel = new JLabel("Chat with John Doe"); // Should get the name from the chat name
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

        // Red and Blue are the colors of the chat bubbles
        Color red = new Color(0xFF6070);
        Color blue = new Color(0x168AFF);

        // Add some dummy chat messages
        // appendMessage("John Doe", "Hi there!", false, red); 
        // appendMessage("You", "Hello! How are you?", true, blue); 
        // appendMessage("John Doe", "I'm good, thanks! How about you?", false, red);
        // appendMessage("You", "I'm doing well, thank you.", true, blue);


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

        JButton sendButton = new JButton("Send"); // Change to a simple text button for now
        sendButton.setBackground(new Color(0x128C7E));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = writeMessageField.getText();
                if (!message.isEmpty()) {
                    chatClient.sendMessage(message); // Send message to ChatClient
                    writeMessageField.setText(""); // Clear the input field
                }
            }
        });
        messageInputPanel.add(sendButton, BorderLayout.EAST);
        add(messageInputPanel, BorderLayout.SOUTH);
    }

    // Define the CustomListCellRenderer class
    class CustomListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (isSelected) {
                component.setBackground(new Color(0xD0D0D0));
                component.setForeground(Color.BLACK);
            } else {
                component.setBackground(new Color(0x1C1D22));
                component.setForeground(Color.WHITE);
            }
            return component;
        }
    }

    public void appendMessage(String sender, String message) {
        // Append received messages to the message area
        String htmlMessage = String.format("<b>%s:</b> %s<br>", sender, message);
        messageArea.setText(messageArea.getText() + htmlMessage);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ChatClient chatClient = new ChatClient(ChatWindow); // Create a ChatClient instance with ChatWindow argument
                ChatWindow chatWindow = new ChatWindow(chatClient);
                chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                chatWindow.setSize(400, 600);
                chatWindow.setVisible(true);
            }
        });
    }
}