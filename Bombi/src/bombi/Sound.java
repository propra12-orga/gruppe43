package bombi;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
/**
 * 
 * @author abuubaida
 *
 */
public class Sound {
	public String name;
	public AudioClip sound;
	private static Sound staticSound = new Sound();
/**	
 * Konstruktor Dateinamen und Dateiort werden uebergeben 
 * @param name
 * @param url
 */
	
	public Sound(String name, URL url){
		this.name = name;
		
		try {
			sound = Applet.newAudioClip(url);
			}
			catch(Exception e){ 	
								e.printStackTrace(System.err);
							  }
	}
/**
 * Default Konstruktor
 */
	private Sound() {
		
	}
/**
 * Zum Abspielen einer Audio-Datei
 */
	public void play() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				 sound.play();
			}
		}).start();
	}
/**
 * Neuer Thread in dem ein Sound ununterbrochen laeuft
 */
	public void loop() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				if(sound != null) sound.loop();
			}
		}).start();
	}
/**
 * Zum Stoppen des Wiedergabe
 */
	public void stop() {
		if(sound!=null) sound.stop();
	}
/**	
 * Der Ort der Datei wird anhand des Dateinamens zur�ckgegeben 
 * @param filName
 * @return
 */
	public static URL getURL(String filName) {
		return staticSound.getClass().getResource(filName);
	}
}
//Ende Sound Klasse