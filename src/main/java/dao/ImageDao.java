package dao;

import common.ImageServerException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class ImageDao {
    /**
     * 插入图片，将Image对象插入到数据库中
     * @param image
     */
    public void insert(Image image){
        //1.获取数据库连接
        Connection connection = DBUtil.getConnection();
        //2.创建片拼接SQL语句
        PreparedStatement statement = null;
        try {
            String sql = "insert into Image_table values(null,?,?,?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1,image.getImage_name());
            statement.setInt(2,image.getSize());
            statement.setString(3,image.getUploadTime());
            statement.setString(4,image.getContentType());
            statement.setString(5,image.getMd5());
            statement.setString(6,image.getPath());
            //3.执行SQL语句
            int ret = statement.executeUpdate();
            if(ret != 1){
                //程序出现问题，抛出一个异常
                throw new ImageServerException("插入数据库错误");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ImageServerException e) {
            e.printStackTrace();
        }finally {
            //4.关闭连接和statement对象
            DBUtil.close(connection,statement,null);
        }
    }
    /**
     * 查询数据库中所有图片信息
     * @return
     */
    public List<Image> selectAll(){
        List<Image> images = new ArrayList<>();
        //1.获取数据库连接
        Connection connection = DBUtil.getConnection();
        //2.创建并拼接sql语句
        String sql = "select * from Image_table";
        //3.执行sql语句
        //将statement, resultSet 设为全局变量，否则下面关闭连接时出错
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            //4.处理结果集
            resultSet = statement.executeQuery();
            while(resultSet.next()){
                Image image = new Image();
                image.setImage_id(resultSet.getInt("image_id"));
                image.setImage_name(resultSet.getString("image_name"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setMd5(resultSet.getString("md5"));
                image.setPath(resultSet.getString("path"));
                images.add(image);
            }
            return images;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //5.关闭连接
            DBUtil.close(connection,statement,resultSet);
        }
        //出现异常返回空
        return null;
    }

    /**
     * 根据image_id查找指定图片信息
     * @param image_id
     * @return
     */
    public Image selectOne(int image_id){
        //1.获取数据库连接
        Connection connection = DBUtil.getConnection();
        //2.构造sql语句
        String sql = "select * from Image_table where image_id = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            //3.执行sql语句
            statement = connection.prepareStatement(sql);
            //用image_id替换？
            statement.setInt(1,image_id);
            resultSet = statement.executeQuery();
            //4.处理结果集
            if(resultSet.next()){
                Image image = new Image();
                image.setImage_id(resultSet.getInt("image_id"));
                image.setImage_name(resultSet.getString("image_name"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setMd5(resultSet.getString("md5"));
                image.setPath(resultSet.getString("path"));
                return image;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            DBUtil.close(connection,statement,resultSet);
        }
        //5.关闭连接
        return null;
    }

    /**
     * 根据image_id删除指定图片
     * @param image_id
     */
    public void delete(int image_id){
        //1.获取数据库连接
        Connection connection = DBUtil.getConnection();
        //2.创建并片接sql语句
        //?作为占位符
        String sql = "delete from Image_table where image_id = ?";
        //3.执行sql语句
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,image_id);
            int ret = statement.executeUpdate();
            if(ret != 1){
                throw new ImageServerException("删除数据库操作失败");
            }
        } catch (SQLException | ImageServerException e) {
            e.printStackTrace();
        }finally {
            //4.关闭连接
            DBUtil.close(connection,statement,null);
        }
    }
    public Image selectByMd5(String md5){
        //1.获取数据库连接
        Connection connection = DBUtil.getConnection();
        //2.构造sql语句
        String sql = "select * from Image_table where md5= ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            //3.执行sql语句
            statement = connection.prepareStatement(sql);
            //用md5替换？
            statement.setString(1,md5);
            resultSet = statement.executeQuery();
            //4.处理结果集
            if(resultSet.next()){
                Image image = new Image();
                image.setImage_id(resultSet.getInt("image_id"));
                image.setImage_name(resultSet.getString("image_name"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setMd5(resultSet.getString("md5"));
                image.setPath(resultSet.getString("path"));
                return image;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            DBUtil.close(connection,statement,resultSet);
        }
        //5.关闭连接
        return null;
    }
    public static void main(String[] args) {
        //1.测试插入
//        Image image = new Image();
//        image.setImage_name("1.png");
//        image.setSize(100);
//        image.setUploadTime("20200706");
//        image.setContentType("image/png");
//        image.setPath("./data/1.png");
//        image.setMd5("11223344");
//        ImageDao imageDao = new ImageDao();
//        imageDao.insert(image);

//        //2.测试查询所有
//        ImageDao imageDao = new ImageDao();
//        List<Image> images = imageDao.selectAll();
//        System.out.println(images);

        //3.测试查找指定图片
//        ImageDao imageDao = new ImageDao();
//        Image image = imageDao.selectOne(1);
//        System.out.println(image);

        //4.测试删除指定图片
       // ImageDao imageDao = new ImageDao();
       // imageDao.delect(1);
    }

}
