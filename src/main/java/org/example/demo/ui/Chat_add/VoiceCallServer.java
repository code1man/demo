package org.example.demo.ui.Chat_add;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VoiceCallServer {
    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("语音通话服务器启动，等待客户端连接...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(clientSocket);
            clientHandlers.add(handler);
            handler.start();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private InputStream in;
        private OutputStream out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    broadcast(buffer, bytesRead);
                }
            }
            catch (SocketException e) {
                System.out.println("连接被重置: " + e.getMessage());
            }
            catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }

        // 转发音频数据给其他客户端
        private void broadcast(byte[] data, int length) {
            synchronized (clientHandlers) {
                for (ClientHandler handler : clientHandlers) {
                    if (handler != this) {
                        try {
                            handler.out.write(data, 0, length);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private void closeConnection() {
            try {
                socket.close();
                clientHandlers.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
