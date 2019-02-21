package com.example.demo.Img;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ImgUtil {

    /**
     * 根据url下载图片并压缩
     * @param picturePath
     * @return
     */
    public String downloadPicutureAndCompression(String picturePath) {
        URL url = null;
        URLConnection con = null;
        InputStream in = null;
        InputStream is = null;
        ByteArrayOutputStream os = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            url = new URL(picturePath);
            con = (HttpURLConnection) url.openConnection();
            in = con.getInputStream();
            //加一层BufferedInputStream
            bufferedInputStream = new BufferedInputStream(in);
            //构造原始图片流 preImage
            BufferedImage preImage= ImageIO.read(bufferedInputStream);
            //获得原始图片的长宽 width/height
            int width=preImage.getWidth();
            int height=preImage.getHeight();
            //构造压缩后的图片流 image 长宽各为原来的1/2
            BufferedImage image=new BufferedImage(width/2, height/2, BufferedImage.TYPE_INT_RGB);
            Graphics graphic=image.createGraphics();
            graphic.drawImage(preImage, 0, 0, width/2, height/2, null);
            os = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", os);
            is = new ByteArrayInputStream(os.toByteArray());
            //is未处理过的流
            //上传阿里云
            //picturePath = aliyunOssUploadV1.uploadFile(Const.FlowPacketCase.COMMONPATH,"jpg",is);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (is != null){
                    is.close();
                }
                if (os != null){
                    os.close();
                }
                if (bufferedInputStream != null){
                    bufferedInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return picturePath;
    }
}
