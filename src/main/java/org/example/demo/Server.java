package org.example.demo;

import org.example.demo.utils.CloseUtil;
import org.example.demo.utils.DbUtil;
import org.example.demo.utils.TCPReceiveUtil;
import org.example.demo.utils.TCPSendUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * 创建服务器
 * 1、指定端口 使用SeverSocket创建服务器
 * 2、阻塞式等待连接 accept
 * 3、操作：输入输出流操作
 * 4、释放资源
 */

public class Server {

    private static CopyOnWriteArrayList<Client> all = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("-----Server-----");

        try (// 1、指定端口 使用SeverSocket创建服务器
             ServerSocket server = new ServerSocket(8888);
             ServerSocket serverSocket = new ServerSocket(7777);) {
            // 2、阻塞式等待连接 accept
            while (true) {
                //Socket client = server.accept();
                Socket socket = serverSocket.accept();
                //Client c = new Client(client, socket);
                Client c = new Client(socket);
                all.add(c); // 管理所有的成员
                c.run();
            }
        }
        // 一般 server不会停止
    }

    static class Client {
        private Socket client;
        private final Socket secondClient;

        private String name;

        private boolean isRunning = false;
        private  TCPSendUtil send;
        private  TCPReceiveUtil receive;
        private final TCPSendUtil send2;
        private final TCPReceiveUtil receive2;

        public Client(Socket client, Socket secondClient) {
            this.client = client;
            this.secondClient = secondClient;
            isRunning = true;
            this.send = new TCPSendUtil(client);
            this.receive = new TCPReceiveUtil(client);
            this.send2 = new TCPSendUtil(secondClient);
            this.receive2 = new TCPReceiveUtil(secondClient);

            System.out.println("一个客户建立了链接");
        }

        public Client( Socket secondClient) {

            this.secondClient = secondClient;
            isRunning = true;
            this.send2 = new TCPSendUtil(secondClient);
            this.receive2 = new TCPReceiveUtil(secondClient);

            System.out.println("一个客户建立了链接");
        }

        public void run() {
            new Thread(() -> {
                while (isRunning) {
                    String order = receive2.receiveUTF();
                    //接受命令
                    if (order != null)
                    {
                        System.out.println("接收到的命令"+order);
                        String[] request =order.split(" ");
                        //登录
                        if(request[0].equals("LOGIN"))
                        {
                            try {
                                handleLogin(request[1], request[2], send2);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        //注册
                        if (request[0].equals("REGISTER"))
                        {
                            try {
                                handleRegister(request[1], request[2], send2);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        //保存聊天记录
                        if(request[0].equals("INFORMATION"))
                        {
                            handleMessage(Integer.parseInt(request[1]),Integer.parseInt(request[2]),request[3],send2);
                        }

                        if(request[0].equals("UPDATE"))
                        {
                            handUpdateUserName(request[1],Integer.parseInt(request[2]),send2);
                        }

                        if(request[0].equals("UPDATEHEAD"))
                        {
                            handUpdateHead(Integer.parseInt(request[1]),request[2],send2);
                        }
                    }

                }
            }).start();
            /*new Thread(() -> {
                while (isRunning) {
                    byte[] image = receive.receiveImg();
                    if (image != null)
                        send.sendImg(image);
                    // 这个根据自己写的部分按照需要写
                }
            }).start();*/
        }

        public void stop(){
            isRunning = false;
        }

        public void restart(){
            isRunning = true;
        }

        public void release() {
            isRunning = false;
            CloseUtil.close(client);
            all.remove(this);
        }

        public Socket getTargetClient(String target) {
            for (Client c : all) {
                if (c.name.equals(target))
                    return c.client;
            }
            return null;
        }

        private void handleLogin(String username, String password, TCPSendUtil sendUtil) throws IOException {
            // 登录逻辑


            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(username);

            // 检查用户名和密码是否为空
            if (username == null || username.isEmpty()) {
                System.out.println("用户名不能为空！");
                return;
            }
            if (password == null || password.isEmpty()) {
                System.out.println("密码不能为空！");
                return;
            }

            String sql = "SELECT passwordhash FROM t_users WHERE username = ?";

            ResultSet resultSet = DbUtil.executeQuery(sql, arrayList);

            try {
                // 检查是否有结果
                if (!resultSet.next()) {
                    System.out.println("用户名不存在！");
                    sendUtil.sendUTF("用户名不存在！");
                    //加入信息框
                } else {

                    // 验证密码
                    String storedPasswordHash = resultSet.getString("passwordhash");
                    System.out.println("数据库中的密码hash: " + storedPasswordHash);

                    if (storedPasswordHash.equals(password)) {  // 假设密码未加密
                        //导入登录时间
                        String sql1 ="UPDATE t_users SET userStatus =? , lastLogin = ? where username = ?";
                        ArrayList<Object> arrayList1 = new ArrayList<>();
                        arrayList1.add("online");
                        arrayList1.add(Timestamp.valueOf(LocalDateTime.now()));
                        arrayList1.add(username);
                        int count = DbUtil.executeUpdate(sql1,arrayList1);
                        System.out.println("影响了 : "+count+"条数据");
                        //更新在线状态
                        //new Main().start(new Stage() ); // 启动主界面
                        System.out.println("密码正确");

                        sendUtil.sendUTF("登陆成功");
                    } else {
                        System.out.println("密码错误");
                        //加入信息框
//                            Alert alert = new Alert(Alert.AlertType.ERROR);
//                            alert.setTitle("登录失败");
                        sendUtil.sendUTF("登录失败");
//                            alert.setHeaderText(null);
//                            alert.setContentText("用户名或密码错误！");
//                            alert.showAndWait();
                    }

                }

            } catch (SQLException ex) {
                ex.printStackTrace();  // 打印错误信息
                throw new RuntimeException(ex);
            }
        }

        private void handleRegister(String username, String password, TCPSendUtil sendUtil) throws IOException {
            // 注册逻辑

            //判断注册账户名是否存在
            String checkUserSql = "SELECT username FROM t_users WHERE username = ?";
            ArrayList<Object> checkUserParams = new ArrayList<>();
            checkUserParams.add(username);
            ResultSet checkUserResult = DbUtil.executeQuery(checkUserSql, checkUserParams);

            try {
                if(checkUserResult.next())
                {
                    // 用户名已存在，返回提示
                    System.out.println("用户名已存在！");
                    sendUtil.sendUTF("用户名已存在！");
                    return;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(username);
            arrayList.add(password);
            arrayList.add(0);
            arrayList.add(0.0);

            // 检查用户名和密码是否为空
            if (username == null || username.isEmpty()) {
                System.out.println("用户名不能为空！");
                return;
            }
            if (password == null || password.isEmpty()) {
                System.out.println("密码不能为空！");
                return;
            }



            String sql = "INSERT INTO t_users (username,passwordhash,controlTimes,goodRatingPercentage) VALUES (?,?,?,?)";

            int count =  DbUtil.executeUpdate(sql,arrayList);
            System.out.println("成功插入："+count);
            sendUtil.sendUTF("注册成功");
            //加入信息框注册成功！


        }

        private void handleMessage(int senderID,int receiveID,String messageText, TCPSendUtil sendUtil){

           String sql = "INSERT INTO t_messages (senderID,receiverID,messageText,messageTime) VALUES (?,?,?,?)";

            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(senderID);
            arrayList.add(receiveID);
            arrayList.add(messageText);
             arrayList.add(Timestamp.valueOf(LocalDateTime.now()));

             int count  = DbUtil.executeUpdate(sql,arrayList);
             System.out.println("插入了"+count+"条信息");
             sendUtil.sendUTF("发送信息成功");
        }

        //更改用户名
        private void handUpdateUserName(String username,int userid,TCPSendUtil sendUtil){

            //判断用户名是否存在
            String checkUserSql = "SELECT username FROM t_users WHERE username = ?";
            ArrayList<Object> checkUserParams = new ArrayList<>();
            checkUserParams.add(username);
            ResultSet checkUserResult = DbUtil.executeQuery(checkUserSql, checkUserParams);

            try {
                if(checkUserResult.next())
                {
                    sendUtil.sendUTF("用户名已存在");
                }
                else{
                    String sql = "UPDATE  t_users SET username = ? WHERE userid = ?";
                    ArrayList<Object> arrayList = new ArrayList<>();
                    arrayList.add(username);
                    arrayList.add(userid);

                    int count = DbUtil.executeUpdate(sql,arrayList);
                    System.out.println("更新了"+count+"条数据");
                    sendUtil.sendUTF("修改用户名成功");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private void handUpdateHead(int userid,String avatar , TCPSendUtil sendUtil){
            String sql = "UPDATE t_users SET avatarurl = ? WHERE userid = ?";

            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(avatar);
            arrayList.add(userid);

            int count  = DbUtil.executeUpdate(sql,arrayList);
            System.out.println("更新了"+count+"条头像数据");
            sendUtil.sendUTF("更新头像成功");

        }


    }
}


