package org.example.demo;
import org.example.demo.ui.Chat_add.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerStart {
    public static void main(String[] args) throws IOException {
        // 使用线程池来并行启动各个服务器
        ExecutorService executorService = Executors.newFixedThreadPool(7); // 创建固定大小为7的线程池

        // 启动 Receiver 服务器
        executorService.submit(() -> {
            Receiver.main(args);
        });

        // 启动 ConfirmServer 服务器
        executorService.submit(() -> {
            ConfirmServer.main(args);
        });

        // 启动 VoiceCallServer 服务器
        executorService.submit(() -> {
            try {
                VoiceCallServer.main(args);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // 启动 Server 服务器
        executorService.submit(() -> {
            try {
                Server.main(args);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //接受远程操控的请求
        executorService.submit(() -> {
            confirmRemoteControlServer.main(args);
        });

        //摄像机功能
        executorService.submit(() -> {
            try {
                CameraServer.main(args);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //远程操控
        executorService.submit(() -> {
            try {
                RemoteControlServer.main(args);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // 关闭线程池（通常不需要立即关闭服务器的线程池，但可以在应用停止时关闭）
        executorService.shutdown();
    }
}
