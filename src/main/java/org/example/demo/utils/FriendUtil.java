package org.example.demo.utils;

import org.example.demo.Client;

import java.io.IOException;
import java.net.Socket;

public class FriendUtil {

    private TCPSendUtil send;
    private TCPReceiveUtil receive;
    private final String SERVER_ADDRESS = "localhost";
    private final int SERVER_PORT = 1314;

    public FriendUtil() {
        try {
            Client.friendClient = new Socket(SERVER_ADDRESS, SERVER_PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        send = new TCPSendUtil(Client.client);
        receive = new TCPReceiveUtil(Client.client);
    }

    public void applyFriend(int fuid) {
        send.sendInt(fuid);
    }

    public void replyFriendRequest(int fuid, String reply) {
        send.sendUTF(fuid + ":" + reply);
    }


}
