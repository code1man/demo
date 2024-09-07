package org.example.demo.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.demo.Client;
import org.example.demo.Server;
import org.example.demo.utils.DbUtil;
import org.example.demo.utils.TCPReceiveUtil;
import org.example.demo.utils.TCPSendUtil;

import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LoginApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("用户登录");
        primaryStage.getIcons().add(new Image("logo.jpg"));
        // 设置背景图片
        Image backgroundImage = new Image("back03.jpg"); // 使用上传的图片路径
        BackgroundImage background = new BackgroundImage(
                backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true)
        );

        // 创建一个VBox用于垂直布局
        VBox vbox = new VBox(20);  // 设置间距为20
        vbox.setPadding(new Insets(20, 30, 20, 30));  // 设置VBox的Padding
        vbox.setAlignment(Pos.CENTER);
        vbox.setMaxWidth(280);  // 设置最大宽度为250
        vbox.setMaxHeight(320); // 设置最大高度为250

        // 设置VBox背景颜色为白色，并加上圆角和阴影效果
        vbox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 0, 0);");

        // logo图片
        ImageView logo = new ImageView("logo.jpg");  // 替换为你logo图片的路径
        logo.setFitWidth(100);
        logo.setFitHeight(80);

        // slogan图片
        ImageView slogen = new ImageView("slogen.png");  // 替换为你slogan图片的路径
        slogen.setFitWidth(120);
        slogen.setFitHeight(15);

        // 创建一个VBox放置logo和slogan
        VBox logoBox = new VBox(10);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.getChildren().addAll(logo, slogen);

        // 用户名输入区域
        Label userNameLabel = new Label("用户名");
        userNameLabel.setStyle("-fx-font-family: '微软雅黑'; -fx-font-size: 14px; ");

        TextField userNameField = new TextField();
        userNameField.setPromptText("请输入用户名");
        userNameField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: lightblue; -fx-border-width: 2px;");

        // 密码输入区域
        Label passwordLabel = new Label("密码");
        passwordLabel.setStyle("-fx-font-family: 'Microsoft YaHei'; -fx-font-size: 14px; ");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("请输入密码");
        passwordField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: lightblue; -fx-border-width: 2px;");

        // 使用GridPane布局输入框和标签
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(userNameLabel, 0, 0);
        gridPane.add(userNameField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);

        // 登录和注册按钮
        Button loginButton = new Button("登录");
        Button registerButton = new Button("注册");

        // 设置按钮宽度
        loginButton.setPrefWidth(70);
        registerButton.setPrefWidth(70);

        // 设置按钮样式
        loginButton.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-border-color: lightblue;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-font-family: '微软雅黑';" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-color: white;" +
                        "-fx-background-color:lightblue"
        );

        registerButton.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-border-color: lightblue;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-font-family: '微软雅黑';" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-color: white;" +
                        "-fx-background-color:lightblue"
        );

        // 将按钮放入HBox
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loginButton, registerButton);

        // 将logoBox、GridPane、按钮添加到VBox
        vbox.getChildren().addAll(logoBox, gridPane, buttonBox);

        // 创建一个带背景图片的Pane
        StackPane root = new StackPane();
        root.setBackground(new Background(background));
        root.getChildren().add(vbox);

        // 调整场景大小
        Scene scene = new Scene(root, 600, 500);  // 缩小场景大小
        primaryStage.setScene(scene);
        primaryStage.show();

        //Socket socket = null;
        //由于外部还没有完成连接代码
        try {
            Client.client = new Socket("127.0.0.1",7777);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        TCPSendUtil sendUtil = new TCPSendUtil(Client.client );
        TCPReceiveUtil receiveUtil = new TCPReceiveUtil(Client.client) ;

        // 登录按钮点击事件
        loginButton.setOnAction(e -> {

            // 获取用户名和密码输入
            String username = userNameField.getText();
            String password = passwordField.getText();
            String request = "LOGIN"+" "+username+" "+password;

            //发送登录请求
            sendUtil.sendUTF(request);

            //接收服务器响应
            String info = receiveUtil.receiveUTF();
            System.out.println(info);

            //如果成功，则跳转，如果失败则信息框提示
            if(info.equals("登陆成功"))
            {
                //将一部分登录信息存入本地
                String sql = "SELECT userid,avatarUrl,controltimes,goodratingpercentage FROM t_users\n" +
                        "WHERE username = ?";

                ArrayList<Object> arrayList = new ArrayList<>();
                arrayList.add(username);
                ResultSet resultSet = DbUtil.executeQuery(sql,arrayList);


                Client.name = username;

                try {
                    if(resultSet.next())
                    {
                        Client.uid = resultSet.getString("userid");
                        if(resultSet.getString("avatarUrl")!=null||!(resultSet.getString("avatarUrl").isEmpty())) {
                            Client.avatarUrl = resultSet.getString("avatarUrl");
                        }
                        Client.controlTimes = (resultSet.getInt("controlTimes"));
                        Client.goodRatingPercentage = resultSet.getDouble("goodRatingPercentage");

                        //测试
                        System.out.println("你好");
                        System.out.println(Client.uid+Client.avatarUrl+Client.controlTimes+Client.goodRatingPercentage);
                    }

                    //以后这里还得插入好友信息

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                System.out.println(Client.uid +" "+ Client.name+" "+Client.avatarUrl+" "+Client.controlTimes+" "+Client.goodRatingPercentage);

                new Home().start(primaryStage); // 启动主界面
            }
            else{
                System.out.println("用户名或密码错误！");
            }


            // } else {

            // }
        });

        // 注册按钮点击事件
        registerButton.setOnAction(e -> {
            // 获取用户名和密码输入
            String username = userNameField.getText();
            String password = passwordField.getText();
            String request = "REGISTER"+" "+username+" "+password;

            sendUtil.sendUTF(request);


            //接收服务器响应
            String info = receiveUtil.receiveUTF();
            System.out.println(info);

            //成功/失败则信息框提示

        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    // 注释掉实际的数据库操作方法
    // private boolean registerUser(String username, String password) {
    //     String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
    //     try (Connection conn = getConnection();
    //          PreparedStatement pstmt = conn.prepareStatement(sql)) {
    //         pstmt.setString(1, username);
    //         pstmt.setString(2, password);
    //         int rowsAffected = pstmt.executeUpdate();
    //         return rowsAffected > 0;
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         return false;
    //     }
    // }

    // private boolean loginUser(String username, String password) {
    //     String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
    //     try (Connection conn = getConnection();
    //          PreparedStatement pstmt = conn.prepareStatement(sql)) {
    //         pstmt.setString(1, username);
    //         pstmt.setString(2, password);
    //         ResultSet rs = pstmt.executeQuery();
    //         return rs.next(); // 如果找到匹配的记录，返回true
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         return false;
    //     }
    // }

    // private Connection getConnection() throws SQLException {
    //     return DriverManager.getConnection(URL, USER, PASSWORD);
    // }
}
