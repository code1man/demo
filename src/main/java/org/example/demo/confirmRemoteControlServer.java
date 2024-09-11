package org.example.demo;

import org.example.demo.utils.DbUtil;
import org.example.demo.utils.TCPReceiveUtil;
import org.example.demo.utils.TCPSendUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class confirmRemoteControlServer {

    private static CopyOnWriteArrayList<Client> all = new CopyOnWriteArrayList<>();

    public static void main(String[] args){
        System.out.println("-----ConfirmRemoteControlServer-----");

        try (// 1、指定端口 使用SeverSocket创建服务器
             ServerSocket serverSocket = new ServerSocket(5200)) {
            // 2、阻塞式等待连接 accept
            while (true) {
                Socket socket = serverSocket.accept();
                Client c = new Client(socket);

                all.add(c); // 管理所有的成员
                c.run();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class  Client implements Runnable {

        public int uip;
        private TCPSendUtil send = null;
        private TCPReceiveUtil receive = null;

        public Socket client;

        public Client(Socket client) {
            this.client = client;
            this.send = new TCPSendUtil(client);
            this.receive = new TCPReceiveUtil(client);
            this.uip = receive.receiveInt();
            System.out.println("接收id：" + uip);

            System.out.println("一个客户建立了链接");
        }

        @Override
        public void run() {
            new Thread(() -> {
                for (int i = 0; i < 2; i++) {
                    String order = receive.receiveUTF();

                    if (order != null) {
                        System.out.println("接收到的命令" + order);
                        String[] request = order.split(" ");

                        if (request[0].equals(("REJECTREMOTECONTROL"))) {
                            selectClient(DbUtil.getID(request[1])).send.sendUTF("REMOTECONTROLEND#" + DbUtil.getUserName(uip));
                        }
                        if (request[0].equals("ACCEPTREMOTECONTROL")) {
                            selectClient(DbUtil.getID(request[1])).send.sendUTF("ACCEPTREMOTECONTROL#" + DbUtil.getUserName(uip));
                        }
                    }
                }
            }).start();
        }

        private Client selectClient(int userID) {
            System.out.println(all);
            for (Client c: all) {
                if (userID == c.uip) {
                    return c;
                }
            }
            return null;
        }
    }
}
