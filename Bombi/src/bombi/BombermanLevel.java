package bombi;

import java.awt.Graphics;

/**
 * Diese Klasse erzeugt Objekte, welche als Spielfeld interpretiert werden. Hierzu wird ein zweidimensionales short-Array benutzt. IdR wird nur ein Objekt
 * dieser Klasse erstellt.
 **/
public class BombermanLevel {

    // ein paar Konstanten, um das Manipulieren des Felder einfacher zu gestalten
    public static final short GRASS = 0;
    public static final short STONE = 1;
    public static final short INDESTRUCTIBLE = 2;
    public static final short EXIT = 3;

    // statische Variable, welche die Groesse der Felder in Pixeln spezifiziert
    private static int tileWidth = 32;
    private static int tileHeight = 32;

    public static int getTileWidth() {
        return tileWidth;
    }

    public static int getTileHeight() {
        return tileHeight;
    }

    public void updateTileDimensions(int pixelWidth, int pixelHeight) {
        tileWidth = pixelWidth / width;
        tileHeight = pixelHeight / height;
    }

    // zweidimensionales Array, welches das Spielfeld darstellt
    private short[][] tiles;
    // Dimension des obigen Arrays
    public int width, height;

    /**
     * Erzeugt ein neues Spielfeld.
     * 
     * @param width: Breite des Spielfelds in Pixel
     * @param height: Hoehe des Spielfelds in Pixel
     **/
    public BombermanLevel(int width, int height, int pixelWidth, int pixelHeight) {
        // Berechne die Dimension des Feldes mit Hilfe der Pixel
        this.width = width;
        this.height = height;
        // Berechne die Größe der Felder in Pixel
        updateTileDimensions(pixelWidth, pixelHeight);
        // erstelle ein leeres Spielfeld
        tiles = new short[this.width][this.height];
        fillRandomly();
    }

    public short getTileByPixel(int pixelX, int pixelY) {
        return getTile(pixelX / tileWidth, pixelY / tileHeight);
    }

    public short getTile(int posX, int posY) {
        if (posX < 0 || posX >= width || posY < 0 || posY >= height) return GRASS;
        return tiles[posX][posY];
    }

    public boolean isSolid(short tile) {
        tile &= ~(1 << 15);
        return (tile == INDESTRUCTIBLE || tile == STONE);
    }

    /**
     * Methode, welche das Spielfeld initialisiert. Alle zwei Zeilen/Spalten wird ein unzerstoerbarer Stein erzeugt. Ein 2x2 Block in den Ecken wird
     * freigelassen, um dem Spieler Bewegungsfreiraum zu ermoeglichen.
     **/
    private void fillRandomly() {
        // iteriere ueber die x- und dann die y-Koordinaten
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if ((j % 2 == 1) && (i % 2 == 1)) // alle 2 Zeilen/Spalten..
                tiles[i][j] = INDESTRUCTIBLE; // ... erzeuge einen unzerstoerbaren Block

                // gehe sicher, dass alle vier Spieler anfangs eingemauert sind
                else if ((i == 2 || i == width - 3) && ((j >= 0 && j <= 2) || (j >= height - 3 && j <= height - 1))) tiles[i][j] = STONE;
                else if ((j == 2 || j == height - 3) && ((i >= 0 && i < 2) || (i > width - 3 && i <= width - 1))) tiles[i][j] = STONE;

                // fuelle alle anderen Bloecke (ausser den Startzonen) zufaellig mit Steinen
                else if ((i >= 3 && i <= width - 4) || (j >= 3 && j <= height - 4)) {
                    if (Math.random() <= 0.75) // 75% Chance fuer Steine
                    tiles[i][j] = STONE;
                    // das folgende ist auskommentiert, da Bloecke anfangs sowieso den Wert 0 besitzen.
                    // else
                    // tiles[i][j] = GRASS;
                }
                markForUpdate(i, j);
            }
        }
        // fuege zufaellig einen Ausgang ein
        int i = (int) (Math.random() * tiles.length);
        int j = (int) (Math.random() * tiles[1].length);
        tiles[i][j] = EXIT;
        markForUpdate(i, j);
    }

    public void draw(Graphics g) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (markedForUpdate(tiles[i][j])) {
                    unmarkForUpdate(i, j);
                    if (tiles[i][j] == GRASS) drawGrass(i, j, g);

                    else if (tiles[i][j] == STONE) drawStone(i, j, g);

                    else if (tiles[i][j] == INDESTRUCTIBLE) drawIndestructible(i, j, g);

                    else if (tiles[i][j] == EXIT) drawExit(i, j, g);
                }
            }
        }
    }

    private boolean markedForUpdate(short tile) {
        return (tile &= (1 << 15)) != 0;
    }

    private void drawGrass(int posX, int posY, Graphics g) {
        // g.setColor(Color.GREEN);
        // g.fillRect(posX * DIM, posY * DIM, DIM, DIM);
        Texture.GRASS.draw(posX * tileWidth, posY * tileHeight, tileWidth, tileHeight, g);
    }

    private void drawStone(int posX, int posY, Graphics g) {
        Texture.STONE.draw(posX * tileWidth, posY * tileHeight, tileWidth, tileHeight, g);
    }

    private void drawIndestructible(int posX, int posY, Graphics g) {
        Texture.BEDROCK.draw(posX * tileWidth, posY * tileHeight, tileWidth, tileHeight, g);
    }

    private void drawExit(int posX, int posY, Graphics g) {
        Texture.EXIT.draw(posX * tileWidth, posY * tileHeight, tileWidth, tileHeight, g);
    }

    public void markForUpdate(int posX, int posY, int radius) {
        int leftX = posX - radius;
        int rightX = posX + radius;
        int topY = posY - radius;
        int bottomY = posY + radius;

        if (leftX < 0) leftX = 0;
        if (rightX >= width) rightX = width - 1;
        if (topY < 0) topY = 0;
        if (bottomY >= height) bottomY = height - 1;

        for (int i = leftX; i <= rightX; i++) {
            for (int j = topY; j <= bottomY; j++)
                markForUpdate(i, j);
        }

    }

    public void markForUpdate(int posX, int posY) {
        if (posX < 0 || posX >= width || posY < 0 || posY >= height) return;
        tiles[posX][posY] |= (1 << 15);
    }

    public void markForUpdateByPixel(int pixelX, int pixelY) {
        markForUpdate(pixelX / tileWidth, pixelY / tileHeight);
    }

    public void destroyBlockByPixel(int pixelX, int pixelY) {
        destroyBlock(pixelX / tileWidth, pixelY / tileHeight);
    }

    public void destroyBlock(int posX, int posY) {
        if (posX < 0 || posX >= width || posY < 0 || posY >= height) return;
        if ((tiles[posX][posY] & ~(1 << 15)) == STONE) tiles[posX][posY] = GRASS;
        markForUpdate(posX, posY);
    }

    public void unmarkForUpdate(int posX, int posY) {
        if (posX < 0 || posX >= width || posY < 0 || posY >= height) return;
        tiles[posX][posY] &= ~(1 << 15);
    }

    public void markAllForUpdate() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                markForUpdate(i, j);
            }
        }
    }

}// Ende der Klasse BombermanLevel
