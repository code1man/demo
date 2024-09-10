package org.example.demo.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.demo.Client;
import org.example.demo.utils.RemoteControlUtil;
import org.example.demo.utils.TCPSendUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class yuanchengkongzhi extends Application {

    private int seconds = 0; // 计时器的秒数
    private Label timerLabel = new Label("远程控制时间: 0 秒");
    private boolean isIcon1 = true; // 用于图标切换的标志位
    private static ImageView imageView;
    private RemoteControlUtil remoteControlUtil;
    private TCPSendUtil tcpSendUtil;

    public yuanchengkongzhi(String fname) {
        remoteControlUtil = new RemoteControlUtil();
        tcpSendUtil = new TCPSendUtil(Client.RemoteControlClient);
        tcpSendUtil.sendUTF(Client.uid + "#" + fname);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image("/logo.jpg"));

        // 设置全屏
        primaryStage.setFullScreen(true);

        // 创建底部背景区域
        Pane bottomPane = new Pane();
        Image backgroundImage = new Image(getClass().getResourceAsStream("/back01.png")); // 替换为你的图片路径
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        Background background = new Background(bgImage);
        bottomPane.setBackground(background);
        bottomPane.setPrefHeight(20); // 设置底部背景高度

        // 投屏框 (黑色背景)，比例增大
        Pane screenPane = new Pane();
        screenPane.setStyle("-fx-background-color: black; -fx-border-color: pink; -fx-border-width: 2;");
        screenPane.setPrefSize(1400, 1000); // 增加宽度和高度

        // 创建 ImageView
        imageView = new ImageView();
        imageView.setFitWidth(1400); // 设置图像宽度
        imageView.setFitHeight(1000); // 设置图像高度
        imageView.setPreserveRatio(true); // 保持图像比例

        screenPane.getChildren().add(imageView);

        VBox.setVgrow(screenPane, Priority.ALWAYS); // 让黑色区域占据更多的垂直空间

        screenPane.setOnMouseClicked(this::handleEvent);
        screenPane.setOnMouseMoved(this::handleEvent);
        screenPane.setOnMouseDragged(this::handleEvent);
        screenPane.setOnMousePressed(this::handleEvent);
        screenPane.setOnMouseReleased(this::handleEvent);
        screenPane.setOnScroll(this::handleEvent);
        screenPane.setOnKeyPressed(this::handleEvent);
        screenPane.setOnKeyReleased(this::handleEvent);

        // 菜单栏，包含计时器、结束按钮和图片按钮
        HBox menuBar = new HBox(20);

        // 结束远程控制按钮
        Button stopButton = new Button("结束远程控制");
        stopButton.setOnAction(e -> {
            primaryStage.close(); // 关闭投屏窗口
            //showRatingWindow();   // 弹出评分窗口
            try {
                remoteControlUtil.close();
                Client.stopRemoteHash();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // 加载两个图标图片
        Image icon1 = new Image("/maike01.png"); // 替换为你的第一个图片路径
        Image icon2 = new Image("/maike02.jpg"); // 替换为你的第二个图片路径

        // 创建图片按钮，初始图标为 icon1
        ImageView iconImageView = new ImageView(icon1);
        iconImageView.setFitWidth(30);  // 设置图标宽度
        iconImageView.setFitHeight(30); // 设置图标高度
        Button iconButton = new Button();
        iconButton.setGraphic(iconImageView); // 将图片设置为按钮的图标

        // 切换图标逻辑
        iconButton.setOnAction(e -> {
            if (isIcon1) {
                iconImageView.setImage(icon2); // 切换到 icon2
            } else {
                iconImageView.setImage(icon1); // 切换回 icon1
            }
            isIcon1 = !isIcon1; // 切换标志位
        });

        // 设置计时器，更新时间
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); // 每隔1秒更新一次
                    seconds++;
                    Platform.runLater(() -> timerLabel.setText("远程控制时间: " + seconds + " 秒"));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();

        menuBar.getChildren().addAll(timerLabel, stopButton, iconButton); // 添加图片按钮
        menuBar.setStyle("-fx-padding: 10; -fx-background-color: #ADD8E6;"); // 设置背景颜色为淡蓝色
        menuBar.setAlignment(Pos.TOP_CENTER); // 设置工具栏居中

        // 主界面的布局，包含菜单栏、投屏框和底部背景
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(menuBar);
        BorderPane.setAlignment(menuBar, Pos.TOP_CENTER); // 菜单栏居中

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER); // 内容区域居中
        content.getChildren().add(screenPane);

        mainLayout.setCenter(content); // 将投屏框放置在页面中央
        mainLayout.setBottom(bottomPane); // 底部背景放置在页面底部

        Scene scene = new Scene(mainLayout, 1600, 900); // 设置初始页面大小
        scene.setFill(Color.LIGHTBLUE); // 设置背景颜色为淡蓝色
        // 添加键盘事件，按ESC退出全屏
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                primaryStage.setFullScreen(false); // 退出全屏模式
            }
        });

        primaryStage.setTitle("远程控制窗口");
        primaryStage.setScene(scene);
        primaryStage.show();

        Client.recieveRemoteHash();
    }

    // 弹出评分窗口
    private void showRatingWindow() {
        Stage ratingStage = new Stage();
        ratingStage.initModality(Modality.APPLICATION_MODAL); // 模态窗口
        ratingStage.setTitle("投屏评分");

        // 创建评分复选框
        VBox ratingBox = new VBox(10);
        ratingBox.setStyle("-fx-padding: 20;");
        Label promptLabel = new Label("请为投屏评分：");
        CheckBox rating1 = new CheckBox("1 星");
        CheckBox rating2 = new CheckBox("2 星");
        CheckBox rating3 = new CheckBox("3 星");
        CheckBox rating4 = new CheckBox("4 星");
        CheckBox rating5 = new CheckBox("5 星");

        // 确认按钮
        Button submitButton = new Button("提交评分");
        submitButton.setOnAction(e -> {
            ratingStage.close(); // 关闭评分窗口
            // 可以在此处处理评分逻辑
        });

        ratingBox.getChildren().addAll(promptLabel, rating1, rating2, rating3, rating4, rating5, submitButton);
        Scene ratingScene = new Scene(ratingBox, 300, 350);
        ratingStage.setScene(ratingScene);
        ratingStage.show();
    }

    public static void updateImage(BufferedImage bufferedImage) {
        if (bufferedImage != null)
        {
            Image javafxImage = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(javafxImage);
        }
    }

    private void handleEvent(javafx.event.Event e) {
        String result;
        if (e instanceof MouseEvent) {
            result = remoteControlUtil.mouseEvent((MouseEvent) e);
        } else if (e instanceof ScrollEvent) {
            result = remoteControlUtil.mouseWheelEvent((ScrollEvent) e);
        } else if (e instanceof KeyEvent) {
            result = remoteControlUtil.keyEvent((KeyEvent) e);
        } else {
            throw new IllegalArgumentException("Unsupported event type: " + e.getEventType());
        }
        tcpSendUtil.sendUTF(result);
    }

    public static void main(String[] args) {
        launch(args);
    }
}