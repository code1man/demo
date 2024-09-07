package org.example.demo.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.demo.Client;
import org.example.demo.utils.DbUtil;
import org.example.demo.utils.TCPReceiveUtil;
import org.example.demo.utils.TCPSendUtil;

import javax.swing.text.Utilities;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class chat extends Application {

    private TextArea chatArea = new TextArea();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image("logo.jpg"));
        // 根布局 Pane，用于设置背景
        Pane root = new Pane();
        root.setStyle("-fx-background-image: url('back01.png'); -fx-background-size: cover;");

        // 对话框部分 (白色背景)
        chatArea.setEditable(false); // 只允许查看信息，不可编辑
        chatArea.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-border-color: black;");
        chatArea.setPrefHeight(400); // 设置高度

        // 工具栏，包含视频通话和语音通话
        HBox toolbar = new HBox(10); // 10 为按钮之间的间距
        Button videoCall = new Button("视频通话");
        Button voiceCall = new Button("语音通话");
        toolbar.getChildren().addAll(videoCall, voiceCall);
        toolbar.setStyle("-fx-padding: 5; -fx-border-color: black; -fx-border-radius: 5;");

        // 发送消息框 (淡蓝色背景，有弧度)
        TextField messageInput = new TextField();
        messageInput.setPromptText("请输入信息发送");
        messageInput.setStyle("-fx-background-color: lightblue; -fx-background-radius: 10; -fx-border-radius: 10;");
        messageInput.setPrefHeight(40); // 设置输入框的高度
        messageInput.setPrefWidth(450); // 设置输入框的宽度

        // 发送按钮 (深蓝色背景，弧度按钮)
        Button sendButton = new Button("发送信息");
        sendButton.setStyle("-fx-background-color: darkblue; -fx-text-fill: white; -fx-background-radius: 10;");
        sendButton.setPrefSize(100, 40); // 设置按钮大小

        // 消息发送事件，点击发送后将信息显示在对话框中
        sendButton.setOnAction(e -> {
            String message = messageInput.getText();
            if (!message.isEmpty()) {
                //存入数据库和txt文件中


                try {
                    String sender = Client.uid;
                    //转成id
                    String sql =  "SELECT userid FROM t_users WHERE username = ?";
                    ArrayList<Object> arrayList = new ArrayList<>();
                    arrayList.add(primaryStage.getTitle());

                    ResultSet resultSet = DbUtil.executeQuery(sql,arrayList);
                    String request = "";
                    if(resultSet.next()) {
                        String receiver = resultSet.getString("userid");
                        //还没弄入文件
                        String messageText = message;
                      request = "INFORMATION"+" "+sender+" "+receiver+" "+messageText;
                    }



                    try {
                        Client.client = new Socket("127.0.0.1",7777);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    TCPSendUtil sendUtil = new TCPSendUtil(Client.client );
                    TCPReceiveUtil receiveUtil = new TCPReceiveUtil(Client.client) ;
                    sendUtil.sendUTF(request);

                    // 获取当前时间戳
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    // 追加信息到 txt 文件
                    String chatLine = Client.name + "   " + message + "    " + timestamp + "    ";
                    saveMessageToFile(primaryStage.getTitle(), chatLine);


                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                chatArea.appendText(Client.name+"   "+message + "   "+Timestamp.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) + "\n");
                messageInput.clear(); // 清空输入框
            }
        });

        // 底部的消息输入和发送按钮布局
        HBox messageBox = new HBox(10); // 10 为输入框和按钮之间的间距
        messageBox.getChildren().addAll(messageInput, sendButton);
        messageBox.setAlignment(Pos.CENTER); // 将消息输入框和按钮居中排列

        // 总体布局，将所有组件按顺序放入
        VBox layout = new VBox(10); // 10 为组件之间的间距
        layout.getChildren().addAll(chatArea, toolbar, messageBox);
        layout.setPadding(new Insets(20)); // 设置内边距
        layout.setAlignment(Pos.BOTTOM_CENTER); // 设置总体布局居中

        // 将布局添加到根 Pane 中
        root.getChildren().add(layout);

        // 场景设置，调整页面大小
        Scene scene = new Scene(root, 600, 550); // 调整页面大小为 800x600
       // primaryStage.setTitle("聊天窗口");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //写进txt
    public void saveMessageToFile(String friendname, String message) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(  DbUtil.getID(friendname) + "_chat.txt", true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        launch(args);
    }

    public TextArea getChatArea() {
        return chatArea;
    }
}
