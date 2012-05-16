package bombi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class BombiGui extends JComponent implements Runnable {
	Spieler spieler;
    BombermansBomben Bombe1;
    BombermanLevel bLevel;
    private static final int WIDTH = 840;
    private static final int HEIGHT = 600;

    private static final long SECOND = 1000000000;
    private static final long SLEEPTIME = SECOND / 60;

    private boolean running = true;
    private Image dbImage;
    private Graphics2D dbg;
    int x = 0;
    int y = 0;
    int radius = 36;
    int fps = 0; // wird durch den main-loop gesetzt
    private Graphics2D g;

    /**
     * Input fŸr Tasten und Begegung des Objektes mit den Pfeiltasten.
     */
    public class AL extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == e.VK_RIGHT) {
                x = x + 40;
                if (x > 804) {
                    x = 804;
                }
            }
            if (keyCode == e.VK_LEFT) {
                x = x - 40;
                if (x <= 0) {
                    x = 0;
                }
            }
            if (keyCode == e.VK_UP) {
                y = y - 40;
                if (y <= 0) {
                    y = 0;
                }
            }
            if (keyCode == e.VK_DOWN) {
                y = y + 40;
                if (y > 564) {
                    y = 564;
                }
            }
            if (keyCode == e.VK_ESCAPE) {
                int result = JOptionPane.showConfirmDialog(null, "Wollen Sie Bomberman wirklich beenden", "Bomberman beenden", JOptionPane.YES_NO_OPTION);
                switch (result) {
                case JOptionPane.YES_OPTION:
                    System.exit(0);
                case JOptionPane.NO_OPTION:
                }
            }
            if (keyCode == e.VK_SPACE) {
                Bombe1 = new BombermansBomben(x, y);
            }
        }

        public void keyReleased(KeyEvent e) {

        }
    }

    public BombiGui() {
        super();
        setFocusable(true);
        this.setSize(WIDTH, HEIGHT);
        bLevel = new BombermanLevel(WIDTH, HEIGHT);
        addKeyListener(new AL());
    }

    /**
     * Double Buffering wobei ein Image erstellt wird und im Hintergrund das nŠchste Bild gemalt wird.
     */
    public void paintBuffer() {
        if (dbImage == null) {
            dbImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
            dbg = (Graphics2D) dbImage.getGraphics();
        }

        // zeichne das Lvel
        bLevel.draw(dbg);
// HIER DAS PROBLEM ******************************************        
        spieler.draw(Texture.SPIELER.draw(96, 0, 32, 32, g));
//************************************************************
        // Eine kleine rote Kugel die zum testen der
        // Bewegung und des Double Bufferings gedacht ist
        dbg.setColor(Color.RED);
        dbg.fillOval(x, y, radius, radius);

        // zeichne die Bombe
        if (Bombe1 != null) {
            Bombe1.draw(dbg);
        }

        // zeige fps an
        dbg.setColor(Color.ORANGE);
        dbg.drawString(fps + "FPS", 800, 20);
    }

    public void paintGame() {
        if (g == null) g = (Graphics2D) this.getGraphics();
        g.drawImage(dbImage, 0, 0, null);
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
     * Methode, welche die bevorzugte Grš§e dieser JComponent zurŸck gibt.
     * 
     * @return: Dimension WIDTH x HEIGHT, welche fest codiert sind.
     */
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    /**
     * Startet das Programm ;-)
     * 
     * @param args
     *            Bisher werden alle Parameter ignoriert.
     */
    public static void main(String[] args) {
        // Fenster erstellen
        JFrame frame = new JFrame();
        frame.setTitle("Bombi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BombiGui bGui = new BombiGui();

        frame.add(bGui);
        frame.pack(); // passt die Grš§e dem Inhalt an

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

        // Vars zum FPS zŠhlen
        long fpsCounter;
        int fps = 0;
        fpsCounter = System.nanoTime();// messen spŠter, ob eine Sek. vergangen ist

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

    public void bombermanUpdate() {
        // bisher sind keine updatebaren Objekte da
    }
}
