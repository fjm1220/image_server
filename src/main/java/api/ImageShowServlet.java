package api;

import dao.Image;
import dao.ImageDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;

public class ImageShowServlet extends HttpServlet {
    /**
     * 查看指定图片内容
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    //改进1，防盗链机制，将允许的链接地址存放入到定义的set中
    static private HashSet<String> whiteList = new HashSet<>();
    static {
        whiteList.add("http://49.235.30.116:8080/image_server/images.html");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String referer = req.getHeader("Referer");
        if(!whiteList.contains(referer)){
            resp.setContentType("application/json; charset=utf-8");
            resp.getWriter().write("{\"ok\":false,\"reson\":\"未授权的访问\"}");
            return;
        }
        //1.解析出image_id
        String image_id = req.getParameter("image_id");
        if(image_id == null || image_id.equals("")){
            resp.setStatus(200);
            resp.setContentType("application/json; charset=utf-8");
            resp.getWriter().write("{\"ok\":false,\"reson\":\"请求解析失败\"}");
            return;
        }
        //2.在数据库中查找，并且得到图片存储的路径，
        ImageDao imageDao = new ImageDao();
        Image image = imageDao.selectOne(Integer.parseInt(image_id));
        if(image == null){
            //数据库中不存在要删除的image_id
            resp.setStatus(200);
            resp.setContentType("application/json; charset=utf-8");
            resp.getWriter().write("{\"ok\":false,\"reson\":\"数据库中不存在对应的image_id\"}");
            return;
        }
        //3.根据路径打开文件，进行读取并写入响应中
        resp.setContentType(image.getContentType());
        File file = new File(image.getPath());
        //图片是二进制文件，用字节流的方式进行读取文件
        FileInputStream fileInputStream = new FileInputStream(file);
        OutputStream outputStream = resp.getOutputStream();
        //buffer缓冲区
        byte[] buffer = new byte[1024];
        while(true){
            //将内容读到buffer中
            int len = fileInputStream.read(buffer);
            if(len == -1){
                //文件读取结束
                break;
            }
            //将读到的部分写到响应对象中
            outputStream.write(buffer);
        }
        fileInputStream.close();
        outputStream.close();

    }
}
