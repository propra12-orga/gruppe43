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

public class Multiauswahl extends JFrame {
	
	public static void main(String[] args){
		new Multiauswahl();
	}

	FlowLayout layout = new FlowLayout();
	
	public Multiauswahl(){
		super("Bomberman");
		setLayout(layout);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setContentPane(new BackGroundPane("img/Bomberman.gif"));

		setVisible(true);
		setSize(600,500);
		setLocationRelativeTo(null);
		setLayout(null);
		

		
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
	    
	    add(button1);
	    add(button2);
	    add(button3);
	    add(button4);
	    setVisible(true);
	    
        Insets insets = getInsets();
        Dimension size = button1.getPreferredSize();
        button1.setBounds(250+ insets.left, 220 + insets.top,
        		size.width+30,size.height+5);
        button2.setBounds(250+ insets.left, 255 + insets.top,
        		size.width+30,size.height+5);
        button3.setBounds(250+ insets.left, 290 + insets.top,
        		size.width+30,size.height+5);
		button4.setBounds(250+ insets.left, 325 + insets.top,
				size.width+30,size.height+5);
        
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
