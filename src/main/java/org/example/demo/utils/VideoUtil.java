package org.example.demo.utils;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class VideoUtil {
    //线程池 screenTimer,录制视频
    private ScheduledThreadPoolExecutor screenTimer;
    //获取屏幕尺寸
    private final Rectangle rectangle = new Rectangle(Constant.WIDTH, Constant.HEIGHT); // 截屏的大小
    //视频类 FFmpegFrameRecorder
    private FFmpegFrameRecorder recorder;
    private Robot robot;

    //线程池 exec，录制音频
    private ScheduledThreadPoolExecutor exec;
    private TargetDataLine line;
    private AudioFormat audioFormat;
    private DataLine.Info dataLineInfo;
    ///是否开启录音设备
    private boolean isHaveDevice = true;
    private long startTime = 0;
    private long videoTS = 0;
    private long pauseTimeStart = 0;//开始暂停的时间
    private long pauseTime = 0;//暂停的时长
    private double frameRate = 5;

    private String state="start";//录制状态：start正在录制，pause暂停录制，stop停止录制
    public String getState() {
        return state;
    }

    public VideoUtil(String fileName, boolean isHaveDevice) {
        recorder = new FFmpegFrameRecorder(fileName + ".mp4", Constant.WIDTH, Constant.HEIGHT);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4); // 13
        recorder.setFormat("mp4");
        // recorder.setFormat("mov,mp4,m4a,3gp,3g2,mj2,h264,ogg,MPEG4");
        recorder.setSampleRate(44100);
        recorder.setFrameRate(frameRate);
        recorder.setVideoQuality(0);
        recorder.setVideoOption("crf", "23");
        // 2000 kb/s, 720P视频的合理比特率范围
        recorder.setVideoBitrate(1000000);

        recorder.setVideoOption("preset", "slow");
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P); // yuv420p
        recorder.setAudioChannels(2);
        recorder.setAudioOption("crf", "0");
        // Highest quality
        recorder.setAudioQuality(0);
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        try {
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(isHaveDevice) {

            audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);
            dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            try {
                line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            } catch (LineUnavailableException e1) {
                System.out.println("未获得音频线路，"+e1);
            }
        }
        this.isHaveDevice = isHaveDevice;
    }


    /**
     * 开始录制
     */
    public void start() {
        state="start";
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        if(pauseTimeStart!=0) {
            //计算暂停的时长
            pauseTime=System.currentTimeMillis()-pauseTimeStart;
            pauseTimeStart=0;//归零
        }
        else {
            //没有暂停过，暂停时长为0
            pauseTime=0;
        }

        // 如果有录音设备则启动录音线程
        if (isHaveDevice) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    SoundCaputre();
                }
            }).start();

        }

        //录屏
        screenCaptrue();
    }

    //开启录屏的线程
    private void screenCaptrue() {
        // 录屏
        screenTimer = new ScheduledThreadPoolExecutor(1);

        screenTimer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                BufferedImage screenCapture = robot.createScreenCapture(rectangle); // 截屏

                BufferedImage videoImg = new BufferedImage(Constant.WIDTH, Constant.HEIGHT,
                        BufferedImage.TYPE_3BYTE_BGR); // 声明一个BufferedImage用重绘截图

                Graphics2D videoGraphics = videoImg.createGraphics();// 创建videoImg的Graphics2D

                videoGraphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
                videoGraphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                        RenderingHints.VALUE_COLOR_RENDER_SPEED);
                videoGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
                videoGraphics.drawImage(screenCapture, 0, 0, null); // 重绘截图

                Java2DFrameConverter java2dConverter = new Java2DFrameConverter();

                Frame frame = java2dConverter.convert(videoImg);
                try {
                    //计算总时长
                    videoTS = 1000L*(System.currentTimeMillis()-startTime-pauseTime);

                    // 检查偏移量
                    if (videoTS > recorder.getTimestamp()) {
                        recorder.setTimestamp(videoTS);
                    }
                    recorder.record(frame); // 录制视频
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // 释放资源
                videoGraphics.dispose();
                videoGraphics = null;
                videoImg.flush();
                videoImg = null;
                java2dConverter = null;
                screenCapture.flush();
                screenCapture = null;
            }

        }, (int) (1000 / frameRate), (int) (1000 / frameRate), TimeUnit.MILLISECONDS);
    }

    /**
     * 开启抓取声音的线程
     */
    public void SoundCaputre() {

        try {
            if(!line.isRunning()){
                line.open(audioFormat);
                line.start();
            }
        } catch (LineUnavailableException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        final int sampleRate = (int) audioFormat.getSampleRate();
        final int numChannels = audioFormat.getChannels();

        int audioBufferSize = sampleRate * numChannels;
        final byte[] audioBytes = new byte[audioBufferSize];

        exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    int nBytesRead = line.read(audioBytes, 0, line.available());
                    int nSamplesRead = nBytesRead / 2;
                    short[] samples = new short[nSamplesRead];

                    // Let's wrap our short[] into a ShortBuffer and
                    // pass it to recordSamples
                    ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
                    ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);

                    // recorder is instance of
                    // org.bytedeco.javacv.FFmpegFrameRecorder
                    recorder.recordSamples(sampleRate, numChannels, sBuff);
                    // System.gc();
                } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
                    e.printStackTrace();
                }
            }
        }, (int) (1000 / frameRate), (int) (1000 / frameRate), TimeUnit.MILLISECONDS);
    }

    /**
     * 暂停录制
     */
    public void pause() {
        state="pause";
        screenTimer.shutdownNow();
        screenTimer = null;
        if (isHaveDevice) {
            exec.shutdownNow();
            exec = null;
        }
        pauseTimeStart = System.currentTimeMillis();

    }
    /**
     * 停止录制
     */
    public void stop() {
        state="stop";
        if (null != screenTimer) {
            screenTimer.shutdownNow();
        }
        try {
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
            recorder.stop();
            recorder.release();
            recorder.close();
            screenTimer = null;
            // screenCapture = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        VideoUtil videoRecord = new VideoUtil("test", true);
        videoRecord.start();
        System.out.println("****start继续录制，pause暂停录制，stop停止录制****");
        while (true) {
            Scanner sc = new Scanner(System.in);
            if(sc.hasNext()) {
                String cmd=sc.next();
                if (cmd.equalsIgnoreCase("stop")) {
                    videoRecord.stop();
                    System.out.println("****已经停止录制****");
                    break;
                }
                if (cmd.equalsIgnoreCase("pause")) {
                    if(videoRecord.getState().equals("pause")) {
                        System.out.println("*error:已经暂停，请勿重复操作pause*");
                        continue;
                    }
                    videoRecord.pause();
                    System.out.println("****已暂停，start继续录制，stop结束录制****");
                }
                if (cmd.equalsIgnoreCase("start")) {
                    if(videoRecord.getState().equals("start")) {
                        System.out.println("*error:请勿重复操作start*");
                        continue;
                    }
                    videoRecord.start();
                    System.out.println("****正在录制****");
                }
            }
        }
    }

}

class Constant{
    public final static int WIDTH=Toolkit.getDefaultToolkit().getScreenSize().width;
    public final static int HEIGHT=Toolkit.getDefaultToolkit().getScreenSize().height;

}
