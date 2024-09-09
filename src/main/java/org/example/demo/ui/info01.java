package org.example.demo.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.demo.Client;
import org.example.demo.Main;
import org.example.demo.utils.TCPSendUtil;

import static javafx.application.Application.launch;

//远程投屏
public class info01 {
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image("/logo.jpg"));
        // 设置申请远程控制的用户（可以从数据库中获取）
        String requesterName = "XXXX"; // 以后从数据库中获取用户的名字

        // 创建标签显示申请远程控制的消息
        Label notificationLabel = new Label(requesterName + " 向您申请远程投屏");
        notificationLabel.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 20));
        notificationLabel.setTextFill(Color.BLACK);

        // 创建“同意”按钮
        Button acceptButton = new Button("同意");
        acceptButton.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 16));
        acceptButton.setTextFill(Color.WHITE);
        acceptButton.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(15), Insets.EMPTY)));
        acceptButton.setPrefWidth(100);
        acceptButton.setPrefHeight(40);

        // 创建“拒绝”按钮
        Button rejectButton = new Button("拒绝");
        rejectButton.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 16));
        rejectButton.setTextFill(Color.WHITE);
        rejectButton.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(15), Insets.EMPTY)));
        rejectButton.setPrefWidth(100);
        rejectButton.setPrefHeight(40);

        // 设置按钮的点击事件
        acceptButton.setOnAction(event -> {
            System.out.println("用户同意了远程投屏");
            // 可以在这里添加处理同意远程控制的逻辑
            new TCPSendUtil(Client.RemoteControlClient).sendUTF("同意");
            new yuanchengkongzhi01().start(Main.stage);
            Client.startRemoteControl();
        });

        rejectButton.setOnAction(event -> {
            System.out.println("用户拒绝了远程投屏");
            // 可以在这里添加处理拒绝远程控制的逻辑
            primaryStage.close(); // 拒绝后关闭窗口
            new TCPSendUtil(Client.RemoteControlClient).sendUTF("拒绝");
        });

        // 创建水平布局放置按钮
        HBox buttonBox = new HBox(30, acceptButton, rejectButton);
        buttonBox.setAlignment(Pos.CENTER);

        // 创建垂直布局放置通知标签和按钮
        VBox layout = new VBox(40, notificationLabel, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        // 创建场景并设置大小
        Scene scene = new Scene(layout, 300, 200);

        // 设置窗口标题和场景
        primaryStage.setTitle("远程投屏请求");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}