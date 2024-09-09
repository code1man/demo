package org.example.demo.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Popup;
import org.example.demo.Client;
import org.example.demo.Main;
import org.example.demo.controller.shenqingController;

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

    public void recieveFriendApllication() {
        int fuid = receive.receiveInt();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("popup.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Popup popup = new Popup();
        popup.getContent().add(root);

        // 从服务器数据库读来的数据
        shenqingController controller = loader.getController();
        controller.updateLabels("张三", "快乐的程序员", "男", "/touxiang");

        // Position the popup near the button (e.g., bottom-right)
        popup.show(Main.stage);
    }

    public void replyFriendRequest(int fuid, String reply) {
        send.sendUTF(fuid + ":" + reply);
    }

    public String recieveReplyFriendRequest() {
        String recieveReply = receive.receiveUTF();
        return recieveReply;
    }
}
