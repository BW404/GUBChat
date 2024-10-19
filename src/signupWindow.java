import java.awt.Color;
import java.awt.Font;
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

    public signupWindow() {
        this.setTitle("GUB Chat Signup");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 500);
        this.setLayout(null);
        setLocationRelativeTo(null);
        this.getContentPane().setBackground(new Color(0X1C1D22));

        // gub logo
        JLabel gubLabel = new JLabel(new ImageIcon("src\\img\\gub_logo.png"));
        gubLabel.setBounds(100, 15, 200, 100);
        this.add(gubLabel);

        // Gub Chat login label
        JLabel loginLabel = new JLabel("GUB Chat Login");
        loginLabel.setBounds(125, 110, 200, 50);
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


        // Revalidate and repaint the frame
        this.revalidate();
        this.repaint();

        // Make the frame visible
        this.setVisible(true);
    }


}
