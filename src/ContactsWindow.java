import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ContactsWindow extends JFrame implements ChatClient.MessageListener {
    private JList<String> contactList;
    private DefaultListModel<String> contactListModel;
    private ChatClient chatClient;
    private String username;
    private JLabel connectionStatus;

    public ContactsWindow(String username, ChatClient chatClient) {
        this.username = username;
        this.chatClient = chatClient;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("GUB Chat - " + username);
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0x26272D));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Chats");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Connection Status
        connectionStatus = new JLabel("Connected");
        connectionStatus.setForeground(Color.GREEN);
        connectionStatus.setHorizontalAlignment(SwingConstants.RIGHT);
        headerPanel.add(connectionStatus, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Contacts List
        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);
        contactList.setBackground(new Color(0x1C1D22));
        contactList.setForeground(Color.WHITE);
        contactList.setSelectionBackground(new Color(0x168AFF));
        contactList.setSelectionForeground(Color.WHITE);
        contactList.setFont(new Font("Roboto", Font.PLAIN, 14));
        contactList.setCellRenderer(new ContactListCellRenderer());

        // Add selection listener to handle chat window creation
        contactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedUser = contactList.getSelectedValue();
                if (selectedUser != null) {
                    ChatManager.getInstance().openChatWindow(selectedUser);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(contactList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void onMessageReceived(String message) {
        if (message.startsWith("ACTIVE_CLIENTS:")) {
            String[] clients = message.substring("ACTIVE_CLIENTS:".length()).split(",");
            updateContactList(clients);
        }
    }

    @Override
    public void onFileReceived(FileWrapper file) {
        // Files are handled in individual chat windows
    }

    @Override
    public void onConnectionStatusChanged(boolean connected) {
        SwingUtilities.invokeLater(() -> {
            connectionStatus.setText(connected ? "Connected" : "Disconnected");
            connectionStatus.setForeground(connected ? Color.GREEN : Color.RED);
        });
    }

    private void updateContactList(String[] clients) {
        SwingUtilities.invokeLater(() -> {
            contactListModel.clear();
            for (String client : clients) {
                if (!client.equals(username)) {
                    contactListModel.addElement(client);
                }
            }
        });
    }

    // Custom cell renderer for contacts list
    private class ContactListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x2C2D32)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
            return label;
        }
    }
}
