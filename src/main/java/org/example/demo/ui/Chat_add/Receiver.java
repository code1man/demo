package org.example.demo.ui.Chat_add;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Receiver {
    private ServerSocket serverSocket;
    private List<ClientHandler> clientWriters;

    public Receiver(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientWriters = new ArrayList<>();
    }

    public void start() throws IOException {
        while (true) {
            Socket clientSocket = serverSocket.accept();
            synchronized (clientWriters) {
                ClientHandler c = new ClientHandler(clientSocket);
                clientWriters.add(c);
                c.start();
            }
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private int uid;

        public ClientHandler(Socket socket) throws IOException {
            this.clientSocket = socket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            uid = Integer.parseInt(in.readLine());
            System.out.println(uid);
            System.out.println("一个用户连接");
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                    broadcastMessage(message); // Broadcast message to all clients
                }
            }
            catch (SocketException e) {
                System.out.println("连接被重置: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                synchronized (clientWriters) {
                    clientWriters.remove(clientSocket);
                }
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastMessage(String message) {
            synchronized (clientWriters) {
                String[] info = message.split(":");
                System.out.println(info[0]);
                for (ClientHandler c: clientWriters) {
                    if (c.uid == Integer.parseInt(info[0])) {
                        c.out.println(info[1]);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Receiver server = new Receiver(10086); // 启动服务器并监听端口 10086
            System.out.println("Server运行中");
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
