package org.example.demo.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.demo.Main;

public class TencentMeeting {

    @FXML
    private Button chat;

    @FXML
    private ImageView chatPic;

    @FXML
    private Button microPhone;

    @FXML
    private ImageView microPic;



    @FXML
    private Button screen;

    @FXML
    private ImageView screenPIc;

    @FXML
    private AnchorPane touxiang;

    @FXML
    private Button video;

    @FXML
    private ImageView videoPic;

    private Stage primaryStage;

    @FXML
    private Button quit;
    @FXML
    private Button Max;
    private boolean isFullScreen = false;
    Image micro=new Image("/microPhone.png");
    Image video1=new Image("/video.png");
    Image screen1=new Image("/share.jpg");
    Image chat1=new Image("/chat.png");

    public void initialize()
    {   this.setPrimaryStage(Main.stage);
        microPic=new ImageView(micro);
        microPhone.setGraphic(microPic);
        videoPic=new ImageView(video1);
        video.setGraphic(videoPic);
        screenPIc=new ImageView(screen1);
        screen.setGraphic(screenPIc);
        chatPic=new ImageView(chat1);
        chat.setGraphic(chatPic);
    }

   public static boolean isPic1=true;
   public static boolean isPic3=true;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
        Max=new Button();
        Max.setOnAction(event -> toggleFullScreen(primaryStage));
    }

    private void toggleFullScreen(Stage stage) {
        // Toggle full screen mode
        isFullScreen = !isFullScreen;
        stage.setFullScreen(isFullScreen);
    }

    @FXML
    void trans1(ActionEvent event) {

        if(isPic1)
        {
            microPhone.setGraphic(new ImageView("/microPhone1.png"));
        }else{
            microPhone.setGraphic(microPic);
        }
        isPic1=!isPic1;
    }

    @FXML
    void trans2(ActionEvent event) {
        if(isPic3)
        {
            video.setGraphic(new ImageView("/video1.png"));
        }else{
            video.setGraphic(videoPic);
        }
        isPic3=!isPic3;
    }

    @FXML
    void trans3(ActionEvent event) {
        System.out.println("链接投屏");
    }

    @FXML
    void trans4(ActionEvent event) {
        System.out.println("链接杨轶");
    }

    public void toggleFullScreen(ActionEvent actionEvent) {
        // Toggle full screen mode

        isFullScreen = !isFullScreen;
        primaryStage.setFullScreen(isFullScreen);
    }

    public void close(ActionEvent actionEvent) {


    }
}
