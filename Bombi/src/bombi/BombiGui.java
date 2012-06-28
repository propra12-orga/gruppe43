package bombi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class BombiGui extends JComponent implements Runnable {
    private int width = 640;
    private int height = 480;
    private int gameWidth, gameHeight;

    private int stepsize = 10;

    private static final long SECOND = 1000000000; // eine Sekunde in
                                                   // Nanosekunden
    private static final long SLEEPTIME = SECOND / 60; // 60 FPS

    private boolean running = true;
    private static int stepCount = 0;
    // im Speicher gehaltenes Bild & zugehoeriges Graphics-Objekt für
    // Double-Buffering
    private BufferedImage dbImage;
    private Graphics2D dbg;
    // das gleiche fuer das Spielfeld
    private BufferedImage gameImage;
    private Graphics2D gameG;

    // managet die Tastatur.. stellt im Grunde eine auf Polling basierende
    // Lösung dar (statt Interrupts)
    private KeyPoller keyPoller;

    // Liste für die Bomben
    private List<Bomben> bombsP1;
    private List<Bomben> bombsP2;

    private boolean multiplayer;
    Player player1,player2;
    BombermanLevel bLevel;
    Robot robot;
    Menu m;
    int fps = 0; // wird durch den main-loop gesetzt
    int tutmsg;

    public BombiGui(boolean multiplayer, int tut) {
        super();
        if (tut==1){
        	initializeLevel("/tut1.map");
        	initializeGraphics();
        	tutmsg=1;
        } else if(tut==2){
        	initializeLevel("/tut2.map");
        	initializeGraphics();
        	tutmsg=2;
        }else if(tut==3){
        	initializeLevel("/tut3.map");
        	initializeGraphics();
        	tutmsg=3;
        }else if(tut==4){
        	initializeLevel("/tut4.map");
        	initializeGraphics();
        	tutmsg=4;
        }else if(tut==5){
        	initializeLevel("/tut5.map");
        	initializeGraphics();
        	tutmsg=5;
        }else if(tut==6){
        	initializeLevel("/tut6.map");
        	initializeGraphics();
        	tutmsg=6;
        } else
        	initializeLevel("/test.map");
        	initializeGraphics();
        

        // erzeuge unseren KeyPoller
        keyPoller = new KeyPoller();
        addKeyListener(keyPoller);

        // erzeuge immer den ersten Spieler
        player1 = new Player(bLevel, bLevel.getTileDim(), bLevel.getTileDim());
        this.multiplayer = multiplayer;
        if (multiplayer)// den Zweiten nur fuer MP
        	player2 = new Player(bLevel, bLevel.getTileDim()+520,(bLevel.getTileDim()-2)+222);
           // player2 = new Player(bLevel, bLevel.getTileDim()
             //       * (bLevel.getWidth() - 2), bLevel.getTileDim()
               //     * (bLevel.getHeight() - 2));
        bombsP1 = new ArrayList<Bomben>();
        bombsP2 = new ArrayList<Bomben>();
        setFocusable(true);
        this.setSize(width, height);
        playAudio.playSound("Fight");
    }


    public BombiGui() {
        this(false,0);
    }

    private void initializeLevel(String pathToMap) {
        try {
            bLevel = new BombermanLevel(LevelParser.parseMap(pathToMap), width,
                    height);
        } catch (Exception e) {
            bLevel = new BombermanLevel(17, 9, width, height);
        }
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
            gameG.drawString("Spieler 1 ... hat gewonnen!", 400, 300);
        if (player1.dead()) {
            gameG.drawString("Spieler1 wurden von einer Bombe getötet. ", 300,
                    250);
            return;
        }
        if (multiplayer && player2 != null) {
            if (player2.exit())
                gameG.drawString("Spieler 2 ... hat gewonnen!", 400, 300);
            if (player2.dead()) {
                gameG.drawString("Spieler2 wurden von einer Bombe getötet. ",
                        300, 250);
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
       
        // Zeichnet die Nachrichten fürs Tutorial
        if (tutmsg==1)
        	dbg.drawString("Bewege dich mit den Pfeiltasten und laufe zum Ausgang.", 100,this.getHeight()-50);
        if (tutmsg==2)
        	dbg.drawString("Die Steine die du siehst, können nicht zerstört werden. Finde einen Weg zum Ausgang.", 50,this.getHeight()-50);
        if (tutmsg==3){
        	dbg.drawString("Zerstörbare Blöcke versperren dir den Weg.Lege eine Bombe neben den Steinen ", 100,this.getHeight()-50);
    	    dbg.drawString("mit der Leertasteund geh in Deckung. Laufe dann zum Ausgang.", 100,this.getHeight()-30);}
        if (tutmsg==4){ 
        	dbg.drawString("Vorsicht! Komme den Gegnern nicht zu nah. Sobald sie dich berühren stirbst du.", 100,this.getHeight()-50);
	        dbg.drawString("Weiche ihnen aus zum laufe zum Ausgang.", 100,this.getHeight()-30);}
        if (tutmsg==5){
        	dbg.drawString("Du kannst deine Bomben mit Kettenreaktionen zum explodieren bringen. Das Explosionsfeuer", 30,this.getHeight()-50);
            dbg.drawString("deiner Bombe bringen scharfe Bomben zur sofortigen explosion.Lege 2 Bomben gleichzeitig", 30,this.getHeight()-30);
            dbg.drawString("hin und sprenge dir den Weg mit einer Kettenreaktion frei. Laufe dann zum Ausgang.", 30,this.getHeight()-10); }
        if (tutmsg==6){
        	dbg.drawString("Es gibt 3 Items im Spiel. Zusatzbombe: Erhöht die Anzahl der Bomben die du gleichzeitig legen kannst.",0,this.getHeight()-50);
        	dbg.drawString("Sprengkraft: Erhöht den Explosionsradius deiner Bombe. Laufschuhe: Erhöhen deine", 0,this.getHeight()-30);
            dbg.drawString("Laufgeschwindigkeit. Sammele alle Items ein und teste sie. Laufe dann zum Ausgang.", 0,this.getHeight()-10); }

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
    public void bombermanUpdate() {
        if (getWidth() != width || getHeight() != height)
            rescale();

        if (player1.bombItem())
        { player1.addmaxbomb();
        	bLevel.destroyBlockByPixel(player1.getPosX()+bLevel.getTileDim()/2, player1.getPosY()+bLevel.getTileDim()/2);
        }
        
        if (player1.explosionItem())
        { player1.addradius();
        	bLevel.destroyBlockByPixel(player1.getPosX()+bLevel.getTileDim()/2, player1.getPosY()+bLevel.getTileDim()/2);
        }
        
        if (player2 != null && player2.bombItem())
        { player2.addmaxbomb();
        	bLevel.destroyBlockByPixel(player2.getPosX()+bLevel.getTileDim()/2, player2.getPosY()+bLevel.getTileDim()/2);
        }
        
        if (player2 != null && player2.explosionItem())
        { player2.addradius();
        	bLevel.destroyBlockByPixel(player2.getPosX()+bLevel.getTileDim()/2, player2.getPosY()+bLevel.getTileDim()/2);
        }
        
        if (stepCount >= 10) {
            playAudio.playSound("Step");
            stepCount = 0;
        }
        if (player1.exit() || player1.dead()) {
        	if(true){ playAudio.playSound("End");{
        	new Menu();
        	}
        	}
        		try{
        			
        			Thread.sleep(100000);
        		}catch (Exception e) {
        			e.printStackTrace();
        		}
            return;
        	}
        if (multiplayer && player2 != null)
            if (player2.exit() || player2.dead())
                return;

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

    private void handleKeyboard() {
        // Ueberpruefe Tastatureingaben player 1
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
                bombsP1.add(new Bomben((posX/bLevel.getTileDim())*bLevel.getTileDim(), (posY/bLevel.getTileDim())*bLevel.getTileDim(), player1.maxradius(), bLevel));
                player1.addcurrentbombs();
            }
            // bombs.add(new Bomben(robot.getPosX(),robot.getPosY(), 2,
            // bLevel));
        }

        if (multiplayer && player2 != null) {
        	if((player1.getPosX()==player2.getPosX())&&(player1.getPosY()==player2.getPosY()))
        		return;
            if (keyPoller.isKeyDown(KeyEvent.VK_A)) {
                player2.Direction(-bLevel.getTileDim() / stepsize, 0);
                stepCount++;
            } else if (keyPoller.isKeyDown(KeyEvent.VK_D)) {
                player2.Direction(bLevel.getTileDim() / stepsize, 0);
                stepCount++;
            }
            if (keyPoller.isKeyDown(KeyEvent.VK_W)) {
                player2.Direction(0, -bLevel.getTileDim() / stepsize);
                stepCount++;
            } else if (keyPoller.isKeyDown(KeyEvent.VK_S)) {
                player2.Direction(0, bLevel.getTileDim() / stepsize);
                stepCount++;
            } else if (keyPoller.isKeyDown(KeyEvent.VK_CONTROL)) {
                playAudio.playSound("Put");
                int posX = player2.getPosX();
                int posY = player2.getPosY();
                if (!bLevel.hasBombByPixel(posX, posY) && player2.bombplantable()) {
                    bombsP2.add(new Bomben((posX/bLevel.getTileDim())*bLevel.getTileDim(), (posY/bLevel.getTileDim())*bLevel.getTileDim(), player2.maxradius(), bLevel));
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
            if (bombsP1.get(i).isExploded()){
                bombsP1.remove(i);
                player1.removecurrentbombs();
            }
        } 
        for (int i = 0; i < bombsP2.size(); i++) {
            bombsP2.get(i).update();
            if (bombsP2.get(i).isExploded()){
                bombsP2.remove(i);
                player2.removecurrentbombs();
            }
        } 
    }
}
