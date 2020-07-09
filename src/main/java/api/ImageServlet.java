package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.Image;
import dao.ImageDao;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ImageServlet extends HttpServlet{
    /**
     * 查看图片全部属性信息或指定图片
     * URL中有image_id参数就是查看指定图片，没有就是查看所有图片
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
m      */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //解析出来image_id
        String image_id = req.getParameter("image_id");
        if(image_id == null || image_id.equals("")){
            //查看所有图片属性,包装在方法中
            selectAll(req,resp);
        }
        else{
            selectOne(image_id,resp);
        }
    }
    private void selectAll(HttpServletRequest req, HttpServletResponse resp)throws IOException{
       resp.setContentType("application/json; charset=utf-8");
        //1.创建ImageDao对象，进行查找数据库
        ImageDao imageDao = new ImageDao();
        List<Image> images = imageDao.selectAll();
        //将查找到的结果转换成Json格式的字符串，然后写回给resp
        Gson gson = new GsonBuilder().create();
        String jsonData = gson.toJson(images);
        resp.getWriter().write(jsonData);
    }
    private void selectOne(String image_id, HttpServletResponse resp)throws IOException{
        ImageDao imageDao = new ImageDao();
        Image image = imageDao.selectOne(Integer.parseInt(image_id));
        Gson gson = new GsonBuilder().create();
        String jsonData = gson.toJson(image);
        resp.getWriter().write(jsonData);
    }
    /**
     * 上传图片
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.获取图片属性信息，并存入数据库
            //创建factory对象还让upload对象
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
            //通过upload对象进一步解析请求
            //FileItem代表一个文件对象
            //HTTP支持一个请求同时上传多个文件
        List<FileItem> items = null;
        try {
            items = upload.parseRequest(req);
        } catch (FileUploadException e) {
            //出现异常，解析错误
            e.printStackTrace();
            //告诉客户端具体错误是什么
            resp.setContentType("application/json; charset=utf-8");
            resp.getWriter().write("{\"ok\":false,\"reson\":\"请求解析失败\"}");
            return;
        }
            //将FileItem中的属性提取出来，转换成Image对象，存入数据库中
        FileItem fileItem = items.get(0);
        Image image = new Image();
        image.setImage_name(fileItem.getName());
        image.setSize((int)fileItem.getSize());
            //手动获取时间，并转成格式化日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        image.setUploadTime(simpleDateFormat.format(new Date()));
        image.setContentType(fileItem.getContentType());
       // image.setMd5("112233445566");
        //计算MD5
        image.setMd5(DigestUtils.md5Hex(fileItem.get()));
         /*   //构造路径进行保存，加入时间戳，让文件路径唯一
        image.setPath("./image/"+System.currentTimeMillis()+fileItem.getName());
        */
         //改进2,利用MD5来表示磁盘文件名，相同内容的只存一份，优化磁盘
        image.setPath("./image/"+image.getMd5());
            //存入数据库中
        ImageDao imageDao = new ImageDao();
        Image image_mad5 = imageDao.selectByMd5(image.getMd5());
        imageDao.insert(image);
        //2.获取图片内容信息，并写入磁盘文件
        if(image_mad5 == null){
            File file = new File(image.getPath());
            try {
                fileItem.write(file);
            } catch (Exception e) {
                e.printStackTrace();
                resp.setContentType("application/json; charset=utf-8");
                resp.getWriter().write("{\"ok\":false,\"reson\":\"写磁盘失败\"}");
                return;
            }
        }
        //3.给客户端返回一个结果数据
//        resp.setContentType("aplication/json; charset=utf-8");
//        resp.getWriter().write("{\"ok\":true}");

        //上传之后可以直接看到新上传的图片
        resp.sendRedirect("images.html");
    }

    /**
     * 删除图片
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");
        //1.解析出req中的image_id
        String image_id = req.getParameter("image_id");
        if(image_id == null || image_id.equals("")){
            resp.setStatus(200);
            resp.getWriter().write("{\"ok\":false,\"reson\":\"解析失败\"}");
            return;
        }
        //2.在数据库中进行查找
            ImageDao imageDao = new ImageDao();
            Image image = imageDao.selectOne(Integer.parseInt(image_id));
            if(image == null){
                //数据库中不存在要删除的image_id
                resp.setStatus(200);
                resp.getWriter().write("{\"ok\":false,\"reson\":\"数据库中不存在对应的image_id\"}");
                return;
            }
         //3.删除数据库中的对应信息
            imageDao.delete(Integer.parseInt(image_id));
            //改进3，删除数据库中的之后，判断数据库中是否还存在相同内容的图片，不存在的话才删除磁盘文件
            Image image_md5 = imageDao.selectByMd5(image.getMd5());
            if(image_md5 == null){
                //4.删除磁盘中的对应信息
                File file = new File(image.getPath());
                file.delete();
                resp.setStatus(200);
                resp.getWriter().write("{\"ok\":true}");
            }
    }
}

