package bombi;

import java.awt.Graphics;
import java.awt.Graphics2D;

public class Spieler {
	
	BombermansBomben b;
	Spieler s;
	Texture t;
	BombermanLevel l;
	Graphics g;
    private int health = 1; // Zustand für abfrage ob Spieler verloren(=0)hat
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
	 * Getter für x-Position
	 */
    public int getPosX() {
		return posX;
	}
	/**
	 *     
	 * @return
	 * Getter für y-Position
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
	 * Konstructor um Bomben-Daten für Kollision zu bekommen 
	 * @param b
	 */
	public Spieler(BombermansBomben b) {
    	this.b=b;
    	posX=posY=0;
    	
    }
	/**
	 * Methode wenn den Spieler die Bombe trift, Variable health auf 0 setzt
	 */
    public void erwischt(){
    	if( (b.getPosX()==s.getPosX()) && (b.getPosY()==s.getPosY()) )
    		health=0;
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
     * @return Die Methode stopStein() ueberprueft die Position des Spielers mit
     * getTileByPixel und STONE,INDESTRUCTIBLE auf Gleichheit.
     * Rueckgabe true wenn gleich ansonsten false
     * Mit der Methode soll im Spiel der Spieler bei einem Stein nicht durchlaufen koennen.
     *  
     */
    public boolean stopStein() {
    	return((l.getTileByPixel(posX, posY)==BombermanLevel.STONE)||(l.getTileByPixel(posX, posY)==BombermanLevel.INDESTRUCTIBLE));
    }
    
    /**
     * 
     * @return
     * Die Methode spielEnde() ueberprueft die Position des Spielers mit
     * getTileByPixel und EXIT auf Gleichheit
     */
    public boolean spielEnde(){
    	return((l.getTileByPixel(posX, posY)==BombermanLevel.EXIT));
    }
    
	/**   
	 * Diese Methode überprüft ob x und y am Rand sind und setzt sie bei Überschreitung zurück 
	 */
    public void move(){
    	if(posX < 0)
            posX = 0;
        if(posX > 800)
            posX = 800;
        if(posY < 0)
            posY = 0;
        if(posY > 560)
            posY = 560;
    }
	
    /**
     * Diese Methode bekommt bei Tastendruck dazugehörige x und y Werte und aktualisiert
     * die Momentane Position.Beispiel: Pfeiltaste Links wird betätigt die Werte die übergeben werden
     * lauten -40 für x-Koordinate ,0 für y-Koordinate
     * ,dann wird posX = posX + (-40) um 40 Pixel kleiner und die neue Position wird gesetzt
     * move() siehe Methode
     * Die if Abfrage prüft ob Spieler vor einem Hinderniss steht.Wenn ja soll er stehen bleiben    
     * @param xdir
     * @param ydir
     */
    public void Direction(int xdir,int ydir) {
		
		posX += xdir;
		posY += ydir; move();
		
		
	}
	
    
	
}

