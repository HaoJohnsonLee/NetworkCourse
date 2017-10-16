
import java.io.*;
import java.net.Socket;
/**
 * @Author: Johnson
 * @Description: Socket http客户端
 * @Date: 2017/10/14
 */
public class Client {
    public static String NEWLINE = System.getProperty("line.separator");
    private Socket socket;
    private final int PORT = 80;

    public Client(String host) {
        try {
            socket = new Socket(host, PORT);
            if(socket==null){
                throw new Exception();
            }
        } catch (IOException e) {
            System.err.println("无法连接至: "+ host+": "+PORT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Client(String host, int port) {
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            System.err.println("无法连接至: "+ host+": "+port);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * @Author: Johnson
     * @param path 请求文档路径
     * @return void
     * @Description: 向服务器发送GET请求
     * @Date: 2017/10/14
     */
    public void doGet(String path) {
        if(socket==null)return;
        StringBuilder request = new StringBuilder();
        //开始组织请求头
        request.append("GET ").append(path).append(" HTTP/1.0").append(NEWLINE);
        request.append("Host: ").append(socket.getInetAddress().getHostName()).append(":").append(socket.getPort()).append(NEWLINE);
        request.append(NEWLINE);
        //结束
        BufferedWriter writer = null;
        BufferedInputStream response = null;
        try {
            //发送请求
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(request.toString());
            writer.flush();
            //得到响应
            response = new BufferedInputStream(socket.getInputStream());
            GEThandler(response, path);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        finally {
            closeWriter(writer);
            closeInputStream(response);
        }
    }

    /**
     * @Author: Johnson
     * @param response BufferedInputStream类型的响应流
     * @param path 请求文件的路径
     * @return void
     * @Description: GET类请求服务器响应处理方法
     * @Date: 21:33 2017/10/14
     */
    private void GEThandler(BufferedInputStream response, String path) {
        StringBuilder header = new StringBuilder();
        BufferedReader responseReader = new BufferedReader(new InputStreamReader(response));
        FileOutputStream fos = null;
        try {
            String temp;
            String fileLengthStr="0";
            String fileFormate="html";
            temp = responseReader.readLine();
            String codeStr = temp.split(" ")[1];
            //获取响应状态码  404不处理
            int code = Integer.parseInt(codeStr);
            if (code == 404) {
                System.out.println(temp);
                return;
            }
            header.append(temp).append(NEWLINE);
            //继续读取响应头
            while ((temp = responseReader.readLine()) != null && !temp.equals("") && !temp.equals(NEWLINE)) {
                header.append(temp).append(NEWLINE);
                if (temp.startsWith("Content-Length: ")) {
                    fileLengthStr = temp.split(": ")[1];
                }else if(temp.startsWith("Content-Length: ")){
                    //获取文件格式
                    fileFormate=temp.split(" ")[1].split("/")[1];
                }
            }
            header.append(NEWLINE);
            System.out.println(header.toString());
            System.out.println(">>>>>>>>>>头信息结束<<<<<<<<<<<");
            int headerLength = header.toString().getBytes().length;
            int bodyLength = Integer.parseInt(fileLengthStr);
            byte[] fielBytes;
            if(bodyLength>2048){
                fielBytes = new byte[2048];
            }else if(bodyLength==0){
                return;
            }
            else{
                fielBytes = new byte[bodyLength];
            }
            //获取文件名
            String fileName = path.substring(path.lastIndexOf('/'));
            if(fileName.equals("/")){
                fileName="/"+socket.getInetAddress().getHostName()+"."+fileFormate;
            }
            File file = new File("clientFiles" + fileName);
            fos = new FileOutputStream(file);
            //文件主体读取
            int byteRead=0;
            //response.skip(headerLength);
            while((byteRead=response.read(fielBytes))!=-1){
                fos.write(fielBytes,0,byteRead);
            }
            System.out.println("接收到的文件长度:" +file.length());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeOutputStream(fos);
            closeReader(responseReader);
        }
    }

    public static void closeOutputStream(OutputStream os) {
        try {
            if (os != null) {
                os.flush();
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void closeInputStream(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void closeWriter(Writer writer) {
        try {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void closeReader(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void closeSocket(Socket socket) {
        try {
            if (socket != null&& !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
