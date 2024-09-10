package org.example.demo;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.demo.utils.TCPReceiveUtil;
import org.example.demo.utils.TCPSendUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.example.demo.ui.Home.controlWindow;

public class Client {

    public static String name = null;
    public static String uid = null;

    public static  String  avatarUrl="/touxiang.png";  //头像路径
    public static  int controlTimes = 0 ;  //操控/touxiang.png次数
    public static double goodRatingPercentage = 0.0;//好评率

    public static int friendNumb = 0;
    public static ArrayList<String>friendNames = new ArrayList<>();

    public static ArrayList<String> selectFriendName = new ArrayList<>();
    public static String sex = "无";
    public static String country = "中国";
    public static String province = "北京";
    public static String birthday = "2000-01-01";
    public static int age = 24;
    public static String signature = "摆烂";

    public static Socket client = null;
    public static Socket secondClient = null;
    public static Socket RemoteControlClient = null;
    public static Socket friendClient = null;
    public static Socket CameraClient = null;
    public static Socket RemoteCastClient = null;


    private static Thread recieveImgThread;
    private static Thread sendImgThread;
    private static Thread robotThread;

    public static Map<String, Stage> chatWindows = new HashMap<>();

    public void init() {
        sendImgThread = new Thread(() -> {
            TCPSendUtil sendUtil = new TCPSendUtil(Client.client);
            while (true) {
                Robot robot = null;
                try {
                    robot = new Robot();
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    Rectangle rec = new Rectangle(0, 0, screenSize.width, screenSize.height);
                    BufferedImage bi = robot.createScreenCapture(rec);
                    byte[] imageBytes = sendUtil.getImageBytes(bi);
                    sendUtil.sendImg(imageBytes);
                } catch (AWTException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

         recieveImgThread = new Thread(()->{
            TCPReceiveUtil receive = new TCPReceiveUtil(Client.client);
            while (true) {
                byte[] imageData = receive.receiveImg();
                ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
                try {
                    BufferedImage image = ImageIO.read(bais);
                    controlWindow.updateImage(image);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        robotThread = new Thread(()->{
            TCPReceiveUtil receiveUtil = new TCPReceiveUtil(Client.RemoteControlClient);
            try {
                Robot robot = new Robot();

                while (true) {
                    String[] order = receiveUtil.receiveUTF().split("#");
                    String type = order[0];
                    switch (type) {
                        case "mouseMoved":
                            int x = Integer.parseInt(order[1]);
                            int y = Integer.parseInt(order[2]);

                            int windowSizeWidth = Integer.parseInt(order[3]);
                            int windowSizeHeight = Integer.parseInt(order[4]);
                            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                            robot.mouseMove(x * screenSize.width / windowSizeWidth, y * screenSize.height / windowSizeHeight);

                            System.out.println("x: " + x + ", y: " + y);
                            break;

                        case "mousePressed":
                            int mouseButtonPress = Integer.parseInt(order[1]);
                            //robot.mousePress(mouseButtonPress);
                            System.out.println("mouseButtonPress: " + mouseButtonPress);
                            break;

                        case "mouseReleased":
                            int mouseButtonRelease = Integer.parseInt(order[1]);
                            //robot.mouseRelease(mouseButtonRelease);
                            System.out.println("mouseReleased: " + mouseButtonRelease);
                            break;

                        case "mouseDragged":
                            int dragX = Integer.parseInt(order[1]);
                            int dragY = Integer.parseInt(order[2]);
                            // robot.mouseMove(dragX, dragY);
                            System.out.println("dragX: " + dragX + ", dragY: " + dragY);
                            break;

                        case "mouseWheel":
                            int wheelAmount = Integer.parseInt(order[1]);
                            //robot.mouseWheel(wheelAmount);
                            System.out.println("wheelAmount: " + wheelAmount);
                            break;

                        case "keyPressed":
                            int keyCodePress = Integer.parseInt(order[1]);
                            //robot.keyPress(keyCodePress);
                            System.out.println("keyCodePress: " + keyCodePress);
                            break;

                        case "keyReleased":
                            int keyCodeRelease = Integer.parseInt(order[1]);
                            //robot.keyRelease(keyCodeRelease);
                            System.out.println("keyCodeRelease: " + keyCodeRelease);
                            break;

                        default:
                            System.out.println("Unknown event type: " + type);
                            break;
                    }
                }
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void startRemoteHash() {
        sendImgThread.start();
    }

    public static void recieveRemoteHash() {
        recieveImgThread.start();
    }

    public static void startRemoteControl(){
        sendImgThread.start();
        robotThread.start();
    }

    public static void stopRemoteControl(){
        sendImgThread.interrupt();
        robotThread.interrupt();
    }

    public static void stopRemoteHash(){
        sendImgThread.interrupt();
        recieveImgThread.interrupt();
    }

    // 手动更新头像的方法
    public static void updateAvatar(ImageView imageView) {
        if (Client.avatarUrl != null && !Client.avatarUrl.isEmpty()) {
            File avatarFile = new File(Client.avatarUrl);
            if (avatarFile.exists()) {
                imageView.setImage(new Image(avatarFile.toURI().toString()));
                System.out.println("头像已更新: " + Client.avatarUrl);
            } else {
                System.out.println("头像文件不存在: " + Client.avatarUrl);
            }
        }
    }
}


