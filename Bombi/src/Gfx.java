import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
 
import javax.imageio.ImageIO;
import javax.swing.*;

public class Gfx extends JPanel {
	
	static BufferedImage img;
	static {
		try {
			img = ImageIO.read(Gfx.class.getClassLoader().getResourceAsStream("Bilder/auto.gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.gray);
		g.fillRect(50, 50, 20, 20);
		g.setColor(Color.BLACK);
		g.fillRoundRect(100, 50, 15, 15, 15, 15); // drawRoundRect(70, 70, 10, 10, 5, 5);
		
		g.drawImage(img, 150, 350, null);
	}
	
	 public static void main(String[] args){
		 SwingUtilities.invokeLater(new Runnable() {
			 @Override
			 public void run() {
				 JFrame f = new JFrame();
				 f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				 
				 Gfx g = new Gfx();
				 g.setPreferredSize(new Dimension(500,500));
				 
				 f.add(g);
			f.pack();
			f.setLocation(250, 250);
			f.setVisible(true);
			 }
		 });
	 }
}
