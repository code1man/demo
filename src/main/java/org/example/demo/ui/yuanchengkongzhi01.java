package org.example.demo.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.demo.Client;
import org.example.demo.utils.RemoteControlUtil;
import org.example.demo.utils.VideoUtil;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import org.example.demo.Client;
import org.example.demo.utils.TCPReceiveUtil;
import org.example.demo.utils.TCPSendUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class yuanchengkongzhi01 extends Application {

    private int seconds = 0;
    private Label timerLabel = new Label("远程控制时间: 0 秒");
    private VideoUtil videoUtil = new VideoUtil("test", true);
    private RemoteControlUtil remoteControlUtil;

    @Override
    public void start(Stage primaryStage) {
        remoteControlUtil = new RemoteControlUtil();

        primaryStage.getIcons().add(new Image("logo.jpg"));

        // 计时器
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                seconds++;
                Platform.runLater(() -> timerLabel.setText("远程控制时间: " + seconds + " 秒"));
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000); // 每秒更新一次

        // 结束控制按钮
        Button endControlBtn = new Button("结束控制");
        endControlBtn.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-background-radius: 15px;");
        endControlBtn.setOnAction(e -> {

            Client.stopRemoteControl();
            Client.stopRemoteHash();
            videoUtil.stop();
            timer.cancel(); // 停止计时器
            primaryStage.close(); // 关闭当前悬浮窗口
            showRatingWindow(); // 弹出评分窗口
        });

        // 图标选择按钮
        Image icon1 = new Image("maike01.png");
        Image icon2 = new Image("maike02.jpg");

        ImageView iconView = new ImageView(icon1);
        iconView.setFitHeight(30); // 设置图标高度
        iconView.setFitWidth(30);  // 设置图标宽度

        ToggleButton iconButton = new ToggleButton();
        iconButton.setGraphic(iconView);
        iconButton.setStyle("-fx-background-color: white;"); // 图标按钮背景色设置为白色

        iconButton.setOnAction(e -> {
            if (iconButton.isSelected()) {
                iconView.setImage(icon2); // 切换到第二个图标
            } else {
                iconView.setImage(icon1); // 切换回第一个图标
            }

        });

        // 创建工具栏
        HBox toolbar = new HBox(10); // 组件间距为10
        toolbar.getChildren().addAll(timerLabel, endControlBtn, iconButton);
        toolbar.setPadding(new Insets(10)); // 设置内边距

        // 设置场景并修改背景色为淡蓝色
        Scene scene = new Scene(toolbar, 270, 70);
        scene.setFill(Color.LIGHTBLUE); // 设置背景颜色为淡蓝色

        primaryStage.setScene(scene);
        primaryStage.setTitle("远程控制客户端");
        primaryStage.show();

        Client.startRemoteControl();
        videoUtil.start();
    }

    // 评分弹窗
    private void showRatingWindow() {
        Stage ratingStage = new Stage();
        ratingStage.setTitle("打分");

        // 创建评分的 RadioButton
        ToggleGroup ratingGroup = new ToggleGroup();
        RadioButton score1 = new RadioButton("赞");
        RadioButton score2 = new RadioButton("踩");
//        RadioButton score3 = new RadioButton("3 分");
//        RadioButton score4 = new RadioButton("4 分");
//        RadioButton score5 = new RadioButton("5 分");

        score1.setToggleGroup(ratingGroup);
        score2.setToggleGroup(ratingGroup);
//        score3.setToggleGroup(ratingGroup);
//        score4.setToggleGroup(ratingGroup);
//        score5.setToggleGroup(ratingGroup);

        // 确认按钮
        Button submitBtn = new Button("提交评分");
        submitBtn.setOnAction(e -> {
            RadioButton selected = (RadioButton) ratingGroup.getSelectedToggle();
            if (selected != null) {
                System.out.println("用户评价: " + selected.getText());
                ratingStage.close(); // 关闭评分窗口

                System.out.println("1");

                //先连接-------------------------------------------------------------

                TCPSendUtil sendUtil = new TCPSendUtil(Client.secondClient);
                TCPReceiveUtil receiveUtil = new TCPReceiveUtil(Client.secondClient) ;
                //---------------------------------------------------------------

                System.out.println("2");
                //先获取控制者的id/名字
                //假设这里是名字

               new Thread(()->{
                   String controller = "马化腾";//要改

                   String request = "FEEDBACK "+controller+" "+selected.getText();
                   sendUtil.sendUTF(request);

                   String[]  result = receiveUtil.receiveUTF().split(" ");
                   Client.controlTimes = Integer.parseInt(result[0]);
                   Client.goodRatingPercentage =Integer.parseInt(result[1])/(double)Client.controlTimes ;
                   //connect reset
                   System.out.println("好评率和操作次数");

               }).start();


            } else {
                System.out.println("请先选择评分！");
            }
        });

        // 布局
        VBox ratingBox = new VBox(10);
        ratingBox.setPadding(new Insets(20));
        ratingBox.getChildren().addAll(score1, score2, submitBtn);

        Scene ratingScene = new Scene(ratingBox, 200, 200);
        ratingStage.setScene(ratingScene);
        ratingStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}