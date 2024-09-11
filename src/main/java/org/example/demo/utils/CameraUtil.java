package org.example.demo.utils;

import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import org.example.demo.Client;
import org.example.demo.ui.shiping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;


// 视频聊天获取摄像头
public class CameraUtil {
    public static boolean isCalling = false;
    private final Webcam webcam;
    private TCPSendUtil tcpSendUtil;
    private TCPReceiveUtil tcpReceiveUtil;

    private final Thread RecieveVideoThread;
    private final Thread SendVideoThread;

    private final String SERVER_ADDRESS = "localhost";
    private final int SERVER_PORT = 8848;
    public shiping videoWindow;

    public CameraUtil(String friendName) {
        // get default webcam and open it获取网络摄像头设置并打开
        try {
            Client.CameraClient = new Socket(SERVER_ADDRESS, SERVER_PORT);
            Client.confirmVidioCallClient = new Socket("localhost", 8849);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tcpReceiveUtil = new TCPReceiveUtil(Client.CameraClient);
        tcpSendUtil = new TCPSendUtil(Client.CameraClient);
        new TCPSendUtil(Client.confirmVidioCallClient).sendUTF(Client.uid + " " + friendName);

        webcam = Webcam.getDefault();

        SendVideoThread = new Thread(()->{
            while(true) {
                BufferedImage bufferedImage = webcam.getImage();
                byte[] image = tcpSendUtil.getImageBytes(bufferedImage);
                tcpSendUtil.sendImg(image);
            }
        });

        RecieveVideoThread = new Thread(()->{
            while(true) {
                // get image获取图片
                byte[] imageData = tcpReceiveUtil.receiveImg();
                if (imageData != null) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
                    try {
                        BufferedImage image = ImageIO.read(bais);
                        Platform.runLater(()->{
                            videoWindow.updateImage(image);
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    //打开摄像头
    public void openVideoModule(String friendName) {
        try {
            webcam.open();

            SendVideoThread.start();
            RecieveVideoThread.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //关闭摄像头
    public void closeVideoModule() {
        try {
            Client.CameraClient.close();
            Client.confirmVidioCallClient.close();
            webcam.close();
            SendVideoThread.interrupt();
            RecieveVideoThread.interrupt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setShiPing(shiping videoWindow) {
        this.videoWindow = videoWindow;
    }
}
