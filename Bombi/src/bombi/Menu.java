package bombi;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;
 
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Menu extends JFrame{
    
    public static void main(String[] args){
        new Menu();
    }
 
    FlowLayout layout = new FlowLayout();  
	/**
	 * Hier haben wir den Konstruktor
	 * Hier wird das Fenster erstellt,
	 * die button hinzugefügt und mittels
	 * ActionEvent wird eine Funktion
	 * eingegeben.
	 */
    public Menu(){
        super("Bomberman");
        setLayout(layout);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

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
        JButton start = new JButton("Einzelspieler");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	JFrame einzel= new JFrame();
            	einzel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            	new Einzelspieler();
                dispose();
            }
        });
        
        JButton button1 = new JButton("Mehrspieler");
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame f = new JFrame();
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                new Multiauswahl();
                dispose();
            }
        });
        
        JButton button2 = new JButton("Tutorial");
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	JFrame tut= new JFrame();
            	tut.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            	new Tutorials();
                dispose();
            }
        });
        
        JButton button3 = new JButton("Steuerung");
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	JFrame steue= new JFrame();
            	steue.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            	new Steuerung();
                dispose();
            }
        });

        JButton close = new JButton("Schließen");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        //Hier werden die Buttons auf dem Fenster eingefügt.
        add(start);
        add(button1);
        add(button2);
        add(button3);
        add(close);
        setVisible(true);

        //Mit insets kann man den buttons eine bestimmte
        //position geben.
        //In zusammenhang mit Dimension size kan man die
        //breite und höhe der buttons ändern.
        Insets insets = getInsets();
        Dimension size = start.getPreferredSize();
        start.setBounds(220+ insets.left, 200 + insets.top,
        		size.width+30,size.height+5);
        button1.setBounds(220+ insets.left, 235 + insets.top,
        		size.width+30,size.height+5);
        button2.setBounds(220+ insets.left, 270 + insets.top,
        		size.width+30,size.height+5);
        button3.setBounds(220+ insets.left,305+ insets.top,
        		size.width+30,size.height+5);
        close.setBounds(220+ insets.left, 340+ insets.top,
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