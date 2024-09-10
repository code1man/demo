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

    public CameraUtil() {
        // get default webcam and open it获取网络摄像头设置并打开
        webcam = Webcam.getDefault();

        RecieveVideoThread = new Thread(()->{
            while(isCalling) {
                // get image获取图片
                BufferedImage bufferedImage = webcam.getImage();
                byte[] image = tcpSendUtil.getImageBytes(bufferedImage);
                tcpSendUtil.sendImg(image);
            }
        });

        SendVideoThread = new Thread(()->{
            Platform.runLater(()->{
            while(isCalling) {
                byte[] imageData = tcpReceiveUtil.receiveImg();
                ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
                try {
                    BufferedImage image = ImageIO.read(bais);
                    videoWindow.updateImage(image);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            });
        });
    }

    //打开摄像头
    public void openVideoModule(String friendName) {
        try {
            Client.CameraClient = new Socket(SERVER_ADDRESS,SERVER_PORT);
            tcpReceiveUtil = new TCPReceiveUtil(Client.CameraClient);
            tcpSendUtil = new TCPSendUtil(Client.CameraClient);
            tcpSendUtil.sendUTF(Client.uid + " " + friendName);
            webcam.open();
            SendVideoThread.start();
            RecieveVideoThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //关闭摄像头
    public void closeVideoModule() {
        try {
            Client.CameraClient.close();
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
