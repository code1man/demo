package org.example.demo;

import org.example.demo.utils.TCPReceiveUtil;
import org.example.demo.utils.TCPSendUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

//import static org.example.demo.Main.loginController;

public class Client {

    public static String name = null;
    public static String uid = null;
    public static int friendNumb = 0;
    public static ArrayList<String>friendNames = null;
    public static String sex = "无";
    public static String country = "中国";
    public static String province = "北京";
    public static String birthday = "2000-01-01";
    public static int age = 24;
    public static String signature = "摆烂";
    public static String imagePath = "/touxiang.png";
    public static Socket client = null;
    public static Socket secondClient = null;
    public static Socket friendClient = null;

    private Thread recieveImgThread;
    private Thread sendImgThread;
    private Thread robotThread;
    private Thread friendResultThread;
    private Thread friendThread;


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
                    //loginController.updateImage(image);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        robotThread = new Thread(()->{
            TCPReceiveUtil receiveUtil = new TCPReceiveUtil(Client.secondClient);
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

        friendThread = new Thread(()->{

        });

        friendResultThread = new Thread(()->{
            TCPReceiveUtil receiveUtil = new TCPReceiveUtil(Client.friendClient);
            while (true) {
                String friendReply = receiveUtil.receiveUTF();
                if (friendReply != null && friendReply.contains(":")) {
                    String[] reply = friendReply.split(":");
                    if (reply[1].equals("同意")) {
                        Client.friendNumb++;
                        Client.friendNames.add(reply[0]);
                        // 弹出好友同意的窗口
                    } else if (reply[1].equals("拒绝")) {
                        // 弹出好友不同意的窗口
                    }
                }
            }
        });
    }

    public void startRemoteHash() {
        sendImgThread.start();
        recieveImgThread.start();
    }

    public void startRemoteControl(){
        sendImgThread.start();
        recieveImgThread.start();
        robotThread.start();
    }

    public void stopRemoteControl(){
        sendImgThread.interrupt();
        recieveImgThread.interrupt();
        robotThread.interrupt();
    }

    public void stopRemoteHash(){
        sendImgThread.interrupt();
        recieveImgThread.interrupt();
    }
}


