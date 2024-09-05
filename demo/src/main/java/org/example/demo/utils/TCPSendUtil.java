package org.example.demo.utils;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class TCPSendUtil {
    private final Socket client;

    public TCPSendUtil(Socket client) throws IOException {
        this.client = client;
    }

    public void sendUTF(String msg) {
        try {
            DataOutputStream dos = new DataOutputStream(this.client.getOutputStream());
            dos.writeUTF(msg);
            dos.flush();
        } catch (IOException e) {
            release();
            System.out.println("发送字符串信息失败");
        }
    }

    // 发送图片
    private void sendImg(byte[] imageBytes) {
        try {
            // 首先发送图像数据的长度
            DataOutputStream dos = new DataOutputStream(this.client.getOutputStream());
            dos.writeInt(imageBytes.length);
            // 然后发送图像数据
            dos.write(imageBytes);
            dos.flush();
        } catch (IOException e) {
            System.err.println("发送图片数据出错");
        }
    }

    // 压缩图片
    public static byte[] getImageBytes(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 获取 JPEG 格式的 ImageWriter
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();

            // 设置压缩参数
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.75f); // 设置压缩质量（0.0f 到 1.0f）

            // 将图像写入 ByteArrayOutputStream
            writer.setOutput(ImageIO.createImageOutputStream(baos));
            writer.write(null, new javax.imageio.IIOImage(image, null, null), param);

            // 返回字节数组
            return baos.toByteArray();
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
