package org.example.demo.utils;

import java.io.*;
import java.net.Socket;

public class TCPReceiveUtil implements Closeable{

    private final Socket client;
    private DataInputStream dis;
    public TCPReceiveUtil(Socket client) {
        this.client = client;
        try {
            this.dis = new DataInputStream(this.client.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //接受字符串
    public String receiveUTF() {
        try {
            return dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("接收字符串失败");
        }
        return null;
    }

    //接受整型
    public int receiveInt() {
        try {
            DataInputStream dis = new DataInputStream(this.client.getInputStream());
            return dis.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("接受好友申请失败");
        }
        return 0;
    }

    //接收文件、对象
    public Object receiveObject() {
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(client.getInputStream()))) {
            return ois.readObject();
        } catch (IOException e) {
            System.out.println("接收对象失败: ");
        } catch (ClassNotFoundException e) {
            System.out.println("接收对象失败: 对象类型未找到");
            release();
        }
        return null;
    }

    //接收图片——注意返回值是Byte[]！！！！
    public byte[] receiveImg() {
        try {
            // 读取图像数据长度
            int length = dis.readInt();
            // 读取图像数据
            byte[] imageData = new byte[length];
            dis.readFully(imageData);
            return imageData;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("接收图片失败");
            return null;
        }
    }

    public void release() {
        CloseUtil.close(client);
    }

    @Override
    public void close() throws IOException {
        if (dis != null)
            dis.close();
    }
}

