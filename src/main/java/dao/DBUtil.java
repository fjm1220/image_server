package dao;

import java.sql.Connection;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DBUtil {
    //3306后面跟的是数据库的名字
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/image_server?characterEncoding=utf8&useSSL=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";
    private static DataSource dataSource = null;
    public static DataSource getDataSource(){
        //通过这个方法来创建DataSource的实例
        //懒汉模式
        //保证线程安全：
        //1、先加锁，加锁低效  2、二次判断  3、volatile保证内存可见性
        if(dataSource == null){
            synchronized (DBUtil.class){
                if(dataSource == null){
                    dataSource = new MysqlDataSource();
                    MysqlDataSource tempDataSource = (MysqlDataSource) dataSource;
                    tempDataSource.setURL(URL);
                    tempDataSource.setUser(USERNAME);
                    tempDataSource.setPassword(PASSWORD);
                }
            }
        }
        return dataSource;
    }
    public static Connection getConnection(){
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void close(Connection connection, PreparedStatement statement, ResultSet resultSet){
       //注意关闭顺序，先连接的后关闭
        try {
            if(resultSet != null){
                resultSet.close();
            }
            if(statement != null){
                statement.close();
            }
            if(connection != null){
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
