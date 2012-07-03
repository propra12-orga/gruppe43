package bombi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class NetworkKeyReceiver{
		
	public NetworkKeyReceiver(int size){
		keyDown = new byte[size / 8];
		keysDown = 0;
	}
	
    private byte[] keyDown; // in diesem Feld werden die gedrückten Tasten
    // gespeichert, d.h. das korrespondiere Bit wird auf
    // 1 oder 0 gesetzt
	private int keysDown; // die Anzahl der momentan gedrückten Tasten

	public NetworkKeyReceiver(){
		this(256); // default soll 256 Bit fassen (32 byte)
	}
	
	public void keyPressed(int keyCode) {
		if (keyCode < 0 || keyCode >= keyDown.length * 8)
			return;
		keyDown[keyCode / 8] |= (1 << (keyCode % 8));
		keysDown++;
	}
	
	public void keyReleased(int keyCode) {
		if (keyCode < 0 || keyCode >= keyDown.length * 8)
			return;
		// setze das zu diesem keyCode gehörige Bit auf 0
		keyDown[keyCode / 8] &= ~(1 << (keyCode % 8));
		keysDown--;
	}
	
	/**
	* Überprüft, ob die Taste zu einem gegeben KeyCode (z.B. KeyEvent.VK_ALT)
	* im Moment gedrückt wird. KeyCodes außerhalb des speicherbaren Bereichs
	* werden ignoriert (return false).
	* 
	* @param keyCode: Der KeyCode der Taste, zu welcher die Abfrage gestellt
	* wird.
	* @return: true falls die zu keyCode gehörende Taste gedrückt wird.
	*/
	public boolean isKeyDown(int keyCode) {
		// false falls keyCode zu groß für den reservierten Bereich
		if (keyCode < 0 || keyCode >= keyDown.length * 8)
			return false;
		return ((keyDown[keyCode / 8] & (1 << (keyCode % 8))) != 0);
	}
	
	/**
	* Überprüft, ob die Taste zu einem gegeben KeyCode (z.B. KeyEvent.VK_ALT)
	* im Moment nicht gedrückt wird. KeyCodes außerhalb des speicherbaren
	* Bereichs werden ignoriert (return false).
	* 
	* @param keyCode: Der KeyCode der Taste, zu welcher die Abfrage gestellt
	* wird.
	* @return: false falls die zu keyCode gehörende Taste gedrückt wird.
	*/
	public boolean isKeyUp(int keyCode) {
		return !isKeyDown(keyCode);
	}
	
	/**
	* Überprüft, ob irgendeine Taste im Moment gedrückt wird.
	* 
	* @return true falls eine beliebige Taste gedrückt wird.
	*/
	public boolean isAnyKeyDown() {
		return keysDown > 0;
	}
}
