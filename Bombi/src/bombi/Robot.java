package bombi;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.List;


/**
 * Diese Klasse erstellt den Computer als Gegner, wenn er denn ausgew�hlt wurde,dar.
 * Funktioniert wie die Spieler-Klasse mit Ausnahme der  RobotDirection Methode die 
 * zuf�llig die Bewegung simulieren soll.
 * Es kommt noch eine Methode dazu die die Bomben nach einem Schema ablegen soll.
 * @author abuubaida
 */
public class Robot{
	/**
	 * TODO 
	 * �ber Menu eine Abfrage implementieren (ob Computergegner ausgew�hlt) die in der BombiGui-Klasse
	 * abgefragt wird.Diese erzeugt dann den Robot oder auch nicht:).
	 * 
	 */
	Bomben b;
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
        posX = 520;
        posY = 360;
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
	public Robot(Bomben b) {
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
 * TODO
 * 
 * Diese Methode bewegt den Robot zuf�llig, ist aber noch abh�ngig vom Spieler ebenso die Bombenablage
 * 
 */
    public void robotDirection() {
     	int r = (int)( Math.random()*4); //Zuf�llig eine Zahl 0,1,2 oder 3  
     	
      	int wert1 = 40;  	// f�r Bewegung nach +
     	int wert2 = -40;	// f�r Bewegung nach -
     	int wert3 = 0;		// f�r Bewegung 0
     	
     	if(r==3){
     		if((l.getTileByPixel(getPosX()+wert1,getPosY()+wert3)==2)||(l.getTileByPixel(getPosX()+wert1,getPosY()+wert3)==1)){
     	    	return;}else
     		posX+=wert1;posY+=wert3;return;}//rechts
     	
     	if(r==0){
     		if((l.getTileByPixel(getPosX()+wert2,getPosY()+wert3)==2)||(l.getTileByPixel(getPosX()+wert2,getPosY()+wert3)==1)){
     			return;}else
     		posX+=wert2;posY+=wert3;return;}//links
     
     	if(r==1){
     		if((l.getTileByPixel(getPosX()+wert3,getPosY()+wert2)==2)||(l.getTileByPixel(getPosX()+wert3,getPosY()+wert2)==1)){
     			return;}else
     		posX+=wert3;posY+=wert2;return;}//oben
     	
     	if(r==2){
     		if((l.getTileByPixel(getPosX()+wert3,getPosY()+wert1)==2)||(l.getTileByPixel(getPosX()+wert3,getPosY()+wert1)==1)){
     			return;}else
     		posX+=wert3;posY+=wert1;return;}//unten

    }	
}
 	// Ende der robot Klasse