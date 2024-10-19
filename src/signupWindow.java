import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class signupWindow extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton signupButton;
    private final JButton loginButton;

    public signupWindow() {
        this.setTitle("GUB Chat Signup");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 500);
        this.setLayout(null);
        this.setResizable(false);
        setLocationRelativeTo(null);
        this.getContentPane().setBackground(new Color(0X1C1D22));

        // gub logo
        JLabel gubLabel = new JLabel(new ImageIcon("src\\img\\gub_logo.png"));
        gubLabel.setBounds(100, 15, 200, 100);
        this.add(gubLabel);

        // Gub Chat login label
        JLabel loginLabel = new JLabel("GUB Chat SignUp");
        loginLabel.setBounds(120, 110, 200, 50);
        loginLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        loginLabel.setForeground(Color.WHITE);
        this.add(loginLabel);

        // Username Label
        JLabel usernamLabel = new JLabel("Username:");
        usernamLabel.setBounds(150, 150, 300, 50);
        usernamLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        usernamLabel.setForeground(Color.WHITE);
        this.add(usernamLabel);

        // Username Field
        usernameField = new JTextField();
        usernameField.setBounds(100, 200, 200, 30);
        usernameField.setFont(new Font("Dubai Bold", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
        usernameField.getBorder(), new EmptyBorder(5, 10, 5, 10) ));    
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
        passwordField.getBorder(), new EmptyBorder(5, 10, 5, 10) ));
        passwordField.setForeground(Color.WHITE);
        passwordField.setBackground(new Color(0X1C1D22));
        passwordField.setCaretColor(Color.WHITE);
        this.add(passwordField);

        // Login Button
        signupButton = new JButton("Signup");
        signupButton.setBounds(100, 330, 200, 30);
        signupButton.setFont(new Font("Comic Sans MS Bold", Font.PLAIN, 16));
        signupButton.setForeground(Color.WHITE);
        signupButton.setBackground(new Color(0X3B5998));
        signupButton.setFocusPainted(false);
        this.add(signupButton);


        // "Already have an account? Login" Button
        loginButton = new JButton("Already have an account? Login");
        loginButton.setBounds(100, 400, 200, 30);
        loginButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(0X1C1D22));
        loginButton.setFocusPainted(false);
        this.add(loginButton);

        // Add ActionListener to the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the signup window
                dispose();
                // Open the login window
                new loginWindow();
            }
        });

        // Revalidate and repaint the frame
        this.revalidate();
        this.repaint();

        // Make the frame visible
        this.setVisible(true);
    }


}
