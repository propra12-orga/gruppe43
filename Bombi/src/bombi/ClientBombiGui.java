package bombi;

import javax.swing.JFrame;

public class ClientBombiGui extends BombiGui {

    public ClientBombiGui(JFrame frame) {
        super(frame);
        // TODO Auto-generated constructor stub
    }

    // private void initializeNetwork() throws Exception {
    // if (server) {
    // ServerSocket serverSock = new ServerSocket(1337);
    // socket = serverSock.accept();
    // } else socket = new Socket("localhost", 1337);
    // toSocket = new BufferedWriter(new OutputStreamWriter(
    // socket.getOutputStream()));
    // fromSocket = new BufferedReader(new InputStreamReader(
    // socket.getInputStream()));
    // if (server) {
    // String packet = "LV\n" + BombermanLevel.createPacket(bLevel)
    // + "EN\n";
    // toSocket.write(packet);
    // toSocket.flush();
    // }
    // keyReceiver = new NetworkKeyReceiver();
    // new Thread(new NetworkManager()).start();
    // }

}
