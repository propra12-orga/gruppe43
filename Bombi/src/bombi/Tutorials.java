package bombi;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Tutorials extends JFrame {
	public static void main(String[] args){
	new Tutorials();	
	}
	
	FlowLayout layout = new FlowLayout();
	
	/**
	 * Hier haben wir den Konstruktor
	 * Hier wird das Fenster erstellt,
	 * die button hinzugefügt und mittels
	 * ActionEvent wird eine Funktion
	 * eingegeben.
	 */
	public Tutorials(){
		super("Bomberman");
		setLayout(layout);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setContentPane(new BackGroundPane("img/Bomberman.gif"));
		
		setSize(600,500);
		setVisible(true);
		//das fenster wird zentriert
		setLocationRelativeTo(null);

		//Indem man setLayout(null); eingibt kann
		//man den button eine bestimmte position
		//das mit der funktion insets später
		//ausgeführt wird
		setLayout(null);
		
		//Ab hier fangen die Buttons an.
		//Die Buttons werden ausgeführt von der Funktion
		//ActionEvent.
		//ActionListener implementiert das Interface und
		//bekommt die Events(AtionEvent) übergeben.
		JButton button1 = new JButton("Tutorial1");
	    button1.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	JFrame frame = new JFrame();
                frame.setTitle("Bombi");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                BombiGui bGui = new BombiGui(false,1,frame);               
                frame.add(bGui);
                frame.pack();
                // passt die Gr��e dem Inhalt an

                // zentriert das Fenster
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                new Thread(bGui).start();         
                dispose();
	            }
	        });
	    
		JButton button2 = new JButton("Tutorial2");
	    button2.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	JFrame frame = new JFrame();
                frame.setTitle("Bombi");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                BombiGui bGui = new BombiGui(false,2,frame);
                frame.add(bGui);
                frame.pack();
                // passt die Gr��e dem Inhalt an

                // zentriert das Fenster
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                new Thread(bGui).start();
                dispose();
	            }
	        });
	    
		JButton button3 = new JButton("Tutorial3");
	    button3.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	JFrame frame = new JFrame();
                frame.setTitle("Bombi");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                BombiGui bGui = new BombiGui(false,3,frame);
                frame.add(bGui);
                frame.pack();
                // passt die Gr��e dem Inhalt an

                // zentriert das Fenster
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                new Thread(bGui).start();
                dispose();
	            }
	        });
	    

	    
		JButton button4 = new JButton("Tutorial4");
	    button4.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	JFrame frame = new JFrame();
                frame.setTitle("Bombi");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                BombiGui bGui = new BombiGui(false,5,frame);
                frame.add(bGui);
                frame.pack();
                // passt die Gr��e dem Inhalt an

                // zentriert das Fenster
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                new Thread(bGui).start();
                dispose();
	            }
	        });
	    
		JButton button5 = new JButton("Tutorial5");
	    button5.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	JFrame frame = new JFrame();
                frame.setTitle("Bombi");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                BombiGui bGui = new BombiGui(false,6,frame);
                frame.add(bGui);
                frame.pack();
                // passt die Gr��e dem Inhalt an

                // zentriert das Fenster
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                new Thread(bGui).start();
                dispose();
	            }
	        });
	    
		JButton button6 = new JButton("Zurück");
	    button6.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
            	JFrame menu= new JFrame();
            	menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            	new Menu();
                dispose();
	            }
	        });
        
	    //Hier werden die Buttons auf dem Fenster eingefügt.   
	    add(button1);
	    add(button2);
	    add(button3);
	    add(button4);
	    add(button5);
	    add(button6);
	    setVisible(true);
	    
        //Mit insets kann man den buttons eine bestimmte
        //position geben.
        //In zusammenhang mit Dimension size kan man die
        //breite und höhe der buttons ändern.
        Insets insets = getInsets();
        Dimension size = button1.getPreferredSize();
        button1.setBounds(165+ insets.left, 200 + insets.top,
        		size.width+30,size.height+5);
        button2.setBounds(305+ insets.left, 200 + insets.top,
        		size.width+30,size.height+5);
        button3.setBounds(165+ insets.left, 240 + insets.top,
        		size.width+30,size.height+5);
        button4.setBounds(305+ insets.left, 240 + insets.top,
        		size.width+30,size.height+5);
        button5.setBounds(235+ insets.left, 280 + insets.top,
        		size.width+30,size.height+5);
        button6.setBounds(235+ insets.left, 320 + insets.top,
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

