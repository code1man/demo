package org.example.demo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Popup;
import org.example.demo.Client;
import org.example.demo.utils.TCPSendUtil;

public class shenqingController {
    public String username;
    public Label usernameLabel;
    public Button yesButton;
    public Button noButton;
    public ImageView profileImageView;
    private Popup popup;

    @FXML
    private void initialize() {
        yesButton.setOnAction(event -> handleButtonClick("接受"));
        noButton.setOnAction(event -> handleButtonClick("拒绝"));
    }

    public void updateLabels(String username, String imgUrl) {
        this.username = username;
        usernameLabel.setText("用户名: " + username);
        profileImageView.setImage(new Image(imgUrl));
    }


    public void setPopup(Popup popup) {
        this.popup = popup; // Set the Popup instance
    }

    public void handleButtonClick(String buttonText) {
        TCPSendUtil sendUtil = new TCPSendUtil(Client.client);
        String request = null;

        if (buttonText.equals("接受")) {
            request = "ADDFRIENDS" + " " + "accepted" + " " + Client.uid + " " + username;
            sendUtil.sendUTF(request);
            Client.friendNames.add(username);
        } else {
            request = "ADDFRIENDS" + " " + "blocked"+" " + Client.uid +" "+ username;
            sendUtil.sendUTF(request);
        }

        if (popup != null) {
            popup.hide();
        }
    }
}
