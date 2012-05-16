package bombi;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Zweck dieser Klasse ist, alle (?) verwendeten Texturen einzulesen und diese als statische Objekte bereitzustellen, damit diese zB in BombermanLevel
 * gezeichnet werden können.
 * 
 * @author tobi
 * 
 */
public class Texture {
    private static final String IMGURL = "/texture.png";

    /**
     * Statische Methode zum Einlesen eines Bildes, welches durch url gegeben ist. Das Bild wird als BufferedImage zurück gegeben
     */
    private static BufferedImage getBufferedImage() {
        try {
            return ImageIO.read(new Texture().getClass().getResource(IMGURL));
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Texturen.");
            e.printStackTrace();
            return null;
        }
    }

    // benutzt obige Methode, um die Texturen einzulesen
    private static final BufferedImage TEXTURE = getBufferedImage();

    /**
     * WICHTIGER TEIL HIER! Bisher werden GRASS, STONE und BEDROCK aus der Datei texture.png erzeugt, welche 256x256 Pixel groß ist. Hierüber können einfach
     * andere Texturen erzeugt werden, mit der folgenden Syntax:
     * 
     * public static final Texture *NAME* = new Texture(startXPixel,startYPixel,Breite,Höhe);
     * 
     * (startXPixel,startYPixel) ist hierbei der linke obere Pixel in der Datei texture.png
     * 
     * texture.png befindet sich in Bombi/img/texture.png
     **/

    // tatsächlicher Zweck dieser Klasse.. Bereitstellung der Texturen =)
    public static final Texture GRASS = new Texture(0, 0, 32, 32);
    public static final Texture STONE = new Texture(32, 0, 32, 32);
    public static final Texture BEDROCK = new Texture(64, 0, 32, 32);
    public static final Texture SPIELER = new Texture(96, 0, 32, 32);

    private BufferedImage texture;

    public Texture() {}// dummy-constructor für obige Methode.. das muss besser gehn!

    public Texture(int sx, int sy, int width, int height) {
        texture = TEXTURE.getSubimage(sx, sy, width, height);
    }

    public void draw(int px, int py, int width, int height, Graphics g) {
        g.drawImage(texture, px, py, width, height, null);
    }
}