package bombi;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JComponent;


public class BombiGui extends JComponent{

	BombermanLevel bLevel;
	private static final int WIDTH = 840;
	private static final int HEIGHT = 600;
	
	public BombiGui(){
	        super();
	        this.setSize(WIDTH,HEIGHT);
		bLevel = new BombermanLevel(WIDTH,HEIGHT);}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponents(g);
		bLevel.draw(g);}
	
	/**
	 * Methode, welche die bevorzugte Größe dieser JComponent
	 * zurück gibt.
	 * @return: Dimension WIDTH x HEIGHT, welche fest codiert sind.
	 */
	@Override
	public Dimension getPreferredSize(){
	    return new Dimension(WIDTH,HEIGHT);}
	
	/**
	 * Startet das Programm ;-)
	 * @param args: Bisher werden alle Parameter ignoriert.
	 */
	public static void main(String[] args){
		//Fenster erstellen
		JFrame frame = new JFrame();
		frame.setTitle("Bombi");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//BorderLayout hilft beim automatischen Anpassen der Größe
		frame.setLayout(new BorderLayout());		
		frame.add(new BombiGui(),BorderLayout.CENTER);
		frame.pack(); //passt die Größe dem Inhalt an
		
		//zentriert das Fenster
		frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);}

}
