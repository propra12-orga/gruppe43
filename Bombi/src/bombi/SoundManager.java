package bombi;

import java.util.ArrayList;
/**
 * Diese Klasse Managed die Sound Dateien, indem sie in eine Liste gepackt werden k�nnen
 * Anhand des Namens k�nnen sie im 1mal-Modus, Schleifen-Modus und gestoppt werden
 * @author abuubaida
 * TODO Man muss noch �nderungen bzgl. stoppen vornehmen
 */
public abstract class SoundManager {
	public ArrayList<Sound> sounds = new ArrayList<Sound>();
	public abstract void initSounds();
//
	public SoundManager() {
		initSounds();
	}
/**
 * Sound zur Liste hinzuf�gen
 * @param sound
 */
	public void addSound(Sound sound) {
		sounds.add(sound);
	}
/**
 * Sound von Liste entfernen
 * @param sound
 */
	public void removeSound(Sound sound) {
		sounds.remove(sound);
	}
/**	
 * Sound anhand des vergebenen Namens abspielen 
 * @param name
 */
	public void playSound(String name) {
		for(Sound s: sounds) {
			if(s.name.equals(name)) {
				s.play();
			}
		}
	}
/**	
 * Sound dauernd abspielen
 * @param name
 */
	public void loopSound(String name) {
		for(Sound s: sounds) {
			if(s.name.equals(name)) {
				s.loop();
			}
		}
	}
/**	
 * Sound stoppen
 * @param name
 */
	public void stopSound(String name) {
		for(Sound s: sounds) {
			if(s.name.equals(name)) {
				s.stop();
			}
		}
	}
// Alle Sounds stopen
	public void stopAllSounds() {
		for(Sound s: sounds) s.stop();
	}
	
}
//Ende SoundManager Klasse