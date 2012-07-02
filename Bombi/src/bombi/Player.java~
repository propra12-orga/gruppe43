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
	Player p,p2;
	Bomben b;
	Texture t = new Texture(192, 0, 32, 48);
	BombermanLevel l;
	Graphics g;
	BombiGui bombi;
    private int health = 1; // Zustand fuer abfrage ob Spieler verloren(=0)hat
    private int i =40;		// Variable um posX,posY,width und height flexibel zuinitialisieren
    private int  posX, posY, width, height;
    private int maxradius=2;
    private int maxbomb=1;
    private int currentbombs=1;
    private int stepsize = 10;
    
     
    /**
     * 
     * @param l erzeugte BombermanLevel mit festen x,y Koordinaten werten
     */
    public Player(BombermanLevel l) {
    	this.l=l;
        posX = l.getTileDim();
        posY = l.getTileDim();
        width = l.getWidth();
        height = l.getHeight();
    }
    /**
     * Dieser Konstructor erstell Objekt fuer zweiten Spieler.
     * Position ist variabel waehlbar
     * @param l
     * @param x
     * @param y
     */
    public Player(BombermanLevel l,int x,int y) {
    	this.l=l;
        posX = x;
        posY = y;
        width  = l.getTileDim(); 
        height = l.getTileDim();
		
    }
  
    /**
     * Die Methode gibt den aktuellen Radius den Explosion zurueck.
     * @return
     */
    public int maxradius() {
    	return maxradius;
    }
    /**
     * Diese Methode erhoeht den aktuellen Radius permanent um 1.
     */
    public void addradius() {
    	maxradius++;
    }
    /**
     * Diese Methode gibt zurueck, wie hoch die Anzahl der maximal legbaren Bomben ist
     * @return
     */
    public int maxbomb() {
    	return maxbomb;
    }
    /** 
     * Diese Methode erhoeht die aktuelle Anzahl an maximal legbaren Bomben permanent um 1.
     */
    public void addmaxbomb() {
    	maxbomb++;
    }
    /**
     * Diese Methode erhoeht die Variable currentbombs um 1. In currentbombs wird gezaehlt, wieviele Bomben auf dem Spielfeld liegen.
     */
    public void addcurrentbombs() {
    	currentbombs++;
    }
    /**
     * Diese Methode verringert die Variable currentbombs um 1.
     */
    public void removecurrentbombs() {
    	currentbombs--;
    }
    public boolean bombplantable() {
    	if (currentbombs<=maxbomb) {
    		return true;}
    	else return false;
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
	 * Getter fuer x-Position
	 */
    public int getPosX() {
		return (posX);
	}
    
    public int getPosXForBomb() {
		return posX+l.getTileDim()/2;
	}
	/**
	 *     
	 * @return
	 * Getter fuer y-Position
	 */
	public int getPosY() {
		return (posY);
	}
	
	public int getPosYForBomb() {
		return posY+l.getTileDim()/2;
	}
	/**
	 * Konstructor um Bomben-Daten fuer Kollision zu bekommen 
	 * @param b
	 */
	public boolean bombPos() {
    return	l.hasFireByPixel(b.getPosX(), b.getPosY());
    		 	
    }
	/**
	 * Methode wenn den Spieler die Bombe trift, Variable health auf 0 setzt
	 * TODO
	 */
    public boolean death(){
    	return l.hasFireByPixel(posX+l.getTileDim()/2, posY+l.getTileDim()/2);
    	
    }
   
    public boolean bombItem(){
    	return l.getTileByPixel(posX+l.getTileDim()/2, posY+l.getTileDim()/2)==5;
    }
	
    public boolean explosionItem() {
    	return l.getTileByPixel(posX+l.getTileDim()/2, posY+l.getTileDim()/2)==6;
    }
    /**
     * 
     * @param g Grafik erzeugen durch laden aus Texture Klasse
     */
    public void draw(Graphics2D g) {
    	int dim = l.getTileDim(); 
		if(health==1){Texture.PLAYER_IDLE_FRONT.draw(posX, posY-dim/2, width, (height*3)/2, g);
			
		}
        
    }
  
    public boolean playerHit(){
    	return ((bombi.player1.getPosX()==bombi.player2.getPosX()) && (bombi.player1.getPosY()==bombi.player2.getPosY()));
    }
    
    /**
     * Grafik erzeugen um anhand uebergebener Werte zu laden
     * @param g
     */
    public void draw1(Graphics2D g) {
		if(health==1){t.draw(posX+11, posY, width/2, height, g);	
		}    
    }
    
    /**
     * 
     * @return
     * Die Methode spielEnde() ueberprueft die Position des Spielers mit
     * getTileByPixel und EXIT auf Gleichheit
     */
    public boolean exit(){
    	return ((l.getTileByPixel(posX+l.getTileDim()/2, posY+l.getTileDim()/2)==BombermanLevel.EXIT));
    	
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
    	
        int dim = l.getTileDim();
        if(l.isSolidByPixel(posX+((dim*stepsize )/100)+xdir,posY+((dim*stepsize)/100)+ydir))
        return;
        if(l.isSolidByPixel(posX+  dim+((xdir*width)/32)   ,posY+  dim+((ydir*height)/32)))
        return;
        if(l.isSolidByPixel(posX+((dim*stepsize )/100)+xdir,posY+  dim+((ydir*height)/32)))
        return;
        if(l.isSolidByPixel(posX+  dim+((xdir*width)/32)   ,posY+((dim*stepsize)/100)+ydir))
        return;
        posX+=xdir;
        posY+=ydir;

       
       }
	
    public void iconPosition(){
    	if(l.getTileByPixel(posX, posY)==BombermanLevel.BOMBPLUS)
    	playAudio.playSound("Pickup");
    }
    // SoundManager instanz (Audios einlesen)
    SoundManager playAudio = new SoundManager() {
        public void initSounds() {
            sounds.add(new Sound("Pickup", Sound.getURL("/Pickup.wav")));
          
        }
    };

    
	
}

