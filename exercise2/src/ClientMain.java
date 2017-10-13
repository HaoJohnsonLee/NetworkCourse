public class ClientMain {
    public static void main(String[] args) {
        Client client =new Client("localhost",8080);
        client.doGet("/p.txt");
    }
}
