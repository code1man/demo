package org.example.demo.utils;

import com.github.sarxos.webcam.Webcam;
import org.example.demo.Client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.example.demo.Main.loginController;

// 视频聊天获取摄像头
public class CameraUtil {
    private Webcam webcam;
    private TCPSendUtil tcpSendUtil;
    private  TCPReceiveUtil tcpReceiveUtil;

    public CameraUtil() {
        // get default webcam and open it获取网络摄像头设置并打开
        webcam = Webcam.getDefault();
        tcpReceiveUtil = new TCPReceiveUtil(Client.client);
        tcpSendUtil = new TCPSendUtil(Client.client);
    }

    public void openVideoModule() {
        webcam.open();
        new Thread(()->{
            while(true) {
                // get image获取图片
                BufferedImage bufferedImage = webcam.getImage();
                byte[] image = tcpSendUtil.getImageBytes(bufferedImage);
                tcpSendUtil.sendImg(image);
            }
        }).start();

        new Thread(()->{
            while(true) {
                byte[] imageData = tcpReceiveUtil.receiveImg();
                ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
                try {
                    BufferedImage image = ImageIO.read(bais);
                    loginController.updateImage(image);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
