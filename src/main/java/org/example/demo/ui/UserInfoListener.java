package org.example.demo.ui;

import javafx.scene.image.Image;

// 定义回调接口，用于传递用户修改后的信息
public interface UserInfoListener {
    void onUserInfoUpdated(String username, String signature, String gender, String birthday, String country, String province, Image avatar);
}
