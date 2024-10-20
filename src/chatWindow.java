import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
public class chatWindow extends JFrame {
    public chatWindow() {
         
        this.setTitle("GUB Chat");
        this.setIconImage(new ImageIcon("src/img/gub_logo.png").getImage()); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(964, 637);
        this.setVisible(true);
        this.getContentPane().setBackground(new Color(0X141416));
        this.setLayout(null);
        setLocationRelativeTo(null);

        // contacts panel
        JPanel contactsPanel = new JPanel();
        contactsPanel.setBounds(0, 0, 320, 637);
        contactsPanel.setBackground(new Color(0X2A2C33));
        // add contacts panel to main window
        this.add(contactsPanel);

        // devider panel
        JPanel deviderPanel = new JPanel();
        deviderPanel.setBounds(320, 0, 2, 637);
        deviderPanel.setBackground(new Color(0X37393C));
        // add devider panel to main window
        this.add(deviderPanel);

        // profile picture panel
        JPanel profilePicturePanel = new JPanel();
        profilePicturePanel.setBounds(345, 6, 50, 50);
        profilePicturePanel.setBackground(Color.WHITE);
        // add profile picture panel to main window
        this.add(profilePicturePanel);

        // Name Panel
        JPanel namePanel = new JPanel();
        namePanel.setBounds(320, 0, 644, 64);
        namePanel.setBackground(new Color(0X2A2C33));
        // add name panel to main window
        this.add(namePanel);


        // chat panel
        JPanel chatPanel = new JPanel();
        chatPanel.setBounds(320, 0, 644, 637);
        chatPanel.setBackground(new Color(0X141416));
        // add chat panel to main window
        this.add(chatPanel);




        class RoundedPanel extends JPanel {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
    
                // Enable anti-aliasing for smoother edges
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
                // Create a circular shape to draw the panel
                Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, getWidth(), getHeight());
    
                // Set the clipping shape
                g2.setClip(circle);
    
                // Fill the background with the component's background color
                g2.setColor(getBackground());
                g2.fill(circle);
    
                g2.dispose();
            }
        }








    }





}
