package org.example.demo.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.demo.Client;
import org.example.demo.utils.TCPReceiveUtil;
import org.example.demo.utils.TCPSendUtil;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class personalinfo extends Application {

    private UserInfoListener userInfoListener;

    // 自定义构造函数，传递回调接口
    public personalinfo(UserInfoListener userInfoListener) {
        this.userInfoListener = userInfoListener;
    }

    @Override
    public void start(Stage primaryStage) {


        primaryStage.getIcons().add(new Image("logo.jpg"));
        // 窗体的根布局
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F0F8FF;");

        // 头像区域
        Label avatarLabel = new Label("点击替换头像");
        ImageView avatarImageView = new ImageView();

        Client.updateAvatar(avatarImageView);

        avatarLabel.setOnMouseClicked(e -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

                    // 获取当前窗口作为文件选择器的父窗口
                    Window currentWindow = avatarImageView.getScene().getWindow();

                    // 打开文件选择器，等待用户选择文件
                    File selectedFile = fileChooser.showOpenDialog(currentWindow);

            if (selectedFile != null) {
                try {
                    // 保存头像文件到本地

                    File targetFile = new File( selectedFile.getName());

                    // 将用户选择的文件复制到指定位置
                    Files.copy(selectedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    // 将头像路径保存到数据库
                    Client.avatarUrl = targetFile.getAbsolutePath();
                    String request = "UPDATEHEAD"+" "+Client.uid+" "+Client.avatarUrl;

                    //由于外部还没有完成连接代码
                    try {
                        Client.client = new Socket("127.0.0.1",7777);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    TCPSendUtil sendUtil = new TCPSendUtil(Client.client );
                    TCPReceiveUtil receiveUtil = new TCPReceiveUtil(Client.client) ;

                    sendUtil.sendUTF(request);
                    System.out.println(receiveUtil.receiveUTF());

                    // 更新ImageView
                    Image avatarImage = new Image(targetFile.toURI().toString());
                    avatarImageView.setImage(avatarImage);  // 更新ImageView

                    System.out.println("图片加载成功: " + targetFile.getAbsolutePath());
                } catch (IOException ex) {
                    System.out.println("保存图片失败: " + ex.getMessage());
                }
            }
                });


        avatarImageView.setFitWidth(100);
        avatarImageView.setFitHeight(100);
        avatarImageView.setStyle("-fx-border-radius: 50; -fx-background-radius: 50; -fx-border-color: lightgray; -fx-border-width: 2px;");
        VBox avatarBox = new VBox(10);
        avatarBox.setAlignment(Pos.CENTER);
        avatarBox.getChildren().addAll(avatarImageView, avatarLabel);

        avatarImageView.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

            // 获取当前窗口作为文件选择器的父窗口
            Window currentWindow = avatarImageView.getScene().getWindow();
            // 打开文件选择器，等待用户选择文件
            File selectedFile = fileChooser.showOpenDialog(currentWindow);

            if (selectedFile != null) {
                // 如果用户选择了文件，则将其转换为Image
                Image avatarImage = new Image(selectedFile.toURI().toString());

                // 将Image设置给ImageView
                avatarImageView.setImage(avatarImage);
            }
        });

        // 输入框与选择框的通用样式
        String commonStyle = "-fx-background-color: white; -fx-border-radius: 15; -fx-background-radius: 15; " +
                "-fx-border-color: lightblue; -fx-border-width: 4px;";

        // 用户名区域
        Label usernameLabel = new Label("用户名:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("请输入用户名");
        usernameField.setStyle(commonStyle);
        HBox usernameBox = new HBox(10, usernameLabel, usernameField);
        usernameBox.setAlignment(Pos.CENTER_LEFT);

        // 个性签名区域
        Label signatureLabel = new Label("个性签名:");
        TextField signatureField = new TextField();
        signatureField.setPromptText("请输入个性签名");
        signatureField.setStyle(commonStyle);
        HBox signatureBox = new HBox(10, signatureLabel, signatureField);
        signatureBox.setAlignment(Pos.CENTER_LEFT);

        // 性别选择区域
        Label genderLabel = new Label("性别:");
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("男", "女");
        genderComboBox.setPromptText("请选择性别");
        genderComboBox.setStyle(commonStyle);
        HBox genderBox = new HBox(10, genderLabel, genderComboBox);
        genderBox.setAlignment(Pos.CENTER_LEFT);

        // 生日区域
        Label birthdayLabel = new Label("生日:");
        TextField birthdayField = new TextField();
        birthdayField.setPromptText("请输入生日");
        birthdayField.setStyle(commonStyle);
        HBox birthdayBox = new HBox(10, birthdayLabel, birthdayField);
        birthdayBox.setAlignment(Pos.CENTER_LEFT);

        // 国家选择区域
        Label countryLabel = new Label("国家:");
        ComboBox<String> countryComboBox = new ComboBox<>();
        countryComboBox.getItems().addAll("中国", "日本", "韩国", "德国", "英国", "法国", "俄罗斯", "美国", "澳大利亚", "加拿大", "其他");
        countryComboBox.setPromptText("请选择国家");
        countryComboBox.setStyle(commonStyle);
        HBox countryBox = new HBox(10, countryLabel, countryComboBox);
        countryBox.setAlignment(Pos.CENTER_LEFT);

        // 省份选择区域
        Label provinceLabel = new Label("省份:");
        ComboBox<String> provinceComboBox = new ComboBox<>();
        provinceComboBox.getItems().addAll(
                "北京市", "天津市", "上海市", "重庆市", "河北省", "山西省", "辽宁省", "吉林省", "黑龙江省",
                "江苏省", "浙江省", "安徽省", "福建省", "江西省", "山东省", "河南省", "湖北省", "湖南省",
                "广东省", "海南省", "四川省", "贵州省", "云南省", "陕西省", "甘肃省", "青海省", "台湾省",
                "内蒙古自治区", "广西壮族自治区", "西藏自治区", "宁夏回族自治区", "新疆维吾尔自治区", "香港特别行政区", "澳门特别行政区"
        );
        provinceComboBox.setPromptText("请选择省份");
        provinceComboBox.setStyle(commonStyle);
        HBox provinceBox = new HBox(10, provinceLabel, provinceComboBox);
        provinceBox.setAlignment(Pos.CENTER_LEFT);

        // 确认修改按钮
        Button submitButton = new Button("确认修改");
        submitButton.setStyle("-fx-background-color: #4169E1; -fx-border-radius: 15; -fx-background-radius: 15; -fx-text-fill: white;");
        submitButton.setOnAction(e -> {
            // 获取用户输入的数据
            String username = usernameField.getText();
            String signature = signatureField.getText();
            String gender = genderComboBox.getValue();
            String birthday = birthdayField.getText();
            String country = countryComboBox.getValue();
            String province = provinceComboBox.getValue();
            Image avatar = avatarImageView.getImage();

            //更新头像
            Client.updateAvatar(Home.userAvatar);

            // 通过回调函数将数据传回主界面
            if (userInfoListener != null) {
                userInfoListener.onUserInfoUpdated(username, signature, gender, birthday, country, province, avatar);
            }

            // 关闭修改窗口
            primaryStage.close();
        });

        // 将所有元素添加到布局中
        root.getChildren().addAll(avatarBox, usernameBox, signatureBox, genderBox, birthdayBox, countryBox, provinceBox);

        // 添加确认按钮，并将其设置为底部居中
        VBox.setMargin(submitButton, new Insets(20, 0, 0, 0));
        VBox buttonContainer = new VBox(submitButton);
        buttonContainer.setAlignment(Pos.CENTER);
        root.getChildren().add(buttonContainer);

        // 设置场景
        Scene scene = new Scene(root, 300, 500);
        primaryStage.setTitle("修改个人信息");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
