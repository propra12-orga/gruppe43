package bombi;

import java.awt.Graphics;

/**
 * Diese Klasse erzeugt Objekte, welche als Spielfeld interpretiert werden.
 * Hierzu wird ein zweidimensionales short-Array benutzt. Die Eintraege dieses
 * Arrays werden im Folgenden als "Kacheln" bezeichnet und stellen einen Teil
 * des Spielfeldes mit fester Groesse dar. In Kacheln werden mehr oder weniger
 * "unbewegliche" Objekte des Spieles festgehalten, also zum Beispiel ein
 * Mauerstueck.
 * 
 * Die Elemente des Arrays haben folgenden Aufbau:
 * 
 * DBF0000000TTTTTT (Binaerdarstellung)
 * 
 * Wobei D anzeigt, ob sich visuell etwas in dieser Kachel geaendert hat, B, ob
 * in diesem Feld eine Bombe liegt und F, ob sich eine Flamme (einer
 * explodierenden Bombe) in dem Feld befindet.
 * 
 * Die niedrigsten 6 Bit (d.h. die Werte zwischen 0 und 127, inklusiv) sind fuer
 * verschiedene Kachelarten reserviert, also fuer (un)zerstoerbare Mauern, Gras
 * oder Ausgaenge.
 **/
public class BombermanLevel {

    // ein paar Konstanten, um das Manipulieren des Feldes einfacher zu
    // gestalten
    /**
     * Wert (ohne D, B, oder F Flag), welcher als Gras-Kachel interpretiert
     * wird.
     */
    public static final short GRASS = 0;
    /**
     * Wert (ohne D, B, oder F Flag), welcher als zerstoerbare Mauer
     * interpretiert wird.
     */
    public static final short STONE = 1;
    /**
     * Wert (ohne D, B, oder F Flag), welcher als unzerstoerbare Mauer
     * interpretiert wird.
     */
    public static final short INDESTRUCTIBLE = 2;
    /** Wert (ohne D, B, oder F Flag), welcher als Ausgang interpretiert wird. */
    public static final short EXIT = 3;
    /**
     * Wert (ohne D, B, oder F Flag), welcher als hinter einer zerstoerbaren
     * Mauer liegender Ausgang interpretiert wird.
     */
    public static final short HIDDENEXIT = 4;
    /**
     * Wert (ohne D, B oder F Flag), welcher als Powerup interpretiert wird,
     * welches die maximale Anzahl an legbaren Bomben um eins erhoeht.
     */
    public static final short BOMBPLUS = 5;
    private static final float BOPLPROB = 0.2f;
    /**
     * Wert (ohne D, B oder F Flag), welcher als Powerup interpretiert wird,
     * welches die Reichweite der Bomben erhoeht.
     */
    public static final short FIREPLUS = 6;
    private static final float FIPLPROB = 0.2f;
    /**
     * Wert (ohne D, B oder F Flag), welcher als Powerup interpretiert wird,
     * welches den Spieler in Chuck Norris verwandelt.
     */
    public static final short CHUCKNORRIS = 7;
    private static final float NORRISPROB = 0.01f;
    
    private static final byte ANIMDURATION = 20;
    private static int animCounter = ANIMDURATION;
    private static int animFrame = 0;
    
    /**
     * Diese Methode ueberprueft lediglich, ob es sich bei einem Wert um einen
     * gueltigen Block handelt. Dabei werden nur die niedrigsten 6 Bit
     * betrachtet.
     * 
     * @param tile Der Wert, welcher auf seine Gueltigkeit ueberprueft werden
     * soll.
     * @return Ist true, falls die niedrigsten 6 Bit von tile einen gueltigen
     * Block darstellen.
     */
    public static final boolean isValidTile(short tile) {
        tile &= TILE;
        return (tile >= GRASS && tile <= HIDDENEXIT);
    }

    // Konstanten fuer die Flags
    private static final short DRAW = (short) (1 << 15);
    private static final short BOMB = (short) (1 << 14);
    private static final short FIRE = (short) (1 << 13);
    private static final short TILE = 127; // 0...0111111

    // Mindestgroesse des Feldes
    private static final int MINDIM = 7;

    private static final String DIMERROR = "Ein sinnvoll erzeugtes Zufallsspielfeld sollte mindestens "
            + MINDIM + "x" + MINDIM + " Kacheln besitzen.";

    // Wahrscheinlichkeit fuer Steine bei zufaelligem Feld = 75%
    private static final double STONEPROBABILITY = 0.75;

    // Variable, welche die Groesse der Felder in Pixeln spezifiziert. Dieser
    // Wert wurde zuvor in tileWidth und tileHeight gespeichert, wir bevorzugen
    // aber quadratische Kacheln
    private int tileDim;
    private boolean drawAll = true; // wird auf true gesetzt, wenn ALLE Kacheln
                                    // neu gezeichnet werden sollen, da dies
                                    // effizienter ist, als ueberall D-Flags zu
                                    // setzen. ATM auf true, da partielles
                                    // Updaten noch nicht von den anderen
                                    // Klassen unterstuetzt ist

    /**
     * Liefert die aktuelle Kantenlaenge aller Kacheln in Pixeln zurueck. Der
     * hier zurueckgegebene Wert wird auch von der draw()-Methode der gleichen
     * Klasse genutzt.
     * 
     * @return Die Kantenlaenge der Kacheln in Pixeln.
     */
    public int getTileDim() {
        return tileDim;
    }

    // fuer Abwaertskompatibilitaet

    /**
     * @deprecated
     * @return siehe getTileDim()
     */
    public int getTileHeight() {
        return tileDim;
    }

    /**
     * @deprecated
     * @return siehe getTileDim()
     */
    public int getTileWidth() {
        return tileDim;
    }

    /**
     * Diese Methode aktualisiert die Kantenlaenge der Kacheln anhand der Breite
     * und Hoehe (in Pixeln) des zur Verfuegung stehenden Bereichs. Konkreter:
     * In der Regel soll ein Objekt dieser Klasse auf ein
     * Panel/Component/BufferedImage gezeichnet werden. Diese Methode passt die
     * Kantenlaenge so an, dass das Spielfeld eben jenes
     * Panel/Component/BufferedImage moeglichst komplett ausfuellt (die Kacheln
     * haben weiterhin quadratische Groesse).
     * 
     * Diese Methode sollte zum Beispiel beim Vergroessern oder Verkleinern des
     * Spielfensters aufgerufen werden.
     * 
     * @param pixelWidth Breite des Bereichs, auf welchen dieses Objekt spaeter
     * gezeichnet werden soll.
     * @param pixelHeight Hoehe des Bereichs, auf welchen dieses Objekt spaeter
     * gezeichnet werden soll
     */
    public void updateTileDimensions(int pixelWidth, int pixelHeight) {
        tileDim = Math.min(pixelWidth / width, pixelHeight / height);
    }

    /**
     * Gibt die Spielfeldbreite in Kacheln zurueck. Dies entspricht visuell der
     * Menge an Steinbloecken, welche sich horizontal auf dem Spielfeld
     * nebeneinander anordnen laesst. Intern ist dies die Laenge des
     * zweidimensionalen Feldes, in welchem die Kacheln gespeichert werden.
     * 
     * @return Die Breite des Spielfelds in Kacheln.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gibt die Spielfeldhoehe in Kacheln zurueck. Dies entspricht visuell der
     * Menge an Steinbloecken, welche sich vertikal auf dem Spielfeld
     * uebereinander anordnen laesst. Intern ist dies die Hoehe des
     * zweidimensionalen Feldes, in welchem die Kacheln gespeichert werden.
     * 
     * @return Die Hoehe des Spielfelds in Kacheln.
     */
    public int getHeight() {
        return height;
    }

    // zweidimensionales Array, welches das Spielfeld darstellt
    private short[][] tiles;
    // Dimension des obigen Arrays
    private int width, height;

    /**
     * Erzeugt ein neues Spielfeld. Die Anzahl der Kacheln sowie die Groesse des
     * Viewports (also Panel/Component/Image), auf welchen dieses Objekt
     * gezeichnet werden soll, muessen hierzu angegeben werden. Das Spielfeld
     * wird dann zufaellig mit Mauern, Gras und einem Eingang befuellt. In jeder
     * zweiten Reihe und jeder zweiten Spalte wird eine unzerstoerbare Mauer
     * erzeugt.
     * 
     * @param width Breite des Spielfelds in Kacheln
     * @param height Hoehe des Spielfelds in Kacheln
     * @param pixelWidth Breite des Spielfelds in Pixeln
     * @param pixelHeight Hoehe des Spielfelds in Pixeln
     */
    public BombermanLevel(int width, int height, int pixelWidth, int pixelHeight) {
        if (width < MINDIM || height < MINDIM) {
            width = Math.max(width, MINDIM);
            height = Math.max(height, MINDIM);
            System.err.println(DIMERROR);
        }
        // Berechne die Dimension des Feldes mit Hilfe der Pixel
        this.width = width;
        this.height = height;
        // Berechne die Größe der Felder in Pixel
        updateTileDimensions(pixelWidth, pixelHeight);
        // erstelle ein leeres Spielfeld
        tiles = new short[this.width][this.height];
        fillRandomly();
    }

    /**
     * Erzeugt ein neues Spielfeld. Die Groesse des Viewports (also
     * Panel/Component/Image), auf welchen dieses Objekt gezeichnet werden soll,
     * muss hierzu angegeben werden. Das Spielfeld wird aus dem uebergebem
     * short-Array erzeugt. Dieses sollte ausschliesslich mit Konstanten dieser
     * Klasse befuellt sein, um eine sinnvolle Auswertung der Kacheln zu
     * ermoeglichen. Bei weniger als 5x5 Kacheln wird ein zufaellig Feld
     * erzeugt.
     * 
     * Eine moegliche Anwendung ist das Auslesen eines Levels aus einer Datei.
     * 
     * @param tiles Die Kacheln in Form eines short-Arrays.
     * @param pixelWidth Breite des Spielfelds in Pixeln
     * @param pixelHeight Hoehe des Spielfelds in Pixeln
     */
    public BombermanLevel(short[][] tiles, int pixelWidth, int pixelHeight) {
        if (tiles.length < MINDIM || tiles[1].length < MINDIM) {
            width = MINDIM;
            height = MINDIM;
            System.err.println(DIMERROR);
            // Berechne die Größe der Felder in Pixel
            updateTileDimensions(pixelWidth, pixelHeight);
            // erstelle ein leeres Spielfeld
            tiles = new short[this.width][this.height];
            fillRandomly();
        }
        // Dimensionen sind in Ordnung, normales Prozedere
        this.tiles = tiles;
        this.width = tiles.length;
        this.height = tiles[1].length;
        // Berechne die Größe der Felder in Pixel
        updateTileDimensions(pixelWidth, pixelHeight);
    }

    /*
     * Methode, welche das Spielfeld initialisiert. Alle zwei Zeilen/Spalten
     * wird ein unzerstoerbarer Stein erzeugt. Ein 2x2 Block in den Ecken wird
     * freigelassen, um dem Spieler Bewegungsfreiraum zu ermoeglichen.
     */
    private void fillRandomly() {
        // erzeuge zunaechst einen unzerstoerbaren Rand
        for (int i = 0; i < width; i++) {
            tiles[i][0] = tiles[i][height - 1] = INDESTRUCTIBLE; // oben und
                                                                 // unten
        }
        for (int j = 1; j < height - 1; j++) {
            tiles[0][j] = tiles[width - 1][j] = INDESTRUCTIBLE; // links und
                                                                // rechts
        }

        // iteriere ueber die x- und dann die y-Koordinaten
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                if ((j % 2 == 0) && (i % 2 == 0)) // alle 2 Zeilen/Spalten..
                    tiles[i][j] = INDESTRUCTIBLE; // ... erzeuge einen
                                                  // unzerstoerbaren Block

                // gehe sicher, dass alle vier Spieler anfangs eingemauert sind
                else if ((i == 3 || i == width - 4)
                        && ((j >= 1 && j <= 3) || (j >= height - 4 && j <= height - 2)))
                    tiles[i][j] = STONE;
                else if ((j == 3 || j == height - 4)
                        && ((i >= 1 && i < 3) || (i > width - 4 && i <= width - 2)))
                    tiles[i][j] = STONE;

                // fuelle alle anderen Bloecke (ausser den Startzonen) zufaellig
                // mit Steinen
                else if ((i >= 4 && i <= width - 5)
                        || (j >= 4 && j <= height - 5)) {
                    if (Math.random() <= STONEPROBABILITY) // zufaellig Stein
                                                           // legen
                        tiles[i][j] = STONE;
                    // das folgende ist auskommentiert, da Bloecke anfangs
                    // sowieso den Wert 0 besitzen.
                    // else
                    // tiles[i][j] = GRASS;
                }
            }
        }
        // fuege zufaellig einen Ausgang ein. Berechne hierzu zunaechst eine
        // zufaellige Koordinate im Inneren des Spielfelds.
        // Wollen zumindest keinen Eingang im unmittelbar um die Spieler
        // bestehenden Bereich (je 3x3) haben, gerne aber im inneren Drittel
        int freeXSpace = Math.max(3, tiles.length / 3);
        int freeYSpace = Math.max(3, tiles[1].length / 3);
        int i = freeXSpace
                + (int) (Math.random() * (tiles.length - 2 * freeXSpace));
        int j = freeYSpace
                + (int) (Math.random() * (tiles[1].length - 2 * freeYSpace));
        if (tiles[i][j] == INDESTRUCTIBLE)
            i++; // hier wird keine
                 // unzerstoerbare Mauer sein
                 // (alle 2 Zeilen/Spalten)
        // Stelle fest, ob der Eingang sichtbar oder hinter einer Mauer
        // platziert wird
        if (tiles[i][j] == STONE)
            tiles[i][j] = HIDDENEXIT;
        else tiles[i][j] = EXIT;
        markAllForUpdate();
    }

    /**
     * Diese Methode liefert den Wert der Kachel, in welcher sich der Punkt
     * (pixelX, pixelY) befindet. Es handelt sich also um eine Angabe in Pixeln
     * und eben nicht den direkten Index auf das interne Feld. Befindet sich der
     * Punkt nicht innerhalb des Spielfeldes, wird der Wert einer
     * unzerstoerbaren Mauer zurueckgeliefert.
     * 
     * Die zurueckgelieferten Werte haben folgende Bedeutung:
     * 
     * GRASS = 0
     * 
     * STONE = 1
     * 
     * INDESTRUCTIBLE = 2
     * 
     * EXIT = 3
     * 
     * HIDDENEXIT = 4
     * 
     * @param pixelX X-Koordinate des Punktes, dessen beinhaltende Kachel
     * zurueckgeliefert wird
     * @param pixelY Y-Koordinate des Punktes, dessen beinhaltende Kachel
     * zurueckgeliefert wird
     * @return Die Kachel, in welcher sich der Punkt (pixelX, pixelY) befindet.
     */
    public short getTileByPixel(int pixelX, int pixelY) {
        return getTile(pixelX / tileDim, pixelY / tileDim);
    }

    /* Interne Methode. Von Aussen wird nur getTileByPixel genutzt. */
    private short getTile(int posX, int posY) {
        if (posX < 0 || posX >= width || posY < 0 || posY >= height)
            return INDESTRUCTIBLE;
        return (short) (tiles[posX][posY] & TILE);
    }

    /**
     * Diese Methode gibt an, ob die Kachel, in welcher sich der Punkt (pixelX,
     * pixelY) befindet, fest/unpassierbar ist.
     * 
     * @param pixelX X-Koordinate des Punktes, dessen beinhaltende Kachel auf
     * Passierbarkeit ueberprueft wird
     * @param pixelY Y-Koordinate des Punktes, dessen beinhaltende Kachel auf
     * Passierbarkeit ueberprueft wird
     * @return true, falls die Kachel, in welcher sich der Punkt (pixelX,
     * pixelY) befindet, eine Mauer darstellt, eine Bombe beinhaltet (B-Flag)
     * oder falls der Punkt ausserhalb des Spielfeldes liegt. false sonst.
     */
    public boolean isSolidByPixel(int pixelX, int pixelY) {
        return isSolid(pixelX / tileDim, pixelY / tileDim);
    }

    /* Interne Methode. Von Aussen wird nur isSolidByPixel genutzt. */
    private boolean isSolid(int posX, int posY) {
        if (posX < 0 || posX >= width || posY < 0 || posY >= height)
            return true;
        short tile = getTile(posX, posY);
        return (tile == INDESTRUCTIBLE || tile == STONE || tile == HIDDENEXIT || hasBomb(
                posX, posY));
    }

    /**
     * Diese Methode zerstoert die Kachel, in welcher sich der Punkt (pixelX,
     * pixelY) befindet. Die Angabe erfolgt in Pixeln. Befindet sich in der zu
     * zerstoerenden Kachel eine zerstoerbare Mauer oder ein Powerup, wird diese
     * in eine Gras-Kachel umgewandelt. Ein versteckter Eingang wird nach Aufruf
     * dieser Methode sichtbar. Alle anderen Blockarten bleiben unveraendert.
     * Die Flags werden uebernommen.
     * 
     * @param pixelX X-Koordinate des Punktes, dessen beinhaltende Kachel
     * zerstoert werden soll
     * @param pixelY Y-Koordinate des Punktes, dessen beinhaltende Kachel
     * zerstoert werden soll
     */
    public void destroyBlockByPixel(int pixelX, int pixelY) {
        destroyBlock(pixelX / tileDim, pixelY / tileDim);
    }

    /* Innere Methode. Von Aussen wird destroyBlockByPixel genutzt. */
    private void destroyBlock(int posX, int posY) {
        if (posX < 0 || posX >= width || posY < 0 || posY >= height)
            return;
        short tile = getTile(posX, posY);
        if (tile == STONE) {
            tiles[posX][posY] &= ~TILE;
            // tiles[posX][posY] |= GRASS;
            tiles[posX][posY] |= spawnPowerup();
            markForUpdate(posX, posY); // moechten Aenderung sehen
        } else if (tile == HIDDENEXIT) {
            tiles[posX][posY] &= ~TILE;
            tiles[posX][posY] |= EXIT;
            markForUpdate(posX, posY);
        }
        else if (tile == BOMBPLUS || tile == FIREPLUS){
            tiles[posX][posY] &= ~TILE;
            tiles[posX][posY] |= GRASS;
            markForUpdate(posX, posY);
        }
        // addFire(posX, posY);
    }
    
    /*
     * Kleine Hilfsmethode, welche zufaellig ein Powerup (oder eben nicht)
     * zurueck gibt. Rueckgabewert ist entweder ein Powerup oder ein
     * Gras-Block.
     */
    private static final short spawnPowerup(){
    	if(Math.random() <= BOPLPROB)
    		return BOMBPLUS;
    	if(Math.random() <= FIPLPROB)
    		return FIREPLUS;
    	//if(Math.random() <= NORRISPROB)
    	//	return CHUCKNORRIS;
    	return GRASS;
    }
    
    /**
     * Diese Methode updatet das BombermanLevel. Im Moment beschraenkt
     * sich dieses Update darauf, die Animationen fuer Powerups zu
     * aktualisieren.
     */
    public void update(){
    	animCounter--;
    	if(animCounter <= 0){
    		animCounter = ANIMDURATION;
    		animFrame = (animFrame + 1) % Texture.ITEMBOMBUP.length;
    	}
    }

    /**
     * Diese Methode zeichnet das Spielfeld auf den zum Graphics-Objekt
     * gehoerenden Bereich (zum Beispiel ein BufferedImage). Es wird immer nur
     * der Ausschnitt gezeichnet, in dem sich tatsaechlich etwas geaendert hat;
     * darueber muss das Spielfeld ueber die Methode markForUpdateByPixel
     * informiert werden.
     * 
     * @param g Das Graphics-Objekt, welches genutzt wird, um das Level zu
     * zeichnen.
     */
    public void draw(Graphics g) {
        short currentTile;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (drawAll || markedForUpdate(tiles[i][j])) {
                    if (!drawAll)
                        unmarkForUpdate(i, j); // if-Abfrage spart
                                               // unnoetige
                                               // Bitoperationen
                    currentTile = (short) (tiles[i][j] & TILE); // Betrachtung
                                                                // unabhaengig
                                                                // von D, B oder
                                                                // F Flags
                    if (currentTile == GRASS)
                        drawGrass(i, j, g);

                    else if (currentTile == STONE || currentTile == HIDDENEXIT)
                        drawStone(i, j, g);

                    else if (currentTile == INDESTRUCTIBLE)
                        drawIndestructible(i, j, g);

                    else if (currentTile == EXIT)
                        drawExit(i, j, g);
                    
                    else if (currentTile == BOMBPLUS)
                    	drawBombPlus(i, j, g);
                    
                    else if (currentTile == FIREPLUS)
                    	drawFirePlus(i, j, g);
                }
            }
        }
        // drawAll = false; // partielles Updaten von anderen Klassen nicht
        // unterstuetzt
    }

    // es folgen Methoden zum einzelnen Zeichnen von Kacheln.

    private void drawGrass(int posX, int posY, Graphics g) {
        Texture.GRASS.draw(posX * tileDim, posY * tileDim, tileDim, tileDim, g);
    }

    private void drawStone(int posX, int posY, Graphics g) {
        Texture.STONE.draw(posX * tileDim, posY * tileDim, tileDim, tileDim, g);
    }

    private void drawIndestructible(int posX, int posY, Graphics g) {
        Texture.BEDROCK.draw(posX * tileDim, posY * tileDim, tileDim, tileDim,
                g);
    }

    private void drawExit(int posX, int posY, Graphics g) {
        Texture.EXIT.draw(posX * tileDim, posY * tileDim, tileDim, tileDim, g);
    }
    
    private void drawBombPlus(int posX, int posY, Graphics g) {
        Texture.ITEMBOMBUP[animFrame].draw(posX * tileDim, posY * tileDim, tileDim, tileDim, g);
    }
    
    private void drawFirePlus(int posX, int posY, Graphics g) {
        Texture.ITEMFIRE[animFrame].draw(posX * tileDim, posY * tileDim, tileDim, tileDim, g);
    }

    // //////////////////////////////////////////////////////////////////////
    // es folgen Methoden zum Setzen/Entfernen/Ueberpruefen der D, B und F Flags
    // //////////////////////////////////////////////////////////////////////
    // D Flag (d.h. Aufforderung, die entsprechende Kachel zu zeichnen)
    // //////////////////////////////////////////////////////////////////////

    /**
     * Diese Methode setzt das D-Flag (teilt dem Level mit, dass diese Kachel
     * neu zu zeichnen ist) aller Kacheln, welche sich im Quadrat mit dem
     * Mittelpunkt (pixelX, pixelY) und der Seitenlaenge 2*pixelRadius + 1
     * befinden. Alle Angaben sind, wie aus der Namensgebung der Parameter
     * gefolgert werden kann, in Pixeln.
     * 
     * Eine sinnvolle Anwendung dieser Methode waere zum Beispiel bei der
     * Bewegung eines Spielerobjekts, welches sich zwischen mehreren Kacheln
     * befinden kann. Auch Bomben, die frei gelegt werden koennen, sollten sich
     * diese Methode bedienen.
     * 
     * Diese Methode ist ueberladen; fuer das Markieren einer einzelnen Kachel
     * bietet sich markForUpdateByPixel(int pixelX, int pixelY) an.
     * 
     * @param pixelX X-Koordinate des innersten Pixels, dessen ihn enthaltende
     * Kachel markiert werden soll
     * @param pixelY Y-Koordinate des innersten Pixels, dessen ihn enthaltende
     * Kachel markiert werden soll
     * @param pixelRadius Radius in Pixeln, innerhalb dessen Kacheln markiert
     * werden sollen.
     */
    public void markForUpdateByPixel(int pixelX, int pixelY, int pixelRadius) {
        markForUpdate(pixelX / tileDim, pixelY / tileDim, pixelRadius / tileDim);
    }

    /*
     * eigentliche Methode... fuer Anwender ist es von aussen sicherer, nur
     * obige Methode aufzurufen
     */
    private void markForUpdate(int posX, int posY, int radius) {
        // bestimme erst die Grenzen
        int leftX = posX - radius;
        int rightX = posX + radius;
        int topY = posY - radius;
        int bottomY = posY + radius;

        // verschiebe die Grenzen, falls sie ausserhalb des Spielfelds liegen
        if (leftX < 0)
            leftX = 0;
        if (rightX >= width)
            rightX = width - 1;
        if (topY < 0)
            topY = 0;
        if (bottomY >= height)
            bottomY = height - 1;

        // markiere dann alle Kacheln innerhalb der Grenzen
        for (int i = leftX; i <= rightX; i++) {
            for (int j = topY; j <= bottomY; j++)
                markForUpdate(i, j);
        }

    }

    /**
     * Diese Methode setzt das D-Flag der Kachel, in welcher sich der Punkt
     * (pixelX, pixelY) befindet. Die Angabe erfolgt in Pixeln.
     * 
     * Diese Methode ist ueberladen; fuer das Markieren mehrerer Kacheln bietet
     * sich u.U. markForUpdateByPixel(int pixelX, int pixelY, int pixelRadius)
     * an.
     * 
     * @param pixelX X-Koordinate des Punktes, dessen beinhaltende Kachel zum
     * Zeichnen markiert werden soll
     * @param pixelY Y-Koordinate des Punktes, dessen beinhaltende Kachel zum
     * Zeichnen markiert werden soll
     */
    public void markForUpdateByPixel(int pixelX, int pixelY) {
        markForUpdate(pixelX / tileDim, pixelY / tileDim);
    }

    /* Innere Methode. Von Aussen wird die markForUpdateByPixel-Methode genutzt */
    private void markForUpdate(int posX, int posY) {
        if (posX < 0 || posX >= width || posY < 0 || posY >= height)
            return;
        tiles[posX][posY] |= DRAW;
    }

    /**
     * @deprecated
     * 
     * Diese Methode entfernt das D-Flag der Kachel, in welcher sich der Punkt
     * (pixelX, pixelY) befindet. Die Angabe erfolgt in Pixeln. Die Methode ist
     * nicht wirklich veraltet, aber sollte in den seltensten Faellen verwendet
     * werden, da das D-Flag bei jedem Zeichnen automatisch entfernt wird.
     * 
     * @param pixelX X-Koordinate des Punktes, dessen beinhaltende Kachel nicht
     * mehr zum Zeichnen markiert werden soll
     * @param pixelY Y-Koordinate des Punktes, dessen beinhaltende Kachel nicht
     * mehr zum Zeichnen markiert werden soll
     */
    public void unmarkForUpdateByPixel(int pixelX, int pixelY) {
        unmarkForUpdate(pixelX / tileDim, pixelY / tileDim);
    }

    /*
     * Interne Methode, welche sowohl von obiger Methode als auch (bzw
     * insbesondere) von der draw-Methode genutzt wird.
     */
    private void unmarkForUpdate(int posX, int posY) {
        if (posX < 0 || posX >= width || posY < 0 || posY >= height)
            return;
        removeFire(posX, posY);
        tiles[posX][posY] &= ~DRAW;

    }

    /**
     * Diese Methode markiert alle Kacheln zum Zeichnen. Das Verhalten ist
     * aequivalent zum Markieren aller einzelnen Kacheln via
     * markForUpdateByPixel, aber weitaus effizienter und daher zu bevorzugen.
     * 
     * Eine sinnvolle Anwendung ist zum Beispiel beim Vergroessern oder
     * Verkleinern des Fensters, in welchem das Spielfeld gezeichnet wird.
     */
    public void markAllForUpdate() {
        drawAll = true;
    }

    /*
     * Liefert true, wenn das D-Flag einer Kachel gesetzt ist.
     */
    private boolean markedForUpdate(short tile) {
        return (tile & DRAW) != 0;
    }

    // //////////////////////////////////////////////////////////////////////
    // B Flag (d.h. Auskunft darueber, ob eine Bombe in diesem Feld liegt)
    // //////////////////////////////////////////////////////////////////////

    /**
     * Diese Methode setzt das B-Flag der Kachel, in welcher sich der Punkt
     * (pixelX, pixelY) befindet. Die Angabe erfolgt in Pixeln.
     * 
     * @param pixelX X-Koordinate des Punktes, in dessen beinhaltende Kachel
     * eine Bombe gelegt wurde
     * @param pixelY Y-Koordinate des Punktes, in dessen beinhaltende Kachel
     * eine Bombe gelegt wurde
     */
    public void putBomb(int posX, int posY) {
        if (posX < 0 || posX >= width || posY < 0 || posY >= height)
            return;
        tiles[posX][posY] |= BOMB;
    }

    /* Innere Methode. Von Aussen wird putBombByPixel genutzt. */
    public void putBombByPixel(int posX, int posY) {
        putBomb(posX / tileDim, posY / tileDim);
    }

    /**
     * Diese Methode entfernt das B-Flag der Kachel, in welcher sich der Punkt
     * (pixelX, pixelY) befindet. Die Angabe erfolgt in Pixeln.
     * 
     * @param pixelX X-Koordinate des Punktes, in dessen beinhaltender Kachel
     * eine Bombe entfernt wurde
     * @param pixelY Y-Koordinate des Punktes, in dessen beinhaltender Kachel
     * eine Bombe entfernt wurde
     */
    public void removeBombByPixel(int posX, int posY) {
        removeBomb(posX / tileDim, posY / tileDim);
    }

    /* Innere Methode. Von Aussen wird removeBombByPixel genutzt. */
    private void removeBomb(int posX, int posY) {
        if (posX < 0 || posX >= width || posY < 0 || posY >= height)
            return;
        tiles[posX][posY] &= ~BOMB;
    }

    /**
     * Diese Methode ueberprueft das B-Flag der Kachel, in welcher sich der
     * Punkt (pixelX, pixelY) befindet. Die Angabe erfolgt in Pixeln.
     * 
     * @param pixelX X-Koordinate des Punktes, dessen beinhaltende Kachel auf
     * eine gelegte Bombe ueberprueft wird.
     * @param pixelY Y-Koordinate des Punktes, dessen beinhaltende Kachel auf
     * eine gelegte Bombe ueberprueft wird.
     * @return true, falls eine Bombe in der Kachel, in welcher sich der Punkt
     * (pixelX, pixelY) befindet, liegt. false sonst.
     */
    public boolean hasBombByPixel(int posX, int posY) {
        return hasBomb(posX / tileDim, posY / tileDim);
    }

    /* Innere Methode. Von Aussen wird hastBombByPixel genutzt. */
    private boolean hasBomb(int posX, int posY) {
        if (posX < 0 || posX >= width || posY < 0 || posY >= height)
            return false;
        return (tiles[posX][posY] & BOMB) != 0;
    }

    // //////////////////////////////////////////////////////////////////////
    // F Flag (d.h. Auskunft darueber, ob eine Bombe in diesem Feld liegt)
    // //////////////////////////////////////////////////////////////////////

    /**
     * Diese Methode setzt das F-Flag der Kachel, in welcher sich der Punkt
     * (pixelX, pixelY) befindet. Die Angabe erfolgt in Pixeln.
     * 
     * @param pixelX X-Koordinate des Punktes, dessen beinhaltende Kachel als
     * brennend/explodierend markiert werden soll
     * @param pixelY Y-Koordinate des Punktes, dessen beinhaltende Kachel als
     * brennend/explodierend markiert werden soll
     */
    public void addFireByPixel(int posX, int posY) {
        addFire(posX / tileDim, posY / tileDim);
    }

    /* Innere Methode. Von Aussen wird nur addFireByPixel genutzt. */
    private void addFire(int posX, int posY) {
        if (posX < 0 || posX >= width || posY < 0 || posY >= height)
            return;
        tiles[posX][posY] |= FIRE;

    }

    /**
     * Diese Methode entfernt das F-Flag der Kachel, in welcher sich der Punkt
     * (pixelX, pixelY) befindet. Die Angabe erfolgt in Pixeln.
     * 
     * @param pixelX X-Koordinate des Punktes, dessen beinhaltende Kachel nicht
     * mehr als brennend/explodierend markiert werden soll
     * @param pixelY Y-Koordinate des Punktes, dessen beinhaltende Kachel nicht
     * mehr als brennend/explodierend markiert werden soll
     */
    public void removeFireByPixel(int posX, int posY) {
        removeFire(posX / tileDim, posY / tileDim);
    }

    /* Innere Methode. Von Aussen wird nur removeFireByPixel genutzt. */
    private void removeFire(int posX, int posY) {
        if (posX < 0 || posX >= width || posY < 0 || posY >= height)
            return;
        tiles[posX][posY] &= ~FIRE;
    }

    /**
     * Diese Methode ueberprueft das F-Flag der Kachel, in welcher sich der
     * Punkt (pixelX, pixelY) befindet. Die Angabe erfolgt in Pixeln.
     * 
     * @param pixelX X-Koordinate des Punktes, dessen beinhaltende Kachel auf
     * eine Explosion geprueft werden soll.
     * @param pixelY Y-Koordinate des Punktes, dessen beinhaltende Kachel auf
     * eine Explosion geprueft werden soll.
     * @return true, falls sich eine Flamme/Explosiom in der Kachel, in welcher
     * sich der Punkt (pixelX, pixelY) befindet, befindet. false sonst.
     */
    public boolean hasFireByPixel(int posX, int posY) {
        return hasFire(posX / tileDim, posY / tileDim);
    }

    /* Innere Methode. Von Aussen wird nur hasFireByPixel genutzt. */
    private boolean hasFire(int posX, int posY) {
        if (posX < 0 || posX >= width || posY < 0 || posY >= height)
            return false;
        return (tiles[posX][posY] & FIRE) != 0;
    }

    // //////////////////////////////////////////////////////////////////////
    // Ende der Methoden zum Setzen/Entfernen/Ueberpruefen der D, B und F Flags
    // //////////////////////////////////////////////////////////////////////
}// Ende der Klasse BombermanLevel
