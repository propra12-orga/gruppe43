package bombi;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author: tobi
 * 
 * Ein speziell für Spiele implementierter KeyListener. Dieser setzt bei Tastendruck das zur entsprechenden Taste gehörende Flag auf 1, beim Loslassen der Taste
 * eben jenes auf 0. Damit lassen sich komfortabel mehrere Tasten gleichzeitig überprüfen. Zudem wird das Problem der anfänglichen Verzögerung bei längerem
 * Tastendruck umgangen.
 */

public class KeyPoller implements KeyListener {

    private boolean[] keyPressed;

    /**
     * Erstellt ein neues KeyPoller Objekt.
     * 
     * @param size : Die Anzahl an bools, welche reserviert werden soll. KeyCodes >= size werden verworfen.
     */
    public KeyPoller(int size) {
        keyPressed = new boolean[size];
    }

    /**
     * Erstellt ein neues KeyPoller-Objekt, welches bis zu 256 Tasten verarbeiten kann.
     */
    public KeyPoller() {
        this(256); // default soll 256 Bit fassen (32 byte)
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        // überprüfe, ob der keyCode der gedrückten Taste außerhalb des reservierten Bereiches liegt
        if (arg0.getKeyCode() < 0 || arg0.getKeyCode() >= keyPressed.length) return;
        // ansonsten setze das entsprechende Flag
        keyPressed[arg0.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // vom Verhalten her analog zu keyPressed
        if (arg0.getKeyCode() < 0 || arg0.getKeyCode() >= keyPressed.length) return;
        keyPressed[arg0.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub
        // diese Methode wird atm nicht weiter benötigt.
    }

    /**
     * Überprüft, ob die Taste zu einem gegeben KeyCode (z.B. KeyEvent.VK_ALT) im Moment gedrückt wird. KeyCodes außerhalb des speicherbaren Bereichs werden
     * ignoriert (return false).
     * 
     * @param keyCode: Der KeyCode der Taste, zu welcher die Abfrage gestellt wird.
     * @return: true falls die zu keyCode gehörende Taste gedrückt wird.
     */
    public boolean isKeyDown(int keyCode) {
        // false falls keyCode zu groß für den reservierten Bereich
        if (keyCode < 0 || keyCode >= keyPressed.length) return false;
        return keyPressed[keyCode];
    }

    /**
     * Überprüft, ob die Taste zu einem gegeben KeyCode (z.B. KeyEvent.VK_ALT) im Moment nicht gedrückt wird. KeyCodes außerhalb des speicherbaren Bereichs
     * werden ignoriert (return false).
     * 
     * @param keyCode: Der KeyCode der Taste, zu welcher die Abfrage gestellt wird.
     * @return: false falls die zu keyCode gehörende Taste gedrückt wird.
     */
    public boolean isKeyUp(int keyCode) {
        // false falls keyCode zu groß für den reservierten Bereich
        if (keyCode < 0 || keyCode >= keyPressed.length) return false;
        return !keyPressed[keyCode];
    }
}
