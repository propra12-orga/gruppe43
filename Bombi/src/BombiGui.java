import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class BombiGui {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//Fenster erstellen
		JFrame frame = new JFrame();
		frame.setTitle("Bombi");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);
		
	}

}
