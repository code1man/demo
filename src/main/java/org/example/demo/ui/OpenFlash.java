package org.example.demo.ui;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.demo.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OpenFlash implements Initializable {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button btnStart;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 在 JavaFX 应用线程中创建并配置 ImageView 和动画
        Platform.runLater(() -> {





            // 创建并配置 ImageView
            Image image1 = new Image(getClass().getResourceAsStream("/flash1.png")); // 图片路径
            ImageView imageView1 = new ImageView(image1);
            imageView1.setFitWidth(900); // 设置图片宽度
            imageView1.setFitHeight(150); // 设置图片高度

            Image image2 = new Image(getClass().getResourceAsStream("/flash2.png")); // 图片路径
            ImageView imageView2 = new ImageView(image2);
            imageView1.setFitWidth(900); // 设置图片宽度
            imageView1.setFitHeight(150); // 设置图片高度

            Image image3 = new Image(getClass().getResourceAsStream("/flash3.png")); // 图片路径
            ImageView imageView3 = new ImageView(image3);
            imageView1.setFitWidth(900); // 设置图片宽度
            imageView1.setFitHeight(150); // 设置图片高度

            Image image4 = new Image(getClass().getResourceAsStream("/flash4.png")); // 图片路径
            ImageView imageView4 = new ImageView(image4);
            imageView1.setFitWidth(900); // 设置图片宽度
            imageView1.setFitHeight(151); // 设置图片高度

            Image image5 = new Image(getClass().getResourceAsStream("/flash5.png")); // 图片路径
            ImageView imageView5 = new ImageView(image5);
            imageView1.setFitWidth(900); // 设置图片宽度
            imageView1.setFitHeight(151); // 设置图片高度




            // 将 ImageView 添加到布局中
            Parent root = progressBar.getParent(); // 获取当前布局的根节点
            if (root != null) {
                // 确保 root 是正确类型的节点
                if (root instanceof javafx.scene.layout.Pane) {
                    ((javafx.scene.layout.Pane) root).getChildren().addAll(imageView1,imageView2,imageView3,imageView4,imageView5);
                } else {
                    System.out.println("Root node is not a Pane or its subclass.");
                }
            }



            // 创建平移动画
            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(4), imageView1);
            translateTransition.setFromX(-900);
            translateTransition.setToX(0); // 目标位置 X
            translateTransition.setFromY(0);
            translateTransition.setToY(0); // 目标位置 Y
            translateTransition.setInterpolator(Interpolator.LINEAR); // 线性插值

            TranslateTransition translateTransition2 = new TranslateTransition(Duration.seconds(4), imageView2);
            translateTransition2.setFromX(900);
            translateTransition2.setToX(0); // 目标位置 X
            translateTransition2.setFromY(150);
            translateTransition2.setToY(150); // 目标位置 Y
            translateTransition2.setInterpolator(Interpolator.LINEAR); // 线性插值

            TranslateTransition translateTransition3 = new TranslateTransition(Duration.seconds(4), imageView3);
            translateTransition3.setFromX(-900);
            translateTransition3.setToX(0); // 目标位置 X
            translateTransition3.setFromY(300);
            translateTransition3.setToY(300); // 目标位置 Y
            translateTransition3.setInterpolator(Interpolator.LINEAR); // 线性插值

            TranslateTransition translateTransition4 = new TranslateTransition(Duration.seconds(4), imageView4);
            translateTransition4.setFromX(900);
            translateTransition4.setToX(0); // 目标位置 X
            translateTransition4.setFromY(450);
            translateTransition4.setToY(450); // 目标位置 Y
            translateTransition4.setInterpolator(Interpolator.LINEAR); // 线性插值

            TranslateTransition translateTransition5 = new TranslateTransition(Duration.seconds(4), imageView5);
            translateTransition5.setFromX(-900);
            translateTransition5.setToX(0); // 目标位置 X
            translateTransition5.setFromY(601);
            translateTransition5.setToY(601); // 目标位置 Y
            translateTransition5.setInterpolator(Interpolator.LINEAR); // 线性插值



            // 启动动画
            translateTransition.play();
            translateTransition2.play();
            translateTransition3.play();
            translateTransition4.play();
            translateTransition5.play();

        });

        // 设置启动任务
        Task<Void> splashTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    updateProgress(i, 100);
                    Thread.sleep(50); // 模拟工作负载
                }
                return null;
            }
        };

        // 绑定进度条到任务的进度
        progressBar.progressProperty().bind(splashTask.progressProperty());

        // 处理任务完成
        splashTask.setOnSucceeded(e -> Platform.runLater(() -> {
            setPrimaryStage(Main.stage);
            primaryStage.close();
            loadMainApp();
        }));

        // 处理任务失败
        splashTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                e.getSource().getException().printStackTrace();
            });
        });

        // 启动任务线程
        new Thread(splashTask).start();
    }

    private void loadMainApp() {
        new LoginApp().start(Main.stage);
    }
}

