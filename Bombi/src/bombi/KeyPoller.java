package bombi;

import java.awt.event.KeyListener;

/**
 * @author: tobi
 * 
 * Ein speziell für Spiele implementierter KeyListener. Dieser setzt bei
 * Tastendruck das zur entsprechenden Taste gehörende Flag auf 1, beim Loslassen
 * der Taste eben jenes auf 0. Damit lassen sich komfortabel mehrere Tasten
 * gleichzeitig überprüfen. Zudem wird das Problem der anfänglichen Verzögerung
 * bei längerem Tastendruck umgangen.
 * 
 * Die ursprüngliche Idee des KeyPollers wurde bereits in anderen Spielen
 * verwendet. Der Ansatz, keyReleased-Events zu verzögern, stammt von
 * http://www.arco.in-berlin.de/keyevent.html und wurde an unsere Bedürfnisse
 * angepasst. Das zugrunde liegende Problem wurde beim Testen unter Linux
 * entdeckt (mit Bitoperationen ergab sich ein sehr hoher Rechenaufwand und
 * dadurch verzögerte Bewegung, zum Teil blieb die Figur stehen. Mit einem
 * boolean[] lief das Spiel vergleichsweise flüssig, aber stockte immer wieder).
 */

public class KeyPoller extends AbstractTimedKeyListener{

    private byte[] keyDown; // in diesem Feld werden die gedrückten Tasten
                            // gespeichert, d.h. das korrespondiere Bit wird auf
                            // 1 oder 0 gesetzt
    private int keysDown; // die Anzahl der momentan gedrückten Tasten

    /**
     * Erstellt ein neues KeyPoller Objekt.
     * 
     * @param size : Die Anzahl an Bits, welche reserviert werden soll. KeyCodes
     * >= size werden verworfen.
     */
    public KeyPoller(int size) {
        keyDown = new byte[size / 8];
        keysDown = 0;
    }

    /**
     * Erstellt ein neues KeyPoller-Objekt, welches bis zu 256 Tasten
     * verarbeiten kann.
     */
    public KeyPoller() {
        this(256); // default soll 256 Bit fassen (32 byte)
    }

    @Override
    protected void keyPressed(int keyCode) {
        if (keyCode < 0 || keyCode >= keyDown.length * 8)
            return;
        keyDown[keyCode / 8] |= (1 << (keyCode % 8));
        keysDown++;
    }

    @Override
    protected void keyReleased(int keyCode) {
        if (keyCode < 0 || keyCode >= keyDown.length * 8)
            return;
        // setze das zu diesem keyCode gehörige Bit auf 0
        keyDown[keyCode / 8] &= ~(1 << (keyCode % 8));
        keysDown--;
    }

    /**
     * Überprüft, ob die Taste zu einem gegeben KeyCode (z.B. KeyEvent.VK_ALT)
     * im Moment gedrückt wird. KeyCodes außerhalb des speicherbaren Bereichs
     * werden ignoriert (return false).
     * 
     * @param keyCode: Der KeyCode der Taste, zu welcher die Abfrage gestellt
     * wird.
     * @return: true falls die zu keyCode gehörende Taste gedrückt wird.
     */
    public boolean isKeyDown(int keyCode) {
        // false falls keyCode zu groß für den reservierten Bereich
        if (keyCode < 0 || keyCode >= keyDown.length * 8)
            return false;
        return ((keyDown[keyCode / 8] & (1 << (keyCode % 8))) != 0);
    }

    /**
     * Überprüft, ob die Taste zu einem gegeben KeyCode (z.B. KeyEvent.VK_ALT)
     * im Moment nicht gedrückt wird. KeyCodes außerhalb des speicherbaren
     * Bereichs werden ignoriert (return false).
     * 
     * @param keyCode: Der KeyCode der Taste, zu welcher die Abfrage gestellt
     * wird.
     * @return: false falls die zu keyCode gehörende Taste gedrückt wird.
     */
    public boolean isKeyUp(int keyCode) {
        return !isKeyDown(keyCode);
    }

    /**
     * Überprüft, ob irgendeine Taste im Moment gedrückt wird.
     * 
     * @return true falls eine beliebige Taste gedrückt wird.
     */
    public boolean isAnyKeyDown() {
        return keysDown > 0;
    }
}
