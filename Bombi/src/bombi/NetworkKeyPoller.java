package bombi;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Eine Erweiterung des KeyPollers. Es wird nicht nur lokal festgehalten, welche
 * Tasten gedrueckt sind, sondern auch beim Druecken oder Loslassen einer Taste
 * ein entfernter PC informiert.
 * 
 * @author tobi
 * 
 */
public class NetworkKeyPoller extends KeyPoller {

    private BufferedWriter toSocket;

    public NetworkKeyPoller(BufferedWriter toSocket) {
        super();
        this.toSocket = toSocket;
    }

    @Override
    protected void keyPressed(int keyCode) {
        super.keyPressed(keyCode);
        try {
            toSocket.write(ServerBombiGui.PRESSED + keyCode + "\n\r");
            toSocket.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void keyReleased(int keyCode) {
        super.keyReleased(keyCode);
        try {
            toSocket.write(ServerBombiGui.RELEASED + keyCode + "\n\r");
            toSocket.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
