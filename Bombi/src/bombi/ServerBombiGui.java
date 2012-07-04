package bombi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.JFrame;

public class ServerBombiGui extends BombiGui {

    // Protokoll
    public static final String PLAYER = "PL";
    public static final String LEVEL = "LV";
    public static final String PRESSED = "PR";
    public static final String RELEASED = "RL";
    public static final String END = "EN";

    // Netzwerk In- und Output
    private ServerSocket serverSock;
    private List<Player> player;
    private List<Socket> socket;
    private List<BufferedReader> fromClients;
    private List<BufferedWriter> toClients;
    private List<NetworkKeyReceiver> keyReceiver;

    public ServerBombiGui(boolean multiplayer, int tut, JFrame frame) {
        super(multiplayer, tut, frame);
    }

    @Override
    protected void initializeInput() {
        // initializeNetwork();
        // keyPoller = new NetworkKeyPoller(socket);
    }

    // public BombiGui(String mp) {
    // super();
    // try {
    // if (mp.equals("Server")) {
    // initializeLevel("test.map");
    // initializeNetwork(true);
    // } else {
    // initializeLevel("test.maps");
    // initializeNetwork(false);
    // }
    // // erzeuge unseren KeyPoller
    // keyPoller = new NetworkKeyPoller(socket);
    // addKeyListener(keyPoller);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // initializeGraphics();
    //
    // if (mp.equals("Server")) {
    // player1 = new Player(bLevel, bLevel.getTileDim(),
    // bLevel.getTileDim());
    // player2 = new Player(bLevel, bLevel.getTileDim()
    // * (bLevel.getWidth() - 2) - 1, bLevel.getTileDim()
    // * (bLevel.getHeight() - 2) - 1);
    // } else {
    // player2 = new Player(bLevel, bLevel.getTileDim(),
    // bLevel.getTileDim());
    // player1 = new Player(bLevel, bLevel.getTileDim()
    // * (bLevel.getWidth() - 2) - 1, bLevel.getTileDim()
    // * (bLevel.getHeight() - 2) - 1);
    // }
    //
    // bombsP1 = new ArrayList<Bomben>();
    // bombsP2 = new ArrayList<Bomben>();
    // setFocusable(true);
    // this.setSize(width, height);
    // playAudio.playSound("Fight");
    // this.multiplayer = true;
    // }

    private void initializeNetwork() throws IOException {
        player = new ArrayList<Player>();
        socket = new ArrayList<Socket>();
        fromClients = new ArrayList<BufferedReader>();
        toClients = new ArrayList<BufferedWriter>();
        keyReceiver = new ArrayList<NetworkKeyReceiver>();
        serverSock = new ServerSocket(1337);
        new Thread(new AcceptThread()).start();
    }

    private int[] nextSpawnPoint(int index) {
        int[] spawnPoint = new int[2];
        switch (index) {
        case 0:
            spawnPoint[0] = bLevel.getTileDim() * (bLevel.getWidth() - 2);
            spawnPoint[1] = bLevel.getTileDim();
            break;
        case 1:
            spawnPoint[0] = bLevel.getTileDim();
            spawnPoint[1] = bLevel.getTileDim() * (bLevel.getHeight() - 2);
            break;
        case 2:
            spawnPoint[0] = bLevel.getTileDim() * (bLevel.getWidth() - 2);
            spawnPoint[1] = bLevel.getTileDim() * (bLevel.getHeight() - 2);
            break;
        default:
            int loopCounter = 0;
            while (true) {
                loopCounter++;
                spawnPoint[0] = (int) (Math.random() * bLevel.getWidth() * bLevel
                        .getTileDim());
                spawnPoint[1] = (int) (Math.random() * bLevel.getHeight() * bLevel
                        .getTileDim());
                if (!bLevel.isSolidByPixel(spawnPoint[0], spawnPoint[1])
                        || loopCounter > 100)
                    break;
            }
        }
        return spawnPoint;
    }

    private void removeClient(int index) {
        if (index < 0)
            return;
        if (index <= toClients.size()) {
            try {
                toClients.get(index).close();
            } catch (IOException e) {
                System.err
                        .println("Cannot properly terminate connection to Client.");
                e.printStackTrace();
            }
            toClients.remove(index);
        }
        if (index <= fromClients.size()) {
            try {
                fromClients.get(index).close();
            } catch (IOException e) {
                System.err
                        .println("Cannot properly terminate connection to Client.");
                e.printStackTrace();
            }
            fromClients.remove(index);
        }
        if (index <= socket.size()) {
            try {
                socket.get(index).close();
            } catch (IOException e) {
                System.err
                        .println("Cannot properly terminate connection to Client.");
                e.printStackTrace();
            }
            socket.remove(index);
        }
        if (index <= player.size())
            player.remove(index);
        if (index <= keyReceiver.size())
            keyReceiver.remove(index);
    }

    private class AcceptThread implements Runnable {

        @Override
        public void run() {
            int index;
            int[] spawnPoint;
            while (true) {
                index = socket.size(); // aktueller Client
                try {

                    socket.add(index, serverSock.accept());
                    spawnPoint = nextSpawnPoint(index);
                    player.add(index, new Player(bLevel, spawnPoint[0],
                            spawnPoint[1]));
                    toClients.add(index, new BufferedWriter(
                            new OutputStreamWriter(socket.get(index)
                                    .getOutputStream())));
                    fromClients.add(index, new BufferedReader(
                            new InputStreamReader(socket.get(index)
                                    .getInputStream())));

                    // sende das Level an den neu verbundenen Client
                    String packet = "LV\n"
                            + BombermanLevel.createPacket(bLevel) + "EN\n";
                    toClients.get(index).write(packet);
                    toClients.get(index).flush();
                    keyReceiver.add(index, new NetworkKeyReceiver());
                    new Thread(new NetworkManager(index)).start();
                } catch (IOException e) {
                    System.err
                            .println("Error while trying to communicate with Client.");
                    e.printStackTrace();
                    removeClient(index);
                } catch (NoSuchElementException e) {
                    System.err
                            .println("Error while creating BombermanLevel packet.");
                    e.printStackTrace();
                    removeClient(index);
                }
            }
        }
    }

    private class NetworkManager implements Runnable {

        private int index;
        private boolean running = true;

        public NetworkManager(int index) {
            this.index = index;
        }

        public void stopThread() {
            running = false;
        }

        @Override
        public void run() {
            String input;
            while (running) {
                try {
                    input = fromClients.get(index).readLine().trim();
                    if (input.startsWith("PR")) {
                        int keyCode = Integer.parseInt(input.substring(2));
                        keyReceiver.get(index).keyPressed(keyCode);
                    } else if (input.startsWith("RL")) {
                        int keyCode = Integer.parseInt(input.substring(2));
                        keyReceiver.get(index).keyReleased(keyCode);
                    }
                } catch (IOException e) {
                    System.err
                            .println("Error while trying to communicate with Client.");
                    e.printStackTrace();
                    stopThread();
                }
            }
            removeClient(index);
        }

    }

}
