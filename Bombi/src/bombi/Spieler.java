package bombi;

import java.awt.Graphics;
import java.awt.Graphics2D;

public class Spieler {
	
	BombermanLevel l;
	
	Graphics g;
    private int health = 1; // Zustand für abfrage ob Spieler verloren(=0)hat
    private static int x, y, width, height;
    Spieler s;
    Texture t;
    
    public Spieler(BombermanLevel l) {
    	this.l=l;
    	
        x = y = 0;
        width = height = 40;
    }
 
    public void draw(Graphics2D g) {
        if (health == 1) {
            Texture.SPIELER1.draw(x, y, width, height, g);
        }
    }
    
    public void stopStein() {
    	
    
    	
    	if((l.width==0)||(INDESTRUCTIBLE==2)){
    		x=x;
    		y=y;
    	}
    }
   
        
    public void move(){
    	if(x < 0)
            x = 0;
        if(x > 800)
            x = 800;
        if(y < 0)
            y = 0;
        if(y > 560)
            y = 560;
    }
   
    public void Direction(int xdir,int ydir){
        
    	x += xdir;
        y += ydir;
        move();
        
    } 
   
        

       
        
        public void verloren(){
        	if(health == 0){
        		x=0;
        		y=0;
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
  
 
  
    public void run(){
        try{
            while(true){
                move();
                Thread.sleep(60);
            }
        }catch(Exception e){System.err.println(e.getMessage());}
    }
    
}

