package bombi;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JFrame;

/**
 * Diese Klasse stellt eine "Erweiterung" der Klasse BombiGui dar. Wie aus dem
 * Namen herzuleiten ist, handelt es sich um den Client einer
 * Multiplayer-Session. Es koennen lediglich zwei Spieler gegeneinander antreten
 * (Server vs. Client).
 * 
 * @author tobi
 * 
 */
public class ClientBombiGui extends BombiGui {

    /*
     * Es folgen die Schluesselwoerter fuer das Protokoll. Die gleichen
     * Konstanten finden sich der Einfachheit halber in der Klasse
     * ServerBombiGui
     */
    /**
     * Dieser String informiert den Client darueber, dass die folgenden Daten
     * aktualisierte Werte fuer den Spieler darstellen.
     */
    public static final String PLAYER = "PL";
    /**
     * Dieser String informiert den Client darueber, dass eine Bombe an der im
     * Reststring codierten Stelle platziert werden soll.
     */
    public static final String BOMB = "BM";
    /**
     * Schluessel, welcher vor der X-Position eines Objekts platziert wird.
     * Hierbei handelt es sich entweder um Bomben oder Spieler.
     */
    public static final String POSX = "X";
    /**
     * Schluessel, welcher vor der Y-Position eines Objekts platziert wird.
     * Hierbei handelt es sich entweder um Bomben oder Spieler.
     */
    public static final String POSY = "Y";
    /**
     * Dieser String informiert den Client darueber, dass die folgenden Zeilen
     * das Spielfeld enthalten. Die folgende Codierung ist analog zu der fuer
     * externe Spielfelder (siehe auch test.map).
     */
    public static final String LEVEL = "LV";
    /**
     * Informiert den Client ueber das Ende eines Paket. Im Moment nur bei
     * Uebertragung des Spielfelds genutzt, alle anderen Pakete bestehen aus
     * einer Zeile.
     */
    public static final String END = "EN";
    /**
     * Informiert den Empfaenger darueber, dass der entfernte PC eine Taste
     * gedrueckt hat; im Reststring sollte sich der keyCode dieser Taste finden.
     */
    public static final String PRESSED = "PR";
    /**
     * Informiert den Empfaenger darueber, dass der entfernte PC eine Taste
     * losgelassen hat; im Reststring sollte sich der keyCode dieser Taste
     * finden.
     */
    public static final String RELEASED = "RL";
    /**
     * Informiert den Client darueber, dass die folgenden Daten aktualisierte
     * Werte fuer eine Kachel des Levels darstellen.
     */
    public static final String TILE = "TI";

    // Netzwerk In- und Output
    private Socket socket;
    private BufferedReader fromServer;
    private BufferedWriter toServer;
    private NetworkKeyReceiver keyReceiver;
    private String host;
    private int port;

    private double seed;

    public ClientBombiGui(JFrame frame, String host, int port) {
        super(true, -1, frame);
        this.host = host;
        this.port = port;
        initializeInput();
    }

    @Override
    protected void initializeInput() {
        if (host == null || port <= 0)
            return;
        try {
            initializeNetwork();
        } catch (IOException e) {
            System.err.println("Unable to connect to Server.");
            e.printStackTrace();
            System.exit(-1);
        }
        keyPoller = new NetworkKeyPoller(toServer);
        addKeyListener(keyPoller);
    }

    private void initializeNetwork() throws IOException {
        socket = new Socket(host, port);
        fromServer = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        toServer = new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream()));
        keyReceiver = new NetworkKeyReceiver();
        new Thread(new NetworkManager()).start();
    }

    protected void handleKeyboard() {
        // Ueberpruefe Tastatureingaben des Hosts
        if (keyPoller.isKeyDown(KeyEvent.VK_LEFT)) {
            player2.Direction(-bLevel.getTileDim() / stepsize, 0);
            stepCount++;
        } else if (keyPoller.isKeyDown(KeyEvent.VK_RIGHT)) {
            player2.Direction(bLevel.getTileDim() / stepsize, 0);
            stepCount++;
        }
        if (keyPoller.isKeyDown(KeyEvent.VK_UP)) {
            player2.Direction(0, -bLevel.getTileDim() / stepsize);
            stepCount++;
        } else if (keyPoller.isKeyDown(KeyEvent.VK_DOWN)) {
            player2.Direction(0, bLevel.getTileDim() / stepsize);
            stepCount++;
        }
        if (keyReceiver.isKeyDown(KeyEvent.VK_LEFT)) {
            player1.Direction(-bLevel.getTileDim() / stepsize, 0);
            stepCount++;
        } else if (keyReceiver.isKeyDown(KeyEvent.VK_RIGHT)) {
            player1.Direction(bLevel.getTileDim() / stepsize, 0);
            stepCount++;
        }
        if (keyReceiver.isKeyDown(KeyEvent.VK_UP)) {
            player1.Direction(0, -bLevel.getTileDim() / stepsize);
            stepCount++;
        } else if (keyReceiver.isKeyDown(KeyEvent.VK_DOWN)) {
            player1.Direction(0, bLevel.getTileDim() / stepsize);
            stepCount++;
        }
    }

    /*
     * Diese innere Klasse erzeugt einen Thread, welcher die vom Server
     * empfangenen Pakete verarbeitet. Durch die Auslagerung in einen Thread
     * wirkt das Spiel fuer den Client weiterhin fluessig.
     */
    private class NetworkManager implements Runnable {

        @Override
        public void run() {
            String input; // temporaerer Speicher fuer Pakete
            while (true) {
                try {
                    // entferne whitespaces, welche nicht vorkommen sollten
                    input = fromServer.readLine().trim();

                    // Fall 1: Server uebermittelt Level
                    if (input.startsWith(LEVEL)) {
                        System.out.print("Receiving Level... ");
                        // erzeuge eine temporaere Datei
                        String filename = "map" + File.pathSeparator + "temp"
                                + this.hashCode();
                        FileWriter fw = new FileWriter(filename);
                        // speichere alle Zeilen in diese Datei
                        while (!(input = fromServer.readLine().trim())
                                .equals(END)) {
                            fw.write(input + "\n");
                        }
                        // schließe den Filewriter sauber
                        fw.flush();
                        fw.close();
                        // lese nun aus der temporaeren Datei das Level
                        bLevel = new BombermanLevel(LevelParser.parseMap("temp"
                                + this.hashCode(), false), width, height);
                        bLevel.setSpawnPowerups(false);
                        player1.setBombermanLevel(bLevel);
                        player2.setBombermanLevel(bLevel);
                        // und loesche die Datei
                        new File(filename).delete();
                        System.out.println("... level successfully received!");
                    }

                    // Fall 2: Server uebermittelt (genaue) Spielerposition
                    else if (input.startsWith(PLAYER)) {
                        // erste Zahl (ergo ID des Spielers) beginnt nach
                        // Schluesselwort ...
                        int beginIndex = PLAYER.length();
                        // .. und endet vor dem Schluessel zur X-Position
                        int endIndex = input.indexOf(POSX, beginIndex);
                        int playerID = Integer.parseInt(input.substring(
                                beginIndex, endIndex));

                        // als naechstes folgt die X-Position
                        beginIndex = endIndex + POSX.length();
                        endIndex = input.indexOf(POSY, beginIndex);
                        // die Koordinaten sind unabhaenging von der
                        // Levelgroesse
                        int posX = (int) (Double.parseDouble(input.substring(
                                beginIndex, endIndex)) * bLevel.getTileDim());
                        // und dann die Y-Position
                        beginIndex = endIndex + POSY.length();
                        int posY = (int) (Double.parseDouble(input
                                .substring(beginIndex)) * bLevel.getTileDim());

                        // setze den Spieler anhand der ID
                        if (playerID == 1) {
                            player1.setPosX(posX);
                            player1.setPosY(posY);
                        } else {
                            player2.setPosX(posX);
                            player2.setPosY(posY);
                        }
                    }

                    // Fall 3: Server sendet eine neue Bombe
                    else if (input.startsWith(BOMB)) {
                        // nach dem Schluesselwort folgt der Radius
                        int beginIndex = BOMB.length();
                        int endIndex = input.indexOf(POSX, beginIndex);
                        int radius = Integer.parseInt(input.substring(
                                beginIndex, endIndex));
                        // dann die X-Position
                        beginIndex = endIndex + POSX.length();
                        endIndex = input.indexOf(POSY, beginIndex);
                        // die Koordinaten sind unabhaenging von der
                        // Levelgroesse
                        int posX = (int) (Double.parseDouble(input.substring(
                                beginIndex, endIndex)) * bLevel.getTileDim());
                        // und zuletzt die Y-Position
                        beginIndex = endIndex + POSY.length();
                        int posY = (int) (Double.parseDouble(input
                                .substring(beginIndex)) * bLevel.getTileDim());
                        // die Bombe wird Clientseitig immer fuer Spieler 2
                        // gesetzt. Dies macht aber keinen Unterschied.
                        bombsP2.add(new Bomben((posX / bLevel.getTileDim())
                                * bLevel.getTileDim(), (posY / bLevel
                                .getTileDim()) * bLevel.getTileDim(), radius,
                                player2, bLevel));
                        System.out.println("Placed remote bomb");
                    }

                    // Fall 4: Eine Taste wurde gedrueckt
                    else if (input.startsWith(PRESSED)) {
                        int keyCode = Integer.parseInt(input.substring(PRESSED
                                .length()));
                        keyReceiver.keyPressed(keyCode);
                    }

                    // Fall 5: Eine Taste wurde losgelassen
                    else if (input.startsWith(RELEASED)) {
                        int keyCode = Integer.parseInt(input.substring(PRESSED
                                .length()));
                        keyReceiver.keyReleased(keyCode);
                    }

                    // Fall 6: Eine neue Kachel wurde verschickt
                    else if (input.startsWith(TILE)) {
                        // nach dem Schluesselwort folgt die Kachel
                        int beginIndex = TILE.length();
                        int endIndex = input.indexOf(POSX, beginIndex);
                        short tile = Short.parseShort(input.substring(
                                beginIndex, endIndex));
                        // dann die X-Position
                        beginIndex = endIndex + POSX.length();
                        endIndex = input.indexOf(POSY, beginIndex);
                        int posX = Integer.parseInt(input.substring(beginIndex,
                                endIndex));
                        // und zuletzt die Y-Position
                        beginIndex = endIndex + POSY.length();
                        int posY = Integer
                                .parseInt(input.substring(beginIndex));
                        // aktualisiere die empfangene Kachel
                        bLevel.setTile(tile, posX, posY);
                        System.out.println("Placed remote bomb");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Corrupt package.");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.err
                            .println("Error while communicating with Server.");
                    e.printStackTrace();
                    System.exit(-1);
                } catch (NullPointerException e) {
                    System.err
                            .println("Error while communicating with Server.");
                    e.printStackTrace();
                    System.exit(-1);
                } catch (IllegalFormatException e) {
                    System.err.println("Error while receiving Level.");
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame clientFrame = new JFrame("Client");
        ClientBombiGui clientBGui = new ClientBombiGui(clientFrame,
                "localhost", 1337);
        clientFrame.add(clientBGui);
        clientFrame.pack();
        clientFrame.setVisible(true);
        new Thread(clientBGui).start();
    }

}
