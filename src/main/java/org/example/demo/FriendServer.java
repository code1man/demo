package org.example.demo;

import org.example.demo.utils.TCPReceiveUtil;
import org.example.demo.utils.TCPSendUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class FriendServer {
    private static CopyOnWriteArrayList<Client> all = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1314);
            Client c = new Client(serverSocket.accept());
            all.add(c);
            c.run();
        } catch (IOException e) {
            System.out.println("好友信息服务器启动失败");
        }
    }

    static class Client{
        private int uid;
        private Socket client;
        private TCPReceiveUtil tcpReceiveUtil;
        private TCPSendUtil tcpSendUtil;

        public Client(Socket client) {
            this.client = client;
            tcpReceiveUtil = new TCPReceiveUtil(client);
            tcpSendUtil = new TCPSendUtil(client);
        }

        public void run() {
            new Thread(() -> {
                while (true) {
                    int targetUid = tcpReceiveUtil.receiveInt();
                    boolean isOnline = false;
                    for (Client c : all) {
                        isOnline = c.uid == targetUid;
                        if (isOnline) {
                            c.tcpSendUtil.sendInt(this.uid);
                            break;
                        }
                    }
                    if (!isOnline) {
                        // 添加离线好友申请到数据库
                    }
                }
            }).start();

            new Thread(() -> {
                while (true) {
                    String replyMsg = tcpReceiveUtil.receiveUTF();
                    if (replyMsg.length() <= 0)
                        continue;
                    String[] replySplit = replyMsg.split(":");
                    if (replySplit.length == 2){
                        int targetUid = Integer.parseInt(replySplit[0]);
                        if (replySplit[1].equals("同意")) {
                            // 添加到数据库
                            // 更改状态
                        } else {
                            // 更改状态
                        }
                        tcpSendUtil.sendUTF(replyMsg);
                    }
                }
            }).start();
        }
    }
}
