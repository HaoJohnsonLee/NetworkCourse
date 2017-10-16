import java.util.Date;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        int port=80;
        String host,path;
        Scanner scanner=new Scanner(System.in);
        System.out.println("Input a host name(default: www.baidu.com):");
        host=scanner.nextLine();
        System.out.println("Input a path（default: /）:");
        path=scanner.nextLine();
        scanner.close();
        if(host.equals("")){
            host="www.baidu.com";
        }if(path.equals("")){
            path="/";
        }
        System.out.println("尝试连接至 "+host +path);
        Client client = new Client(host, port);
        client.doGet(path);
        System.out.println(new Date());


    }
}
