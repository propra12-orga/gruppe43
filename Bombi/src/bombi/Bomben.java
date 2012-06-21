package bombi;

import java.awt.Graphics2D;

/**
 * Diese Klasse erzeugt Objekte, welche als Bomben interpretiert werden.
 * 
 * @author Georgiadis
 **/
public class Bomben {

    // Ein paar Konstanten für die verschiedene Arten von Bomben
    private static final short EXPLODED = 0;
    private static final short BOMB = 1;
    private static final short EXPLODING = 2;
    // Countdowns & Timer
    private static final short COUNTDOWN = 120;
    private static final short ANIMCOUNTER = 10;
    private static final short RADIUSDELAY = 0;

    // Verschiedene Variablen
    private int state = BOMB;
    private int posX, posY;
    private int radius;
    private int radiusup = 0;
    private int radiusdown = 0;
    private int radiusleft = 0;
    private int radiusright = 0;
    private int countdown = COUNTDOWN;
    private int animCounter = ANIMCOUNTER;
    private int radiusDelayCounter = RADIUSDELAY;
    private int animFrame = 0;
    private BombermanLevel bLevel;
    Player player1, player2;
    int breakup = 0;
    int breakdown = 0;
    int breakright = 0;
    int breakleft = 0;

    /**
     * Erstellt die Bombe
     * 
     * @param posX Position des Spielers auf der X-Achse
     * @param posY Position des Spielers auf der Y-Achse
     * @param radius Explosionsradius der Bombe
     * @param bLevel Level in dem die Bombe gezeichnet wird
     */
    public Bomben(int posX, int posY, int radius, BombermanLevel bLevel) {
        this.posX = posX;
        this.posY = posY;
        this.radius = radius;
        this.bLevel = bLevel;
        bLevel.putBombByPixel(posX, posY);
    }

    /**
     * 
     * @return Gibt die Position der Bombe auf der X-Achse zurück
     */
    public int getPosX() {
        return posX;
    }

    /**
     * 
     * @return Gibt die Position der Bombe auf der Y-Achse zurück
     */
    public int getPosY() {
        return posY;
    }

    // Update für die Bombe. Countdown etc wird runter gezählt
    // Sobald Countdown auf 0 steht, wir markiert welche Steine zerstört werden
    // und prüft, ob unzerstörbare im Weg sind.
    /**
     * Überprüft dauerhaft den Zustand der Bombe Falls die Bombe noch nicht
     * explodiert, läuft ein Countdown runter bis zur Explosion. Falls die Bombe
     * explodiert wird markiert, wo der Strahl sichtbar ist, welche Blöcke
     * zerstört werden und löst Kettenreaktion aus. Ebenfalls wird Feuer
     * erstellt, dass Spieler und Gegner schaden kann.
     */
    public void update() {
        if (state == EXPLODED)
            return;
        int width = bLevel.getTileWidth();
        int height = bLevel.getTileHeight();

        if (countdown > 0 && !bLevel.hasFireByPixel(posX, posY))
            countdown--;
        else if (state == BOMB) {
            state = EXPLODING;
        } else if (state == EXPLODING && radius > 0) {
            if (radiusDelayCounter > 0) {
                radiusDelayCounter--;
                return;
            }
            radiusDelayCounter = RADIUSDELAY;
            radius--;
            radiusup++;
            radiusdown++;
            radiusleft++;
            radiusright++;
            bLevel.addFireByPixel(posX, posY);
            for (int i = 1; i <= radiusup; i++) {
                bLevel.addFireByPixel(posX, posY - i * height);
                if (bLevel.getTileByPixel(posX, posY - i * height) == 2) {
                    break;
                }
                if (bLevel.getTileByPixel(posX, posY - i * height) == 1
                        && breakup == 0) {
                    bLevel.destroyBlockByPixel(posX, posY - i * height);
                    breakup = 1;
                }
            }
            for (int i = 1; i <= radiusdown; i++) {
                bLevel.addFireByPixel(posX, posY + i * height);
                if (bLevel.getTileByPixel(posX, posY + i * height) == 2) {
                    break;
                }
                if (bLevel.getTileByPixel(posX, posY + i * height) == 1
                        && breakdown == 0) {
                    bLevel.destroyBlockByPixel(posX, posY + i * height);
                    breakdown = 1;
                }
            }
            for (int i = 1; i <= radiusright; i++) {
                bLevel.addFireByPixel(posX + i * width, posY);
                if (bLevel.getTileByPixel(posX + i * width, posY) == 2) {
                    break;
                }
                if (bLevel.getTileByPixel(posX + i * width, posY) == 1
                        && breakright == 0) {
                    bLevel.destroyBlockByPixel(posX + i * width, posY);
                    breakright = 1;
                }
            }
            for (int i = 1; i <= radiusleft; i++) {
                bLevel.addFireByPixel(posX - i * width, posY);
                if (bLevel.getTileByPixel(posX - i * width, posY) == 2) {
                    break;
                }
                if (bLevel.getTileByPixel(posX - i * width, posY) == 1
                        && breakleft == 0) {
                    bLevel.destroyBlockByPixel(posX - i * width, posY);
                    breakleft = 1;
                }
            }
        } else if (radiusDelayCounter > 0)
            radiusDelayCounter--;
        else {
            state = EXPLODED;
            bLevel.removeFireByPixel(posX, posY);
            for (int i = 1; i <= radiusup; i++) {
                bLevel.removeFireByPixel(posX, posY - i * height);
            }
            for (int i = 1; i <= radiusdown; i++) {
                bLevel.removeFireByPixel(posX, posY + i * height);
            }
            for (int i = 1; i <= radiusright; i++) {
                bLevel.removeFireByPixel(posX + i * width, posY);
            }
            for (int i = 1; i <= radiusleft; i++) {
                bLevel.removeFireByPixel(posX - i * width, posY);
            }
            bLevel.removeBombByPixel(posX, posY);
            breakright = 0;
            breakleft = 0;
            breakup = 0;
            breakdown = 0;

        }
    } // SoundManager Audios einlesen
    SoundManager playAudio = new SoundManager() {
        public void initSounds() {
            sounds.add(new Sound("Bumm", Sound.getURL("/Bumm.wav")));   // Explusionssound
        }
    };

    /**
     * Diese Methode zeichnet die Bombe und die Explosion.
     * 
     * @param g Das Graphics-Objekt, welches genutzt wird, um die Bomben zu
     * zeichnen.
     */
    public void draw(Graphics2D g) {
        if (state == EXPLODED)
            return;
        int width = bLevel.getTileWidth();
        int height = bLevel.getTileHeight();
        if (state == BOMB) {
            if (animCounter > 0)
                animCounter--;
            else {
                animFrame = (animFrame + 1) % Texture.BOMB.length;
                animCounter = ANIMCOUNTER;
            }
            Texture.BOMB[animFrame].draw(posX, posY, width, height, g);
            
        } else if (state == EXPLODING) {
            Texture.EXPLMID.draw(posX, posY, width, height, g);
            playAudio.playSound("Bumm"); // sound abspielen
            for (int i = 1; i < radiusup
                    && bLevel.getTileByPixel(posX, posY - i * height) != 2; i++) {
                if (radius == 0) {
                    bLevel.markForUpdateByPixel(posX, posY - i * height);
                    if (i == radiusup)
                        break;
                }
                if (bLevel.getTileByPixel(posX, posY - i * height) == 1)
                    break;
                Texture.EXPLVER.draw(posX, posY - i * height, width, height, g);
                if (radius == 0) {
                    Texture.EXPLBOT.draw(posX, posY + radiusup * height, width,
                            height, g);
                    bLevel.markForUpdateByPixel(posX, posY - radiusup * height);
                }
            }

            for (int i = 1; i < radiusdown
                    && bLevel.getTileByPixel(posX, posY + i * height) != 2; i++) {
                if (radius == 0) {
                    bLevel.markForUpdateByPixel(posX, posY + i * height);
                    if (i == radiusdown)
                        break;
                }
                if (bLevel.getTileByPixel(posX, posY + i * height) == 1)
                    break;
                Texture.EXPLVER.draw(posX, posY + i * height, width, height, g);
                if (radius == 0) {
                    Texture.EXPLTOP.draw(posX, posY - radiusdown * height,
                            width, height, g);
                    bLevel.markForUpdateByPixel(posX, posY + radiusdown
                            * height);
                }
            }

            for (int i = 1; i < radiusleft
                    && bLevel.getTileByPixel(posX - i * width, posY) != 2; i++) {
                if (radius == 0) {
                    bLevel.markForUpdateByPixel(posX - i * width, posY);
                    if (i == radiusleft)
                        break;
                }
                if (bLevel.getTileByPixel(posX - i * width, posY) == 1)
                    break;
                Texture.EXPLHOR.draw(posX - i * width, posY, width, height, g);
                if (radius == 0) {
                    Texture.EXPLLEF.draw(posX - radiusleft * width, posY,
                            width, height, g);
                    bLevel.markForUpdateByPixel(posX - radiusleft * width, posY);
                }
            }

            for (int i = 1; i < radiusright
                    && bLevel.getTileByPixel(posX + i * width, posY) != 2; i++) {
                if (radius == 0) {
                    bLevel.markForUpdateByPixel(posX + i * width, posY);

                    if (i == radiusright)
                        break;
                }
                if (bLevel.getTileByPixel(posX + i * width, posY) == 1)
                    break;
                Texture.EXPLHOR.draw(posX + i * width, posY, width, height, g);

                if (radius == 0) {
                    Texture.EXPLRIG.draw(posX + radiusright * width, posY,
                            width, height, g);
                    bLevel.markForUpdateByPixel(posX + radiusright * width,
                            posY);
                }
            }

        }
        bLevel.markForUpdateByPixel(posX, posY);
    }

    public boolean isExploded() {
        return state == EXPLODED;
    }
}
