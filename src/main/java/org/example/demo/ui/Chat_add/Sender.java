package org.example.demo.ui.Chat_add;

import org.example.demo.utils.DbUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Sender {
    public int uid;
    public int friendId;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Sender(String host, int port,int uid,String friendName) throws IOException {
        this.uid = uid;
        this.friendId = DbUtil.getID(friendName);
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        if (port == 10086)
            out.println(uid);
    }

    public void sendMessage(String message) {
        System.out.println(message);
        out.println(friendId + ":" + message);
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
