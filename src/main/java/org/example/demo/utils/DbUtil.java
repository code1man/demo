package org.example.demo.utils;

//数据库相关工具类

import org.example.demo.Client;

import java.sql.*;
import java.util.ArrayList;

//Connection con 连接对象
public class DbUtil {
    private static Connection con = null; // 声明Connection对象
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    public DbUtil() {
    }

    // 建立返回值为Connection的方法
    public static Connection getConnection() {
        try { // 通过访问数据库的URL获取数据库连接对象
            con = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/CAC?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai","root","123456");
            System.out.println("数据库连接成功");
        } catch (SQLException e) {
            System.out.println("数据库连接失败");
        }
        return con;
    }

    //获取命令对象
    public static PreparedStatement getpreparedStatement(String sql) {
        try {
            con = getConnection();
            preparedStatement = con.prepareStatement(sql);
        } catch (SQLException e) {
            System.out.println("sql语法错误");
        }
        return preparedStatement;
    }

    // 返回受影响的行数
    public static int executeUpdate(String sql, ArrayList<Object> params) {
        try {
            preparedStatement = getpreparedStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet executeQuery(String sql, ArrayList<Object> params) {
        getpreparedStatement(sql);
        try {
            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }
            resultSet = preparedStatement.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getID(String username){
        int id = 0;
        String sql = "SELECT userid FROM t_users WHERE username = ?";
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(username);
        ResultSet resultSet1 = DbUtil.executeQuery(sql,arrayList) ;
        try {
            if (resultSet1.next())
            {
               id = resultSet1.getInt("userid");

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public static String getUserName(int userid){
        String name = "";
        String sql = "SELECT username FROM t_users WHERE userid = ?";
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(userid);
        ResultSet resultSet1 = DbUtil.executeQuery(sql,arrayList) ;
        try {
            if (resultSet1.next())
            {
                name += resultSet1.getString("username");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return name;
    }


    public  static  void  findAllFriends(int userid){
        String sql = "SELECT username FROM t_users u\n" +
                "INNER JOIN t_friends f ON u.userid = f.friendid\n" +
                "WHERE f.userid = ? AND f.status = 'accepted'";
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(userid);

        ResultSet resultSet1 = DbUtil.executeQuery(sql,arrayList) ;
        try {
            while(resultSet1.next())
            {
                Client.friendNames.add(resultSet1.getString("username"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }




    public static void close() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
