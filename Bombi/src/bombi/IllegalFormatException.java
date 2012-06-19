package bombi;

/**
 * Diese Klasse stellt Exceptions bereit, welche (falls geworfen) dem Benutzer
 * signalisieren, dass das Format (atm lediglich bezogen auf *.map-Dateien)
 * ungueltig ist.
 * 
 * @author tobi
 * 
 */
public class IllegalFormatException extends Exception {

    // automatisch erzeugt
    private static final long serialVersionUID = 1L;

    private static final String IFE = "IllegalFormatException\n";

    /**
     * Erzeugt eine IllegalFormatException ohne genauere Fehlerangabe.
     */
    public IllegalFormatException() {
        super(IFE);
    }

    /**
     * Erzeugt eine IllegalFormatException mit genaueren Angaben zum Fehler,
     * welche in Form einer Zeichenkette uebergeben werden.
     * 
     * @param errorMsg Genauere Angaben zum Fehler (verursachende Zeile oder
     * aehnliches)
     */
    public IllegalFormatException(final String errorMsg) {
        super(IFE + errorMsg);
    }
}
