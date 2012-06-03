package bombi;
/**
 * Diese Klasse erstellt den Computer als Gegner, wenn er denn ausgew�hlt wurde,dar.
 * Funktioniert wie die Spieler-Klasse mit Ausnahme der  RobotDirection Methode die 
 * zuf�llig die Bewegung simulieren soll.
 * TODO
 * Es kommt noch eine Methode dazu die die Bomben nach einem Schema ablegen soll.
 * @author Hassan
 */
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Robot{
	/**
	 * TODO 
	 * �ber Menu eine Abfrage implementieren (ob Computergegner ausgew�hlt) die in der BombiGui-Klasse
	 * abgefragt wird.Diese erzeugt dann den Robot oder auch nicht:).
	 * 
	 */
	BombermansBomben b;
	Robot r;
	Texture t;
	BombermanLevel l;
	Graphics g;
    private int health = 1; // Zustand f�r abfrage ob Spieler verloren(=0)hat
    private int posX, posY, width, height;
	
	
    
    /**
     * 
     * @param l erzeugte BombermanLevel mit seinen x,y werten
     */
    public Robot(BombermanLevel l) {
    	this.l=l;
        posX = 560;
        posY = 400;
        width = height = 40;
    }
    /**
     * 
     * @param posX
     * @param posY
     * Spieler Konstructor Positions-Koordinaten f�r den Spieler
     */
    public Robot(int posX,int posY){
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
	 * Konstructor um Bomben-Koordinaten f�r Kollision zu bekommen 
	 * @param b
	 */
	public Robot(BombermansBomben b) {
    	this.b=b;
    	posX=posY=0;
    	
    }
	/**
	 * Methode wenn den Spieler die Bombe trift, Variable health auf 0 setzt
	 * 
	 */
    public void erwischt(){
    	if( (b.getPosX()==r.getPosX()) && (b.getPosY()==r.getPosY()) )
    		health=0;
    }
	
    /**
     * 
     * @param g Grafik erzeugen durch laden aus Texture Klasse
     */
    public void draw(Graphics2D g) {
		if(health==1){Texture.ROBOT.draw(posX, posY, width, height, g);
			
		}
        
    }
 
    /**
     * 
     * @return
     * Die Methode spielEnde() �berpr�ft die Position des Spielers mit
     * getTileByPixel und EXIT auf Gleichheit
     */
    public boolean spielEnde(){
    	return((l.getTileByPixel(posX, posY)==BombermanLevel.EXIT));
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
     * 
     */
    
    public void robotDirection(int xdir,int ydir) {
        
    	if((l.getTileByPixel(getPosX()+xdir,getPosY()+ydir)==2)||(l.getTileByPixel(getPosX()+xdir,getPosY()+ydir)==1))
    	return;
    	posX+=xdir;
		posY+=ydir;
	
    
    }
    	
    }
/**
 *TODO hier muss noch ein zuf�llige Bewegung von Robot implemeniert werden
 */
    
 /*   public void RobotDirection(int xdir,int ydir) {
    	
    	
    	xdir =(int) ((Math.random() * 40)*(Math.random() * -1 ));
    		if((xdir==40)||(xdir==-40)){ ydir = 0;posX+=xdir;posY+=ydir;}
    		else{	
    			ydir= (int) ((Math.random() * 40)*(Math.random() * -1 ));
    			posX+=xdir;posY+=ydir;
    		}move();	
    		if(!stopStein()){
    	}posX-=xdir;
    	posY-=ydir;
    	}
    	
}*/
  