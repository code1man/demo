package org.example.demo.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class Shenqing extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("shenqing.fxml"));
        Pane pane = loader.load();

        // Initialize the Popup
        Popup popup = new Popup();
        popup.getContent().add(pane);
        popup.setAutoHide(true);

        // Get screen dimensions
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Set the position of the popup to the bottom-right corner of the screen
        popup.setX(screenBounds.getWidth() - pane.getPrefWidth() - 10);
        popup.setY(screenBounds.getHeight() - pane.getPrefHeight() - 10);

        // Show the popup
        popup.show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
