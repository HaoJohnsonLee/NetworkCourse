import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Johnson on 2017/10/14.
 */
public class ThreadedServer implements Runnable{

    private final static int CODE_OK = 200;
    private final static int CODE_NOT_FOUND = 404;
    private final static int CODE_CREATE=201;
    private final static String OK = "OK";
    private final static String NOT_FOUND = "Not Found";
    private final static String CREATE = "Created";
    private final static String TYPE_PNG="Content-Type: image/png";
    private final static String TYPE_JPEG="Content-Type: image/jpeg";
    private final static String TYPE_DOC="Content-Type: application/msword";
    private final static String TYPE_HTML="Content-Type: text/html";
    private final static String TYPE_TXT="Content-Type: text/plain";
    private Socket socket;
    public ThreadedServer(Socket socket){
        this.socket=socket;
    }
    @Override
    public void run() {
        BufferedReader requestReader = null;
        StringBuffer requestBuilder = new StringBuffer();
        OutputStream response = null;
        if(socket==null){
            return;
        }
        try {
            requestReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //the first line
            String line1 = requestReader.readLine();
            String method = line1.substring(0, line1.indexOf('/'));
            if (method.equalsIgnoreCase("GET ")) {
                response = socket.getOutputStream();
                GETHandler(requestReader, line1, response);
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    /**
     * @Author: Johnson
     * @param requestReader 请求字符读取对象
     * @param line1 请求头的第一行 "GET path HTTP/1.0"
     * @param out 响应流
     * @return void
     * @Description: 处理客户端发送的GET请求 并返回响应
     * @Date: 2017/10/14
     */
    private void GETHandler(BufferedReader requestReader, String line1, OutputStream out) {
        FileInputStream fileResponse = null;
        StringBuffer responseHeader = new StringBuffer("HTTP/1.0 ");
        File resource = null;
        byte[] data = null;
        try {
            //String temp = requestReader.readLine();
            String path = line1.substring(line1.indexOf('/') + 1, line1.lastIndexOf('/') - 5);
            String fileFormate = path.substring(path.lastIndexOf('.') + 1);
            resource = new File(path);
            if (!resource.exists()) {
                throw new IOException("File not found");
            }
            responseHeader.append(CODE_OK).append(" ").append(OK).append(Client.NEWLINE);
            //判断文件类型，添加适当内容
            if (fileFormate.equalsIgnoreCase("png")) {
                responseHeader.append(TYPE_PNG).append(Client.NEWLINE);
            } else if (fileFormate.equalsIgnoreCase("jpg") || fileFormate.equalsIgnoreCase("jpeg")) {
                responseHeader.append(TYPE_JPEG).append(Client.NEWLINE);
            } else if (fileFormate.equalsIgnoreCase("doc") || fileFormate.equalsIgnoreCase("docx")) {
                responseHeader.append(TYPE_DOC).append(Client.NEWLINE);
            } else if (fileFormate.equalsIgnoreCase("htm") || fileFormate.equalsIgnoreCase("html")) {
                responseHeader.append(TYPE_HTML).append(Client.NEWLINE);
            } else if (fileFormate.equalsIgnoreCase("txt")) {
                responseHeader.append(TYPE_TXT).append(Client.NEWLINE);
            }

            //添加文件长度
            responseHeader.append("Content-Length: ").append(resource.length()).append(Client.NEWLINE);
            responseHeader.append(Client.NEWLINE);
            out.flush();
            //写入文件
            fileResponse = new FileInputStream(resource);
            data = new byte[fileResponse.available()];
            fileResponse.read(data);
        } catch (IOException e) {
            responseHeader.append(CODE_NOT_FOUND).append(" ").append(NOT_FOUND).append(Client.NEWLINE);
            responseHeader.append(Client.NEWLINE);
        } finally {
            try {
                //写入头信息
                out.write(responseHeader.toString().getBytes());
                if (data != null) {
                    out.write(data);
                }
                Client.closeOutputStream(out);
                Client.closeInputStream(fileResponse);
                System.out.println(responseHeader.toString());
                System.out.println("<<<<<<<<<<<<<<<<<<<<");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @Author: Johnson
     * @param request 客户端请求
     * @param line1 请求头第一行
     * @param out 服务器响应
     * @Description: 处理客户端PUT请求，并响应
     * @Date: 2017/10/14
     */
    private void PUTHandler(InputStream request, String line1, OutputStream out){
        BufferedReader requestReader = new BufferedReader(new InputStreamReader(request));
        StringBuffer response=new StringBuffer("HTTP/1.0 ").append(CODE_CREATE).
                append(" ").append(CREATE).append(Client.NEWLINE);
        String path=line1.split(" ")[1];
        FileOutputStream fos=null;
        try{
            String type=null,lengthStr=null,host=null;
            //读取文件头
            String temp;
            while((temp=requestReader.readLine())!=null){
                if(temp.startsWith("Content-Type: ")){
                    type=temp.split(" ")[1];
                }else if(temp.startsWith("Content-Length: ")){
                    lengthStr=temp.split(" ")[1];
                }else if(temp.startsWith("Host: ")){
                    host=temp.split(" ")[1];
                }
            }
            if(lengthStr==null){
                return;
            }
            File file=new File(path);
            fos=new FileOutputStream(file);
            int length=Integer.parseInt(lengthStr);
            byte[] tempBytes;
            if(length>2048){
                tempBytes=new byte[2048];
            }else{
                tempBytes=new byte[length];
            }
            int readLength=0;
            while((readLength=request.read(tempBytes))!=-1){
                fos.write(tempBytes,0,readLength);
            }
            //返回响应
            response.append("location: ").append("http://").append(host).append(path).append(Client.NEWLINE);
            response.append("Content-Type: ").append(type).append(Client.NEWLINE);
            response.append("Content-Length: ").append(file.length()).append(Client.NEWLINE);
            response.append(Client.NEWLINE);
            response.append(host).append(path);
            out.write(response.toString().getBytes());
            out.flush();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }finally {
            Client.closeOutputStream(fos);
            Client.closeOutputStream(out);
        }
    }
}
