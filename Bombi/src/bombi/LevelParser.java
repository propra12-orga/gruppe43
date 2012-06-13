package bombi;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Diese Klasse stellt Objekte bereit, welche eine externe Textdatei
 * auslesen und versuchen, aus dieser ein Level zu erzeugen. Eine
 * beispielhafte Datei ist unter map/test.map zu finden. Das Format
 * wird dort naeher erlaeutert.
 * 
 * @author tobi
 *
 */
public class LevelParser {
	
	public static short[][] parseMap(String path, boolean fullPath) throws FileNotFoundException, IllegalFormatException, IOException{
		BufferedReader fileIn = null;
		if(!fullPath)// nur Dateiname angegeben, map soll in ./map/ liegen
			path = "./map/"+path;
		fileIn = new BufferedReader(new FileReader(path));
		Dimension dim = parseDim(fileIn);
		fileIn.close();
		fileIn = new BufferedReader(new FileReader(path));
		short[][] map = parseMap(fileIn, dim);
		fileIn.close();
		return map;
	}
	
	private static short[][] parseMap(BufferedReader fileIn, Dimension dim)
			throws IllegalFormatException, IOException{
		String temp;
		boolean level = false;
		short[][] map = new short[dim.width][dim.height];
		while((temp = fileIn.readLine())!= null){
			temp = removeComment(temp.trim());
			if(temp.equals("[LEVEL]")){
				level = true;
				break;
			}
		}
		if(!level)
			throw new IllegalFormatException("[LEVEL] marker not found.");
		for(int i = 0; i < dim.height; i++){
			temp = fileIn.readLine();
			int beginIndex = 0;
			int endIndex;
			short tile;
			// parse alle bis auf den letzten Eintrag
			for(int j = 0; j < dim.width - 1; j++){
				endIndex = temp.indexOf(',', beginIndex);				
				tile = Short.parseShort(temp.substring(beginIndex, endIndex));
				// FIXME Ueberpruefung, ob es sich um einen zulaessigen Wert handelt
				map[j][i] = tile;
				beginIndex = endIndex+1;
			}
			// der letzte Eintrag endet mit ;
			endIndex = temp.indexOf(';', beginIndex);
			tile = Short.parseShort(temp.substring(beginIndex, endIndex));
			// FIXME Ueberpruefung, ob es sich um einen zulaessigen Wert handelt
			map[dim.width - 1][i] = tile;
		}
		return map;
	}

	public static short[][] parseMap(String path) throws FileNotFoundException, IllegalFormatException, IOException{
		return parseMap(path, false);
	}
	
	private static Dimension parseDim(BufferedReader fileIn)
			throws IllegalFormatException, IOException{
		String temp;
		boolean dim = false;
		while((temp = fileIn.readLine())!= null){
			temp = removeComment(temp.trim());
			if(!temp.equals("")){
				if(dim){// in dieser Zeile muss die Dimension folgen
					// , und ; sind die einzigen Trennzeichen hier
					int comma = temp.indexOf(',');
					if(comma < 0)
						throw new IllegalFormatException("Dimensions not properly specified.");
					int semicolon = temp.indexOf(';', comma+1);
					if(semicolon < 0)
						throw new IllegalFormatException("Dimensions not properly specified.");
					int w = Integer.parseInt(temp.substring(0, comma));
					int h = Integer.parseInt(temp.substring(comma+1, semicolon));
					return new Dimension(w,h);
				}
				if(temp.equals("[DIM]"))
					dim = true;
			}
		}
		throw new IllegalFormatException("Dimensions not properly specified.");
	}
	
	private static final String removeComment(final String line){
		int commentPos = line.indexOf("//");
		if(commentPos == -1) return line;
		return line.substring(0, commentPos);
	}

}
