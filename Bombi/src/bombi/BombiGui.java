package bombi;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class BombiGui extends JComponent implements Runnable {
    private static final int WIDTH = 840;
    private static final int HEIGHT = 600;

    private static final long SECOND = 1000000000; // eine Sekunde in Nanosekunden
    private static final long SLEEPTIME = SECOND / 60; // 60 FPS

    private boolean running = true;

    // im Speicher gehaltenes Bild & zugehöriges Graphics-Objekt für Double-Buffering
    private BufferedImage dbImage;
    private Graphics2D dbg;

    // managet die Tastatur.. stellt im Grunde eine auf Polling basierende Lösung dar (statt Interrupts)
    private KeyPoller keyPoller;

    Spieler spieler;
    BombermansBomben Bombe1;
    BombermanLevel bLevel;
   
    int x = 0;
    int y = 0;
    int radius = 36;
    int fps = 0; // wird durch den main-loop gesetzt

    public BombiGui() {
        super();
        // erzeuge die Objekte für Doublebuffering
        dbImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        dbg = (Graphics2D) dbImage.getGraphics();

        // erzeuge unseren KeyPoller
        keyPoller = new KeyPoller();
        addKeyListener(keyPoller);

        setFocusable(true);
        this.setSize(WIDTH, HEIGHT);
        bLevel = new BombermanLevel(WIDTH, HEIGHT);
        spieler = new Spieler(bLevel);
        
        System.out.println(KeyEvent.VK_LEFT + " " + KeyEvent.VK_A);
    }

    /**
     * Double Buffering wobei ein Image erstellt wird und im Hintergrund das nächste Bild gemalt wird.
     */
    public void paintBuffer() {
    	if(spieler.spielEnde()) {
    		dbg.drawString("Spieler ... hat gewonnen!", 400, 300);
    		return;
    	}
        // zeichne das Lvel
        bLevel.draw(dbg);

        // zeichne die Bombe
        if (Bombe1 != null) {
            Bombe1.draw(dbg);
            
        }

        // zeichne zuletzt den Spieler
        spieler.draw(dbg);

        // zeige fps an
        dbg.setColor(Color.ORANGE);
        dbg.drawString(fps + "FPS", 800, 20);
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
  
    /**
     * Startet das Programm ;-)
     * 
     * @param args Bisher werden alle Parameter ignoriert.
     */
    public static void main(String[] args) {
        // Fenster erstellen
        JFrame frame = new JFrame();
        frame.setTitle("Bombi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BombiGui bGui = new BombiGui();
       
        frame.add(bGui);
        frame.pack(); // passt die Gr��e dem Inhalt an

        // zentriert das Fenster
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
      
        new Thread(bGui).start();
        
       
    }

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
    	
    	if(spieler.spielEnde())
    		return;
    	
    	
        // überprüfe Tastatureingaben
        if (keyPoller.isKeyDown(KeyEvent.VK_LEFT)) {
            spieler.Direction(-40,0);
        } else if (keyPoller.isKeyDown(KeyEvent.VK_RIGHT)) {
            spieler.Direction(+40,0);
        }
        if (keyPoller.isKeyDown(KeyEvent.VK_UP)) {
            spieler.Direction(0,-40);
        } else if (keyPoller.isKeyDown(KeyEvent.VK_DOWN)) {
            spieler.Direction(0,+40);
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
    }
}
