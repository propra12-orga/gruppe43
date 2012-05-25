package bombi;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class Spieler {
	
	
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
    
    public Spieler(int posX,int posY){
    	this.posX = posX;
    	this.posY = posY;
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
     * 
     * @param xdir
     * @param ydir
     */
    public void Direction(int xdir,int ydir) {
    	 
           posX += xdir;
           posY += ydir; move();
           if(stopStein()){
        	   posX-=xdir;
        	   posY-=ydir;
        	 
           }
         
    	  	  }
       
    
   
}

