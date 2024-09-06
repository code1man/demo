package org.example.demo.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.example.demo.Main;
import org.example.demo.utils.RemoteControlUtil;
import org.example.demo.utils.TCPSendUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class LoginController {

    private TCPSendUtil sendUtil;
    private RemoteControlUtil remoteControlUtil;

    @FXML
    public Button primaryButton;
    @FXML
    public ImageView imageView;
    @FXML
    public Pane pane;

    @FXML
    private void switchToSecondary() throws IOException {
        Main.setRoot("UserHome");
    }

    @FXML
    private void initialize() throws IOException {
       /* sendUtil = new TCPSendUtil(Client.secondClient);
        remoteControlUtil = new RemoteControlUtil();
        // 设置鼠标事件处理器
        pane.setOnMouseClicked(this::handleMouseEvent);
        pane.setOnMousePressed(this::handleMouseEvent);
        pane.setOnMouseReleased(this::handleMouseEvent);
        pane.setOnMouseMoved(this::handleMouseEvent);
        pane.setOnMouseDragged(this::handleMouseEvent);

        // 设置键盘事件处理器
        pane.setFocusTraversable(true); // 使 ImageView 可获得焦点
        pane.setOnKeyPressed(this::handleKeyEvent);
        pane.setOnKeyReleased(this::handleKeyEvent);*/
    }

    private void handleMouseEvent(MouseEvent event) {
        sendUtil.sendUTF(remoteControlUtil.mouseEvent(event));
    }

    private void handleKeyEvent(KeyEvent event) {
        sendUtil.sendUTF(remoteControlUtil.keyEvent(event));
    }

    public void updateImage(BufferedImage bufferedImage) {
        if (bufferedImage != null)
        {
            Image javafxImage = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(javafxImage);
        }
    }
}
