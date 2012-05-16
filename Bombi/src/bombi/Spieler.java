package bombi;

import java.awt.Graphics2D;

public class Spieler {

    private int health = 1; // Zustand für abfrage ob Spieler verloren(=0)hat

    private static int px, py, width, height;

    public Spieler() {
        px = py = 0;
        width = height = 40;
    }

    public void draw(Graphics2D g) {
        if (health == 1) {
            Texture.SPIELER.draw(px, py, width, height, g);
        }

    }

}
