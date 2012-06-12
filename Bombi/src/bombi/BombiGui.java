package bombi;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class BombiGui extends JComponent implements Runnable {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 440;

    private static final long SECOND = 1000000000; // eine Sekunde in Nanosekunden
    private static final long SLEEPTIME = SECOND / 60; // 60 FPS

    private boolean running = true;
    private static int stepCount = 0;
    // im Speicher gehaltenes Bild & zugehöriges Graphics-Objekt für Double-Buffering
    private BufferedImage dbImage;
    private Graphics2D dbg;

    // managet die Tastatur.. stellt im Grunde eine auf Polling basierende Lösung dar (statt Interrupts)
    private KeyPoller keyPoller;

    // Liste für die Bomben
    private List<Bomben> bombs;
    
    Player player1,player2;
    BombermansBomben Bombe1;
    BombermanLevel bLevel;
    Robot robot;
    
    int x = 0;
    int y = 0;
    int radius = 36;
    int fps = 0; // wird durch den main-loop gesetzt

    public BombiGui() {
        super();
        // erzeuge die Objekte für Doublebuffering
        dbImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        dbg = (Graphics2D) dbImage.getGraphics();
    	playAudio.playSound("Fight");
        // erzeuge unseren KeyPoller
        keyPoller = new KeyPoller();
        addKeyListener(keyPoller);

        setFocusable(true);
        this.setSize(WIDTH, HEIGHT);
        bLevel = new BombermanLevel(15,11,WIDTH, HEIGHT);
        player1 = new Player(bLevel);
        player2 = new Player(bLevel,520,360);
        bombs = new ArrayList<Bomben>();
        robot = new Robot(bLevel);
        System.out.println(KeyEvent.VK_LEFT + " " + KeyEvent.VK_A);
    }

    /**
     * Double Buffering wobei ein Image erstellt wird und im Hintergrund das nächste Bild gemalt wird.
     */
    public void paintBuffer() {
    	
    	if(player1.exit()||player2.exit()) {
    		//s.playSound("Exit");
    		if(player1.exit()==true)
    		dbg.drawString("Spieler 1 ... hat gewonnen!", 400, 300);
    		else dbg.drawString("Spieler 2 ... hat gewonnen!", 400, 300);
    		return;
    	}
    	
    	if(player1.dead()) {
    		dbg.drawString("Spieler1 wurden von einer Bombe getötet. ", 300, 250);
    		return;
    	}
    	if(player2.dead()) {
    		dbg.drawString("Spieler2 wurden von einer Bombe getötet. ", 300, 250);
    		return;
    	}
        // zeichne das Lvel
        bLevel.draw(dbg);

     // zeichne die Bombe
        for (int i=0; i<bombs.size(); i++) {
        	bombs.get(i).draw(dbg);   
        }

        // zeichne zuletzt den Spieler
        player1.draw(dbg);
        player2.draw1(dbg);

        // robot zeichnen
       // robot.draw(dbg);
        // zeige fps an
        dbg.setColor(Color.ORANGE);
        dbg.drawString(fps + "FPS", this.getWidth() - 50, 20);
    }

    private void gameDrawBuffer() {
        Graphics2D g;
        try {
            g = (Graphics2D) this.getGraphics();
            if (g != null && dbImage != null) {
                g.drawImage(dbImage, 0, 0, this.getWidth(), this.getHeight(), 0, 0, WIDTH, HEIGHT, null);
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
        return new Dimension(WIDTH, HEIGHT);
    }
  
  
    //SoundManager instanz (Audios einlesen)
    SoundManager playAudio = new SoundManager() {
    	public void initSounds() {
    		sounds.add(new Sound("Exit", Sound.getURL("/Exit.wav")));
    		sounds.add(new Sound("Bumm", Sound.getURL("/Bumm.wav")));
    		sounds.add(new Sound("Put", Sound.getURL("/Put.wav")));
    		sounds.add(new Sound("Step", Sound.getURL("/Step.wav")));
    		sounds.add(new Sound("Fight", Sound.getURL("/Fight.wav")));
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
        fpsCounter = System.nanoTime();// messen sp�ter, ob eine Sek. vergangen ist
       
 /*while-schleife war vorher auf "running".hab die Abfrage ob ein spieler
  * das EXIT erreicht hat wenn ja m�sste die while schleife abrechen und 
  * das spiel beendet werden. 
  * Gibt bestimmt eine bessere m�glichkeit mit etwas wie stop()...     
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
                this.fps = fps; // update die GLOBALE Variable mit den aktuellen fps
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
     * Methode, welche (atm) 60x pro Sekunde aufgerufen wird und (nicht sichtbare) Updates an der Spielumgebung durchführt. Hierzu gehören das Einlesen von
     * Tastatureingaben, Bewegen des Spielerobjekts, Herunterzählen des BombenCounters etc.
     */
    public void bombermanUpdate() {

    	if(stepCount>=10){playAudio.playSound("Step");stepCount=0;}
    	if(player1.exit()||player2.exit()||player1.dead()||player2.dead())
    		return;
    // �berpr�fe Tastatureingaben player 1
        if (keyPoller.isKeyDown(KeyEvent.VK_LEFT)) {
            player1.Direction(-40,0);robot.robotDirection();
            stepCount++;
        } else if (keyPoller.isKeyDown(KeyEvent.VK_RIGHT)) {
            player1.Direction(+40,0);robot.robotDirection();
            stepCount++;
        }
        if (keyPoller.isKeyDown(KeyEvent.VK_UP)) {
            player1.Direction(0,-40);robot.robotDirection();
            stepCount++;
        } else if (keyPoller.isKeyDown(KeyEvent.VK_DOWN)) {
        	player1.Direction(0,+40);robot.robotDirection();
        	stepCount++;
        } else if (keyPoller.isKeyDown(KeyEvent.VK_SPACE)) {
        	playAudio.playSound("Put");
        	int posX = player1.getPosX();
            int posY = player1.getPosY();
            if (!bLevel.hasBombByPixel(posX,posY)){
            bombs.add(new Bomben(posX, posY, 2, bLevel));
            }
            //bombs.add(new Bomben(robot.getPosX(),robot.getPosY(), 2, bLevel));
        }
        
        if (keyPoller.isKeyDown(KeyEvent.VK_S)) {
            player2.Direction(-40,0);
            stepCount++;
        } else if (keyPoller.isKeyDown(KeyEvent.VK_D)) {
            player2.Direction(+40,0);
            stepCount++;
        }
        if (keyPoller.isKeyDown(KeyEvent.VK_E)) {
            player2.Direction(0,-40);
            stepCount++;
        } else if (keyPoller.isKeyDown(KeyEvent.VK_X)) {
        	player2.Direction(0,+40);
        	stepCount++;
        } else if (keyPoller.isKeyDown(KeyEvent.VK_A)) {
        	playAudio.playSound("Put");
        	int posX = player2.getPosX();
            int posY = player2.getPosY();
            if (!bLevel.hasBombByPixel(posX,posY)){
            bombs.add(new Bomben(posX, posY, 2, bLevel)); }
           
        }

        if (keyPoller.isKeyDown(KeyEvent.VK_ESCAPE)) {
        	
            int result = JOptionPane.showConfirmDialog(null, "Wollen Sie Bomberman wirklich beenden", "Bomberman beenden", JOptionPane.YES_NO_OPTION);
            switch (result) {
            case JOptionPane.YES_OPTION:
                System.exit(0);
            case JOptionPane.NO_OPTION:
            }
        }
        // Ende der Tastatureingabenüberprüfung
        updateBombs();
    }
    private void updateBombs() {
    	for (int i=0; i<bombs.size();i++) {
    		bombs.get(i).update();
    	}
    }
}
