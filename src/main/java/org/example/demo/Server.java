package org.example.demo;

import org.example.demo.utils.CloseUtil;

import org.example.demo.utils.DbUtil;

import org.example.demo.utils.TCPReceiveUtil;
import org.example.demo.utils.TCPSendUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.sql.*;
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

        private TCPSendUtil send;
        private TCPReceiveUtil receive;

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

        public Client(Socket secondClient) {

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
                    if (order != null) {
                        System.out.println("接收到的命令" + order);
                        String[] request = order.split(" ");
                        //登录
                        if (request[0].equals("LOGIN")) {
                            try {
                                handleLogin(request[1], request[2], send2);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

//                        if(request[0].equals("LOAD"))
//                        {
//
//                        }
                        //注册
                        if (request[0].equals("REGISTER")) {
                            try {
                                handleRegister(request[1], request[2], send2);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        //保存聊天记录
//                        if (request[0].equals("INFORMATION")) {
//                            handleMessage(Integer.parseInt(request[1]), Integer.parseInt(request[2]), request[3], send2);
//                        }


                        //修改用户名
                        if (request[0].equals("UPDATE")) {
                            handUpdateUserName(request[1], Integer.parseInt(request[2]), send2);
                        }

                        //更新头像
                        if (request[0].equals("UPDATEHEAD")) {
                            handUpdateHead(Integer.parseInt(request[1]), request[2], send2);
                        }

                        //添加好友
                        if (request[0].equals("ADDFRIENDS")) {
                            handleAddFriends(request[1], Integer.parseInt(request[2]), request[3], send2);
                        }

///*                        if (request[0].equals("SEARCHFRIENDS")) {
//                            searchFriends1(Integer.parseInt(request[1]), request[2], send2);
//                        }
                        if (request[0].equals("SEARCHFRIENDS")) {
                            searchFriends(request[1], Integer.parseInt(request[2]), send2);
                        }

                        if (request[0].equals("TOPFRIENDS")) {
                            goodFriends(Integer.parseInt(request[1]), send2);
                        }

                        if (request[0].equals("VOICECHAT")) {
                            handleVoiceChat(Integer.parseInt(request[1]), Integer.parseInt(request[2]), send2);
                        }

                        if (request[0].equals("FinishVoiceChat")) {

                        }


                        if (request[0].equals("SHOWPENDINGFRIENDS")) {
                            showPendingFriends(Integer.parseInt(request[1]), send2);
                        }

                        if (request[0].equals("SENDMESSAGE")) {
                            handleMessage(Integer.parseInt(request[1]), request[2], request[3], send2);
                        }

                        //获取离线信息
                        if(request[0].equals("GETMESSAGE"))
                        {
                            getMessage(Integer.parseInt(request[1]),request[2],send2);
                        }

                        //用户反馈
                        if (request[0].equals("FEEDBACK")) {
                            feedback(request[1], request[2], send2);
                        }

                        if (request[0].equals("GETUSERINFO")) {
                            getUserInfo(request[1],send2);
                        }
                        //客户端获取用户id
                        if (request[0].equals("GETID"))
                        {
                            getID(request[1],send2);
                        }
                    }

                }
            }).start();

//           new Thread(() -> {
//                    if (order != null)
//                        send2.sendUTF(order);
//                }
//            }).start();

//            new Thread(() -> {
//
//                while (isRunning) {
//                    byte[] image = receive.receiveImg();
//                    if (image != null)
//                        send.sendImg(image);
//                    // 这个根据自己写的部分按照需要写
//                }
//            }).start();
//        }


        }

        public void stop() {
            isRunning = false;
        }

        public void restart() {
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
                        String sql1 = "UPDATE t_users SET userStatus =? , lastLogin = ? where username = ?";
                        ArrayList<Object> arrayList1 = new ArrayList<>();
                        arrayList1.add("online");
                        arrayList1.add(Timestamp.valueOf(LocalDateTime.now()));
                        arrayList1.add(username);
                        int count = DbUtil.executeUpdate(sql1, arrayList1);
                        System.out.println("影响了 : " + count + "条数据");
                        //更新在线状态
                        //new Main().start(new Stage() ); // 启动主界面
                        System.out.println("密码正确");

                        load(username, sendUtil);
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

        private void load(String username, TCPSendUtil sendUtil) {
            String sql = "SELECT userID,avatarUrl,controlTimes,goodRatingPercentage FROM t_users\n" +
                    "WHERE username = ?";
            String sql1 = "SELECT friendID FROM t_friends WHERE userID = ? AND status = 'accepted'";

            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(username);

            ArrayList<Object> arrayList1 = new ArrayList<>();
            arrayList1.add(DbUtil.getID(username));

            ResultSet resultSet = DbUtil.executeQuery(sql, arrayList);
            ResultSet resultSet1 = DbUtil.executeQuery(sql1, arrayList1);

            try {
                StringBuilder result = new StringBuilder();
                while (resultSet.next()) {
                    result.append(resultSet.getString("userID")).append(" "); // 注意这里的 "username" 是小写的
                    result.append(resultSet.getString("avatarUrl")).append(" "); // 注意这里的 "username" 是小写的
                    result.append(resultSet.getInt("controlTimes")).append(" "); // 注意这里的 "username" 是小写的
                    result.append(resultSet.getDouble("goodRatingPercentage")).append(" "); // 注意这里的 "username" 是小写的

                }
                while (resultSet1.next()) {
                    result.append(DbUtil.getUserName(resultSet1.getInt("friendID"))).append("#"); // 注意这里的 "username" 是小写的
                }

                // 如果有结果，去掉最后的空格并发送
                if (result.length() > 0) {
                    result.setLength(result.length() - 1); // 去掉最后一个空格
                }

                sendUtil.sendUTF(result.toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
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
                if (checkUserResult.next()) {
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
            arrayList.add("/touxiang.png");
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


            String sql = "INSERT INTO t_users (username,passwordhash,avatarUrl,controlTimes,goodRatingPercentage) VALUES (?,?,?,?,?)";

            int count = DbUtil.executeUpdate(sql, arrayList);
            System.out.println("成功插入：" + count);
            sendUtil.sendUTF("注册成功");
            //加入信息框注册成功！


        }

        private void handleMessage(int senderID, String receiveName, String messageText, TCPSendUtil sendUtil) {

            int receiveid = DbUtil.getID(receiveName);
            String sql = "INSERT INTO t_messages (senderID,receiverID,messageText,messageTime,messageStatus) VALUES (?,?,?,?,?)";

            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(senderID);
            arrayList.add(receiveid);
            arrayList.add(messageText);
            arrayList.add(Timestamp.valueOf(LocalDateTime.now()));
            //接收方是否在线
            if (DbUtil.getUserState(receiveid).equals("online")) {
                arrayList.add(1);
            } else {
                arrayList.add(0);
            }

            int count = DbUtil.executeUpdate(sql, arrayList);
            System.out.println("插入了" + count + "条信息");
            sendUtil.sendUTF("发送信息成功");
        }

        //更改用户名
        private void handUpdateUserName(String username, int userid, TCPSendUtil sendUtil) {

            //判断用户名是否存在
            String checkUserSql = "SELECT username FROM t_users WHERE username = ?";
            ArrayList<Object> checkUserParams = new ArrayList<>();
            checkUserParams.add(username);
            ResultSet checkUserResult = DbUtil.executeQuery(checkUserSql, checkUserParams);

            try {
                if (checkUserResult.next()) {
                    sendUtil.sendUTF("用户名已存在");
                } else {
                    String sql = "UPDATE  t_users SET username = ? WHERE userid = ?";
                    ArrayList<Object> arrayList = new ArrayList<>();
                    arrayList.add(username);
                    arrayList.add(userid);

                    int count = DbUtil.executeUpdate(sql, arrayList);
                    System.out.println("更新了" + count + "条数据");
                    sendUtil.sendUTF("修改用户名成功");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private void handUpdateHead(int userid, String avatar, TCPSendUtil sendUtil) {
            String sql = "UPDATE t_users SET avatarurl = ? WHERE userid = ?";

            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(avatar);
            arrayList.add(userid);

            int count = DbUtil.executeUpdate(sql, arrayList);
            System.out.println("更新了" + count + "条头像数据");
            sendUtil.sendUTF("更新头像成功");

        }

        private void handleAddFriends(String status, int userid, String friendName, TCPSendUtil sendUtil) {
            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(userid);
            arrayList.add(DbUtil.getID(friendName));
            arrayList.add(Timestamp.valueOf(LocalDateTime.now()));

            ArrayList<Object> arrayList1 = new ArrayList<>();
            arrayList1.add(DbUtil.getID(friendName));
            arrayList1.add(userid);
            arrayList1.add(status);
            arrayList1.add(arrayList.get(2));

            ArrayList<Object> arrayList2 = new ArrayList<>();
            arrayList2.add(status);
            arrayList2.add(userid);

            //申请
            if (status.equals("pending")) {
                if (DbUtil.getUserState(DbUtil.getID(friendName)).equals("online")) {
                    String sql = "INSERT INTO t_friends  (userID,friendID,requestDate) VALUES (?,?,?)";

                    int count = DbUtil.executeUpdate(sql, arrayList);
                    System.out.println(count + "条记录   " + userid + "正在申请好友");
                    sendUtil.sendUTF("已申请");
                } else {

                }
            }
            //接受
            else if (status.equals("accepted")) {
                //先修改申请时的状态
                String sql1 = "UPDATE t_friends SET status = ? WHERE userid = ?";
                int count = DbUtil.executeUpdate(sql1, arrayList2);
                System.out.println(count + "条记录   " + DbUtil.getID(friendName) + "同意了好友申请");
                sendUtil.sendUTF("已同意");

                //我们还要再插入一条
                String sql = "INSERT INTO t_friends  (userid,friendid,status) VALUES (?,?,?)";
                int count1 = DbUtil.executeUpdate(sql, arrayList1);
                System.out.println(count + "条记录   " + DbUtil.getID(friendName) + "  和 " + userid + "相互成为了好友");
                sendUtil.sendUTF(friendName);

                // org.example.demo.Client.friendNames.add(DbUtil.getUserName(DbUtil.getID(friendName)));
                //另外一端也需要改     ，首先要接收到对方接受的信息

            }
            //拒绝
            else {
                //只用修改申请时的状态
                String sql = "UPDATE t_friends SET status = ? WHERE userid = ?";
                int count = DbUtil.executeUpdate(sql, arrayList2);
                System.out.println(count + "条记录   " + DbUtil.getID(friendName) + "拒绝了好友申请");
                sendUtil.sendUTF("已拒绝");
            }

        }

        /*private void searchFriends1(int userid, String like ,TCPSendUtil sendUtil){
            String getFriendsSql1 = "SELECT username " +
                    "FROM t_users u " +
                    "INNER JOIN t_friends f ON u.userID = f.friendID " +
                    "WHERE f.userID = ? AND f.status = 'accepted' " +
                    "AND u.username LIKE ?";

            ArrayList<Object> arrayList  = new ArrayList<>();
            arrayList.add(userid);
            arrayList.add(like);

            ResultSet resultSet = DbUtil.executeQuery(getFriendsSql1,arrayList);
            while (true) {
                try {
                    if (!resultSet.next())
                    {
                        //System.out.println("查找为空");
                        break;
                    }
                    //System.out.println(resultSet.getString("u.username"));
                    org.example.demo.Client.selectFriendName.add(resultSet.getString("u.username"));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            //org.example.demo.Client.friendResultSet = DbUtil.executeQuery(getFriendsSql1,arrayList);
        }*/

        private void searchFriends(String search, int userid, TCPSendUtil sendUtil) {
//            String getFriendsSql = "\n" +
//                    "SELECT u.username FROM t_users u \n" +
//                    "INNER JOIN t_friends f ON u.userid = f.friendid \n" +
//                    "WHERE f.userid = ? AND f.status = 'accepted'";
            String getFriendsSql = "SELECT u.userName FROM t_users u \n" +
                    "        WHERE u.userName LIKE ? \n" +
                    "        AND u.userID NOT IN (SELECT friendID FROM t_friends WHERE userID = ? AND status = 'accepted' ) \n" +
                    "       AND u.userID != ? ";
            // String getFriendsSql = "SELECT username FROM t_users WHERE userid = ?";
            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add("%" + search + "%");
            arrayList.add(userid);
            arrayList.add(userid);
            System.out.println(arrayList.get(0));

            ResultSet resultSet = DbUtil.executeQuery(getFriendsSql, arrayList);
            System.out.println(resultSet);

            try {
                StringBuilder result = new StringBuilder();
                while (resultSet.next()) {
                    result.append(resultSet.getString("username")).append(" "); // 注意这里的 "username" 是小写的
                }

                // 如果有结果，去掉最后的空格并发送
                if (result.length() > 0) {
                    result.setLength(result.length() - 1); // 去掉最后一个空格
                }

                sendUtil.sendUTF(result.toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private void goodFriends(int userid, TCPSendUtil sendUtil) {
            String getTopUsersSql =
                    "SELECT u.userName FROM t_users u \n" +
                            "WHERE u.userID NOT IN (SELECT friendID FROM t_friends WHERE userID = ? AND status = 'accepted')\n" +
                            "ORDER BY u.goodRatingPercentage DESC\n" +
                            "LIMIT 3";

            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(userid);

            ResultSet resultSet = DbUtil.executeQuery(getTopUsersSql, arrayList);

            try {
                StringBuilder result = new StringBuilder();
                while (resultSet.next()) {
                    result.append(resultSet.getString("username")).append(" "); // 注意这里的 "username" 是小写的
                }

                // 如果有结果，去掉最后的空格并发送
                if (result.length() > 0) {
                    result.setLength(result.length() - 1); // 去掉最后一个空格
                }

                sendUtil.sendUTF(result.toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        //org.example.demo.Client.friendResultSet = DbUtil.executeQuery(getFriendsSql1,arrayList);
        private void showPendingFriends(int userID, TCPSendUtil sendUtil) {
            String sql = "SELECT userID FROM t_friends WHERE\n" +
                    "friendID = ? AND\n" +
                    "status = 'pending'";
            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(userID);

            ResultSet resultSet = DbUtil.executeQuery(sql, arrayList);

            try {
                StringBuilder result = new StringBuilder();
                while (resultSet.next()) {
                    result.append(DbUtil.getUserName(Integer.parseInt(resultSet.getString("userID")))).append(" "); // 注意这里的 "username" 是小写的
                }

                // 如果有结果，去掉最后的空格并发送
                if (result.length() > 0) {
                    result.setLength(result.length() - 1); // 去掉最后一个空格
                }

                System.out.println("查找正在申请的好友在这里：");
                System.out.println(result.toString());
                sendUtil.sendUTF(result.toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


        private void handleVoiceChat(int userid, int friendid, TCPSendUtil sendUtil) {
            String sql = "INSERT INTO t_sessions (sessionType,hostID,participantID,startTime) VALUES (?,?,?,?)";
            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add("voice_chat");
            arrayList.add(userid);
            arrayList.add(friendid);
            arrayList.add(Timestamp.valueOf(LocalDateTime.now()));

            int count = DbUtil.executeUpdate(sql, arrayList);
            System.out.println("插入" + count + "条会话记录");


            int sessionID = 0;

            try {
                ResultSet rs = DbUtil.getpreparedStatement(sql).getGeneratedKeys();
                if (rs.next()) {
                    sessionID = rs.getInt(1);
                    System.out.println("sessionID" + sessionID);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            String sql2 = "INSERT INTO t_voiceChat (sessionID,  hostID, participantID) VALUES (?, ?, ?)";
            ArrayList<Object> arrayList1 = new ArrayList<>();
            arrayList1.add(sessionID);
            arrayList1.add(userid);
            arrayList1.add(friendid);

            int count1 = DbUtil.executeUpdate(sql, arrayList1);
            System.out.println("插入" + count + "条语音通话记录");
            sendUtil.sendUTF(sessionID + "");
        }

        private void finishVoiceChat(TCPSendUtil sendUtil) {
            String sql1 = "INSERT INTO t_sessions (endTime) VALUES (?) ";
        }

        private void upDateStatus(int userID) {
            String sql = "UPDATE t_users SET userStatus = 'offline' WHERE userID = ?";
            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(userID);

            int count = DbUtil.executeUpdate(sql, arrayList);

            System.out.println(count + "条下线记录");
            System.out.println("用户" + DbUtil.getUserName(userID) + "已下线");
        }

        private void feedback(String controller, String flag, TCPSendUtil sendUtil) {
            String sql = "UPDATE t_users SET controlTimes = ? AND goodRatingPercentage = ? WHERE userName = ?";

            int controllerID = DbUtil.getID(controller);
            int oldControlTimes = DbUtil.getControlTimes(controllerID);
            Double oldGoodRatingPercentage = DbUtil.getGoodRatingPercentage(controllerID);
            //获取好评次数
            int goodTimes = (int) (oldControlTimes * oldGoodRatingPercentage);


            StringBuilder result = new StringBuilder();

            result.append(oldControlTimes + 1).append(" ");


            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(oldControlTimes + 1);
            if (flag.equals("赞")) {
                arrayList.add((goodTimes + 1) / (oldControlTimes + 1)*100);
                result.append(goodTimes + 1).append(" ");
            } else {
                arrayList.add((goodTimes) / (oldControlTimes + 1)*100);
                result.append(goodTimes).append(" ");
            }
            arrayList.add(controller);

            int count = DbUtil.executeUpdate(sql, arrayList);
            System.out.println("修改了" + count + "条控制次数和好评率信息");

            // 如果有结果，去掉最后的空格并发送
            if (result.length() > 0) {
                result.setLength(result.length() - 1); // 去掉最后一个空格
            }

            sendUtil.sendUTF(result.toString());

        }

        private void getUserInfo(String userName, TCPSendUtil sendUtil) {
            String sql = "SELECT userStatus,controlTimes,goodRatingPercentage FROM t_users WHERE userName = ?";

            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(userName);

            ResultSet resultSet = DbUtil.executeQuery(sql, arrayList);

            try {
                StringBuilder result = new StringBuilder();
                while (resultSet.next()) {
                    result.append(resultSet.getString("userStatus")).append(" "); // 注意这里的 "username" 是小写的
                    result.append(resultSet.getString("controlTimes")).append(" "); // 注意这里的 "username" 是小写的
                    result.append(resultSet.getDouble("goodRatingPercentage")).append(" "); // 注意这里的 "username" 是小写的
                    //result.append(resultSet.getDouble("goodRatingPercentage")).append(" "); // 注意这里的 "username" 是小写的

                }

                // 如果有结果，去掉最后的空格并发送
                if (result.length() > 0) {
                    result.setLength(result.length() - 1); // 去掉最后一个空格
                }

                sendUtil.sendUTF(result.toString());


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private void getID(String userName, TCPSendUtil sendUtil)
        {
            int id = DbUtil.getID(userName);
            sendUtil.sendUTF(id+"");
        }

        private void getMessage(int userid,String friendName ,TCPSendUtil sendUtil)
        {
            String sql = "\n" +
                    "SELECT messageText,messageTime FROM t_messages WHERE senderID = ? AND receiverID = ? AND messageStatus = 0";

            int friendID = DbUtil.getID(friendName);

            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(friendID);
            arrayList.add(userid);

            ResultSet resultSet = DbUtil.executeQuery(sql, arrayList);


            String sql1 = "SELECT messageTime FROM t_messages WHERE senderID = ? AND receiverID = ?\n" +
                    "ORDER BY messageTime DESC \n" +
                    "LIMIT 1";

            ResultSet resultSet1 = DbUtil.executeQuery(sql1,arrayList);


            try {
                StringBuilder result = new StringBuilder();
                while(resultSet1.next())
                {
                    result.append((resultSet1.getTimestamp("messageTime")).toString()).append("#");
                }
                while (resultSet.next()) {
                    result.append(friendName).append("#");
                    result.append(resultSet.getString("messageText")).append("#"); // 注意这里的 "username" 是小写的
                    result.append((resultSet.getTimestamp("messageTime")).toString()).append("#"); // 注意这里的 "username" 是小写的
                    //result.append(resultSet.getDouble("goodRatingPercentage")).append(" "); // 注意这里的 "username" 是小写的
                    //result.append(resultSet.getDouble("goodRatingPercentage")).append(" "); // 注意这里的 "username" 是小写的

                }

                // 如果有结果，去掉最后的空格并发送
                if (result.length() > 0) {
                    result.setLength(result.length() - 1); // 去掉最后一个空格
                }

                sendUtil.sendUTF(result.toString());


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}



