package org.example.demo.ui;

import javafx.application.Application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.demo.Client;
import org.example.demo.ui.Chat_add.Sender;
import org.example.demo.ui.Chat_add.VoiceCallClient;
import org.example.demo.utils.CameraUtil;
import org.example.demo.utils.DbUtil;
import org.example.demo.utils.TCPReceiveUtil;
import org.example.demo.utils.TCPSendUtil;

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
    private String username;
    private Button sendButton;
    private Button voiceCall;
    private Button videoCall;
    private TextField messageInput;
    private Sender sender;
    private CameraUtil cameraUtil;
    private VBox chatBox;
    private VBox nameBox;
    private Sender sender_call;


    private String friendName;
    public chat(String username,String friendName) {
        this.username = username;
        this.friendName = friendName;
        //可能要改成数据库中记录的用户名
    }
    public chat(){           //需要有一个无参的构造函数

    }
    public void initialize() {
        try {
            sender_call = new Sender("localhost", 9999);
            // 初始化客户端并连接到服务器
            sender = new Sender("localhost", 10086); // 替换为服务器的IP和端口
            startListening();
            System.out.println("success initialize");
        } catch (IOException e) {System.out.println("fail initialize");

            chatArea.appendText("Failed to connect to server: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }
    private HBox createBubble(String message, boolean isSent) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-background-color: lightblue; -fx-background-radius: 15; -fx-padding: 10; -fx-text-fill: black;");

        // 创建气泡背景
        HBox bubbleBox = new HBox();
        bubbleBox.setPadding(new Insets(5));
        bubbleBox.setAlignment(isSent ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        bubbleBox.setMinWidth(100);
        bubbleBox.setMaxWidth(300);

        if (isSent) {
            // 发送消息的气泡
            bubbleBox.getChildren().add(messageLabel);
            bubbleBox.setAlignment(Pos.CENTER_LEFT);
        } else {
            // 接收消息的气泡
            bubbleBox.setAlignment(Pos. CENTER_RIGHT);
            bubbleBox.getChildren().add(messageLabel);
        }

        return bubbleBox;
    }

    @FXML
    public void sendMessage() {
        String message = messageInput.getText();
        if (!message.trim().isEmpty()) {
            // 创建气泡背景
            HBox bubbleBox = createBubble(message, true);
            bubbleBox.getChildren().add(nameBox);// 传入 true 表示发送消息
            chatBox.getChildren().add(bubbleBox);

            // 滚动到最新消息
            ScrollPane scrollPane = (ScrollPane) chatBox.getParent().getParent();
            scrollPane.setVvalue(1.0);

            // 清空输入框
            messageInput.clear();
        }
        }


    // 从服务器接收消息并显示在 chatArea 中
    public void startListening() {
        Thread thread = new Thread(() -> {
            try {
                String message;
                while ((message = sender.receiveMessage()) != null) {
                    String finalMessage = message;
                    chatArea.appendText(finalMessage + "\n"); // 显示收到的消息
                }
            } catch (IOException e) {
                chatArea.appendText("Connection lost.\n");
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
    private void showVoiceCallWindow() {
        // 创建一个新的窗口
        Stage newWindow = new Stage();

        // 设置窗口标题
        newWindow.setTitle("语音通话请求");

        // 创建一个标签，显示信息
        Label label = new Label("有语音通话请求...");

        // 创建按钮用于接受或拒绝通话
        Button acceptButton = new Button("接受");
        Button rejectButton = new Button("拒绝");

        // 为按钮添加事件处理程序（示例功能）
        acceptButton.setOnAction(event -> {
            // 处理接受通话
            newWindow.close();
            initiateVoiceCall();
        });

        rejectButton.setOnAction(event -> {
            // 处理拒绝通话
            newWindow.close();
            terminateVoiceCall();
            //拒绝通话
        });

        // 将UI元素排列在一个布局中
        VBox layout = new VBox(10);  // 元素之间的间距
        layout.getChildren().addAll(label, acceptButton, rejectButton);
        layout.setAlignment(Pos.CENTER);

        // 设置场景并显示窗口
        Scene scene = new Scene(layout, 300, 200);
        newWindow.setScene(scene);

        // 设置为模态窗口（在关闭之前阻止与其他窗口的交互）
        newWindow.initModality(Modality.APPLICATION_MODAL);

        // 显示新窗口
        newWindow.show();
    }
    public void startListening_call() {

        Thread thread = new Thread(() -> {
            try {
                String message;
                while ((message = sender_call.receiveMessage()) != null) {
                    System.out.println(message);
                    String finalMessage = message;
                    if(finalMessage.equals("1"))         //有语音通话的请求的信息
                    {
                        if (!VoiceCallClient.isCalling) {
                            System.out.println("准备加载确认界面");
                            Platform.runLater(this::showVoiceCallWindow);
                        }
                    }
                }
            } catch (IOException e) {
                chatArea.appendText("无法语音通话");
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

            String request = "VOICECHAT"+" "+hostID+" "+ DbUtil.getID(friendName);


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

    public void videoCall(ActionEvent actionEvent) {
        if (!VoiceCallClient.isCalling) {
            initiateVoiceCall();
            videoCall.setText("挂断视频通话");
        } else {
            terminateVoiceCall();
            videoCall.setText("发起视频通话");
        }
    }

    private void initiateVideoCall() {
        System.out.println("发起视频通话...");
        // 设置正在视频通话
        VoiceCallClient.isCalling = true;
        CameraUtil.isCalling = true;

        cameraUtil = new CameraUtil();
        // 启动客户端的音频捕获和发送逻辑
        try {
            new Thread(() -> {
                try {
                    VoiceCallClient.main(null); // 启动音频捕获和接收线程
                    cameraUtil.openVideoModule();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
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

    private void terminateVideoCall() {
        System.out.println("结束视频通话...");

        VoiceCallClient.isCalling = false;
        CameraUtil.isCalling = false;

        try {
            if (VoiceCallClient.socket != null && !VoiceCallClient.socket.isClosed()) {
                VoiceCallClient.socket.close();
                cameraUtil.closeVideoModule();
                System.out.println("Socket连接已关闭");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        initialize();

        nameBox=new VBox(10);// 接受从数据库获得的用户名
        primaryStage.getIcons().add(new Image("/logo.jpg"));
        // 根布局 Pane，用于设置背景
        Pane root = new Pane();
        root.setStyle("-fx-background-image: url('/back01.png'); -fx-background-size: cover;");

//        // 对话框部分 (白色背景)
//        chatArea = new TextArea();
//        chatArea.setEditable(false); // 只允许查看信息，不可编辑
//        chatArea.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-border-color: black;");
//
//        chatArea.setPrefHeight(400); // 设置高度

        chatBox = new VBox(10);
        chatBox.setPadding(new Insets(10));
        chatBox.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-border-color: black;");
        Label label=new Label(username);
        nameBox.getChildren().add(label);
        nameBox.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-border-color: black;");
        ScrollPane chatScrollPane = new ScrollPane();
        chatScrollPane.setContent(chatBox);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        chatScrollPane.setPrefHeight(400);
        // 工具栏，包含视频通话和语音通话
        HBox toolbar = new HBox(10); // 10 为按钮之间的间距
        videoCall = new Button("视频通话");
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

        voiceCall.setOnAction(
                actionEvent -> {
                    sender_call.sendMessage("1");       //传递一串特殊字符用于表示点击事件
                    call(actionEvent);
                }
        );

        voiceCall.setOnAction(this::call);
        videoCall.setOnAction(this::call);

        // 底部的消息输入和发送按钮布局
        HBox messageBox = new HBox(10); // 10 为输入框和按钮之间的间距
        messageBox.getChildren().addAll(messageInput, sendButton);
        messageBox.setAlignment(Pos.CENTER); // 将消息输入框和按钮居中排列

        // 总体布局，将所有组件按顺序放入
        VBox layout = new VBox(10); // 10 为组件之间的间距
        layout.getChildren().addAll(nameBox,chatScrollPane, toolbar, messageBox);
        layout.setPadding(new Insets(20)); // 设置内边距
        layout.setAlignment(Pos.BOTTOM_CENTER); // 设置总体布局居中

        // 将布局添加到根 Pane 中
        root.getChildren().add(layout);

        // 场景设置，调整页面大小
        Scene scene = new Scene(root, 600, 550); // 调整页面大小为 800x600
        primaryStage.setTitle("聊天窗口");
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
