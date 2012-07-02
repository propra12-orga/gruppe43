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
 * Eine Verallgemeinerung des frueheren KeyPollers. In dieser Variante wird
 * offen gelassen, was bei Tastendruck bzw. Loslassen einer Taste passiert.
 * 
 */

public abstract class AbstractTimedKeyListener implements KeyListener {

    private final Map<Integer, TimedKey> map = new HashMap<Integer, TimedKey>(4);
    /*
     * in diesem Hash-Feld werden, mit dem keyCode als Schlüssel, Timer erzeugt,
     * welche nach Start & Ablauf der ihnen übergebenen Zeit (atm immer 1
     * Millisek.) das eigentlich für keyReleased angedachte Verhalten auslösen
     * (ergo das Setzten des entsprechenden Bits auf 0).
     */

    private final int timerDelay = 1; // in Millisekunden

    /*
     * Diese Klasse ist im Groben ein Wrapper für die Timer-Klasse. Das
     * gewünschte Verhalten bei Ablauf des Timers ist direkt in dieser Klasse
     * enthalten (konkret in der Methode actionPerformed, welcher der Timer nach
     * Ablauf der Zeit aufruft). Der eigentliche Zweck ist, den Aufruf der
     * keyReleased-Methode zu verzögern. Das ist unter Windows z.B. irrelevant,
     * unter Linux und angeblich Solaris sehr wichtig, da bei längerem
     * Tastendruck ständig keyPressed und keyReleased Events ausgelöst werden.
     * Mit diesem Timer umgehen wir dies. Zum einen wollen wir so unnötige
     * Rechenoperationen vermeiden, zum anderen (wichtiger) ein "Stocken" des
     * Spiels durch die ständigen Events.
     */
    private class TimedKey implements ActionListener {
        private final int keyCode; // keyCode für die entsprechende Taste
        private final Timer releaseTimer = new Timer(timerDelay, this);

        private TimedKey(int keyCode) {
            this.keyCode = keyCode;
            // map.put(code, this);
            // dies war im verlinkten Original nicht auskommentiert, der
            // Übersicht halber findet diese Operation aber nach dem Erzeugen
            // statt
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
            releaseTimer.stop(); // halte zunächst den Timer an, da ein weiteres
                                 // Zählen nur Rechenleistung verschlingt
            map.remove(keyCode); // entferne das Timer-Objekt aus der Hash-Map
                                 // (einzige Referenz...)
            keyReleased(keyCode); // Aktion beim Loslassen der Taste
            // System.out.println("released"); // für Debugging-Zwecke, da das
            // angestrebte Verhalten optisch nicht leicht vom alten
            // unterscheidbar ist
        }
    }

    protected void createTimedKey(int keyCode) {
        map.put(keyCode, new TimedKey(keyCode));
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        int keyCode = arg0.getKeyCode();
        TimedKey tk = map.get(keyCode); // suche nach bereits vorhandenen Timern
        if (tk == null) {
            keyPressed(keyCode);
            createTimedKey(keyCode);
        } else tk.stopTimer();
    }

    protected abstract void keyPressed(int keyCode);

    @Override
    public void keyReleased(KeyEvent arg0) {
        int keyCode = arg0.getKeyCode();
        // vom Verhalten her analog zu keyPressed
        TimedKey tk = map.get(keyCode);
        if (tk != null)
            tk.restartTimer();
    }

    protected abstract void keyReleased(int keyCode);

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub
        // diese Methode wird atm nicht weiter benötigt.
    }
}