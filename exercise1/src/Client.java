import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {
    public static String NEWLINE=System.getProperty("line.separator");
    private Socket socket;
    private String host;
    private int port;
    private final int PORT=80;
    public Client(String host){
        try{
            this.host=host;
            this.port=PORT;
            socket = new Socket(host, PORT);
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
    public Client(String host,int port){
        try{
            this.host=host;
            this.port=port;
            socket = new Socket(host, port);
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    public void doGet(String path){

        StringBuilder request=new StringBuilder();
        request.append("GET ").append(path).append(" HTTP/1.0").append(NEWLINE);
        request.append("Host: ").append(host).append(":").append(port).append(NEWLINE);
        request.append(NEWLINE);
        BufferedWriter writer=null;
        try {
            //send request
            writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(request.toString());
            writer.flush();
            //get reply msg
            BufferedInputStream response=new BufferedInputStream(socket.getInputStream());

        }catch (IOException ioe){
            System.err.println(ioe.getMessage());
        }finally {
            try {
                if(writer!=null) {
                    writer.close();
                }
            } catch (IOException ioe) {
                System.err.println(ioe.getMessage());
            }
        }
    }
}
