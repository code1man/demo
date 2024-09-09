// 远程投屏
package org.example.demo.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.demo.Main;
import org.example.demo.ui.Home;

public class TencentMeeting {

    public Button FullScreen;
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

    private boolean isFullScreen = false;
    Image micro=new Image("/microPhone.png");
    Image video1=new Image("/video.png");
    Image screen1=new Image("/share.jpg");
    Image chat1=new Image("/chat.png");
    //Image exit=new Image("/exit.jpg");
    //Image max=new Image("/max.jpg");
    public void initialize()
    {
        this.setPrimaryStage(primaryStage);

        //ImageView imageView3=new ImageView(max);
        //FullScreen.setGraphic(imageView3);
        //ImageView imageView1=new ImageView(exit);
        //quit.setGraphic(imageView1);

        microPic=new ImageView(micro);
        microPhone.setGraphic(microPic);
        videoPic=new ImageView(video1);
        video.setGraphic(videoPic);
        screenPIc=new ImageView(screen1);
        screen.setGraphic(screenPIc);
        chatPic=new ImageView(chat1);
        chat.setGraphic(chatPic);
        microPic.setFitHeight(50);
        microPic.setFitWidth(50);
        chatPic.setFitHeight(50);
        chatPic.setFitWidth(50);
        screenPIc.setFitHeight(50);
        screenPIc.setFitWidth(50);
        videoPic.setFitHeight(50);
        videoPic.setFitWidth(50);
    }

   public static boolean isPic1=true;
   public static boolean isPic3=true;

    public void setPrimaryStage(Stage stage) {

        this.primaryStage =Main.stage;

        FullScreen.setOnAction(event -> toggleFullScreen(primaryStage));
    }

    private void toggleFullScreen(Stage stage) {
        // Toggle full screen mode
        isFullScreen = !isFullScreen;
        stage.setFullScreen(isFullScreen);
    }

    @FXML
    void trans1(ActionEvent event) {

        if(isPic1)
        {   ImageView imageView=new ImageView("/microPhone1.png");
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            microPhone.setGraphic(imageView);
        }else{
            microPhone.setGraphic(microPic);
        }
        isPic1=!isPic1;
    }

    @FXML
    void trans2(ActionEvent event) {
        if(isPic3)
        {
            ImageView imageView1=new ImageView("/video1.png");
            imageView1.setFitHeight(50);
            imageView1.setFitWidth(50);
            video.setGraphic(imageView1);
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
         new Home().start(Main.stage);
    }
}
