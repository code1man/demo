package org.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.demo.controller.LoginController;
import org.example.demo.utils.CameraUtil;

import java.io.IOException;
import java.net.Socket;

public class Main extends Application {

    public static LoginController loginController;
    private static Scene scene;

    @Override
     public void start(Stage stage) throws IOException {
        Client.client = new Socket("localhost", 8888);
        Client.secondClient = new Socket("localhost", 7777);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();
        loginController = loader.getController();
        scene = new Scene(root, 640, 480);

        CameraUtil cameraUtil = new CameraUtil();
        cameraUtil.openVideoModule();

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
}