package org.example.demo.ui;

import javafx.application.Application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

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
import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.example.demo.ui.Chat_add.*;


import java.io.IOException;
public class chat extends Application {
    private TextArea chatArea = new TextArea();
    private String username;
    private Button sendButton;
    private Button voiceCall;
    private TextField messageInput;
    private Sender sender;


    private String friendName;
    public chat(String username,String friendName) {
        this.username = username;
        this.friendName = friendName;
        //可能要改成数据库中记录的用户名
    }
    public void initialize() {
        try {
            // 初始化客户端并连接到服务器
            sender = new Sender("localhost", 10086); // 替换为服务器的IP和端口
            startListening();
            System.out.println("成功");
        } catch (IOException e) {System.out.println("成功个屁");

            chatArea.appendText("Failed to connect to server: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    @FXML
    public void sendMessage() {
        String message = messageInput.getText();
        if (!message.isEmpty()) {
            // 格式化消息：用户名、消息内容、时间戳
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String formattedMessage = Client.name + "   " + message + "    " + timestamp + "    ";

            String request = "SENDMESSAGE "+Client.uid+" "+ friendName+" "+message  ;
            //------------------------------------------------------------
            try {
                Client.client = new Socket("127.0.0.1",7777);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            TCPSendUtil sendUtil = new TCPSendUtil(Client.client );
            TCPReceiveUtil receiveUtil = new TCPReceiveUtil(Client.client) ;
            //--------------------------------------------------------------------
            sendUtil.sendUTF(request);


            // 将格式化消息发送到服务器
            sender.sendMessage(formattedMessage);

            // 显示发送的消息到聊天区域
            chatArea.appendText(formattedMessage + "\n");

            // 保存消息到本地txt文件
            chat.saveMessageToFile(friendName, formattedMessage);

            // 清空输入框
            messageInput.clear();
        }
    }


    // 从服务器接收消息并显示在 chatArea 中
   public   void startListening() {
        Thread thread = new Thread(() -> {
            try {
                String message;
                while ((message = sender.receiveMessage()) != null) {
                    String finalMessage = message;
                    //chatArea.appendText(finalMessage + "\n"); // 显示收到的消息
                }
            } catch (IOException e) {
                chatArea.appendText("Connection lost.\n");
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void call(ActionEvent actionEvent) {
        //实际上要用这个 VoiceCallClient.socket
        //先连接
        //------------------------------------------------------------
        try {
            Client.client = new Socket("127.0.0.1",7777);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        TCPSendUtil sendUtil = new TCPSendUtil(Client.client );
        TCPReceiveUtil receiveUtil = new TCPReceiveUtil(Client.client) ;
        //--------------------------------------------------------------------

        int sessionID = 0;
        if (!VoiceCallClient.isCalling) {
            initiateVoiceCall();

            String  hostID = Client.uid;

            String request = "VOICECHAT"+" "+hostID+" "+DbUtil.getID(friendName);


            sendUtil.sendUTF(request);

            sessionID =  Integer.parseInt(receiveUtil.receiveUTF()) ;

            voiceCall.setText("挂断通话");
        } else {
            terminateVoiceCall();

            String request = "FinishVoiceChat";
            sendUtil.sendUTF(request);
            System.out.println(receiveUtil.receiveUTF());

            voiceCall.setText("发起语音通话");
        }
    }
    private void initiateVoiceCall() {
        System.out.println("发起语音通话...");
        // 设置为正在通话状态
        VoiceCallClient.isCalling = true;

        // 启动客户端的音频捕获和发送逻辑
        try {
            new Thread(() -> {
                try {
                    VoiceCallClient.main(null); // 启动音频捕获和接收线程
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void terminateVoiceCall() {
        System.out.println("结束语音通话...");

        // 1. 停止音频捕获和播放
        VoiceCallClient.isCalling = false;

        // 2. 关闭Socket连接
        try {
            if (VoiceCallClient.socket != null && !VoiceCallClient.socket.isClosed()) {
                VoiceCallClient.socket.close();
                System.out.println("Socket连接已关闭");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        initialize();
                                               // 接受从数据库获得的用户名
        primaryStage.getIcons().add(new Image("/logo.jpg"));
        // 根布局 Pane，用于设置背景
        Pane root = new Pane();
        root.setStyle("-fx-background-image: url('/back01.png'); -fx-background-size: cover;");

        // 对话框部分 (白色背景)
        chatArea = new TextArea();

        chatArea.setEditable(false); // 只允许查看信息，不可编辑
        chatArea.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-border-color: black;");
        chatArea.setPrefHeight(400); // 设置高度

        // 工具栏，包含视频通话和语音通话
        HBox toolbar = new HBox(10); // 10 为按钮之间的间距
        Button videoCall = new Button("视频通话");

        voiceCall = new Button("语音通话");

        toolbar.getChildren().addAll(videoCall, voiceCall);
        toolbar.setStyle("-fx-padding: 5; -fx-border-color: black; -fx-border-radius: 5;");

        // 发送消息框 (淡蓝色背景，有弧度)

        messageInput = new TextField();

        messageInput.setPromptText("请输入信息发送");
        messageInput.setStyle("-fx-background-color: lightblue; -fx-background-radius: 10; -fx-border-radius: 10;");
        messageInput.setPrefHeight(40); // 设置输入框的高度
        messageInput.setPrefWidth(450); // 设置输入框的宽度

        // 发送按钮 (深蓝色背景，弧度按钮)

        sendButton = new Button("发送信息");

        sendButton.setStyle("-fx-background-color: darkblue; -fx-text-fill: white; -fx-background-radius: 10;");
        sendButton.setPrefSize(100, 40); // 设置按钮大小

        // 消息发送事件，点击发送后将信息显示在对话框中
        sendButton.setOnAction(e -> {
            sendMessage();
//            String message = messageInput.getText();
//            if (!message.isEmpty()) {
//                //存入数据库和txt文件中
//
//                try {
//                    String sender = Client.uid;
//                    //转成id
//                    String sql =  "SELECT userid FROM t_users WHERE username = ?";
//                    ArrayList<Object> arrayList = new ArrayList<>();
//                    arrayList.add(primaryStage.getTitle());
//
//                    ResultSet resultSet = DbUtil.executeQuery(sql,arrayList);
//                    String request = "";
//
//
//
//                    if(resultSet.next()) {
//                        String receiver = resultSet.getString("userid");
//                        //还没弄入文件
//                        String messageText = message;
//                      request = "INFORMATION"+" "+sender+" "+receiver+" "+messageText;
//                    }
//
//                    try {
//                        Client.client = new Socket("127.0.0.1",7777);
//                    } catch (IOException ex) {
//                        throw new RuntimeException(ex);
//                    }
//
//                    TCPSendUtil sendUtil = new TCPSendUtil(Client.client );
//                    TCPReceiveUtil receiveUtil = new TCPReceiveUtil(Client.client) ;
//                    sendUtil.sendUTF(request);
//
//                    // 获取当前时间戳
//                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//
//                    System.out.println("发送了信息");
//                    // 追加信息到 txt 文件
//                    String chatLine = Client.name + "   " + message + "    " + timestamp + "    ";
//                    saveMessageToFile(primaryStage.getTitle(), chatLine);
//
//
//                } catch (SQLException ex) {
//                    throw new RuntimeException(ex);
//                }
//
//                chatArea.appendText(Client.name+"   "+message + "   "+Timestamp.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) + "\n");
//                messageInput.clear(); // 清空输入框

          //  }
        });

        voiceCall.setOnAction(this::call);


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

        //primaryStage.setTitle("聊天窗口");

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    //写进txt
    public static void saveMessageToFile(String friendname, String message,String timestamp) {

        //先连接----------------------------------------------------------------
        try {
            Client.client = new Socket("127.0.0.1",7777);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        TCPSendUtil sendUtil = new TCPSendUtil(Client.client );
        TCPReceiveUtil receiveUtil = new TCPReceiveUtil(Client.client) ;
        //---------------------------------------------------------------------

        String request = "GETID "+friendname;
        sendUtil.sendUTF(request);
        int id  = Integer.parseInt(receiveUtil.receiveUTF());

        // 读取最后一条消息的时间戳
        File chatFile = new File(id + "_chat.txt");
        String lastTimestamp = "";
        if (chatFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(chatFile))) {
                String lastLine = "";
                while ((lastLine = reader.readLine()) != null) {
                    String[] result = lastLine.split(" ");
                    System.out.println(lastLine);
                    // 找到最后一行，假设最后一行记录了时间戳
                    lastTimestamp = result[0]+" "+result[1];  // 假设时间戳在第一位
                    System.out.println(lastTimestamp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 检查当前消息时间戳是否晚于最后一条消息的时间戳
        if (timestamp.compareTo(lastTimestamp)>0) {
            System.out.println("该离线消息已进入txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(id + "_chat.txt", true))) {
                writer.write(message);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveMessageToFile(String friendname, String message) {

        //先连接----------------------------------------------------------------
        try {
            Client.client = new Socket("127.0.0.1",7777);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        TCPSendUtil sendUtil = new TCPSendUtil(Client.client );
        TCPReceiveUtil receiveUtil = new TCPReceiveUtil(Client.client) ;
        //---------------------------------------------------------------------

        String request = "GETID "+friendname;
        sendUtil.sendUTF(request);
        int id  = Integer.parseInt(receiveUtil.receiveUTF());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(  id+ "_chat.txt", true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTXTLastTime(String friendname){

        //先连接----------------------------------------------------------------
        try {
            Client.client = new Socket("127.0.0.1",7777);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        TCPSendUtil sendUtil = new TCPSendUtil(Client.client );
        TCPReceiveUtil receiveUtil = new TCPReceiveUtil(Client.client) ;
        //---------------------------------------------------------------------

        String request = "GETID "+friendname;
        sendUtil.sendUTF(request);
        int id  = Integer.parseInt(receiveUtil.receiveUTF());

        File chatFile = new File(id + "_chat.txt");
        String lastTimestamp = "";
        if (chatFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(chatFile))) {
                String lastLine = "";
                while ((lastLine = reader.readLine()) != null) {
                    String[] result = lastLine.split(" ");
                    //System.out.println(lastLine);
                    // 找到最后一行，假设最后一行记录了时间戳
                    lastTimestamp = result[0]+" "+result[1];  // 假设时间戳在第一位
                    //System.out.println(lastTimestamp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  lastTimestamp;
    }


    public static void main(String[] args) {
        launch(args);
    }

    public TextArea getChatArea() {
        return chatArea;
    }



}
