package bombi;

import java.awt.Graphics;
import java.awt.Graphics2D;

public class Spieler{
	
	
	
	BombermanLevel l;
	Graphics g;
    private int health = 1; // Zustand f�r abfrage ob Spieler verloren(=0)hat
    private int posX, posY, width, height;
    Spieler s;
    Texture t;
  
    public Spieler(BombermanLevel l) {
    	this.l=l;
    	
        posX = posY = 0;
        width = height = 40;
    }
 
    public void draw(Graphics2D g) {
        if (health == 1) {
            Texture.SPIELER1.draw(posX, posY, width, height, g);
            
        }
    }
    
    public boolean stopStein() {
    	return((l.getTileByPixel(posX, posY)==BombermanLevel.STONE)||(l.getTileByPixel(posX, posY)==BombermanLevel.INDESTRUCTIBLE));
    }
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
   
    public void Direction(int xdir,int ydir){
        
    	posX += xdir; 
        posY += ydir;
        move();
        if (stopStein()) {
       posX -= xdir;
        posY -= ydir;}
    } 
   
    
	
	

       
        
        public void verloren(){
        	if(health == 0){
        		posX=0;
        		posY=0;
        		
        	}
        	
        	
        }
        /*
        public void keyReleased(KeyEvent e){
            int keyCode = e.getKeyCode();
            if(keyCode == e.VK_LEFT){
                setXDirection(-32,0);
            }
            if(keyCode == e.VK_RIGHT){
                setXDirection(+32,0);
            }
            if(keyCode == e.VK_UP){
                setXDirection(0,-32);
            }
            if(keyCode == e.VK_DOWN){
                setXDirection(0,+32);
            }
           
        }*/
  
 
  
    
       
              

    
    
}

