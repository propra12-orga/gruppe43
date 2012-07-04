package bombi;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;

public class ServerBombiGui extends BombiGui {

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
    private ServerSocket serverSock;
    private Socket socket;
    private BufferedReader fromClient;
    private BufferedWriter toClient;
    private NetworkKeyReceiver keyReceiver;

    private static final int PRECISEUPDATE = 20;
    private int puCounter = PRECISEUPDATE;
    private double seed = Math.random();

    public ServerBombiGui(JFrame frame) {
        super(true, -1, frame);
    }

    @Override
    protected void initializeInput() {
        try {
            initializeNetwork();
        } catch (IOException e) {
            System.err.println("Unable to open Server.");
            e.printStackTrace();
            System.exit(-1);
        }
        keyPoller = new NetworkKeyPoller(toClient);
        addKeyListener(keyPoller);
    }

    private void initializeNetwork() throws IOException {
        serverSock = new ServerSocket(1337);
        System.out.println("Waiting for client..");
        socket = serverSock.accept();

        toClient = new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream()));
        fromClient = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));

        // sende das Level an den neu verbundenen Client
        String packet = LEVEL + "\n" + BombermanLevel.createPacket(bLevel)
                + END + "\n";
        toClient.write(packet);
        toClient.flush();
        keyReceiver = new NetworkKeyReceiver();
        new Thread(new NetworkManager()).start();
        System.out.println("... successfully connected!");
    }

    private void removeClient() {
        if (toClient != null) {
            try {
                toClient.close();
            } catch (IOException e) {
                System.err.println("Unable to cleanly finish communication.");
                e.printStackTrace();
            } finally {
                toClient = null;
            }
        }
        if (fromClient != null) {
            try {
                fromClient.close();
            } catch (IOException e) {
                System.err.println("Unable to cleanly finish communication.");
                e.printStackTrace();
            } finally {
                fromClient = null;
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Unable to cleanly finish communication.");
                e.printStackTrace();
            } finally {
                socket = null;
            }
        }
    }

    @Override
    protected void bombermanUpdate() {
        super.bombermanUpdate();
        if (puCounter >= 0)
            puCounter--;
        else {
            puCounter = PRECISEUPDATE;
            sendPlayer(player1.getPosX(), player1.getPosY(), 1);
            sendPlayer(player2.getPosX(), player2.getPosY(), 2);
        }
        // sende dem Client neue Kacheln (idR Powerups)
        send(bLevel.createNewTilePacket());
    }

    @Override
    protected void handleKeyboard() {
        // Ueberpruefe Tastatureingaben des Hosts
        if (keyPoller.isKeyDown(KeyEvent.VK_LEFT)) {
            player1.Direction(-bLevel.getTileDim() / stepsize, 0);
            stepCount++;
        } else if (keyPoller.isKeyDown(KeyEvent.VK_RIGHT)) {
            player1.Direction(bLevel.getTileDim() / stepsize, 0);
            stepCount++;
        }
        if (keyPoller.isKeyDown(KeyEvent.VK_UP)) {
            player1.Direction(0, -bLevel.getTileDim() / stepsize);
            stepCount++;
        } else if (keyPoller.isKeyDown(KeyEvent.VK_DOWN)) {
            player1.Direction(0, bLevel.getTileDim() / stepsize);
            stepCount++;
        } else if (keyPoller.isKeyDown(KeyEvent.VK_SPACE)) {
            playAudio.playSound("Put");
            int posX = player1.getPosXForBomb();
            int posY = player1.getPosYForBomb();
            if (!bLevel.hasBombByPixel(posX, posY) && player1.bombplantable()) {
                bombsP1.add(new Bomben((posX / bLevel.getTileDim())
                        * bLevel.getTileDim(), (posY / bLevel.getTileDim())
                        * bLevel.getTileDim(), player1.maxradius(), player1,
                        bLevel));
                sendBomb(posX, posY, player1.maxradius());
                player1.addcurrentbombs();
            }
        }
        if (keyReceiver.isKeyDown(KeyEvent.VK_LEFT)) {
            player2.Direction(-bLevel.getTileDim() / stepsize, 0);
            stepCount++;
        } else if (keyReceiver.isKeyDown(KeyEvent.VK_RIGHT)) {
            player2.Direction(bLevel.getTileDim() / stepsize, 0);
            stepCount++;
        }
        if (keyReceiver.isKeyDown(KeyEvent.VK_UP)) {
            player2.Direction(0, -bLevel.getTileDim() / stepsize);
            stepCount++;
        } else if (keyReceiver.isKeyDown(KeyEvent.VK_DOWN)) {
            player2.Direction(0, bLevel.getTileDim() / stepsize);
            stepCount++;
        } else if (keyReceiver.isKeyDown(KeyEvent.VK_SPACE)) {
            playAudio.playSound("Put");
            int posX = player2.getPosXForBomb();
            int posY = player2.getPosYForBomb();
            if (!bLevel.hasBombByPixel(posX, posY) && player2.bombplantable()) {
                bombsP2.add(new Bomben((posX / bLevel.getTileDim())
                        * bLevel.getTileDim(), (posY / bLevel.getTileDim())
                        * bLevel.getTileDim(), player2.maxradius(), player2,
                        bLevel));
                sendBomb(posX, posY, player2.maxradius());
                player2.addcurrentbombs();
            }

        }
    }

    private void send(String packet) {
        if (toClient != null) {
            try {
                toClient.write(packet + "\n");
                toClient.flush();
            } catch (IOException e) {
                System.err
                        .println("Error while trying to communicate with Client.");
                e.printStackTrace();
                removeClient();
            }
        }
    }

    private void sendBomb(int posX, int posY, int radius) {
        double pX = posX / (double) bLevel.getTileDim();
        double pY = posY / (double) bLevel.getTileDim();
        send(BOMB + radius + POSX + pX + POSY + pY);
    }

    private void sendPlayer(int posX, int posY, int id) {
        double pX = posX / (double) bLevel.getTileDim();
        double pY = posY / (double) bLevel.getTileDim();
        send(PLAYER + id + POSX + pX + POSY + pY);
    }

    private class NetworkManager implements Runnable {

        private boolean running = true;

        public void stopThread() {
            running = false;
        }

        @Override
        public void run() {
            String input;
            while (running) {
                try {
                    input = fromClient.readLine().trim();
                    if (input.startsWith(PRESSED)) {
                        int keyCode = Integer.parseInt(input.substring(2));
                        keyReceiver.keyPressed(keyCode);
                    } else if (input.startsWith(RELEASED)) {
                        int keyCode = Integer.parseInt(input.substring(2));
                        keyReceiver.keyReleased(keyCode);
                    }
                } catch (IOException e) {
                    System.err
                            .println("Error while trying to communicate with Client.");
                    e.printStackTrace();
                    stopThread();
                    removeClient();
                } catch (NullPointerException e) {
                    System.err.println("Client has been removed.");
                    e.printStackTrace();
                    stopThread();
                }
            }
        }

    }

    public static void main(String[] args) {
        JFrame serverFrame = new JFrame("Server");
        ServerBombiGui serverBGui = new ServerBombiGui(serverFrame);
        serverFrame.add(serverBGui);
        serverFrame.pack();
        serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        serverFrame.setVisible(true);
        new Thread(serverBGui).start();
    }

}
