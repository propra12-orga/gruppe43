package bombi;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class Schwierigkeitsgrad extends JFrame {
	
	public static void main(String[] args){
		new Schwierigkeitsgrad();
	}
	
	JRadioButton b1 = new JRadioButton("Leicht");
	JRadioButton b2 = new JRadioButton("Mittel");
	JRadioButton b3 = new JRadioButton("Schwer");
	ButtonGroup bg = new ButtonGroup();
	
	FlowLayout layout = new FlowLayout();
	
	public Schwierigkeitsgrad(){
		super("Bomberman");
		setLayout(layout);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setContentPane(new BackGroundPane("img/Bomberman.png"));
		
		setLocationRelativeTo(null);
		setSize(900,700);
		setVisible(true);
		
		bg.add(b1);
		bg.add(b2);
		bg.add(b3);
		b1.addItemListener(new itemHandler());
		b2.addItemListener(new itemHandler());
		b3.addItemListener(new itemHandler());
		add(b1);
		add(b2);
		add(b3);
		setVisible(true);
	}
	private class itemHandler implements ItemListener{

		public void itemStateChanged(ItemEvent e) {
			//Hier kommen die Funktionen
			
		}
		
	}
    class BackGroundPane extends JPanel{
    	Image img = null;
    	
		
		BackGroundPane(String imagefile) {
    		if (imagefile != null) {
    			MediaTracker mt = new MediaTracker(this);
    			img = Toolkit.getDefaultToolkit().getImage(imagefile);
    			mt.addImage(img, 0);
    			try{
    				mt.waitForAll();
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	protected void paintComponent(Graphics g){
    		super.paintComponent(g);
    		g.drawImage(img,0,0,this.getWidth(),this.getHeight(),this);
    	}
        
    
    }
}
