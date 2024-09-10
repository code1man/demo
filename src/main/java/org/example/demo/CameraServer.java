package org.example.demo;

import org.example.demo.utils.CloseUtil;
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
             ServerSocket server = new ServerSocket(8848)) {
            while (true) {
                Socket client = server.accept();
                Client c = new Client(client);
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
        private boolean isRunning = false;
        private final TCPSendUtil send;
        private final TCPReceiveUtil receive;

        public Client(Socket client) {
            this.client = client;
            isRunning = true;
            this.send = new TCPSendUtil(client);
            this.receive = new TCPReceiveUtil(client);

            System.out.println("一个客户建立了链接");
        }

        public void run() {
            new Thread(() -> {
                while (isRunning) {
                    byte[] image = receive.receiveImg();
                    if (image != null)
                        send.sendImg(image);
                    // 这个根据自己写的部分按照需要写
                }
            }).start();
        }

        public void stop(){
            isRunning = false;
        }

        public void restart(){
            isRunning = true;
        }

        public void release() {
            isRunning = false;
            CloseUtil.close(client, send, receive);
            all.remove(this);
        }

        public Socket getTargetClient(int targetUid) {
            for (Client c : all) {
                if (c.uid == targetUid)
                    return c.client;
            }
            return null;
        }
    }
}

