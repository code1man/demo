package org.example.demo;

import org.example.demo.utils.CloseUtil;
import org.example.demo.utils.DbUtil;
import org.example.demo.utils.TCPReceiveUtil;
import org.example.demo.utils.TCPSendUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class CameraServer {

    private static CopyOnWriteArrayList<Client> all = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("-----CameraServer-----");

        try (
             ServerSocket server = new ServerSocket(8848);
             ServerSocket serverSocket = new ServerSocket(8849);) {
            while (true) {
                Socket client = server.accept();
                Socket client1 = serverSocket.accept();
                Client c = new Client(client, client1);
                all.add(c);
                c.run();
                if (c.client.isClosed())
                    c.release();
            }
        }
    }

    static class Client {
        private final Socket client;

        private int uid;
        private int fuid;
        private boolean isRunning = false;
        private TCPSendUtil send;
        private TCPReceiveUtil receive;

        public Client(Socket client, Socket serverSocket) {
            this.client = client;
            isRunning = true;
            this.send = new TCPSendUtil(client);
            this.receive = new TCPReceiveUtil(client);

            String message = new TCPReceiveUtil(serverSocket).receiveUTF();
            String[] each = null;
            if (message != null) {
                each = message.split(" ");
                uid = Integer.parseInt(each[0]);
                fuid = DbUtil.getID(each[1]);
            }
            System.out.println("视频：" + uid + " " + fuid);

            System.out.println("一个客户建立了视频链接");
        }

        public void run() {
            new Thread(() -> {

                while (true) {
                    byte[] image = receive.receiveImg();
                    System.out.println(image);
                    if (image != null && selectClient(fuid) != null) {
                        selectClient(fuid).send.sendImg(image);
                    }
                    // send.sendImg(image);
                    // 这个根据自己写的部分按照需要写
                }
            }).start();
        }

        public void stopCamera(){
            isRunning = false;
        }

        public void startCamera(){
            isRunning = true;
        }

        public void release() {
            isRunning = false;
            CloseUtil.close(client, send, receive);
            all.remove(this);
        }

        private Client selectClient(int userID) {
            System.out.println(all);
            for (Client c: all) {
                if (userID == c.uid) {
                    return c;
                }
            }
            return null;
        }
    }
}

