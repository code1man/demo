package org.example.demo.utils;

import javafx.scene.input.*;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.Map;

//远程操控
public class RemoteControlUtil{

    private final Map<KeyCode, Integer> keyCodeMap;

    public RemoteControlUtil() {
        keyCodeMap = new HashMap<>();
        initializeKeyCodeMap();
    }

    /**
     * 返回鼠标的真正事件，鼠标事件不能直接处理，需要进过转换
     */
    public int getMouseKey(MouseButton button) {
        return switch (button) {
            case PRIMARY -> InputEvent.BUTTON1_DOWN_MASK; // 左键
            case MIDDLE -> InputEvent.BUTTON2_DOWN_MASK; // 中键
            case SECONDARY -> InputEvent.BUTTON3_DOWN_MASK; // 右键
            default -> throw new IllegalArgumentException("Unknown mouse button: " + button);
        };
    }

    public String mouseEvent(MouseEvent e) {
        if (e == null) {
            throw new IllegalArgumentException("MouseEvent cannot be null");
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        String eventType = e.getEventType().getName(); // Get event type name as a string
        int x = (int) e.getX();
        int y = (int) e.getY();

        if (eventType.equals("MOUSE_MOVED") || eventType.equals("MOUSE_DRAGGED"))
            return "mouseMoved#" + x + "#" + y + "#" + screenSize.width + "#" + screenSize.height;
        else if (eventType.equals("MOUSE_PRESSED")) {
            int mouseKey = getMouseKey(e.getButton());
            return "mousePressed#" + mouseKey;
        }
        else if (eventType.equals("MOUSE_RELEASED")) {
            int mouseKey = getMouseKey(e.getButton());
            return "mouseReleased#" + mouseKey;
        }
        else if (eventType.equals("MOUSE_CLICKED")) {
            int mouseKey = getMouseKey(e.getButton());
            return "mouseClicked#" + mouseKey;
        }
        else throw new IllegalArgumentException("Unknown mouse event type: " + eventType);
    }

    /**
     * 滚轮事件
     */
    public String mouseWheelEvent(ScrollEvent e) {
        if (e == null) {
            throw new IllegalArgumentException("ScrollEvent cannot be null");
        }

        // Determine the direction of the scroll
        double deltaY = e.getDeltaY();
        int scrollAmount = (int) (deltaY > 0 ? 1 : -1); // Positive for up, negative for down

        // Simulate mouse wheel event with Robot
        return "mouseWheel#" + scrollAmount;
    }

    /**
     * 键盘事件
     */
    public String keyEvent(KeyEvent e) {
        if (e == null) {
            throw new IllegalArgumentException("KeyInfo cannot be null");
        }

        int keyCode = 0;
        if (keyCodeMap.get(e.getCode())!=null) {
            keyCode = keyCodeMap.get(e.getCode());
        } else return "no";
        String type = e.getEventType().getName();

        if (type.equals("KEY_PRESSED"))
            return "keyPressed#" + keyCode;
        else if (type.equals("KEY_RELEASED"))
            return "keyReleased#" + keyCode;
        else throw new IllegalArgumentException("Unknown key event type: " + type);
    }

    private void initializeKeyCodeMap() {
        // Modifier keys
        keyCodeMap.put(KeyCode.SHIFT, java.awt.event.KeyEvent.VK_SHIFT);
        keyCodeMap.put(KeyCode.CONTROL, java.awt.event.KeyEvent.VK_CONTROL);
        keyCodeMap.put(KeyCode.ALT, java.awt.event.KeyEvent.VK_ALT);
        keyCodeMap.put(KeyCode.META, java.awt.event.KeyEvent.VK_META); // Meta key (Command key on Mac)

        // Function keys
        keyCodeMap.put(KeyCode.F1, java.awt.event.KeyEvent.VK_F1);
        keyCodeMap.put(KeyCode.F2, java.awt.event.KeyEvent.VK_F2);
        keyCodeMap.put(KeyCode.F3, java.awt.event.KeyEvent.VK_F3);
        keyCodeMap.put(KeyCode.F4, java.awt.event.KeyEvent.VK_F4);
        keyCodeMap.put(KeyCode.F5, java.awt.event.KeyEvent.VK_F5);
        keyCodeMap.put(KeyCode.F6, java.awt.event.KeyEvent.VK_F6);
        keyCodeMap.put(KeyCode.F7, java.awt.event.KeyEvent.VK_F7);
        keyCodeMap.put(KeyCode.F8, java.awt.event.KeyEvent.VK_F8);
        keyCodeMap.put(KeyCode.F9, java.awt.event.KeyEvent.VK_F9);
        keyCodeMap.put(KeyCode.F10,java.awt.event.KeyEvent.VK_F10);
        keyCodeMap.put(KeyCode.F11, java.awt.event.KeyEvent.VK_F11);
        keyCodeMap.put(KeyCode.F12, java.awt.event.KeyEvent.VK_F12);

        // Navigation keys
        keyCodeMap.put(KeyCode.UP, java.awt.event.KeyEvent.VK_UP);
        keyCodeMap.put(KeyCode.DOWN, java.awt.event.KeyEvent.VK_DOWN);
        keyCodeMap.put(KeyCode.LEFT, java.awt.event.KeyEvent.VK_LEFT);
        keyCodeMap.put(KeyCode.RIGHT, java.awt.event.KeyEvent.VK_RIGHT);

        // Editing keys
        keyCodeMap.put(KeyCode.ENTER, java.awt.event.KeyEvent.VK_ENTER);
        keyCodeMap.put(KeyCode.BACK_SPACE, java.awt.event.KeyEvent.VK_BACK_SPACE);
        keyCodeMap.put(KeyCode.TAB, java.awt.event.KeyEvent.VK_TAB);
        keyCodeMap.put(KeyCode.ESCAPE, java.awt.event.KeyEvent.VK_ESCAPE);

        // Number and letter keys
        // 映射数字键盘（NUMPAD）
        keyCodeMap.put(KeyCode.NUMPAD0, java.awt.event.KeyEvent.VK_NUMPAD0);
        keyCodeMap.put(KeyCode.NUMPAD1, java.awt.event.KeyEvent.VK_NUMPAD1);
        keyCodeMap.put(KeyCode.NUMPAD2, java.awt.event.KeyEvent.VK_NUMPAD2);
        keyCodeMap.put(KeyCode.NUMPAD3, java.awt.event.KeyEvent.VK_NUMPAD3);
        keyCodeMap.put(KeyCode.NUMPAD4, java.awt.event.KeyEvent.VK_NUMPAD4);
        keyCodeMap.put(KeyCode.NUMPAD5, java.awt.event.KeyEvent.VK_NUMPAD5);
        keyCodeMap.put(KeyCode.NUMPAD6, java.awt.event.KeyEvent.VK_NUMPAD6);
        keyCodeMap.put(KeyCode.NUMPAD7, java.awt.event.KeyEvent.VK_NUMPAD7);
        keyCodeMap.put(KeyCode.NUMPAD8, java.awt.event.KeyEvent.VK_NUMPAD8);
        keyCodeMap.put(KeyCode.NUMPAD9, java.awt.event.KeyEvent.VK_NUMPAD9);

        // 映射数字键（DIGIT）
        keyCodeMap.put(KeyCode.DIGIT0, java.awt.event.KeyEvent.VK_0);
        keyCodeMap.put(KeyCode.DIGIT1, java.awt.event.KeyEvent.VK_1);
        keyCodeMap.put(KeyCode.DIGIT2, java.awt.event.KeyEvent.VK_2);
        keyCodeMap.put(KeyCode.DIGIT3, java.awt.event.KeyEvent.VK_3);
        keyCodeMap.put(KeyCode.DIGIT4, java.awt.event.KeyEvent.VK_4);
        keyCodeMap.put(KeyCode.DIGIT5, java.awt.event.KeyEvent.VK_5);
        keyCodeMap.put(KeyCode.DIGIT6, java.awt.event.KeyEvent.VK_6);
        keyCodeMap.put(KeyCode.DIGIT7, java.awt.event.KeyEvent.VK_7);
        keyCodeMap.put(KeyCode.DIGIT8, java.awt.event.KeyEvent.VK_8);
        keyCodeMap.put(KeyCode.DIGIT9, java.awt.event.KeyEvent.VK_9);
        for (char c = 'A'; c <= 'Z'; c++) {
            keyCodeMap.put(KeyCode.getKeyCode(String.valueOf(c)), java.awt.event.KeyEvent.VK_A + (c - 'A'));
        }

        // Special keys
        keyCodeMap.put(KeyCode.SPACE, java.awt.event.KeyEvent.VK_SPACE);
        keyCodeMap.put(KeyCode.MINUS, java.awt.event.KeyEvent.VK_MINUS);
        keyCodeMap.put(KeyCode.EQUALS, java.awt.event.KeyEvent.VK_EQUALS);
        keyCodeMap.put(KeyCode.SEMICOLON, java.awt.event.KeyEvent.VK_SEMICOLON);
        keyCodeMap.put(KeyCode.QUOTE, java.awt.event.KeyEvent.VK_QUOTE);
        keyCodeMap.put(KeyCode.COMMA, java.awt.event.KeyEvent.VK_COMMA);
        keyCodeMap.put(KeyCode.PERIOD, java.awt.event.KeyEvent.VK_PERIOD);
        keyCodeMap.put(KeyCode.SLASH, java.awt.event.KeyEvent.VK_SLASH);
        keyCodeMap.put(KeyCode.BACK_SLASH, java.awt.event.KeyEvent.VK_BACK_SLASH);
    }
}

