package org.example.demo.ui.Chat_add;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Sender {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Sender(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendMessage(String message) {
        System.out.println(message);
        out.println(message);
    }

    public String receiveMessage() throws IOException {
        String message = in.readLine();
        System.out.println(message);
        return message;
    }

    public void close() throws IOException {
        socket.close();
    }

}
