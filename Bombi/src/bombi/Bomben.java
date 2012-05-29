package bombi;

import java.awt.Graphics2D;

/**
 * Diese Klasse erzeugt Objekte, welche als Bomben interpretiert werden.
 **/
public class Bomben {

    // Ein paar Konstanten für die verschiedene Arten von Bomben
    private static final short EXPLODED = 0;
    private static final short BOMB = 1;
    private static final short EXPLODING = 2;
    private static final boolean koli= false;
    // Countdowns & Timer
    private static final short COUNTDOWN = 120;
    private static final short ANIMCOUNTER = 10;
    private static final short RADIUSDELAY = 0;
    
    
    // Verschiedene Variablen
    private int state = BOMB;
    private int posX, posY;
    private int radius;
    private int radiusCounter = 0;
    private int countdown = COUNTDOWN;
    private int animCounter = ANIMCOUNTER;
    private int radiusDelayCounter = RADIUSDELAY;
    private int animFrame = 0;
    private BombermanLevel bLevel;

    
    // erstellt eine Bombe
    public Bomben(int posX, int posY, int radius, BombermanLevel bLevel) {
        this.posX = posX;
        this.posY = posY;
        this.radius = radius;
        this.bLevel = bLevel;
    }
    
    // Gibt die Position der Bombe auf der X-Achse zurück
    public int getPosX() {
        return posX;
    }

    // Gibt die Position der Bombe auf der Y-Achse zurück
    public int getPosY() {
        return posY;
    }
    
    // Update für die Bombe. Countdown etc wird runter gezählt
    public void update() {
        int width = bLevel.getTileWidth();
        int height = bLevel.getTileHeight();
        
        if(countdown>0) countdown--;
        else if (state == BOMB) {
        	state = EXPLODING;
        } else if (state == EXPLODING && radius >0) {
        		if (radiusDelayCounter > 0) {
                    radiusDelayCounter--;
                    return;
        	}
        		radiusDelayCounter = RADIUSDELAY;
                radius--;
                radiusCounter++;
                for (int i = 1; i <= radiusCounter; i++) {
                    bLevel.destroyBlockByPixel(posX + i * width, posY);
                    bLevel.destroyBlockByPixel(posX - i * width, posY);
                    bLevel.destroyBlockByPixel(posX, posY + i * height);
                    bLevel.destroyBlockByPixel(posX, posY - i * height);
                	
                }
        	}else if (radiusDelayCounter > 0) radiusDelayCounter--;
        	else state = EXPLODED;
    }

    
    public void draw(Graphics2D g) {
        if (state == EXPLODED) return;
        int width = bLevel.getTileWidth();
        int height = bLevel.getTileHeight();
        if (state == BOMB) {
            if (animCounter > 0) animCounter--;
            else {
                animFrame = (animFrame + 1) % Texture.BOMB.length;
                animCounter = ANIMCOUNTER;
            }
            Texture.BOMB[animFrame].draw(posX, posY, width, height, g);
        } else if (state == EXPLODING) {
            Texture.EXPLMID.draw(posX, posY, width, height, g);
            for (int i = 1; i <= radiusCounter; i++) {
                if (radius == 0) {
                    bLevel.markForUpdateByPixel(posX + i * width, posY);
                    bLevel.markForUpdateByPixel(posX - i * width, posY);
                    bLevel.markForUpdateByPixel(posX, posY + i * height);
                    bLevel.markForUpdateByPixel(posX, posY - i * height);
                    if (i == radiusCounter) break;
                }
                Texture.EXPLHOR.draw(posX + i * width, posY, width, height, g);
                Texture.EXPLHOR.draw(posX - i * width, posY, width, height, g);
                Texture.EXPLVER.draw(posX, posY - i * height, width, height, g);
                Texture.EXPLVER.draw(posX, posY + i * height, width, height, g);
            }
            if (radius == 0) {
                Texture.EXPLRIG.draw(posX + radiusCounter * width, posY, width, height, g);
                Texture.EXPLLEF.draw(posX - radiusCounter * width, posY, width, height, g);
                Texture.EXPLTOP.draw(posX, posY - radiusCounter * height, width, height, g);
                Texture.EXPLBOT.draw(posX, posY + radiusCounter * height, width, height, g);
                bLevel.markForUpdateByPixel(posX + radiusCounter * width, posY);
                bLevel.markForUpdateByPixel(posX - radiusCounter * width, posY);
                bLevel.markForUpdateByPixel(posX, posY + radiusCounter * height);
                bLevel.markForUpdateByPixel(posX, posY - radiusCounter * height);
            }

        }
        bLevel.markForUpdateByPixel(posX, posY);
    }
}
        





