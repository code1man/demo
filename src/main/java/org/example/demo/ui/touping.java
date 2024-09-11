package org.example.demo.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class touping extends Application {

    private int seconds = 0; // 计时器的秒数
    private Label timerLabel = new Label("投屏时间: 0 秒");
    private ImageView imageView;
    private boolean isStart = true;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image("/logo.jpg"));
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
        bottomPane.setPrefHeight(100); // 设置底部背景高度

        // 投屏框 (黑色背景)，增加高度
        Pane screenPane = new Pane();
        screenPane.setStyle("-fx-background-color: black; -fx-border-color: pink; -fx-border-width: 2;");
        screenPane.setPrefSize(600, 500); // 设置投屏区域大小，增加高度

        // 菜单栏，包含计时器和取消按钮
        HBox menuBar = new HBox(20);
        Button stopButton = new Button("结束投屏");
        stopButton.setOnAction(e -> {
            // 停止投屏，并弹出评分窗口
            primaryStage.close(); // 关闭投屏窗口
            showRatingWindow();   // 弹出评分窗口

        });

        // 设置计时器，更新时间
        new Thread(() -> {
            while (isStart) {
                try {
                    Thread.sleep(1000); // 每隔1秒更新一次
                    seconds++;
                    Platform.runLater(() -> timerLabel.setText("投屏时间: " + seconds + " 秒"));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();

        menuBar.getChildren().addAll(timerLabel, stopButton);
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

        Scene scene = new Scene(mainLayout, 800, 600); // 增大页面大小
        primaryStage.setTitle("投屏窗口");
        primaryStage.setScene(scene);
        primaryStage.show();


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
        Scene ratingScene = new Scene(ratingBox, 300, 250);
        ratingStage.setScene(ratingScene);
        ratingStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
