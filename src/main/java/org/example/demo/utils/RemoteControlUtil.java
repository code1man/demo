package org.example.demo.utils;

import javafx.scene.input.ScrollEvent;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.Socket;

public class RemoteControlUtil implements KeyListener, MouseListener{

    private Robot robot;
    private Socket client;

    public static final int LEFT_BUTTON = MouseEvent.BUTTON1;
    public static final int RIGHT_BUTTON = MouseEvent.BUTTON3;
    public static final int MIDDLE_BUTTON = MouseEvent.BUTTON2;
    public static final int MOUSEPRESSED = MouseEvent.MOUSE_PRESSED;
    public static final int MOUSERELEASED = MouseEvent.MOUSE_RELEASED;

    public RemoteControlUtil(Socket client) {
        try {
            this.client = client;
            robot = new Robot();
        } catch (AWTException ex) {
            System.out.println("robot创建失败");
        }
    }

    //获取鼠标位置
    public void getMousePosition() {
        int x = MouseInfo.getPointerInfo().getLocation().x;
        int y = MouseInfo.getPointerInfo().getLocation().y;
    }

    //按下并释放
    @Override
    public void keyTyped(KeyEvent e) {
    }

    //键盘按压
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode(); // 获取按键的 KeyCode
        System.out.println("Key Pressed: " + KeyEvent.getKeyText(keyCode) + " (Code: " + keyCode + ")");
    }

    //鼠标释放
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode(); // 获取按键的 KeyCode
        System.out.println("Key Pressed Re: " + KeyEvent.getKeyText(keyCode) + " (Code: " + keyCode + ")");
    }

    //滚轮
    public void handleScroll(ScrollEvent e) {
        double deltaY = e.getDeltaY();
    }

    //鼠标点击
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}

