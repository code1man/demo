package org.example.demo.ui.Chat_add;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class VoiceCallClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    public static boolean isCalling = false;  // 用于控制通话状态
    private static TargetDataLine microphone;
    public static Socket socket;

    public static void main(String[] args) throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        System.out.println("已连接到语音通话服务器");

        isCalling = true;

        // 启动线程捕获和发送音频
        new Thread(() -> captureAndSendAudio()).start();

        // 启动线程接收和播放音频
        new Thread(() -> receiveAndPlayAudio()).start();
    }

    // 捕获音频并发送到服务器
    private static void captureAndSendAudio() {
        AudioFormat format = new AudioFormat(44100, 16, 1, true, true);

        try {
            microphone = AudioSystem.getTargetDataLine(format);
            microphone.open(format);
            microphone.start();

            byte[] buffer = new byte[4096];
            OutputStream out = socket.getOutputStream();
            int bytesRead;

            while (isCalling) {  // 只在isCalling为true时继续捕获
                bytesRead = microphone.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            // 通话结束后，停止捕获并关闭资源
            microphone.stop();
            microphone.close();
            socket.close();
            System.out.println("捕获停止，连接关闭。");

        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    // 接收服务器的音频数据并播放
    private static void receiveAndPlayAudio() {
        AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
        SourceDataLine speakers;

        try {
            speakers = AudioSystem.getSourceDataLine(format);
            speakers.open(format);
            speakers.start();

            byte[] buffer = new byte[4096];
            InputStream in = socket.getInputStream();
            int bytesRead;

            while (isCalling) {  // 只在isCalling为true时继续接收音频
                bytesRead = in.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    speakers.write(buffer, 0, bytesRead);
                }
            }

            // 通话结束后，停止播放并关闭资源
            speakers.stop();
            speakers.close();

        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
}
