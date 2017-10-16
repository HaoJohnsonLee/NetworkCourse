public class ServerMain {
    public static void main(String[] args) {
        Server server=new Server(80);
        server.listen();
    }
}
