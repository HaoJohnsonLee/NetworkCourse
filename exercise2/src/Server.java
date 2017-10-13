import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    public Server(int port){
        try {
            serverSocket=new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen(){
        while(true){
            BufferedReader requestReader=null;
            StringBuilder requestBuilder=new StringBuilder();
            try {
                Socket client=serverSocket.accept();
                requestReader=new BufferedReader(new InputStreamReader(client.getInputStream()));
                //the first line
                String line1=requestReader.readLine();
                String method=line1.substring(0,line1.indexOf('/'));
                System.out.println(method);
                if(method.equalsIgnoreCase("GET ")){
                    getHandler(requestReader,line1);
                }
                /*while((temp=requestReader.readLine())!=null){
                    requestBuilder.append(temp);
                }*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void getHandler(BufferedReader requestReader,String line1){
        try {
            String temp=requestReader.readLine();
            String path=line1.substring(line1.indexOf('/'),line1.lastIndexOf('/')-5);
            FileInputStream fileInputStream=new FileInputStream(new File(path));
            System.out.println(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
