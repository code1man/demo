package org.example.demo;

import org.example.demo.utils.CloseUtil;
import org.example.demo.utils.TCPReceiveUtil;
import org.example.demo.utils.TCPSendUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * 创建服务器
 * 1、指定端口 使用SeverSocket创建服务器
 * 2、阻塞式等待连接 accept
 * 3、操作：输入输出流操作
 * 4、释放资源
 */

public class Server {

    private static CopyOnWriteArrayList<Client> all = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("-----Server-----");

        try (// 1、指定端口 使用SeverSocket创建服务器
             ServerSocket server = new ServerSocket(8888);
             ServerSocket serverSocket = new ServerSocket(7777);) {
            // 2、阻塞式等待连接 accept
            while (true) {
                Socket client = server.accept();
                Socket socket = serverSocket.accept();
                Client c = new Client(client, socket);
                all.add(c); // 管理所有的成员
                c.run();
            }
        }
        // 一般 server不会停止
    }

    static class Client {
        private final Socket client;
        private final Socket secondClient;

        private String name;

        private boolean isRunning = false;
        private final TCPSendUtil send;
        private final TCPReceiveUtil receive;
        private final TCPSendUtil send2;
        private final TCPReceiveUtil receive2;

        public Client(Socket client, Socket secondClient) {
            this.client = client;
            this.secondClient = secondClient;
            isRunning = true;
            this.send = new TCPSendUtil(client);
            this.receive = new TCPReceiveUtil(client);
            this.send2 = new TCPSendUtil(secondClient);
            this.receive2 = new TCPReceiveUtil(secondClient);

            System.out.println("一个客户建立了链接");
        }

        public void run() {
            new Thread(() -> {
                while (isRunning) {
                    String order = receive2.receiveUTF();
                    if (order != null)
                        send2.sendUTF(order);
                }
            }).start();
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
            CloseUtil.close(client);
            all.remove(this);
        }

        public Socket getTargetClient(String target) {
            for (Client c : all) {
                if (c.name.equals(target))
                    return c.client;
            }
            return null;
        }
    }
}

