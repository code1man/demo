package org.example.demo.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class shenqingController {
    public Label usernameLabel;
    public Label signatureLabel;
    public Label genderLabel;
    public Button yesButton;
    public Button noButton;
    public ImageView profileImageView;

    public void updateLabels(String username, String signature, String gender, String imgUrl) {
        usernameLabel.setText("用户名: " + username);
        signatureLabel.setText("个性签名: " + signature);
        genderLabel.setText("性别: " + gender);
        profileImageView.setImage(new Image(imgUrl));
    }
}
