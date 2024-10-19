import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
public class chatWindow extends JFrame {
    public chatWindow() {
        this.setTitle("GUB Chat");
        this.setIconImage(new ImageIcon("src/img/gub_logo.png").getImage()); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1177, 960);
        this.setVisible(true);
        this.getContentPane().setBackground(new Color(0X1C1D22));
    }





}
