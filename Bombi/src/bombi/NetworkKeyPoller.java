package bombi;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Eine Erweiterung des KeyPollers. Es wird nicht nur lokal
 * festgehalten, welche Tasten gedrueckt sind, sondern auch
 * beim Druecken oder Loslassen einer Taste ein entfernter
 * PC informiert.
 * @author tobi
 *
 */
public class NetworkKeyPoller extends KeyPoller {

    public static final String PRESSED = "PR";
    public static final String RELEASED = "RL";

    private Socket socket;
    private BufferedWriter toServer;

    public NetworkKeyPoller(Socket socket) throws IOException {
    	super();
        this.socket = socket;
        toServer = new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream()));
    }

    @Override
    protected void keyPressed(int keyCode) {
    	super.keyPressed(keyCode);
        try {
            toServer.write(PRESSED + keyCode + "\n");
            toServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void keyReleased(int keyCode) {
    	super.keyReleased(keyCode);
        try {
            toServer.write(RELEASED + keyCode + "\n");
            toServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
