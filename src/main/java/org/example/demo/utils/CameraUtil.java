package org.example.demo.utils;

import com.github.sarxos.webcam.Webcam;
import org.example.demo.Client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;


// 视频聊天获取摄像头
public class CameraUtil {
    public static boolean isCalling = false;
    private final Webcam webcam;
    private final TCPSendUtil tcpSendUtil;
    private final TCPReceiveUtil tcpReceiveUtil;

    private final Thread RecieveVideoThread;
    private final Thread SendVideoThread;

    private final String SERVER_ADDRESS = "localhost";
    private final int SERVER_PORT = 8888;

    public CameraUtil() {
        // get default webcam and open it获取网络摄像头设置并打开
        webcam = Webcam.getDefault();
        tcpReceiveUtil = new TCPReceiveUtil(Client.CameraClient);
        tcpSendUtil = new TCPSendUtil(Client.CameraClient);

        RecieveVideoThread = new Thread(()->{
            while(isCalling) {
                // get image获取图片
                BufferedImage bufferedImage = webcam.getImage();
                byte[] image = tcpSendUtil.getImageBytes(bufferedImage);
                tcpSendUtil.sendImg(image);
            }
        });

        SendVideoThread = new Thread(()->{
            while(isCalling) {
                byte[] imageData = tcpReceiveUtil.receiveImg();
                ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
                try {
                    BufferedImage image = ImageIO.read(bais);
                    //loginController.updateImage(image);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    //打开摄像头
    public void openVideoModule() {
        try {
            Client.CameraClient = new Socket(SERVER_ADDRESS,SERVER_PORT);
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
}
