import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Johnson on 2017/10/16.
 */
public class ThreadedServerMain {
    private static final int SERVER_PORT = 80;
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket=new ServerSocket(SERVER_PORT);
            while(true){
                Socket socket=serverSocket.accept();
                ThreadedServer server=new ThreadedServer(socket);
                new Thread(server).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
