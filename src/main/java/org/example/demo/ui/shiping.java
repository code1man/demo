package org.example.demo.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.demo.Client;
import org.example.demo.utils.CameraUtil;
import org.example.demo.utils.TCPReceiveUtil;
import org.example.demo.utils.TCPSendUtil;

import java.awt.image.BufferedImage;

public class shiping extends Application {

    private boolean micOn = true;
    public String friendName;
    private boolean cameraOn = true;
    public ImageView imageView = null;
    private CameraUtil cameraUtil = new CameraUtil();
    private boolean isSender = false;
    private HBox hangUpBox;

    public shiping(String friendName, boolean isSender) {
        this.friendName = friendName;
        this.isSender = isSender;
    }

    @Override
    public void start(Stage primaryStage) {
        // 创建图片按钮
        Button micButton = createIconButton("", "/maike01.png");
        Button cameraButton = createIconButton("", "/shexiangtou.png");
        Button hangUpButton = createIconButton("", "/quit.jpg");
        Button acceptButton = createIconButton("", "/accept.png");
        acceptButton.setOnAction(event -> {
            cameraUtil.openVideoModule(friendName);
            acceptButton.setDisable(true);
            hangUpBox.getChildren().clear();
            hangUpBox.getChildren().add(hangUpButton);
            hangUpBox.setAlignment(Pos.CENTER); // 使按钮居中
        });

        // 根据 isSender 的值决定是否显示 senderButton
        acceptButton.setVisible(!isSender);

        // 设置按钮的点击事件，切换图标
        micButton.setOnAction(event -> toggleIcon(micButton, "/maike01.png", "/maike02.jpg", micOn = !micOn));
        cameraButton.setOnAction(event -> toggleIcon(cameraButton, "/shexiangtou.png", "/shexiangtou01.jpg", cameraOn = !cameraOn));

        // 挂断按钮点击事件，关闭窗口
        hangUpButton.setOnAction(event -> {
            primaryStage.close();
            new TCPSendUtil(Client.CameraClient).sendUTF("over");
        });

        // 设置按钮布局，并增加按钮之间的间距
        HBox buttonBox = new HBox(50, micButton, cameraButton); // 将间距设置为 50

        if (!isSender) {
            hangUpBox = new HBox(100, hangUpButton, acceptButton);
        } else {
            hangUpBox = new HBox(hangUpButton);
        }
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 10;");
        hangUpBox.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // 创建一个 VBox，用于将按钮放置在页面底部
        VBox layout = new VBox();
        layout.setStyle("-fx-background-color: black;");

        imageView = new ImageView();
        imageView.setFitWidth(400);  // 设置背景图像的宽度
        imageView.setFitHeight(600); // 设置背景图像的高度
        imageView.setPreserveRatio(true);

        // 将空白区域放在上面，按钮布局添加到 VBox 底部
        StackPane spacer = new StackPane();  // 占用上方空间
        VBox.setVgrow(spacer, Priority.ALWAYS);  // 将剩余的空间分配给空白部分

        layout.getChildren().addAll(spacer, buttonBox, hangUpBox);

        StackPane root = new StackPane();
        root.getChildren().addAll(imageView, layout);
        // 创建场景并显示
        Scene scene = new Scene(root, 400, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("视频通话");
        primaryStage.show();

        cameraUtil.setShiPing(this);
        if (isSender)
            cameraUtil.openVideoModule(friendName);

        new Thread(()->{
            while (true) {
                if (new TCPReceiveUtil(Client.CameraClient).receiveUTF().equals("over")) {
                    Platform.runLater(()->{
                        cameraUtil.closeVideoModule();
                        primaryStage.close();
                    });
                };
            }
        }).start();
    }

    // 创建带图标的按钮
    private Button createIconButton(String text, String imagePath) {
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        Button button = new Button(text, imageView);
        button.setStyle("-fx-background-color: white; -fx-border-radius: 15; -fx-background-radius: 15;");
        return button;
    }

    // 切换按钮的图标
    private void toggleIcon(Button button, String onIconPath, String offIconPath, boolean isOn) {
        String iconPath = isOn ? onIconPath : offIconPath;
        Image image = new Image(getClass().getResourceAsStream(iconPath));
        ((ImageView) button.getGraphic()).setImage(image);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void updateImage(BufferedImage bufferedImage) {
        if (bufferedImage != null)
        {
            Image javafxImage = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(javafxImage);
        }
    }
}