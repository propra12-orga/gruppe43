package bombi;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Zweck dieser Klasse ist, alle verwendeten Texturen einzulesen und diese als
 * statische Objekte bereitzustellen, damit diese zB in BombermanLevel
 * gezeichnet werden können. Sie dient im Grunde als Wrapper fuer das von Java2D
 * bereitgestellte Objekt BufferedImage; zusaetzlich werden Methoden zum
 * effizienten (aber speicherlastigerem) Skalieren und Spiegeln sowie zum
 * ineffezienten Ersetzen der Farben bereitgestellt.
 * 
 * @author tobi
 * 
 */
public class Texture {
    // aktuell der Pfad zur Datei, welche die Texturen beinhaltet
    private static final String IMGURL = "/texture.png";

    // die vier Farben, welche von Spieler zu Spieler variabel sein sollen
    private static final int HOUSECOLOR1 = 0xffcccccc; // grau
    private static final int HOUSECOLOR1_SHADOW = 0xffaaaaaa; // dunkelgrau
    private static final int HOUSECOLOR2 = 0xffcccc00; // gelb
    private static final int HOUSECOLOR2_SHADOW = 0xffaaaa00; // dunkelgelb
    private static final int[] HOUSECOLORS = { HOUSECOLOR1, HOUSECOLOR1_SHADOW,
            HOUSECOLOR2, HOUSECOLOR2_SHADOW };

    /*
     * Statische Methode zum Einlesen eines Bildes, welches durch IMGURL gegeben
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

    private BufferedImage texture; // Original-Bild zum verlustfreien Skalieren
    private BufferedImage scaledTexture; // tatsächlich gemaltes Bild

    private int width, height;

    /**
     * @deprecated
     * 
     * Dummy-Konstruktor, welcher kein brauchbares Objekt erzeugt. Wird nur
     * genutzt, um den relativen Pfad zusammen mit der getClass() Methode nutzen
     * zu koennen.
     */
    public Texture() {}// dummy-constructor für obige Methode.. das muss besser
                       // gehn!

    /**
     * Erzeugt ein neues Texturen-Objekt aus einem Teilbild von img/texture.png.
     * 
     * @param sx X-Koordinate des linken oberen Pixels des Teilbilds
     * @param sy Y-Koordinate des linken oberen Pixels des Teilbilds
     * @param width Breite des Teilbilds
     * @param height Hoehe des Teilbilds
     */
    public Texture(int sx, int sy, int width, int height) {
        scaledTexture = texture = TEXTURE.getSubimage(sx, sy, width, height);
        this.width = width;
        this.height = height;
    }

    /**
     * Erzeugt ein neues Texturen-Objekt anhand eines kompletten Bildes.
     * 
     * @param texture Das Bild, um welches das Texture-Objekt gewrappt wird.
     */
    public Texture(BufferedImage texture) {
        this.texture = scaledTexture = texture;
        width = texture.getWidth();
        height = texture.getHeight();
    }

    /**
     * Zeichnet die Textur (d.h. das beim Erzeugen uebergebene Bild) auf den per
     * Graphics-Objekt spezifizierten Bereich.
     * 
     * Wird eine andere Groesse als die des eigentlichen Bildes angegeben, so
     * wird das zugrunde liegende Bild einmalig skaliert und das neu erzeugte
     * Objekt gezeichnet. Dieser Ansatz ist dann effizient, wenn die Texturen
     * nur selten skaliert werden (davon kann bei einem Spiel ausgegangen
     * werden).
     * 
     * @param px X-Koordinate des linken oberen Pixels des Zielobjekts, ab dem
     * die Textur gezeichnet werden soll
     * @param py Y-Koordinate des linken oberen Pixels des Zielobjekts, ab dem
     * die Textur gezeichnet werden soll
     * @param width Breite des Bildes auf dem Zielobjekt
     * @param height Hoehe des Bildes auf dem Zielobjekt
     * @param g Zum Zielobjekt gehoeriges Graphics-Objekt
     */
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

    // private Texture replaceColor(int fromColor, int toColor) {
    // return replaceColors(new int[] { fromColor }, new int[] { toColor });
    // }

    private Texture replaceColors(int[] fromColors, int[] toColors) {
        int length = Math.min(fromColors.length, toColors.length);
        int width = this.texture.getWidth();
        int height = this.texture.getHeight();
        BufferedImage temp = new BufferedImage(width, height,
                BufferedImage.TYPE_4BYTE_ABGR);
        temp.getGraphics().drawImage(texture, 0, 0, null);
        int[] rgb = new int[width * height];
        temp.getRGB(0, 0, width, height, rgb, 0, width);
        for (int i = 0; i < rgb.length; i++) {
            for (int j = 0; j < length; j++) {
                if (rgb[i] == fromColors[j]) {
                    rgb[i] = toColors[j];
                    break;
                }
            }
        }
        temp.setRGB(0, 0, width, height, rgb, 0, width);
        BufferedImage texture = new BufferedImage(width, height,
                BufferedImage.TYPE_4BYTE_ABGR);
        texture.setAccelerationPriority(this.texture.getAccelerationPriority());
        Graphics2D g = (Graphics2D) texture.getGraphics();
        g.drawImage(temp, 0, 0, width, height, null);
        return new Texture(texture);
    }

    public static Texture[] replaceColors(Texture[] textures, int[] fromColors,
            int[] toColors) {
        Texture[] newTextures = new Texture[textures.length];
        for (int i = 0; i < textures.length; i++)
            newTextures[i] = textures[i].replaceColors(fromColors, toColors);
        return newTextures;
    }

    public static Texture[][] replaceColors(Texture[][] textures,
            int[] fromColors, int[] toColors) {
        if (textures.length == 0)
            return null;
        Texture[][] newTextures = new Texture[textures.length][textures[1].length];
        for (int i = 0; i < textures.length; i++)
            for (int j = 0; j < textures[1].length; j++)
                newTextures[i][j] = textures[i][j].replaceColors(fromColors,
                        toColors);
        return newTextures;
    }

    /*************************************************************************
     ******************** Es folgen die Texturen *****************************
     *************************************************************************/

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

    public static Texture PLAYER_IDLE_FRONT = new Texture(192, 0, 32, 48);
    public static Texture PLAYER_IDLE_FRONT_RED = PLAYER_IDLE_FRONT
            .replaceColors(HOUSECOLORS, new int[] { 0xffff0000, 0xffbb0000,
                    0xff0000ff, 0xff0000bb });

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
}