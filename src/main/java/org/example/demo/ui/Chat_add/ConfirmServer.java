package org.example.demo.ui.Chat_add;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


public class ConfirmServer {
    private ServerSocket serverSocket;
    private List<PrintWriter> clientWriters;

    public ConfirmServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientWriters = new ArrayList<>();
    }

    public void start() throws IOException {
        while (true) {
            Socket clientSocket = serverSocket.accept();
            synchronized (clientWriters) {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(out);
                new ClientHandler(clientSocket,out).start();
            }
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket, PrintWriter out) throws IOException {
            this.clientSocket = socket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = out;
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    broadcastMessage(message, out); // Broadcast message to all clients
                }

                //System.out.println("广播成功");
            } catch (SocketException e) {
                System.out.println("广播不成功");
                System.out.println("连接被重置: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastMessage(String message, PrintWriter sender) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    if (writer != sender) {  // 排除发送者，不给自己广
                        writer.println(message);  // 向其他客户端发送消息
                        System.out.println("dasfa");
                    }
                    else{
                        System.out.println("aaaaaa");
                    }
                }
            }
        }
    }


        public static void main(String[] args) {
        try {
            ConfirmServer server = new ConfirmServer(9999); // 启动服务器并监听端口 9999
            System.out.println("监听语音通话点击事件运行中");
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
