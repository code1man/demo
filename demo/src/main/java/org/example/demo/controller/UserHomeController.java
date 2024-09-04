package org.example.demo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.demo.Main;

import java.io.IOException;

public class UserHomeController {
    public Button secondaryButton;

    @FXML
    private void switchToPrimary() throws IOException {
        Main.setRoot("Login");
    }
}
