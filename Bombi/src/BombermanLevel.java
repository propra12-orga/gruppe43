import java.awt.Color;
import java.awt.Graphics;

/**
 * Diese Klasse erzeugt Objekte, welche als Spielfeld interpretiert werden. Hierzu
 * wird ein zweidimensionales short-Array benutzt. IdR wird nur ein Objekt dieser
 * Klasse erstellt.
 **/
public class BombermanLevel{

    //ein paar Konstanten, um das Manipulieren des Felder einfacher zu gestalten
    private static final short GRASS = 0;
    private static final short STONE = 1;
    private static final short INDESTRUCTIBLE = 2;

	
    //statische Variable, welche die Groesse der Felder in Pixeln spezifiziert
	private static final int DIM = 40;
	
    //zweidimensionales Array, welches das Spielfeld darstellt
	private short[][] tiles;
	//Dimension des obigen Arrays
    private int width, height;
	
    /**
     * Erzeugt ein neues Spielfeld.
     * @param width:  Breite des Spielfelds in Pixel
     * @param height: Hoehe des Spielfelds in Pixel
     **/
	public BombermanLevel(int width, int height){
        //Berechne die Dimension des Feldes mit Hilfe der Pixel
		this.width = width/DIM;
		this.height = height/DIM;A
        //erstelle ein leeres Spielfeld
		tiles = new short[this.width][this.height];
		fillRandomly();}
	
    /**
     * Methode, welche das Spielfeld initialisiert. Alle zwei Zeilen/Spalten wird
     * ein unzerstoerbarer Stein erzeugt. Ein 2x2 Block in den Ecken wird freigelassen,
     * um dem Spieler Bewegungsfreiraum zu ermoeglichen. 
     **/
	private void fillRandomly() {
        //iteriere ueber die x- und dann die y-Koordinaten
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				if((j%2 == 1) && (i%2 == 1)) //alle 2 Zeilen/Spalten..
					tiles[i][j] = INDESTRUCTIBLE; //... erzeuge einen unzerstoerbaren Block

                //gehe sicher, dass alle vier Spieler anfangs eingemauert sind
				else if((i==2 || i==width-3) && ((j>=0 && j<=2) || (j>=height-3 && j<=height-1)))
					tiles[i][j] = STONE;
				else if((j==2 || j==height-3) && ((i>=0 && i<2) || (i>width-3 && i<=width-1)))
					tiles[i][j] = STONE;

                //fuelle alle andere Bloecke zufaellig mit Steinen
				else if((i>=3 && i<=width-4) || (j>=3 && j<=height-4))
					tiles[i][j] = (short)(Math.random()*2);
            }
        }
    }


	public void draw(Graphics g) {
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				if(tiles[i][j] == GRASS) drawGrass(i,j,g);
				
                else if(tiles[i][j] == STONE) drawStone(i,j,g);

                else if(tiles[i][j] == INDESTRUCTIBLE) drawIndestructible(i,j,g);	
            }
	    }
    }


    private void drawGrass(int posX, int posY, Graphics g){
        g.setColor(Color.GREEN);
		g.fillRect(posX*DIM,posY*DIM,DIM,DIM);
    }
    
    private void drawStone(int posX, int posY, Graphics g){
        g.setColor(Color.GRAY);
		g.fillRect(posX*DIM,posY*DIM,DIM,DIM);
    }
    
    private void drawIndestructible(int posX, int posY, Graphics g){
        g.setColor(Color.DARK_GRAY);
		g.fillRect(posX*DIM,posY*DIM,DIM,DIM);
    }
	
}//Ende der Klasse BombermanLevel
