package org.example.demo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import org.example.demo.Main;

import java.io.IOException;

public class LoginController {

    public Button primaryButton;
    public ImageView imageView;

    @FXML
    private void switchToSecondary() throws IOException {
        Main.setRoot("UserHome");
    }
}
