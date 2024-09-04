package org.example.demo.utils;

import java.io.*;
import java.net.Socket;

public class TCPReceiveUtil{

    private final Socket client;

    public TCPReceiveUtil(Socket client) {
        this.client = client;
    }

    //接受字符串
    public void receiveUTF() {
        try {
            DataInputStream dis = new DataInputStream(this.client.getInputStream());
            String result = dis.readUTF();
            System.out.println(result);
        } catch (IOException e) {
            System.out.println("接收字符串失败");
            release();
        }
    }

    //接收文件
    public void receiveFile() {
        try (InputStream is = new BufferedInputStream(client.getInputStream());
             OutputStream os = new BufferedOutputStream(new FileOutputStream("copy1.png"))) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();

        } catch (IOException e) {
            System.out.println("接收文件失败");
            release();
        }
    }

    //接收图片——注意返回值是Byte[]！！！！
    public byte[] receiveImg() {
        try {
            DataInputStream dis = new DataInputStream(this.client.getInputStream());
            // 读取图像数据长度
            int length = dis.readInt();
            // 读取图像数据
            byte[] imageData = new byte[length];
            dis.readFully(imageData);

            return imageData;
        } catch (IOException e) {
            System.err.println("接收图片失败");
            return null;
        }
    }

    public void release() {
        CloseUtil.close(client);
    }
}

