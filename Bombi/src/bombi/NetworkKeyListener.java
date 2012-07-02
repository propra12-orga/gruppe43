package bombi;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class NetworkKeyListener extends AbstractTimedKeyListener {

    public static final String PRESSED = "PR";
    public static final String RELEASED = "RL";

    private Socket socket;
    private BufferedWriter toServer;

    public NetworkKeyListener(Socket socket) throws IOException {
        this.socket = socket;
        toServer = new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream()));
    }

    @Override
    protected void keyPressed(int keyCode) {
        try {
            toServer.write(PRESSED + keyCode + "\n");
            toServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void keyReleased(int keyCode) {
        try {
            toServer.write(RELEASED + keyCode + "\n");
            toServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
