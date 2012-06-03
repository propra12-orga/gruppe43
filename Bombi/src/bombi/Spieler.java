package bombi;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Spieler{
	
	Bomben b;
	Spieler s;
	Texture t;
	BombermanLevel l;
	Graphics g;
    private int health = 1; // Zustand f�r abfrage ob Spieler verloren(=0)hat
    private int posX, posY, width, height;
	
	
    
    /**
     * 
     * @param l erzeugte BombermanLevel mit seinen x,y werten
     */
    public Spieler(BombermanLevel l) {
    	this.l=l;
        posX = posY = 0;
        width = height = 40;
    }
    /**
     * 
     * @param posX
     * @param posY
     * Spieler Konstructor
     */
    public Spieler(int posX,int posY){
    	this.posX = posX;
    	this.posY = posY;
    }
	/**
	 * 
	 * @return
	 * Getter f�r x-Position
	 */
    public int getPosX() {
		return posX;
	}
	/**
	 *     
	 * @return
	 * Getter f�r y-Position
	 */
	public int getPosY() {
		return posY;
	}
	public int getHealth(){
		return health;
	}
	/**
	 * Setter x-Position
	 * @param posX
	 */
	public void setPosX(int posX) {
		this.posX = posX;
	}
	/**
	 * Setter y-Position
	 * @param posY
	 */
	public void setPosY(int posY) {
		this.posY = posY;
	}
	/**
	 * Konstructor um Bomben-Daten f�r Kollision zu bekommen 
	 * @param b
	 */
	public Spieler(BombermansBomben b) {
    	//this.b=b;
    	posX=posY=0;
    	
    }
	/**
	 * Methode wenn den Spieler die Bombe trift, Variable health auf 0 setzt
	 */
    public void erwischt(){
    	if( l.hasFireByPixel(s.getPosX(), s.getPosY()))
		health = 0;
    }
   
	
    /**
     * 
     * @param g Grafik erzeugen durch laden aus Texture Klasse
     */
    public void draw(Graphics2D g) {
		if(health==1){Texture.SPIELER1.draw(posX, posY, width, height, g);
			
		}
        
    }
    
    /**
     * 
     * @return
     * Die Methode spielEnde() ueberprueft die Position des Spielers mit
     * getTileByPixel und EXIT auf Gleichheit
     */
    public boolean spielEnde(){
    	return ((l.getTileByPixel(posX, posY)==BombermanLevel.EXIT));
    	
    }
  
    /**
     * Diese Methode bekommt bei Tastendruck dazugeh�rige x und y Werte und aktualisiert
     * die momentane Position.Beispiel: Pfeiltaste Links wird bet�tigt die Werte die �bergeben werden
     * lauten -40 f�r x-Koordinate ,0 f�r y-Koordinate
     * ,dann wird posX = posX + (-40) um 40 Pixel kleiner und die neue Position wird gesetzt
     * move() siehe Methode
     * Die if Abfrage pr�ft ob Spieler vor einem Hinderniss steht.Wenn ja soll er stehen bleiben    
     * @param xdir
     * @param ydir
     */
  
    public void Direction(int xdir,int ydir) {
    
    	if((l.getTileByPixel(getPosX()+xdir,getPosY()+ydir)==2)||(l.getTileByPixel(getPosX()+xdir,getPosY()+ydir)==1))
    	return;
    	posX+=xdir;
		posY+=ydir;
	
    
    }
	
    
    
	
}

