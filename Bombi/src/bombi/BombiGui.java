package bombi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class BombiGui extends JComponent implements Runnable {
    protected int width = 640;
    protected int height = 480;
    private int gameWidth, gameHeight;

    private static final long SECOND = 1000000000; // eine Sekunde in
                                                   // Nanosekunden
    private static final long SLEEPTIME = SECOND / 60; // 60 FPS

    private boolean running = true;
    protected static int stepCount = 0;
    // im Speicher gehaltenes Bild & zugehoeriges Graphics-Objekt für
    // Double-Buffering
    private BufferedImage dbImage;
    private Graphics2D dbg;
    // das gleiche fuer das Spielfeld
    private BufferedImage gameImage;
    private Graphics2D gameG;

    // managet die Tastatur.. stellt im Grunde eine auf Polling basierende
    // Lösung dar (statt Interrupts)
    protected KeyPoller keyPoller;

    // Netzwerk In- und Output
    private Socket socket;
    private BufferedReader fromSocket;
    private BufferedWriter toSocket;

    // Liste für die Bomben
    protected List<Bomben> bombsP1;
    protected List<Bomben> bombsP2;

    private boolean multiplayer;
    private int tut;
    private JFrame frame;
    Player player1, player2;
    BombermanLevel bLevel;
    Menu m;
    int fps = 0; // wird durch den main-loop gesetzt
    int tutmsg;
    int tutcounter = 420;

    public BombiGui(boolean multiplayer, int tut, JFrame frame) {
        super();
        this.frame = frame;
        this.tut = tut;
        if (tut >= 1 && tut <= 6) {
            initializeLevel("/tut" + tut + ".map");
            tutmsg = tut;
        } else {
            initializeLevel("/empty.map");
        }
        initializeGraphics();
        initializeInput();

        // erzeuge immer den ersten Spieler
        player1 = new Player(bLevel, bLevel.getTileDim(), bLevel.getTileDim());
        this.multiplayer = multiplayer;
        if (multiplayer)// den Zweiten nur fuer MP
            player2 = new Player(bLevel, bLevel.getTileDim()
                    * (bLevel.getWidth() - 2), bLevel.getTileDim()
                    * (bLevel.getHeight() - 2));

        bombsP1 = new ArrayList<Bomben>();
        bombsP2 = new ArrayList<Bomben>();
        setFocusable(true);
        this.setSize(width, height);
        playAudio.playSound("Fight");
    }

    public BombiGui(JFrame frame) {
        this(false, 0, frame);
    }

    private void initializeLevel(String pathToMap) {
        try {
            bLevel = new BombermanLevel(LevelParser.parseMap(pathToMap), width,
                    height);
        } catch (Exception e) {
            bLevel = new BombermanLevel(17, 9, width, height);
        }
    }

    protected void initializeInput() {
        // erzeuge unseren KeyPoller
        keyPoller = new KeyPoller();
        addKeyListener(keyPoller);

    }

    private void initializeGraphics() {
        // erzeuge das Bild, auf welches das eigentliche Spielfeld gezeichnet
        // wird
        gameWidth = bLevel.getTileDim() * bLevel.getWidth();
        gameHeight = bLevel.getTileDim() * bLevel.getHeight();
        gameImage = new BufferedImage(gameWidth, gameHeight,
                BufferedImage.TYPE_3BYTE_BGR);
        gameG = (Graphics2D) gameImage.getGraphics();
        // erzeuge die Objekte für Doublebuffering
        dbImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        dbg = (Graphics2D) dbImage.getGraphics();
    }

    /**
     * Double Buffering wobei ein Image erstellt wird und im Hintergrund das
     * nächste Bild gemalt wird.
     */
    public void paintBuffer() {

        if (player1.exit())
            Texture.P1WIN.draw(bLevel.getTileDim() * 3,
                    bLevel.getTileDim() * 4, bLevel.getTileDim() * 10,
                    bLevel.getTileDim() * 4, dbg);
        if (player1.death()) {
            Texture.P1DIED.draw(bLevel.getTileDim() * 3,
                    bLevel.getTileDim() * 4, bLevel.getTileDim() * 10,
                    bLevel.getTileDim() * 4, dbg);

            return;
        }
        if (multiplayer && player2 != null) {
            if (player2.exit())
                Texture.P2WIN.draw(bLevel.getTileDim() * 3,
                        bLevel.getTileDim() * 4, bLevel.getTileDim() * 10,
                        bLevel.getTileDim() * 4, dbg);
            if (player2.death()) {
                Texture.P2DIED.draw(bLevel.getTileDim() * 3,
                        bLevel.getTileDim() * 4, bLevel.getTileDim() * 10,
                        bLevel.getTileDim() * 4, dbg);
                return;
            }
        }

        // zeichne das Lvel
        bLevel.draw(gameG);

        // zeichne die Bombe für Spieler 1
        for (int i = 0; i < bombsP1.size(); i++) {
            bombsP1.get(i).draw(gameG);
        }
        // zeichne die Bombe für Spieler 2
        for (int i = 0; i < bombsP2.size(); i++) {
            bombsP2.get(i).draw(gameG);
        }

        // zeichne zuletzt den Spieler
        player1.draw(gameG);

        if (multiplayer && player2 != null)
            player2.draw(gameG);

        dbg.setColor(Color.BLACK);
        dbg.fillRect(0, 0, width, height);
        dbg.drawImage(gameImage, (width - gameWidth) / 2,
                (height - gameHeight) / 2, null);
        // zeige fps an
        dbg.setColor(Color.ORANGE);
        dbg.drawString(fps + "FPS", this.getWidth() - 50, 20);

        // Zeigt Punktestand für Spieler 1 an.
        dbg.setColor(Color.ORANGE);
        dbg.drawString("Punkte Spieler 1: " + player1.points(), this.getWidth()
                - 19 * (this.getWidth() / 20), 20);

        // Zeigt den Punktestand für Spieler2 an.
        if (multiplayer && player2 != null) {
            dbg.setColor(Color.ORANGE);
            dbg.drawString("Punkte Spieler 2: " + player2.points(),
                    this.getWidth() - 8 * (this.getWidth() / 20), 20);
        }

        // Zeichnet die Nachrichten fürs Tutorial
        if (tutmsg == 1 && tutcounter > 0)
            Texture.TUTMSG1.draw(bLevel.getTileDim() * 3,
                    bLevel.getTileDim() * 4, bLevel.getTileDim() * 10,
                    bLevel.getTileDim() * 4, dbg);
        else if (tutmsg == 2 && tutcounter > 0)
            Texture.TUTMSG2.draw(bLevel.getTileDim() * 3,
                    bLevel.getTileDim() * 4, bLevel.getTileDim() * 10,
                    bLevel.getTileDim() * 4, dbg);
        else if (tutmsg == 3 && tutcounter > 0)
            Texture.TUTMSG3.draw(bLevel.getTileDim() * 3,
                    bLevel.getTileDim() * 4, bLevel.getTileDim() * 10,
                    bLevel.getTileDim() * 4, dbg);
        else if (tutmsg == 4 && tutcounter > 0)
            Texture.TUTMSG4.draw(bLevel.getTileDim() * 3,
                    bLevel.getTileDim() * 4, bLevel.getTileDim() * 10,
                    bLevel.getTileDim() * 4, dbg);
        else if (tutmsg == 5 && tutcounter > 0)
            Texture.TUTMSG5.draw(bLevel.getTileDim() * 3,
                    bLevel.getTileDim() * 4, bLevel.getTileDim() * 10,
                    bLevel.getTileDim() * 4, dbg);
        else if (tutmsg == 6 && tutcounter > 0)
            Texture.TUTMSG6.draw(bLevel.getTileDim() * 3,
                    bLevel.getTileDim() * 4, bLevel.getTileDim() * 10,
                    bLevel.getTileDim() * 4, dbg);

    }

    private void gameDrawBuffer() {
        Graphics2D g;
        try {
            g = (Graphics2D) this.getGraphics();
            if (g != null && dbImage != null) {
                g.drawImage(dbImage, 0, 0, null);
                g.dispose();
            }
        } catch (Exception e) {
            System.err.println("Error while handling graphics context!");
            e.printStackTrace();
        }
    }

    /**
     * Methode, welche die bevorzugte Groesse dieser JComponent zurueck gibt.
     * 
     * @return: Dimension WIDTH x HEIGHT, welche fest codiert sind.
     */
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    // SoundManager instanz (Audios einlesen)
    SoundManager playAudio = new SoundManager() {
        public void initSounds() {
            sounds.add(new Sound("End", Sound.getURL("/End.wav")));
            sounds.add(new Sound("Bumm", Sound.getURL("/Bumm.wav")));
            sounds.add(new Sound("Put", Sound.getURL("/Put.wav")));
            sounds.add(new Sound("Step", Sound.getURL("/Step.wav")));
            sounds.add(new Sound("Fight", Sound.getURL("/Fight.wav")));
            sounds.add(new Sound("Pickup", Sound.getURL("/Pickup.wav")));

        }
    };

    /**
     * Startet den Spieleloop.
     */
    public void run() {
        long beforeUpdate, updateTime;

        // Vars zum FPS zaehlen
        long fpsCounter;
        int fps = 0;
        fpsCounter = System.nanoTime();// messen sp�ter, ob eine Sek. vergangen
                                       // ist

        /*
         * while-schleife war vorher auf "running".hab die Abfrage ob ein
         * spieler das EXIT erreicht hat wenn ja m�sste die while schleife
         * abrechen und das spiel beendet werden. Gibt bestimmt eine bessere
         * m�glichkeit mit etwas wie stop()...
         */
        while (running) {

            beforeUpdate = System.nanoTime();

            // update alle Objekte, Spieler etc
            bombermanUpdate();

            // zeichne alle Objekte auf den Buffer
            paintBuffer();
            // zeichne den Buffer sichtbar fuer den Nutzer
            gameDrawBuffer();
            // haben gezeichnet, ergo..
            fps++;

            if (System.nanoTime() - fpsCounter >= SECOND) {
                this.fps = fps; // update die GLOBALE Variable mit den aktuellen
                                // fps
                fps = 0; // lokaler Counter auf 0
                fpsCounter = System.nanoTime();
            }

            updateTime = System.nanoTime() - beforeUpdate;

            if (updateTime < SLEEPTIME) {// warte ein wenig....
                try {
                    Thread.sleep((SLEEPTIME - updateTime) / 1000000);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * Methode, welche (atm) 60x pro Sekunde aufgerufen wird und (nicht
     * sichtbare) Updates an der Spielumgebung durchführt. Hierzu gehören das
     * Einlesen von Tastatureingaben, Bewegen des Spielerobjekts, Herunterzählen
     * des Countdowns der Bomben etc.
     */
    protected void bombermanUpdate() {
        if (getWidth() != width || getHeight() != height)
            rescale();

        if (tutcounter > 0)
            tutcounter--;
        
        if(player1.hitByFire()){
        	player1.removeLife();}
        
        if(player2 != null && player2.hitByFire()){
        	player2.removeLife();}
        
        if (player1.bombItem()) {
            playAudio.playSound("Pickup");
            player1.addmaxbomb();
            bLevel.destroyBlockByPixel(player1.getPosX() + bLevel.getTileDim()
                    / 2, player1.getPosY() + bLevel.getTileDim() / 2);
        }
        if (player1.explosionItem()) {
            playAudio.playSound("Pickup");
            player1.addradius();
            bLevel.destroyBlockByPixel(player1.getPosX() + bLevel.getTileDim()
                    / 2, player1.getPosY() + bLevel.getTileDim() / 2);
        }
        if (player1.heartItem()) 	
        { playAudio.playSound("Pickup");
        	player1.addLife();
        	bLevel.destroyBlockByPixel(player1.getPosX()+bLevel.getTileDim()/2, player1.getPosY()+bLevel.getTileDim()/2);
        }   
        if (player1.boostItem())
        { playAudio.playSound("Pickup");
        	player1.removeStepsize();
        	bLevel.destroyBlockByPixel(player1.getPosX()+bLevel.getTileDim()/2, player1.getPosY()+bLevel.getTileDim()/2);
        }

        if (player2 != null && player2.bombItem()) {
            playAudio.playSound("Pickup");
            player2.addmaxbomb();
            bLevel.destroyBlockByPixel(player2.getPosX() + bLevel.getTileDim()
                    / 2, player2.getPosY() + bLevel.getTileDim() / 2);
        }

        if (player2 != null && player2.explosionItem()) {
            playAudio.playSound("Pickup");
            player2.addradius();
            bLevel.destroyBlockByPixel(player2.getPosX() + bLevel.getTileDim()
                    / 2, player2.getPosY() + bLevel.getTileDim() / 2);
        }
        if (player2 != null && player2.heartItem())
        { playAudio.playSound("Pickup");
        	player2.addLife();
        	bLevel.destroyBlockByPixel(player2.getPosX()+bLevel.getTileDim()/2, player2.getPosY()+bLevel.getTileDim()/2);
        }
        if (player2 != null && player2.boostItem())
        { playAudio.playSound("Pickup");
        	player2.removeStepsize();
        	bLevel.destroyBlockByPixel(player2.getPosX()+bLevel.getTileDim()/2, player2.getPosY()+bLevel.getTileDim()/2);
        }

        if (stepCount >= 15) {
            playAudio.playSound("Step");
            stepCount = 0;
        }
        if (player1.exit() && tut > 0 || player1.death() && tut > 0) {
            {
                if (player1.exit())
                    playAudio.playSound("Exit");
                if (player1.death())
                    playAudio.playSound("End");

            }

            try {

                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            running = false;
            new Tutorials();
            frame.dispose();
            return;
        }

        if (player1.exit() && tut == 0 || player1.death() && tut == 0) {
            {
                if (player1.exit())
                    playAudio.playSound("Exit");
                if (player1.death())
                    playAudio.playSound("End");
            }

            try {

                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            running = false;
            new Menu();
            frame.dispose();
            return;
        }

        if (multiplayer && player2 != null) {
            if (player2.exit()) {
                playAudio.playSound("Exit");
                return;
            }
            if (player2.death()) {
                playAudio.playSound("End");
                return;
            }

        }

        handleKeyboard();
        updateBombs();
    }

    private void rescale() {
        width = getWidth();
        height = getHeight();
        dbg.dispose();
        gameG.dispose();
        bLevel.updateTileDimensions(width, height);
        initializeGraphics();
    }

    protected void handleKeyboard() {
        // Ueberpruefe Tastatureingaben player 1
        if (keyPoller.isKeyDown(KeyEvent.VK_LEFT)) {
            player1.Direction(-bLevel.getTileDim() / player1.getStepsize(), 0);
            stepCount++;
        } else if (keyPoller.isKeyDown(KeyEvent.VK_RIGHT)) {
            player1.Direction(bLevel.getTileDim() / player1.getStepsize(), 0);
            stepCount++;
        }
        if (keyPoller.isKeyDown(KeyEvent.VK_UP)) {
            player1.Direction(0, -bLevel.getTileDim() / player1.getStepsize());
            stepCount++;
        } else if (keyPoller.isKeyDown(KeyEvent.VK_DOWN)) {
            player1.Direction(0, bLevel.getTileDim() / player1.getStepsize());
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
                player1.addcurrentbombs();
            }
            // bombs.add(new Bomben(robot.getPosX(),robot.getPosY(), 2,
            // bLevel));
        }

        if (multiplayer && player2 != null) {
            if ((player1.getPosX() == player2.getPosX())
                    && (player1.getPosY() == player2.getPosY()))
                return;
            if (keyPoller.isKeyDown(KeyEvent.VK_A)) {
                player2.Direction(-bLevel.getTileDim() / player2.getStepsize(), 0);
                stepCount++;
            } else if (keyPoller.isKeyDown(KeyEvent.VK_D)) {
                player2.Direction(bLevel.getTileDim() / player2.getStepsize(), 0);
                stepCount++;
            }
            if (keyPoller.isKeyDown(KeyEvent.VK_W)) {
                player2.Direction(0, -bLevel.getTileDim() / player2.getStepsize());
                stepCount++;
            } else if (keyPoller.isKeyDown(KeyEvent.VK_S)) {
                player2.Direction(0, bLevel.getTileDim() / player2.getStepsize());
                stepCount++;
            } else if (keyPoller.isKeyDown(KeyEvent.VK_CONTROL)) {
                playAudio.playSound("Put");
                int posX = player2.getPosX();
                int posY = player2.getPosY();
                if (!bLevel.hasBombByPixel(posX, posY)
                        && player2.bombplantable()) {
                    bombsP2.add(new Bomben((posX / bLevel.getTileDim())
                            * bLevel.getTileDim(), (posY / bLevel.getTileDim())
                            * bLevel.getTileDim(), player2.maxradius(),
                            player2, bLevel));
                    player2.addcurrentbombs();
                }

            }
        }

        if (keyPoller.isKeyDown(KeyEvent.VK_ESCAPE)) {

            int result = JOptionPane.showConfirmDialog(null,
                    "Wollen Sie Bomberman wirklich beenden",
                    "Bomberman beenden", JOptionPane.YES_NO_OPTION);
            switch (result) {
            case JOptionPane.YES_OPTION:
                System.exit(0);
            case JOptionPane.NO_OPTION:
            }
        }

    }

    private void updateBombs() {
        for (int i = 0; i < bombsP1.size(); i++) {
            bombsP1.get(i).update();
            if (bombsP1.get(i).isExploded()) {
                bombsP1.remove(i);
                player1.removecurrentbombs();
            }
        }
        for (int i = 0; i < bombsP2.size(); i++) {
            bombsP2.get(i).update();
            if (bombsP2.get(i).isExploded()) {
                bombsP2.remove(i);
                player2.removecurrentbombs();
            }
        }
    }
}
