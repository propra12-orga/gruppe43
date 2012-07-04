package bombi;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Steuerung extends JFrame {
	
	public static void main(String[] args){
		new Steuerung();
	}

	FlowLayout layout = new FlowLayout();
	
	/**
	 * Hier haben wir den Konstruktor
	 * 
	 */
	
	public Steuerung(){
		super("Bomberman");
		setLayout(layout);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setContentPane(new BackGroundPane("img/Bombermannnnn.jpg"));
		
		setVisible(true);
		setSize(600,500);
		//das fenster wird zentriert
		setLocationRelativeTo(null);
		
		/**
         * Indem man setLayout(null); eingibt kann 
         * man den button eine bestimmte position
         * das mit der funktion insets später
         * ausgeführt wird
         */
		setLayout(null);
		
        /**
         * Ab hier fangen die Buttons an.
         * Die Buttons werden ausgeführt von der funktion
         * ActionEvent.
         * ActionListener implementiert das Interface und
         * bekommt die Events(ActionEvent) übergeben.
         */
		
        JButton button2 = new JButton("Zurück");
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame f = new JFrame();
                
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setLocationRelativeTo(null);
                
                new Menu();
                dispose();
            	
            }
        });
        
        /*
         * Hier werden die Buttons auf dem fenster eingefügt.
         */       
        
        add(button2);
        setVisible(true);
        
        /**
         * Mit insets kann man den buttons eine bestimmte
         * position geben.
         * In zusammenhang mit Dimension size kann man die
         * breite und höhe der buttons ändern.
         */
        
        Insets insets = getInsets();
        Dimension size = button2.getPreferredSize();
        button2.setBounds(245+ insets.left, 360 + insets.top,
        		size.width+30,size.height+5);

	}
	
    class BackGroundPane extends JPanel{
    	Image img = null;
    	
		
		BackGroundPane(String imagefile) {
    		if (imagefile != null) {
    			MediaTracker mt = new MediaTracker(this);
    			//Liefert das aktuelle Toolkit zurück
    			img = Toolkit.getDefaultToolkit().getImage(imagefile);
    			mt.addImage(img, 0);
    			try{
    				mt.waitForAll();
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    	}
		
		/**
		 * Mit protected kann keine andere Klasse in der Klasse
		 * zugreifen,doch die Klasse selber kann mit einer
		 * Unterklasse auf andere Klassen zugreifen.
		 */
    	protected void paintComponent(Graphics g){
    		super.paintComponent(g);
    		g.drawImage(img,0,0,this.getWidth(),this.getHeight(),this);
    	}
        
    
    }
}
