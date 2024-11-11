import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class loginWindow extends JFrame {
    // Declare the components
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton; // Fixed typo in variable name
    private final JButton signupButton;

    // Constructor
    public loginWindow() {
        this.setTitle("GUB Chat Login");
        this.setIconImage(new ImageIcon("src/img/gub_logo.png").getImage()); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 500);
        this.getContentPane().setBackground(new Color(0X26272D));
        this.setResizable(false);
        System.out.println("Login Window");
        this.setLayout(null);
        setLocationRelativeTo(null);

        // GUB logo
        JLabel gubLabel = new JLabel(new ImageIcon("src/img/gub_logo.png"));
        gubLabel.setBounds(100, 15, 200, 100);
        this.add(gubLabel);

        // Gub Chat login label
        JLabel loginLabel = new JLabel("GUB Chat Login");
        loginLabel.setBounds(125, 110, 200, 50);
        loginLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        loginLabel.setForeground(Color.WHITE);
        this.add(loginLabel);

        // Username Label
        JLabel usernameLabel = new JLabel("Username:"); // Fixed typo in variable name
        usernameLabel.setBounds(150, 150, 300, 50);
        usernameLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        usernameLabel.setForeground(Color.WHITE);
        this.add(usernameLabel);

        // Username Field
        usernameField = new JTextField();
        usernameField.setBounds(100, 200, 200, 30);
        usernameField.setFont(new Font("Dubai Bold", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                usernameField.getBorder(), new EmptyBorder(5, 10, 5, 10)));
        usernameField.setForeground(Color.WHITE);
        usernameField.setBackground(new Color(0X1C1D22));
        usernameField.setCaretColor(Color.WHITE);
        this.add(usernameField);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(150, 225, 300, 50);
        passwordLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        passwordLabel.setForeground(Color.WHITE);
        this.add(passwordLabel);

        // Password Field
        passwordField = new JPasswordField();
        passwordField.setBounds(100, 275, 200, 30);
        passwordField.setFont(new Font("Dubai Bold", Font.PLAIN, 20));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                passwordField.getBorder(), new EmptyBorder(5, 10, 5, 10)));
        passwordField.setForeground(Color.WHITE);
        passwordField.setBackground(new Color(0X1C1D22));
        passwordField.setCaretColor(Color.WHITE);
        this.add(passwordField);

        // Login Button
        loginButton = new JButton("Login"); // Fixed variable name
        loginButton.setBounds(100, 330, 200, 30);
        loginButton.setFont(new Font("Comic Sans MS Bold", Font.PLAIN, 16));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(0X40a366));
        loginButton.setFocusPainted(false);
        this.add(loginButton);

        // "Don't have an account? Signup" Button
        signupButton = new JButton("Don't have an account? SignUp");
        signupButton.setBounds(100, 400, 200, 30);
        signupButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        signupButton.setBorder(BorderFactory.createEmptyBorder());
        signupButton.setForeground(Color.WHITE);
        signupButton.setBackground(new Color(0X26272D));
        signupButton.setFocusPainted(false);
        this.add(signupButton);

        // Add ActionListener to the signup button
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the login window
                dispose();
                // Open the signup window
                new signupWindow(); 
            }
        });

        // Add ActionListener to the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Authenticate the user
                String enteredUsername = usernameField.getText();
                String enteredPassword = new String(passwordField.getPassword());
                if (authenticate(enteredUsername, enteredPassword)) {
                    // Close the login window
                    dispose();
                    // Open the chat window
                    new ChatWindow();
                } else {
                    // Show an error message
                    JOptionPane.showMessageDialog(loginWindow.this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Make the frame visible after adding components
        this.setVisible(true);
    }

    private boolean authenticate(String username, String password) {
        // Replace this with your actual authentication logic
        // TODO: Authenticate the user
        // TODO: It should connect ot teh server and check if the username and password are valid

        return username.equals("taj") && password.equals("pass") || username.equals("admin") && password.equals("admin");
    }


}
