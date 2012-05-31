package bombi;

import java.awt.Graphics;
import java.awt.Graphics2D;
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
    public static Texture GRASS = new Texture(0, 0, 32, 32);
    public static Texture STONE = new Texture(32, 0, 32, 32);
    public static Texture BEDROCK = new Texture(64, 0, 32, 32);
    public static Texture EXIT = new Texture(0, 32, 32, 32);

    public static Texture ROBOT = new Texture(192, 0, 32, 32);
    public static Texture SPIELER1 = new Texture(96, 0, 32, 32);

    public static Texture BOMB1 = new Texture(0, 64, 32, 32);
    public static Texture BOMB2 = new Texture(32, 64, 32, 32);
    public static Texture BOMB3 = new Texture(64, 64, 32, 32);
    public static Texture[] BOMB = { BOMB1, BOMB2, BOMB3 };

    public static Texture EXPLMID = new Texture(96, 32, 32, 32);
    public static Texture EXPLHOR = new Texture(128, 32, 32, 32);
    public static Texture EXPLVER = new Texture(96, 64, 32, 32);
    public static Texture EXPLRIG = new Texture(160, 32, 32, 32);
    public static Texture EXPLBOT = new Texture(96, 96, 32, 32);
    public static Texture EXPLTOP = EXPLBOT.mirrorVertically();
    public static Texture EXPLLEF = EXPLRIG.mirrorHorizontally();

    private BufferedImage texture; // Original-Bild zum verlustfreien Skalieren
    private BufferedImage scaledTexture; // tatsächlich gemaltes Bild

    private int width, height;

    public Texture() {}// dummy-constructor für obige Methode.. das muss besser gehn!

    public Texture(int sx, int sy, int width, int height) {
        scaledTexture = texture = TEXTURE.getSubimage(sx, sy, width, height);
        this.width = width;
        this.height = height;
    }

    public Texture(BufferedImage texture) {
        this.texture = scaledTexture = texture;
        width = texture.getWidth();
        height = texture.getHeight();
    }

    public void draw(int px, int py, int width, int height, Graphics g) {
        if (this.width != width || this.height != height) rescale(width, height);
        g.drawImage(scaledTexture, px, py, null);
    }

    private void rescale(int width, int height) {
        scaledTexture = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) scaledTexture.getGraphics();
        g.drawImage(texture, 0, 0, width, height, null);
        g.dispose();
    }

    private Texture mirrorHorizontally() {
        int width = this.texture.getWidth();
        int height = this.texture.getHeight();
        BufferedImage texture = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) texture.getGraphics();
        g.drawImage(this.texture, 0, 0, width, height, width, 0, 0, height, null);
        g.dispose();
        return new Texture(texture);
    }

    private Texture mirrorVertically() {
        int width = this.texture.getWidth();
        int height = this.texture.getHeight();
        BufferedImage texture = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) texture.getGraphics();
        g.drawImage(this.texture, 0, 0, width, height, 0, width, height, 0, null);
        g.dispose();
        return new Texture(texture);
    }
}