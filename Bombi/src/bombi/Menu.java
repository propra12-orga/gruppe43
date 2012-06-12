package bombi;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Menu extends JFrame{
    
    public static void main(String[] args){
        new Menu();
    }
 
/*
 * Der Konstruktor.
 */
    public Menu(){
        super("Bomberman");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(createMenuPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setSize(250,250);
    }
/*
 * Hier wird das Menü erstellt.
 * Das Menü formt sich wie ein Gitter durch GridLayout
 */ 
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(4 , 1));
     
        
 /*
  * Ab hier fangen die Buttons an.
  * Die Buttons werden ausgeführt von der funktion ActionListener.
  */
        JButton start = new JButton("Einzelspieler");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    JFrame frame = new JFrame();
                    
                    frame.setTitle("Bombi");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    
                    BombiGui bGui = new BombiGui();
                    
                    frame.add(bGui);
                    frame.pack();
                    // passt die Gr��e dem Inhalt an

                    // zentriert das Fenster
                    frame.setLocationRelativeTo(null);

                    frame.setVisible(true);
                    
                    new Thread(bGui).start();
                    
                    setVisible(false);
            }
        });
        
        JButton button1 = new JButton("Mehrspieler");
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        
        JButton button2 = new JButton("Einstellungen");
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        
        JButton close = new JButton("Schließen");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
 /*
  * Hier werden die Buttons auf dem fenster eingefügt.
  */
        panel.add(start);
        panel.add(button1);
        panel.add(button2);
        panel.add(close);
        return panel;
    }
}