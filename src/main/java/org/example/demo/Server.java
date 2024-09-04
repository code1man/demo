package org.example.demo;

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
             ServerSocket server = new ServerSocket(8888)) {
            // 2、阻塞式等待连接 accept
            while (true) {
                Socket client = server.accept();
                Client c = new Client(client);
                all.add(c); // 管理所有的成员
                new Thread(c).start();
            }
        }
        // 一般 server不会停止
    }

    static class Client implements Runnable {
        private final Socket client;

        private String name;

        private boolean isRunning = false;

        public Client(Socket client) {
            this.client = client;
            isRunning = true;
            System.out.println("一个客户建立了链接");
        }

        @Override
        public void run() {
            while (isRunning) {

                // 这个根据自己写的部分按照需要写
            }
        }

        public void stop(){
            isRunning = false;
        }

        public void restart(){
            isRunning = true;
        }
    }
}

