package bombi;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Zweck dieser Klasse ist, alle verwendeten Texturen einzulesen und diese als
 * statische Objekte bereitzustellen, damit diese zB in BombermanLevel
 * gezeichnet werden können.
 * 
 * @author tobi
 * 
 */
public class Texture {
    private static final String IMGURL = "/texture.png";

    private static final int HOUSECOLOR1 = 0xffcccccc;
    private static final int HOUSECOLOR1_SHADOW = 0xffaaaaaa;
    private static final int HOUSECOLOR2 = 0xffcccc00;
    private static final int HOUSECOLOR2_SHADOW = 0xffaaaa00;

    /**
     * Statische Methode zum Einlesen eines Bildes, welches durch url gegeben
     * ist. Das Bild wird als BufferedImage zurück gegeben
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

    /*
     * WICHTIGER TEIL HIER! Bisher werden die unten folgenden Texture aus der
     * Datei texture.png erzeugt. Hierueber können einfach andere Texturen
     * erzeugt werden, mit der folgenden Syntax:
     * 
     * public static final Texture *NAME* = new Texture(startXPixel,
     * startYPixel, Breite, Hoehe);
     * 
     * (startXPixel, startYPixel) ist hierbei der linke obere Pixel der
     * einzulesenden Textur in der Datei texture.png
     * 
     * texture.png befindet sich in Bombi/img/texture.png
     */

    // tatsächlicher Zweck dieser Klasse.. Bereitstellung der Texturen =)
    public static Texture GRASS = new Texture(0, 0, 32, 32);
    public static Texture STONE = new Texture(32, 0, 32, 32);
    public static Texture BEDROCK = new Texture(64, 0, 32, 32);
    public static Texture EXIT = new Texture(0, 32, 32, 32);

    public static Texture PLAYER_MOVE_FRONT1 = new Texture(224, 0, 32, 48);
    public static Texture PLAYER_MOVE_FRONT2 = PLAYER_MOVE_FRONT1
            .mirrorHorizontally();

    public static Texture PLAYER_MOVE_BACK1 = new Texture(224, 48, 32, 48);
    public static Texture PLAYER_MOVE_BACK2 = PLAYER_MOVE_BACK1
            .mirrorHorizontally();

    public static Texture PLAYER_MOVE_LEFT1 = new Texture(256, 48, 32, 48);
    public static Texture PLAYER_MOVE_LEFT2 = new Texture(288, 48, 32, 48);

    public static Texture PLAYER_MOVE_RIGHT1 = PLAYER_MOVE_LEFT1
            .mirrorHorizontally();
    public static Texture PLAYER_MOVE_RIGHT2 = PLAYER_MOVE_LEFT2
            .mirrorHorizontally();

    public static Texture PLAYER_IDLE_FRONTlll = new Texture(192, 0, 32, 48);
    public static Texture PLAYER_IDLE_FRONT = PLAYER_IDLE_FRONTlll
            .replaceColor(HOUSECOLOR1, 0xffff0000)
            .replaceColor(HOUSECOLOR1_SHADOW, 0xffbb0000)
            .replaceColor(HOUSECOLOR2, 0xff0000ff)
            .replaceColor(HOUSECOLOR2_SHADOW, 0xff0000bb);

    public static Texture PLAYER_IDLE_BACK = new Texture(192, 48, 32, 48);
    public static Texture PLAYER_IDLE_LEFT1 = new Texture(256, 0, 32, 48);
    public static Texture PLAYER_IDLE_LEFT2 = new Texture(288, 0, 32, 48);
    public static Texture PLAYER_IDLE_RIGHT1 = PLAYER_IDLE_LEFT1
            .mirrorHorizontally();
    public static Texture PLAYER_IDLE_RIGHT2 = PLAYER_IDLE_LEFT2
            .mirrorHorizontally();

    public static Texture[][] PLAYER_MOVE = {
            { PLAYER_MOVE_FRONT1, PLAYER_IDLE_FRONT, PLAYER_MOVE_FRONT2,
                    PLAYER_IDLE_FRONT },
            { PLAYER_MOVE_BACK1, PLAYER_IDLE_BACK, PLAYER_MOVE_BACK2,
                    PLAYER_IDLE_BACK },
            { PLAYER_MOVE_LEFT1, PLAYER_IDLE_LEFT2, PLAYER_MOVE_LEFT2,
                    PLAYER_IDLE_LEFT1 },
            { PLAYER_MOVE_RIGHT1, PLAYER_IDLE_RIGHT2, PLAYER_MOVE_RIGHT2,
                    PLAYER_IDLE_RIGHT1 } };

    public static Texture[] PLAYER_IDLE = { PLAYER_IDLE_FRONT,
            PLAYER_IDLE_BACK, PLAYER_IDLE_LEFT1, PLAYER_IDLE_RIGHT1 };

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

    // Abwaertskompatibilitaet
    public static Texture SPIELER1 = PLAYER_IDLE_FRONT;
    public static Texture ROBOT = PLAYER_IDLE_FRONT;

    private BufferedImage texture; // Original-Bild zum verlustfreien Skalieren
    private BufferedImage scaledTexture; // tatsächlich gemaltes Bild

    private int width, height;

    public Texture() {}// dummy-constructor für obige Methode.. das muss besser
                       // gehn!

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
        if (this.width != width || this.height != height)
            rescale(width, height);
        g.drawImage(scaledTexture, px, py, null);
    }

    private void rescale(int width, int height) {
        this.width = width;
        this.height = height;
        scaledTexture = new BufferedImage(width, height,
                BufferedImage.TYPE_4BYTE_ABGR);
        scaledTexture
                .setAccelerationPriority(texture.getAccelerationPriority());
        Graphics2D g = (Graphics2D) scaledTexture.getGraphics();
        g.drawImage(texture, 0, 0, width, height, null);
        g.dispose();
    }

    private Texture mirrorHorizontally() {
        int width = this.texture.getWidth();
        int height = this.texture.getHeight();
        BufferedImage texture = new BufferedImage(width, height,
                BufferedImage.TYPE_4BYTE_ABGR);
        texture.setAccelerationPriority(this.texture.getAccelerationPriority());
        Graphics2D g = (Graphics2D) texture.getGraphics();
        g.drawImage(this.texture, 0, 0, width, height, width, 0, 0, height,
                null);
        g.dispose();
        return new Texture(texture);
    }

    private Texture mirrorVertically() {
        int width = this.texture.getWidth();
        int height = this.texture.getHeight();
        BufferedImage texture = new BufferedImage(width, height,
                BufferedImage.TYPE_4BYTE_ABGR);
        texture.setAccelerationPriority(this.texture.getAccelerationPriority());
        Graphics2D g = (Graphics2D) texture.getGraphics();
        g.drawImage(this.texture, 0, 0, width, height, 0, width, height, 0,
                null);
        g.dispose();
        return new Texture(texture);
    }

    private Texture replaceColor(int fromColor, int toColor) {
        int width = this.texture.getWidth();
        int height = this.texture.getHeight();
        BufferedImage temp = new BufferedImage(width, height,
                BufferedImage.TYPE_4BYTE_ABGR);
        temp.getGraphics().drawImage(texture, 0, 0, null);
        int[] rgb = new int[width * height];
        temp.getRGB(0, 0, width, height, rgb, 0, width);
        for (int i = 0; i < rgb.length; i++) {
            if (rgb[i] == fromColor)
                rgb[i] = toColor;
        }
        temp.setRGB(0, 0, width, height, rgb, 0, width);
        BufferedImage texture = new BufferedImage(width, height,
                BufferedImage.TYPE_4BYTE_ABGR);
        texture.setAccelerationPriority(this.texture.getAccelerationPriority());
        Graphics2D g = (Graphics2D) texture.getGraphics();
        g.drawImage(temp, 0, 0, width, height, null);
        return new Texture(texture);
    }
}