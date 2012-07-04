package bombi;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Diese Klasse stellt statische Methoden bereit, welche eine externe Textdatei
 * auslesen und versuchen, aus dieser ein Level zu erzeugen. Eine beispielhafte
 * Datei ist unter map/test.map zu finden. Das Format wird dort naeher
 * erlaeutert.
 * 
 * @author tobi
 * 
 */
public class LevelParser {

    /**
     * Diese Methode liest ein zweidimensionales short-Feld aus einer externen
     * Datei aus, welches zu einem gueltigen BombermanLevel verarbeitet werden
     * kann. Hierbei ist jede Datei zulaessig, welche im Klartext vorliegt und
     * dem in map/test.map spezifiziertem Format folgt.
     * 
     * @param path Der Pfad zur externen Datei, welche verarbeitet werden soll
     * @param fullPath Gibt an, ob es sich bei path um einen absoluten oder
     * relativen Pfad handelt. Es wird von einem absoluten Pfad ausgegangen,
     * wenn fullPath true ist. Ansonsten wird die Datei in map/path gesucht.
     * @return Zweidimensionales Feld, welches zu einem gueltigen BombermanLevel
     * verarbeitet werden kann.
     * @throws FileNotFoundException Wird geworfen, falls die Datei, welche
     * ueber path (und fullPath) angegeben wurde, nicht im Dateisystem gefunden
     * werden kann.
     * @throws IllegalFormatException Wird geworfen, falls die Datei, welche
     * ueber path (und fullPath) angegeben wurde, nicht dem gueltigen Format
     * (siehe hierzu map/test.map) folgt.
     * @throws IOException Wird geworfen, falls beim Einlesen der Datei, welche
     * ueber path (und fullPath) angegeben wurde, ein Fehler auftritt (Datei
     * wird waehrend des Einlesens geloescht oder Aehnliches).
     */
    public static short[][] parseMap(String path, boolean fullPath)
            throws FileNotFoundException, IllegalFormatException, IOException {
        BufferedReader fileIn = null;
        if (!fullPath)// nur Dateiname angegeben, map soll in ./map/ liegen
            path = "map" + File.pathSeparator + path;
        fileIn = new BufferedReader(new FileReader(path));
        Dimension dim = parseDim(fileIn); // lese zunaechst Breite und Hoehe aus
        fileIn.close();
        fileIn = new BufferedReader(new FileReader(path)); // starte von vorne
        short[][] map = parseMap(fileIn, dim); // lese Level-Informationen aus
        fileIn.close();
        return map;
    }

    /**
     * Diese Methode liest ein zweidimensionales short-Feld aus einer externen
     * Datei aus, welches zu einem gueltigen BombermanLevel verarbeitet werden
     * kann. Hierbei ist jede Datei zulaessig, welche im Klartext vorliegt und
     * dem in map/test.map spezifiziertem Format folgt.
     * 
     * @param path Der relative Pfad zur externen Datei, welche verarbeitet
     * werden soll. Konkret wird die Datei in map/path gesucht.
     * @return Zweidimensionales Feld, welches zu einem gueltigen BombermanLevel
     * verarbeitet werden kann.
     * @throws FileNotFoundException Wird geworfen, falls die Datei, welche
     * ueber path angegeben wurde, nicht im Dateisystem gefunden werden kann.
     * @throws IllegalFormatException Wird geworfen, falls die Datei, welche
     * ueber path angegeben wurde, nicht dem gueltigen Format (siehe hierzu
     * map/test.map) folgt.
     * @throws IOException Wird geworfen, falls beim Einlesen der Datei, welche
     * ueber path angegeben wurde, ein Fehler auftritt (Datei wird waehrend des
     * Einlesens geloescht oder Aehnliches).
     */
    public static short[][] parseMap(String path) throws FileNotFoundException,
            IllegalFormatException, IOException {
        return parseMap(path, false);
    }

    /*
     * Hilfsmethode, welche die Breite und Hoehe aus der externen Datei ausliest
     * und diese mittels eines Dimension-Objekts zurueck gibt.
     */
    private static Dimension parseDim(final BufferedReader fileIn)
            throws IllegalFormatException, IOException {
        int lineCounter = 0;
        String temp;
        boolean dim = false; // signalisiert, ob zuvor [DIM] eingelesen wurde
        while ((temp = fileIn.readLine()) != null) {
            lineCounter++;
            temp = removeComment(temp).trim(); // entferne zeilenweise blanks
                                               // und comments
            if (!temp.equals("")) { // ueberspringe leere Zeilen
                if (dim) {// in dieser Zeile muss die Dimension folgen
                    // , und ; sind die einzigen Trennzeichen hier
                    int comma = temp.indexOf(',');
                    if (comma < 0) // es gibt kein , --> ungueltiges Format
                        throw new IllegalFormatException(errorMsg(lineCounter,
                                DIM_ERROR));
                    int semicolon = temp.indexOf(';', comma + 1);
                    if (semicolon < 0) // kein ; --> ungueltiges Format
                        throw new IllegalFormatException(errorMsg(lineCounter,
                                DIM_ERROR));
                    int w = Integer.parseInt(temp.substring(0, comma).trim());
                    int h = Integer.parseInt(temp.substring(comma + 1,
                            semicolon).trim());
                    return new Dimension(w, h);
                }
                if (temp.equals(DIM)) // signalisiert: B,H; folgt
                    dim = true;
            }
        }
        if (!dim)
            throw new IllegalFormatException(DIM_NOT_FOUND);
        throw new IllegalFormatException(errorMsg(lineCounter, DIM_ERROR));
    }

    /*
     * Hilfsmethode, welche das angefragte zweidimensionale Array erzeugt. Die
     * Breite und Hoehe dieses Arrays wird ueber dim angegeben.
     */
    private static short[][] parseMap(final BufferedReader fileIn,
            final Dimension dim) throws IllegalFormatException, IOException {
        int lineCounter = 0;
        String temp;
        boolean level = false; // true falls [LEVEL] eingelesen wurde
        short[][] map = new short[dim.width][dim.height];

        // suche zunaecht nach [LEVEL]
        while ((temp = fileIn.readLine()) != null) {
            lineCounter++;
            temp = removeComment(temp).trim();
            if (temp.equals(LEVEL)) {
                level = true;
                break;
            }
        }
        if (!level)
            throw new IllegalFormatException(LEVEL_NOT_FOUND);
        for (int i = 0; i < dim.height; i++) {
            while ((temp = fileIn.readLine()) != null) {
                lineCounter++;
                temp = removeComment(temp).trim();
                if (!temp.equals("")) // suche naechste nichtleere Zeile
                    break;
            }
            if (temp == null)
                throw new IllegalFormatException(errorMsg(lineCounter,
                        LEVEL_ERROR));
            int beginIndex = 0;
            int endIndex;
            short tile;
            // parse alle bis auf den letzten Eintrag
            for (int j = 0; j < dim.width - 1; j++) {
                endIndex = temp.indexOf(',', beginIndex);
                if (endIndex < 0)
                    throw new IllegalFormatException(errorMsg(lineCounter,
                            LEVEL_ERROR));
                tile = Short.parseShort(temp.substring(beginIndex, endIndex)
                        .trim());
                if (!BombermanLevel.isValidTile(tile))
                    throw new IllegalFormatException(tileErrorMsg(lineCounter,
                            tile));
                map[j][i] = tile;
                beginIndex = endIndex + 1;
            }
            // der letzte Eintrag endet mit ;
            endIndex = temp.indexOf(';', beginIndex);
            if (endIndex < 0)
                throw new IllegalFormatException(errorMsg(lineCounter,
                        LEVEL_ERROR));
            tile = Short
                    .parseShort(temp.substring(beginIndex, endIndex).trim());
            if (!BombermanLevel.isValidTile(tile))
                throw new IllegalFormatException(
                        tileErrorMsg(lineCounter, tile));
            map[dim.width - 1][i] = tile;
        }
        return map;
    }

    /*
     * Kleine Hilfsmethode, welche in einer gegebener Zeichenkette nach // sucht
     * (signalisiert Kommentare) und alles ab dort verwirft. Ergo wird
     * "[DIM] //hallo" zu "[DIM] " verarbeitet etc.
     */
    private static final String removeComment(final String line) {
        int commentPos = line.indexOf(COMMENT);
        if (commentPos == -1) // -1 falls // nicht auftritt
            return line;
        return line.substring(0, commentPos);
    }

    // zwei Methoden, welche das Erzeugen von Fehlermeldungen vereinfachen

    private static final String errorMsg(final int line, final String msg) {
        return String.format(ERROR_FORMAT, line, msg);
    }

    private static final String tileErrorMsg(final int line, final short tile) {
        return errorMsg(line, String.format(BLOCK_ERROR, tile));
    }

    // es folgen ein paar konstante Zeichenketten

    private static final String DIM = "[DIM]";
    private static final String LEVEL = "[LEVEL]";
    private static final String COMMENT = "//";

    private static final String ERROR_FORMAT = "ERROR at line %d: %s\n";
    private static final String LEVEL_ERROR = "Level not properly specified.";
    private static final String LEVEL_NOT_FOUND = "ERROR: [LEVEL] marker not found.\n";
    private static final String BLOCK_ERROR = "%d is not a valid block.";

    private static final String DIM_ERROR = "Dimensions not properly specified.";
    private static final String DIM_NOT_FOUND = "ERROR: [DIM] marker not found.\n";

}
