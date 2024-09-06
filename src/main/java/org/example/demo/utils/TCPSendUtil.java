package org.example.demo.utils;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class TCPSendUtil {
    private final Socket client;
    private final DataOutputStream dos;

    public TCPSendUtil(Socket client) {
        this.client = client;
        try {
            this.dos = new DataOutputStream(this.client.getOutputStream());
        } catch (IOException e) {
            release();
            throw new RuntimeException(e);
        }
    }

    // 发送信息
    public void sendUTF(String msg)
    {
        try {
            dos.writeUTF(msg);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("发送字符串信息失败");
        }
    }

    // 发送 对象
    public void sendObject(Object obj)
    {
        try {
            ObjectOutputStream dos = new ObjectOutputStream(this.client.getOutputStream());
            dos.writeObject(obj);
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
            release();
            System.out.println("发送监听事件失败");
        }
    }

    // 发送图片
    public void sendImg(byte[] imageBytes) {
        try {
            // 首先发送图像数据的长度
            dos.writeInt(imageBytes.length);
            // 然后发送图像数据
            dos.write(imageBytes);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            release();
            System.err.println("发送图片数据出错");
        }
    }

    // 压缩图片
    public byte[] getImageBytes(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 获取 JPEG 格式的 ImageWriter
            ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();

            // 设置压缩参数
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.75f); // 设置压缩质量（0.0f 到 1.0f）

            // 将图像写入 ByteArrayOutputStream
            writer.setOutput(ImageIO.createImageOutputStream(baos));
            writer.write(null, new javax.imageio.IIOImage(image, null, null), param);

            // 返回字节数组
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 发送文件
    public void sendFile(String filePath) {
        // 文件上传
        InputStream is;
        try {
            is = new FileInputStream(filePath);
            OutputStream os = new BufferedOutputStream(client.getOutputStream());
            byte[] flush = new byte[1024];
            int len = -1;
            while ((len = is.read(flush)) != -1) {
                os.write(flush, 0, len);
            }
            os.flush();
            is.close();
            // 3、释放资源
        } catch (IOException e) {
            System.out.println("发送文件时出错");
        }
    }

    public void release() {
        CloseUtil.close(client);
    }
}
