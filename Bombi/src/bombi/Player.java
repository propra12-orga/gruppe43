package bombi;

import java.awt.Graphics;
import java.awt.Graphics2D;
/**
 * Die Klasse erzeugt Spieler Object
 * Grafiken werden geladen.
 * Spieler werden bewegt und mit Abfragen ob Exit erreicht oder eine Bombe den Spieler
 * erwischt hat versehen
 * @author abuubaida
 *
 */
public class Player{
	Player p;
	Bomben b;
	Texture t = new Texture(192, 0, 32, 48);
	BombermanLevel l;
	Graphics g;
    private int health = 1; // Zustand fuer abfrage ob Spieler verloren(=0)hat
    private int posX, posY, width, height;
    
    /**
     * 
     * @param l erzeugte BombermanLevel mit festen x,y Koordinaten werten
     */
    public Player(BombermanLevel l) {
    	this.l=l;
        posX = posY = 40;
        width = height = 40;
    }
    /**
     * Dieser Konstructor erstell Objekt f�r zweiten Spieler.
     * Position ist variabel w�hlbar
     * @param l
     * @param x
     * @param y
     */
    public Player(BombermanLevel l,int x,int y) {
    	this.l=l;
        posX = x;
        posY = y;
        width = height = 40;
		
    }
    public Player(){
    	
    }
    /**
     * 
     * @param posX
     * @param posY
     * Spieler Konstructor
     */
    public Player(int posX,int posY){
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
	public boolean bombPos() {
    return	l.hasFireByPixel(b.getPosX(), b.getPosY());
    		 	
    }
	/**
	 * Methode wenn den Spieler die Bombe trift, Variable health auf 0 setzt
	 * TODO
	 */
    public boolean dead(){
    	if(l.hasFireByPixel(posX, posY))
		return true;
    	return l.hasFireByPixel(posX, posY);
    	
    }
   
	
    /**
     * 
     * @param g Grafik erzeugen durch laden aus Texture Klasse
     */
    public void draw(Graphics2D g) {
    	int dim = l.getTileDim();
		if(health==1){Texture.SPIELER1.draw(posX, posY-dim/2, width, (height*3)/2, g);
			
		}
        
    }
  
    
    /**
     * Grafik erzeugen um anhand �bergebener Werte zu laden
     * @param g
     */
    public void draw1(Graphics2D g) {
		if(health==1){t.draw(posX, posY, width, height, g);	
		}    
    }
    
    /**
     * 
     * @return
     * Die Methode spielEnde() ueberprueft die Position des Spielers mit
     * getTileByPixel und EXIT auf Gleichheit
     */
    public boolean exit(){
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
    
    	if((l.isSolidByPixel(getPosX()+xdir,getPosY()+ydir)))
    	return;
    	posX+=xdir;
		posY+=ydir;
	
    
    }
	
    
    
	
}

