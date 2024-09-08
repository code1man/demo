package org.example.demo.utils;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.bytedeco.opencv.global.opencv_imgcodecs.cvLoadImage;

public class VideoUtil {

    private ScheduledThreadPoolExecutor screenTimer;
    private Rectangle rectangle;
    private FFmpegFrameRecorder recorder;
    private Robot robot;
    private OpenCVFrameConverter.ToIplImage conveter;
    private BufferedImage screenCapture;
    private ScheduledThreadPoolExecutor exec;
    private TargetDataLine line;
    private AudioFormat audioFormat;
    private DataLine.Info dataLineInfo;
    private boolean isHaveDevice;
    private String fileName;
    private long startTime = 0;
    private long videoTS = 0;
    private long pauseTime = 0;
    private double frameRate = 5;

    //构造器
    public VideoUtil(String fileName, boolean isHaveDevice) {
        init(fileName, isHaveDevice);
    }

    void init(String fileName, boolean isHaveDevice) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        rectangle = new Rectangle(screenSize.width, screenSize.height);
        recorder = new FFmpegFrameRecorder(fileName + ".mp4", screenSize.width, screenSize.height);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        System.out.println("设置视频格式:h264");
        recorder.setFormat("mp4");
        recorder.setSampleRate(44100);
        recorder.setFrameRate(frameRate);

        recorder.setVideoQuality(0);
        recorder.setVideoOption("crf", "23");
        recorder.setVideoBitrate(1000000);
        recorder.setVideoOption("preset", "slow");
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.setAudioChannels(2);
        recorder.setAudioOption("crf", "0");
        recorder.setAudioQuality(0);
//        recorder.setAudioCodec(avcodec.AV_CODEC_ID_IMC);
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        try {
            recorder.start();
        } catch (Exception e) {
            System.out.println(" 录制视频初始化异常");
            e.printStackTrace();
        } catch (Error error) {
            System.out.println("录制视频初始化错误");
        }
        conveter = new OpenCVFrameConverter.ToIplImage();
        this.isHaveDevice = isHaveDevice;
        this.fileName = fileName;
    }
    public void start() {

        System.out.println("录屏开始!start");
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        if (pauseTime == 0) {
            pauseTime = System.currentTimeMillis();
        }

        if (isHaveDevice) {
            new Thread(() -> caputre()).start();
        }
        screenTimer = new ScheduledThreadPoolExecutor(1);
        screenTimer.scheduleAtFixedRate(() -> {
            try {
                screenCapture = robot.createScreenCapture(rectangle);
                String name = fileName + ".JPEG";
                File f = new File(name);
                try {
                    ImageIO.write(screenCapture, "JPEG", f);
                } catch (IOException e) {
                    System.out.println("写文件时发生io异常");
                    e.printStackTrace();
                }
                IplImage image = cvLoadImage(name);
                videoTS = 1000 * (System.currentTimeMillis() - startTime - (System.currentTimeMillis() - pauseTime));
                if (videoTS > recorder.getTimestamp()) {
                    recorder.setTimestamp(videoTS);
                }
                recorder.record(conveter.convert(image));
                f.delete();
                System.gc();
            } catch (Exception ex) {
                System.out.println("录屏线程异常");
                ex.printStackTrace();
            }
        }, (int) (1000 / frameRate), (int) (1000 / frameRate), TimeUnit.MILLISECONDS);
    }

    private void caputre() {
        audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);
        dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        try {
            line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            line.open(audioFormat);
        } catch (LineUnavailableException e1) {
            System.out.println("准备录制音频时发生异常");
            e1.printStackTrace();
        }
        line.start();

        int sampleRate = (int) audioFormat.getSampleRate();
        int numChannels = audioFormat.getChannels();

        int audioBufferSize = sampleRate * numChannels;
        byte[] audioBytes = new byte[audioBufferSize];

        exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(() -> {
            try {
                int nBytesRead = line.read(audioBytes, 0, line.available());
                int nSamplesRead = nBytesRead / 2;
                short[] samples = new short[nSamplesRead];

                ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
                ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);

                recorder.recordSamples(sampleRate, numChannels, sBuff);
                System.gc();
            } catch (Exception e) {
                System.out.println("录制音频时发生异常");
                e.printStackTrace();
            }
        }, (int) (1000 / frameRate), (int) (1000 / frameRate), TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (null != screenTimer) {
            screenTimer.shutdownNow();
        }
        try {
            recorder.stop();
            recorder.release();
            recorder.close();
            System.out.println("录屏结束!");
            screenTimer = null;
            screenCapture = null;
            if (isHaveDevice) {
                if (null != exec) {
                    exec.shutdownNow();
                }
                if (null != line) {
                    line.stop();
                    line.close();
                }
                dataLineInfo = null;
                audioFormat = null;
            }
        } catch (Exception e) {
            System.out.println("录制结束出现异常");
            e.printStackTrace();
        }
    }
}
