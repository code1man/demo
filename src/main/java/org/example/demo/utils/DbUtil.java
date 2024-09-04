package org.example.demo.utils;

//数据库相关工具类
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//Connection con 连接对象
public class DbUtil {
    private static Connection con; // 声明Connection对象
    private static PreparedStatement preparedStatement;

    public DbUtil() {
    }

    // 建立返回值为Connection的方法
    public static Connection getConnection(String user, String password, String dataBaseName) {
        try { // 通过访问数据库的URL获取数据库连接对象
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + dataBaseName + "?useUnicode=true&characterEncoding=gbk", user, password);
            System.out.println("数据库连接成功");
        } catch (SQLException e) {
            System.out.println("数据库连接失败");
        }
        return con;
    }

    public static PreparedStatement getpreparedStatement(String sql) {
        try {
            con = getConnection("root", "zkd2621023939", "sys");
            preparedStatement = con.prepareStatement(sql);
        } catch (SQLException e) {
            System.out.println("sql语法错误");
        }
        return preparedStatement;
    }
}
