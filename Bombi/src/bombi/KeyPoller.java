package bombi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Timer;

/**
 * @author: tobi
 * 
 * Ein speziell für Spiele implementierter KeyListener. Dieser setzt bei Tastendruck das zur entsprechenden Taste gehörende Flag auf 1, beim Loslassen der Taste
 * eben jenes auf 0. Damit lassen sich komfortabel mehrere Tasten gleichzeitig überprüfen. Zudem wird das Problem der anfänglichen Verzögerung bei längerem
 * Tastendruck umgangen.
 * 
 * Die ursprüngliche Idee des KeyPollers wurde bereits in anderen Spielen verwendet. Der Ansatz, keyReleased-Events zu verzögern, stammt von
 * http://www.arco.in-berlin.de/keyevent.html und wurde an unsere Bedürfnisse angepasst. Das zugrunde liegende Problem wurde beim Testen unter Linux entdeckt
 * (mit Bitoperationen ergab sich ein sehr hoher Rechenaufwand und dadurch verzögerte Bewegung, zum Teil blieb die Figur stehen. Mit einem boolean[] lief das
 * Spiel vergleichsweise flüssig, aber stockte immer wieder).
 */

public class KeyPoller implements KeyListener {

    private byte[] keyDown; // in diesem Feld werden die gedrückten Tasten gespeichert, d.h. das korrespondiere Bit wird auf 1 oder 0 gesetzt

    private final Map<Integer, TimedKey> map = new HashMap<Integer, TimedKey>(4);
    /*
     * in diesem Hash-Feld werden, mit dem keyCode als Schlüssel, Timer erzeugt, welche nach Start & Ablauf der ihnen übergebenen Zeit (atm immer 1 Millisek.)
     * das eigentlich für keyReleased angedachte Verhalten auslösen (ergo das Setzten des entsprechenden Bits auf 0).
     */

    private final int timerDelay = 1; // in Millisekunden

    /*
     * Diese Klasse ist im Groben ein Wrapper für die Timer-Klasse. Das gewünschte Verhalten bei Ablauf des Timers ist direkt in dieser Klasse enthalten
     * (konkret in der Methode actionPerformed, welcher der Timer nach Ablauf der Zeit aufruft). Der eigentliche Zweck ist, den Aufruf der keyReleased-Methode
     * zu verzögern. Das ist unter Windows z.B. irrelevant, unter Linux und angeblich Solaris sehr wichtig, da bei längerem Tastendruck ständig keyPressed und
     * keyReleased Events ausgelöst werden. Mit diesem Timer umgehen wir dies. Zum einen wollen wir so unnötige Rechenoperationen vermeiden, zum anderen
     * (wichtiger) ein "Stocken" des Spiels durch die ständigen Events.
     */
    private class TimedKey implements ActionListener {
        private final int keyCode; // keyCode für die entsprechende Taste
        private final Timer releaseTimer = new Timer(timerDelay, this);

        private TimedKey(int keyCode) {
            this.keyCode = keyCode;
            // map.put(code, this);
            // dies war im verlinkten Original nicht auskommentiert, der Übersicht halber findet diese Operation aber nach dem Erzeugen statt
        }

        // führt lediglich die restart-Methode des Timers aus (Wrapper)
        public void restartTimer() {
            releaseTimer.restart();
        }

        // führt lediglich die stop-Methode des Timers aus (Wrapper)
        public void stopTimer() {
            releaseTimer.stop();
        }

        @Override
        // Methode wird ausgeführt, sobald der Timer 0 erreicht.
        public void actionPerformed(ActionEvent e) {
            releaseTimer.stop(); // halte zunächst den Timer an, da ein weiteres Zählen nur Rechenleistung verschlingt
            map.remove(keyCode); // entferne das Timer-Objekt aus der Hash-Map (einzige Referenz...)
            keyDown[keyCode / 8] &= ~(1 << (keyCode % 8)); // das alte Verhalten der keyReleased Methode. Setze da zu diesem keyCode gehörige Bit auf 0
            // System.out.println("released"); // für Debugging-Zwecke, da das angestrebte Verhalten optisch nicht leicht vom alten unterscheidbar ist
        }
    }

    /**
     * Erstellt ein neues KeyPoller Objekt.
     * 
     * @param size : Die Anzahl an Bits, welche reserviert werden soll. KeyCodes >= size werden verworfen.
     */
    public KeyPoller(int size) {
        keyDown = new byte[size / 8];
    }

    /**
     * Erstellt ein neues KeyPoller-Objekt, welches bis zu 256 Tasten verarbeiten kann.
     */
    public KeyPoller() {
        this(256); // default soll 256 Bit fassen (32 byte)
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        int keyCode = arg0.getKeyCode();
        // überprüfe, ob der keyCode der gedrückten Taste außerhalb des reservierten Bereiches liegt
        if (keyCode < 0 || keyCode >= keyDown.length * 8) return;
        // ansonsten setze das entsprechende Flag
        keyDown[keyCode / 8] |= (1 << (keyCode % 8));
        // und erzeuge ein zugehöriges TimedKey-Objekt bzw stoppe den Timer
        TimedKey tk = map.get(keyCode);
        if (tk == null) {
            map.put(keyCode, new TimedKey(keyCode));
            // System.out.println("pressed");
        } else tk.stopTimer();
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        int keyCode = arg0.getKeyCode();
        // vom Verhalten her analog zu keyPressed
        if (keyCode < 0 || keyCode >= keyDown.length * 8) return;
        // keyPressed[code] = false;
        TimedKey tk = map.get(keyCode);
        if (tk != null) tk.restartTimer();
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
        if (keyCode < 0 || keyCode >= keyDown.length * 8) return false;
        return ((keyDown[keyCode / 8] & (1 << (keyCode % 8))) != 0);
    }

    /**
     * Überprüft, ob die Taste zu einem gegeben KeyCode (z.B. KeyEvent.VK_ALT) im Moment nicht gedrückt wird. KeyCodes außerhalb des speicherbaren Bereichs
     * werden ignoriert (return false).
     * 
     * @param keyCode: Der KeyCode der Taste, zu welcher die Abfrage gestellt wird.
     * @return: false falls die zu keyCode gehörende Taste gedrückt wird.
     */
    public boolean isKeyUp(int keyCode) {
        return !isKeyDown(keyCode);
    }
}
