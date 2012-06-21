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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Multiauswahl extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args){
		new Multiauswahl();
	}

	FlowLayout layout = new FlowLayout();
	
	/**
	 * Hier haben wir den Konstruktor
	 * Hier wird das Fenster erstellt,
	 * die button hinzugefügt und mittels
	 * ActionEvent wird eine Funktion
	 * eingegeben.
	 */
	public Multiauswahl(){
		super("Bomberman");
		setLayout(layout);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setContentPane(new BackGroundPane("img/Bomberman.gif"));
		
		setVisible(true);
		setSize(600,500);
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
		JButton button1 = new JButton("Lokal");
	    button1.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	JFrame frame = new JFrame();
                
                frame.setTitle("Bombi");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                BombiGui bGui = new BombiGui(true,0,frame);
                
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
	    
		JButton button2 = new JButton("Host");
	    button2.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	                
	            }
	        });
	    
		JButton button3 = new JButton("Client");
	    button3.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	                
	            }
	        });
	    
        JButton button4 = new JButton("Zurück");
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame f = new JFrame();
                
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setLocationRelativeTo(null);
                
                new Menu();
                dispose();
            	
            }
        });
	    
        //Hier werden die Buttons auf dem Fenster eingefügt.
	    add(button1);
	    add(button2);
	    add(button3);
	    add(button4);
	    setVisible(true);
	    
        //Mit insets kann man den buttons eine bestimmte
        //position geben.
        //In zusammenhang mit Dimension size kan man die
        //breite und höhe der buttons ändern.
        Insets insets = getInsets();
        Dimension size = button1.getPreferredSize();
        button1.setBounds(250+ insets.left, 230 + insets.top,
        		size.width+30,size.height+5);
        button2.setBounds(250+ insets.left, 265 + insets.top,
        		size.width+30,size.height+5);
        button3.setBounds(250+ insets.left, 300 + insets.top,
        		size.width+30,size.height+5);
		button4.setBounds(250+ insets.left, 335 + insets.top,
				size.width+30,size.height+5);
        
	}
		
	    class BackGroundPane extends JPanel{
	    	/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
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
