package dao;

import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class ImageDaoTest {

    @Test
    public void insert() {
        ImageDao imageDao = new ImageDao();
        Image image = new Image();
        image.setImage_name("1.2.jpg" );
        image.setSize(3796926);
        image.setUploadTime("20200709");
        image.setContentType("image/jpeg ");
        image.setMd5("08a1e2215a19d80082c1db63862c8481");
        image.setPath("./image/08a1e2215a19d80082c1db63862c8481");
        imageDao.insert(image);
    }

    @Test
    public void selectAll() {
        ImageDao imageDao = new ImageDao();
        List<Image> list = new ArrayList<Image>();
        list= imageDao.selectAll();
        System.out.println("size = "+list.size());
        for(Image image:list){
            System.out.println("name:"+image.getImage_name());
        }

    }

    @Test
    public void selectOne() {
        ImageDao imageDao = new ImageDao();
        Image image = imageDao.selectOne(1);
        System.out.println(image.getImage_name());
        System.out.println(image.getSize());
        System.out.println(image.getPath());
        System.out.println(image.getMd5());
        System.out.println(image.getContentType());
    }

    @Test
    public void delete() {
        ImageDao imageDao = new ImageDao();
        imageDao.delete(1);
    }

    @Test
    public void selectByMd5() {
        ImageDao imageDao = new ImageDao();
        imageDao.selectByMd5("08a1e2215a19d80082c1db63862c8481");
    }
}