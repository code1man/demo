package org.example.demo.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
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
import org.example.demo.utils.DbUtil;
import org.example.demo.utils.TCPReceiveUtil;
import org.example.demo.utils.TCPSendUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Home extends Application {

    private Label userAvatarLabel;
    private Button btnRemote;
    private Button btnFriends;
    private Button btnLocalDevices;
    private Label productLogoLabel;
    private BorderPane root;
    private AnchorPane rightPane; // 使用 AnchorPane 来控制右侧内容块的位置
    private VBox rightContentBox; // 右侧内容块
    private TextField searchField; // 搜索框

    public static ImageView userAvatar;

    // 用于缓存好友的聊天窗口




    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //先连接
        try {
            Client.client = new Socket("127.0.0.1",7777);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        TCPSendUtil sendUtil = new TCPSendUtil(Client.client );
        TCPReceiveUtil receiveUtil = new TCPReceiveUtil(Client.client) ;


        Button applyButton = new Button("申请");
        Button acceptButton =  new Button("接受");
        Button refuseButton =  new Button("拒绝");

        acceptButton.setOnAction(ev->{
            String friendName = "";//这里后面要改从消息列表上取
            String request = "addFriends"+" "+"accepted"+" "+Client.uid+" "+DbUtil.getID(friendName);

            sendUtil.sendUTF(request);

            System.out.println(receiveUtil.receiveUTF());

        });

        applyButton.setOnAction(ev->{
            String friendName = "";     //这里后面要改，从检索出来的标签取

            String request = "addFriends"+" "+"pending"+" "+Client.uid+" "+DbUtil.getID(friendName);

            sendUtil.sendUTF(request);

            System.out.println(receiveUtil.receiveUTF());

        });

        refuseButton.setOnAction(ev->{
            String friendName = "";     //这里后面要改，从检索出来的消息列表取

            String request = "addFriends"+" "+"blocked"+" "+Client.uid+" "+DbUtil.getID(friendName);

            sendUtil.sendUTF(request);

            System.out.println(receiveUtil.receiveUTF());

        });


        primaryStage.setTitle("客户端0.0.1");
        primaryStage.getIcons().add(new Image("logo.jpg"));
        // 左侧菜单区域
        VBox leftMenu = new VBox(20); // 将间距设为 20
        leftMenu.setPadding(new Insets(15));
        leftMenu.setPrefWidth(200);
        leftMenu.setStyle("-fx-background-color: white; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: lightgray; " +
                "-fx-border-width: 2px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0.5, 2, 2);");

        leftMenu.setAlignment(Pos.CENTER); // 将内容在VBox中居中对齐

        System.out.println(Client.avatarUrl);
        // 用户头像区域（修改为按钮）
        //ImageView userAvatar = new ImageView(new Image(getClass().getResourceAsStream(Client.avatarUrl)));

        File avatarFile = new File(Client.avatarUrl);


// 如果文件存在，加载头像到 ImageView 中

        userAvatar = new ImageView(new Image(avatarFile.toURI().toString()));
        Client.updateAvatar(userAvatar);

        //ImageView userAvatar = new ImageView(new Image("file:"+Client.avatarUrl));

        userAvatar.setFitWidth(100);
        userAvatar.setFitHeight(100);

        userAvatarLabel = new Label("主页");
        userAvatarLabel.setStyle("-fx-font-family: '微软雅黑'; -fx-font-size: 16px;");

        VBox avatarBox = new VBox(10);
        avatarBox.setAlignment(Pos.CENTER);
        avatarBox.getChildren().addAll(userAvatar, userAvatarLabel);

        // 将avatarBox包装在一个按钮中，使其可点击
        Button avatarButton = new Button();
        avatarButton.setStyle("-fx-background-color: transparent;"); // 去除按钮默认样式
        avatarButton.setGraphic(avatarBox);
        avatarButton.setPadding(new Insets(0)); // 去除内边距

        // 设置头像按钮的点击事件
        avatarButton.setOnAction(e -> updateRightContent("userInfo"));

        // 功能模块按钮
        btnRemote = new Button("远程投屏模块");
        btnFriends = new Button("好友列表模块");
        btnLocalDevices = new Button("本地设备模块");

        // 设置按钮大小
        btnRemote.setPrefWidth(150);   // 按钮宽度设置为 150
        btnRemote.setPrefHeight(40);   // 按钮高度设置为 40

        btnFriends.setPrefWidth(150);
        btnFriends.setPrefHeight(40);

        btnLocalDevices.setPrefWidth(150);
        btnLocalDevices.setPrefHeight(40);

        String buttonStyle = "-fx-font-family: '微软雅黑'; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white; " +
                "-fx-background-color: lightblue; " +
                "-fx-border-color: #add8e6; " +
                "-fx-border-width: 2px; " +
                "-fx-background-radius: 15px; " +
                "-fx-border-radius: 15px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0, 2, 2);";

        String buttonPressedStyle = "-fx-background-color: #1e90ff; " +
                "-fx-border-color: #1e90ff;";

        btnRemote.setStyle(buttonStyle);
        btnFriends.setStyle(buttonStyle);
        btnLocalDevices.setStyle(buttonStyle);

        btnRemote.setOnMousePressed(e -> btnRemote.setStyle(buttonStyle + buttonPressedStyle));
        btnRemote.setOnMouseReleased(e -> btnRemote.setStyle(buttonStyle));

        btnFriends.setOnMousePressed(e -> btnFriends.setStyle(buttonStyle + buttonPressedStyle));
        btnFriends.setOnMouseReleased(e -> btnFriends.setStyle(buttonStyle));

        btnLocalDevices.setOnMousePressed(e -> btnLocalDevices.setStyle(buttonStyle + buttonPressedStyle));
        btnLocalDevices.setOnMouseReleased(e -> btnLocalDevices.setStyle(buttonStyle));

        // 产品LOGO区域
        ImageView productLogo = new ImageView(new Image(getClass().getResourceAsStream("/logo.jpg")));
        productLogo.setFitWidth(100);
        productLogo.setFitHeight(80);

        productLogoLabel = new Label("版本0.0.1");
        productLogoLabel.setStyle("-fx-font-family: '微软雅黑'; -fx-font-size: 16px;");
        productLogoLabel.setAlignment(Pos.CENTER);

        VBox logoBox = new VBox(10);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.getChildren().addAll(productLogo, productLogoLabel);

        leftMenu.getChildren().addAll(avatarButton, btnRemote, btnFriends, btnLocalDevices, logoBox);

        root = new BorderPane();
        root.setLeft(leftMenu);

        root.setStyle("-fx-background-image: url('back03.jpg'); " +
                "-fx-background-size: cover;");

        // 创建右侧内容块
        rightContentBox = new VBox(10);
        rightContentBox.setPadding(new Insets(15));

        // 设置右侧内容块的大小
        rightContentBox.setPrefSize(550, 450);  // 设置预期大小
        rightContentBox.setMaxSize(550, 450);   // 设置最大大小
        rightContentBox.setMinSize(550, 450);   // 设置最小大小
        // 模拟菜单选择触发的用户信息展示
        displayUserInfo();

        // 设置右侧内容块的样式：淡蓝色背景、圆角、阴影
        rightContentBox.setStyle("-fx-background-color: #add8e6; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: lightgray; " +
                "-fx-border-width: 2px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0.5, 2, 2);");

        rightContentBox.setAlignment(Pos.CENTER_LEFT);

        updateRightContent("默认页面");

        // 使用 AnchorPane 放置右侧内容块
        rightPane = new AnchorPane();
        rightPane.getChildren().add(rightContentBox);
        AnchorPane.setRightAnchor(rightContentBox, 10.0); // 右侧距离
        AnchorPane.setTopAnchor(rightContentBox, 30.0);   // 顶部距离

        root.setCenter(rightPane); // 将右侧内容区域设置到 BorderPane 的中心位置

        btnRemote.setOnAction(e -> updateRightContent("远程投屏模块内容"));
        btnFriends.setOnAction(e -> updateRightContent("好友列表模块内容"));
        btnLocalDevices.setOnAction(e -> updateRightContent("本地设备模块内容"));

        searchField = new TextField();
        searchField.setPromptText("搜索...");

        // 设置搜索框的样式
        searchField.setStyle("-fx-background-color: #ffffff; " +      // 背景色
                "-fx-border-color: #cccccc; " +         // 边框色
                "-fx-border-width: 2px; " +            // 边框宽度
                "-fx-border-radius: 10px; " +          // 边框弧度
                "-fx-background-radius: 10px; " +      // 背景弧度
                "-fx-padding: 5px; " +                 // 内边距
                "-fx-font-size: 14px; " +              // 字体大小
                "-fx-pref-width: 550px;");             // 预期宽度

        HBox searchBox = new HBox(searchField);
        searchBox.setPadding(new Insets(10));
        searchBox.setAlignment(Pos.CENTER_RIGHT);

        root.setTop(searchBox);

        Scene scene = new Scene(root, 800, 600); // 设置整体界面大小为800x600
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void displayUserInfo() {
        // 显示用户基本信息
        VBox userInfoBox = new VBox(10);
        userInfoBox.setPadding(new Insets(10));
        userInfoBox.setStyle("-fx-background-color: white; " +
                "-fx-border-radius: 15; " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: lightgray; " +
                "-fx-border-width: 2px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0.5, 2, 2);");

        // 头像区域
        Label avatarLabel = new Label("点击上传头像");
        ImageView avatarImageView = new ImageView();
        avatarImageView.setFitWidth(100);
        avatarImageView.setFitHeight(100);
        avatarImageView.setStyle("-fx-border-radius: 50; -fx-background-radius: 50; -fx-border-color: lightgray; -fx-border-width: 2px;");

        // 创建头像框布局，放置在用户信息上方
        VBox avatarBox = new VBox(10);
        avatarBox.setAlignment(Pos.CENTER);
        avatarBox.getChildren().addAll(avatarImageView, avatarLabel);

        // 头像点击事件，打开文件选择器
        avatarImageView.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

            // 获取当前窗口作为文件选择器的父窗口
            Window currentWindow = avatarImageView.getScene().getWindow();

            // 打开文件选择器，等待用户选择文件
            File selectedFile = fileChooser.showOpenDialog(currentWindow);

            if (selectedFile != null) {
                try {
                    // 如果用户选择了文件，则将其转换为Image
                    Image avatarImage = new Image(selectedFile.toURI().toString());
                    avatarImageView.setImage(avatarImage);  // 更新ImageView
                    System.out.println("图片加载成功: " + selectedFile.getAbsolutePath());
                } catch (Exception ex) {
                    System.out.println("加载图片失败: " + ex.getMessage());
                }
            }
        });

        // 用户信息
        Label usernameLabel = new Label("用户名: " + Client.name);
        Label ageLabel = new Label("年龄: 16");
        Label birthdayLabel = new Label("生日: 2000-01-12");
        Label passwordLabel = new Label("密码: 123456");

        usernameLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");
        ageLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");
        birthdayLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");
        passwordLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");

        // 将头像和用户信息添加到用户信息框
        userInfoBox.getChildren().addAll(avatarBox, usernameLabel, ageLabel, birthdayLabel, passwordLabel);

        // 将用户信息显示在右侧的内容框中
        if (rightContentBox != null) {
            rightContentBox.getChildren().clear();  // 清除之前的内容
            rightContentBox.getChildren().add(userInfoBox);  // 添加用户信息框
        }
    }

    private void updateRightContent(String content) {
        // 清空现有内容
        rightContentBox.getChildren().clear();

        //先连接
        try {
            Client.client = new Socket("127.0.0.1",7777);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        TCPSendUtil sendUtil = new TCPSendUtil(Client.client );
        TCPReceiveUtil receiveUtil = new TCPReceiveUtil(Client.client) ;

        switch (content) {

            case "默认页面":
                // 清空现有内容
                rightContentBox.getChildren().clear();

                // 创建新的 Label
                Label welcomeLabel = new Label("WELCOME USE");
                Label welcomeLabel2 = new Label("JOIN US FOR FASTER AND EASIER");

                // 设置 Label 的样式
                welcomeLabel.setStyle("-fx-font-family: '微软雅黑'; " +
                        "-fx-font-size: 40px; " +
                        "-fx-text-fill: white; " +
                        "-fx-alignment: center;"); // 设置字体、字号、颜色，并将文本居中对齐

                welcomeLabel2.setStyle("-fx-font-family: '微软雅黑'; " +
                        "-fx-font-size: 20px; " +  // 根据需要设置字号
                        "-fx-text-fill: white; " +
                        "-fx-alignment: center;"); // 设置字体、字号、颜色，并将文本居中对齐

                // 创建一个 VBox 用于包含两个 Label
                VBox welcomeBox = new VBox(20);
                welcomeBox.setPadding(new Insets(20));
                welcomeBox.setAlignment(Pos.CENTER);
                welcomeBox.getChildren().addAll(welcomeLabel, welcomeLabel2); // 添加两个 Label

                // 将欢迎文本添加到 rightContentBox
                rightContentBox.getChildren().add(welcomeBox);
                break;


            case "好友列表模块内容":
                // 创建一个 VBox 用于包含搜索框
                VBox searchBox = new VBox(10);
                searchBox.setPadding(new Insets(10, 0, 0, 0)); // 设置上边距

                // 创建搜索框
                TextField searchField = new TextField();
                searchField.setPromptText("搜索好友");
                searchField.setStyle("-fx-background-color: white; " +
                        "-fx-border-color: lightgray; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 15; " +
                        "-fx-background-radius: 15;");

                searchBox.getChildren().add(searchField);
              //  String search =""+ searchField.getText();

                // 创建一个 VBox 用于包含所有好友条块
                VBox friendsListBox = new VBox(10); // 设置条块之间的间距
                searchBox.getChildren().add(friendsListBox); // 将好友列表添加到searchBox中



                // 查询并刷新好友列表的方法
                Runnable refreshFriendsList = () -> {
                    String search = searchField.getText(); // 获取搜索框中的内容

                    String getFriendsSql = "SELECT u.username " +
                            "FROM t_users u " +
                            "INNER JOIN t_friends f ON u.userID = f.friendID " +
                            "WHERE f.userID = ? AND f.status = 'accepted'";



                    ArrayList<Object> arrayList = new ArrayList<>();
                    arrayList.add(Client.uid);
                    ResultSet friendResultSet;

                    // 如果有搜索内容，则使用带搜索条件的 SQL，否则使用默认 SQL
                    if (!search.equals("") && search != null) {
                        String request = "SEARCHFRIENDS1"+" "+Client.uid+" "+"%"+search+"%";
                        sendUtil.sendUTF(request);

//                        arrayList.add("%" + search + "%");
//                        friendResultSet = DbUtil.executeQuery(getFriendsSql1, arrayList);
                    } else {
                        String request = "SEARCHFRIENDS2"+" "+Client.uid;
                        sendUtil.sendUTF(request);
                       // friendResultSet = DbUtil.executeQuery(getFriendsSql, arrayList);

                    }

                    // 清空原有的好友列表
                    friendsListBox.getChildren().clear();

                    // 添加好友条块
                    while (true) {
                        try {
                            if (!Client.friendResultSet.next()) break;
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        // 创建用户名标签
                        Label friendLabel = null;
                        try {
                            friendLabel = new Label(Client.friendResultSet.getString("username"));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        friendLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");
                        friendLabel.setPadding(new Insets(10));

                        // 创建申请联机按钮
                        Button applyConnectBtn = new Button("申请通话");
                        applyConnectBtn.setStyle("-fx-font-family: '微软雅黑'; -fx-font-size: 12px; " +
                                "-fx-background-color: lightblue; -fx-background-radius: 10; " +
                                "-fx-border-radius: 10; -fx-border-color: lightblue; -fx-border-width: 1px;");
                        applyConnectBtn.setPadding(new Insets(5));

                        // 创建发消息按钮
                        Button sendMessageBtn = new Button("发消息");
                        sendMessageBtn.setStyle("-fx-font-family: '微软雅黑'; -fx-font-size: 12px; " +
                                "-fx-background-color: lightblue; -fx-background-radius: 10; " +
                                "-fx-border-radius: 10; -fx-border-color: lightblue; -fx-border-width: 1px;");
                        sendMessageBtn.setPadding(new Insets(5));

                        // 设置申请联机按钮点击事件
                        Label finalFriendLabel = friendLabel;
                        applyConnectBtn.setOnAction(e -> {
                            System.out.println("申请联机: " + finalFriendLabel.getText());
                            openChatWindow(finalFriendLabel.getText());
                        });

                    // 设置发消息按钮点击事件
                        sendMessageBtn.setOnAction(e -> {
                            System.out.println("发送消息: " + finalFriendLabel.getText());
                            openChatWindow(finalFriendLabel.getText());
                        });

                        // 创建一个 Region 用于填充空白
                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);

                        // 将用户名、spacer 和两个按钮放到 HBox 中
                        HBox friendBox = new HBox(20);  // 设置用户名和按钮之间的间距
                        friendBox.setAlignment(Pos.CENTER_LEFT);
                        friendBox.setPadding(new Insets(10));
                        friendBox.setStyle("-fx-background-color: white; " +
                                "-fx-border-radius: 15; -fx-background-radius: 15; " +
                                "-fx-border-color: lightgray; -fx-border-width: 2px; " +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0.5, 2, 2);");

                        // 将两个按钮放在一起，发消息在左侧，申请联机在右侧
                        friendBox.getChildren().addAll(friendLabel, spacer, sendMessageBtn, applyConnectBtn);

                        // 将每个好友条块添加到 VBox 中
                        friendsListBox.getChildren().add(friendBox);
                    }
                };

                // 初始化时显示所有好友
                refreshFriendsList.run();

                // 监听搜索框内容的变化，每次用户输入时更新好友列表
                searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                    refreshFriendsList.run();
                });

                // 创建滑动框
                ScrollPane scrollPane = new ScrollPane(friendsListBox);
                scrollPane.setFitToWidth(true); // 设置宽度适应
                scrollPane.setStyle("-fx-background-color: yellow; " + // 设置背景色为白色
                        "-fx-border-color: pink; " + // 设置边框颜色为 Deep Sky Blue
                        "-fx-border-width: 6px; " + // 设置边框宽度为 6px
                        "-fx-border-radius: 15; " + // 设置弧形边框
                        "-fx-background-radius: 15;"); // 设置背景弧形边框


                // 创建一个主 VBox，用于放置搜索框和滑动框
                VBox mainBox = new VBox(10);
                mainBox.getChildren().addAll(searchBox, scrollPane);

                // 将主 VBox 添加到右侧内容板块
                rightContentBox.getChildren().add(mainBox);
                break;

// Home.java 的相关修改部分

// 在显示用户信息的case中
            case "userInfo":
                VBox userInfoBox = new VBox(10);
                userInfoBox.setPadding(new Insets(10));
                userInfoBox.setStyle("-fx-background-color: white; " +
                        "-fx-border-radius: 15; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: lightgray; " +
                        "-fx-border-width: 2px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0.5, 2, 2);");

                // 添加修改信息按钮
                Button editInfoButton = new Button("修改信息");

                // 头像区域
                Label avatarLabel = new Label("点击上传头像");
                ImageView avatarImageView = new ImageView();
                avatarImageView.setFitWidth(100);
                avatarImageView.setFitHeight(100);
                avatarImageView.setStyle("-fx-border-radius: 50; -fx-background-radius: 50; -fx-border-color: lightgray; -fx-border-width: 2px;");

                // 用户信息展示
                Label usernameLabel = new Label("用户名: "+Client.name);
                Label signatureLabel = new Label("个性签名: XXX");
                Label genderLabel = new Label("性别: 男");
                Label birthdayLabel = new Label("生日: 2000-01-12");
                Label countryLabel = new Label("国家: 中国");
                Label provinceLabel = new Label("省份: 北京市");

                // 监听修改信息按钮点击事件，打开 personalinfo 窗口
                editInfoButton.setOnAction(e -> {
                    new personalinfo((username, signature, gender, birthday, country, province, avatar) -> {
                        // 更新用户信息
                        usernameLabel.setText("用户名: " + username);
                        signatureLabel.setText("个性签名: " + signature);
                        genderLabel.setText("性别: " + gender);
                        birthdayLabel.setText("生日: " + birthday);
                        countryLabel.setText("国家: " + country);
                        provinceLabel.setText("省份: " + province);
                        /*avatarImageView.setImage*///(avatar);

                        //本地以及数据库中的数据也需要更新

                        //由于外部还没有完成连接代码
//                        try {
//                            Client.client = new Socket("127.0.0.1",7777);
//                        } catch (IOException ex) {
//                            throw new RuntimeException(ex);
//                        }
//
//                        TCPSendUtil sendUtil = new TCPSendUtil(Client.client );
//                        TCPReceiveUtil receiveUtil = new TCPReceiveUtil(Client.client) ;

                        Client.name = username;
                        String request = "UPDATE"+" "+username+" "+Client.uid;

                        sendUtil.sendUTF(request);
                        System.out.println(receiveUtil.receiveUTF());

                    }).start(new Stage());
                });

                // 将头像和用户信息添加到用户信息框
                userInfoBox.getChildren().addAll(avatarImageView, usernameLabel, signatureLabel, genderLabel, birthdayLabel, countryLabel, provinceLabel, editInfoButton);

                if (rightContentBox != null) {
                    rightContentBox.getChildren().clear();  // 清除之前的内容
                    rightContentBox.getChildren().add(userInfoBox);  // 添加用户信息框
                }
                break;

            case "远程投屏模块内容":
                // 创建一个弧形边框白色背景的块
                VBox remoteCastingBox = new VBox(20);
                remoteCastingBox.setPadding(new Insets(20));
                remoteCastingBox.setAlignment(Pos.CENTER);
                remoteCastingBox.setStyle("-fx-background-color: white; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-border-color: lightgray; " +
                        "-fx-border-width: 2px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0.5, 2, 2);");

                // 图片按钮1：远程投屏
                // 图片按钮1：远程投屏
                ImageView leftImage = new ImageView(new Image(getClass().getResourceAsStream("/touping.png")));
                leftImage.setFitWidth(100);
                leftImage.setFitHeight(100);
                Button leftButton = new Button();
                leftButton.setGraphic(leftImage);
                leftButton.setStyle("-fx-background-color: transparent;"); // 去除按钮默认样式
                leftButton.setOnAction(e -> openScreenCastingWindow()); // 点击事件，弹出 touping 窗口


                // 小标题1：远程投屏
                Label leftLabel = new Label("远程投屏");
                leftLabel.setStyle("-fx-font-family: '微软雅黑'; -fx-font-size: 16px; -fx-text-fill: black;");

                // 图片按钮与小标题1放在VBox
                VBox leftBox = new VBox(10);
                leftBox.setAlignment(Pos.CENTER);
                leftBox.getChildren().addAll(leftButton, leftLabel);

                // 图片按钮2：远程控制
                // 图片按钮2：远程控制
                ImageView rightImage = new ImageView(new Image(getClass().getResourceAsStream("/yuanchengkongzhi.png")));
                rightImage.setFitWidth(100);
                rightImage.setFitHeight(100);
                Button rightButton = new Button();
                rightButton.setGraphic(rightImage);
                rightButton.setStyle("-fx-background-color: transparent;");
                rightButton.setOnAction(e -> openControlWindow()); // 点击事件，弹出 yuanchengkongzhi 窗口


                // 小标题2：远程控制
                Label rightLabel = new Label("远程控制");
                rightLabel.setStyle("-fx-font-family: '微软雅黑'; -fx-font-size: 16px; -fx-text-fill: black;");

                // 图片按钮与小标题2放在VBox
                VBox rightBox = new VBox(10);
                rightBox.setAlignment(Pos.CENTER);
                rightBox.getChildren().addAll(rightButton, rightLabel);

                // 设置左右图片按钮和小标题放在一个HBox中
                HBox buttonBox = new HBox(50);
                buttonBox.setAlignment(Pos.CENTER);
                buttonBox.getChildren().addAll(leftBox, rightBox);

                // 三角图标按钮
                ImageView arrowIcon = new ImageView(new Image(getClass().getResourceAsStream("/dianji.png")));
                arrowIcon.setFitWidth(20);
                arrowIcon.setFitHeight(20);
                Button dropDownButton = new Button();
                dropDownButton.setGraphic(arrowIcon);
                dropDownButton.setStyle("-fx-background-color: transparent;"); // 去除按钮背景

                // 提示 "选择对象"
                Label promptLabel = new Label("选择对象");
                promptLabel.setStyle("-fx-font-family: '微软雅黑'; -fx-font-size: 16px; -fx-text-fill: black;");

                // 三角按钮和提示放在HBox中
                HBox dropDownBox = new HBox(10);
                dropDownBox.setAlignment(Pos.CENTER_LEFT);
                dropDownBox.getChildren().addAll(dropDownButton, promptLabel);

                // 多选列表框
                ListView<HBox> targetListView = new ListView<>();
                targetListView.setPrefWidth(200);
                targetListView.setVisible(false); // 初始设置为隐藏


                //12
                //String[] targets = {"XX01", "XX02", "XX03", "XX04", "XX05"};
                for (String target : Client.friendNames) {
                    CheckBox checkBox = new CheckBox(target);
                    HBox hBox = new HBox(10);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.getChildren().add(checkBox);
                    targetListView.getItems().add(hBox);
                }

                // 下拉按钮事件：显示/隐藏选项列表
                dropDownButton.setOnAction(e -> {
                    targetListView.setVisible(!targetListView.isVisible()); // 切换列表可见性
                    arrowIcon.setRotate(targetListView.isVisible() ? 90 : 0); // 切换箭头方向
                });

                // 将下拉选择框和列表框添加到remoteCastingBox
                remoteCastingBox.getChildren().addAll(buttonBox, dropDownBox, targetListView);

                // 将整个块添加到rightContentBox中
                rightContentBox.getChildren().add(remoteCastingBox);
                break;


        }
    }
    // 打开 Chat 窗口的方法
    private void openChatWindow(String friendname) {
        // 使用 Platform.runLater 启动新的窗口
        // 检查是否已经存在好友的聊天窗口
        //Stage chatStage = chatWindows.get(friendname);

        if (Client.chatWindows.get(friendname) != null) {
            // 如果已经存在，则直接显示
            Client.chatWindows.get(friendname).show();
            Client.chatWindows.get(friendname).toFront();  // 将窗口置于前端
        } else {
            // 如果不存在，则新建聊天窗口
            Platform.runLater(() -> {
                try {
                    // 创建 Chat 窗口的实例
                    chat chatWindow = new chat();

                    Stage  chatStage = new Stage();
                    chatStage.setTitle(friendname);

                    chatWindow.start(chatStage); // 启动新的 Chat 窗口
                    // 初始化聊天区域
                    TextArea chatArea = chatWindow.getChatArea();
                    chatArea.appendText(loadChatHistory(DbUtil.getID(friendname))); // 加载历史记录



                    // 保存聊天窗口到缓存
                    Client.chatWindows.put(friendname, chatStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }


    // 新窗口方法：用于显示屏幕投屏的窗口
    private void openScreenCastingWindow() {
        Platform.runLater(() -> {
            try {
                // 创建 touping 窗口的实例并启动
                touping toupingWindow = new touping();
                Stage toupingStage = new Stage();
                toupingWindow.start(toupingStage); // 启动 touping 窗口
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // 新窗口方法：用于显示设备控制的窗口
    private void openControlWindow() {
        Platform.runLater(() -> {
            try {
                // 创建 yuanchengkongzhi 窗口的实例并启动
                yuanchengkongzhi controlWindow = new yuanchengkongzhi();
                Stage controlStage = new Stage();
                controlWindow.start(controlStage); // 启动 yuanchengkongzhi 窗口
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // 加载聊天历史的方法，从 txt 文件中读取并返回
    private String loadChatHistory(int id) {
        StringBuilder chatHistory = new StringBuilder();
        File chatFile = new File( id + "_chat.txt");

        if (chatFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(chatFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    chatHistory.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return chatHistory.toString();
    }


}