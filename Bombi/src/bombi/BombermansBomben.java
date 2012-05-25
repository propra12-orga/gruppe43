package bombi;

import java.awt.Color;
import java.awt.Graphics;


/**
 * Diese Klasse erzeugt Objekte, welche als Bomben interpretiert werden.
 **/

public class BombermansBomben {

	// Ein paar Konstanten für die verschiedene Arten von Bomben
    private static final short EXPLODEDBOMB          = 0;
    private static final short BOMB                  = 1;
    private int zustand=BOMB;
    private int posX;
	private int posY;
    
    
    
    public BombermansBomben(int x, int y) {
        posX = x ;
        posY = y;
	}



public void draw(Graphics g) {
	if(zustand==BOMB){
	g.setColor(Color.BLACK);
    g.fillOval(posX, posY, 30, 30);
	}
}
}
