package com.example.demo.Img;


import com.gif4j.*;

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


    /**
     * 缩放gif图片
     *
     * @param src
     * @param dest
     * @param width
     * @param height
     * @throws IOException
     */
    public static void makeGif(File src, File dest, int width, int height) throws IOException {
        GifImage gifImage = GifDecoder.decode(src);// 创建一个GifImage对象.
        GifImage resizeIMG = GifTransformer.resize(gifImage, width, height, true);
        GifEncoder.encode(resizeIMG, dest);
    }

    public static void makeGif(String src, String dest, int width, int height) throws IOException {
        GifImage gifImage = GifDecoder.decode(new File(src));// 创建一个GifImage对象.
        makeGif(new File(src), new File(dest), gifImage.getScreenWidth() / 2, gifImage.getScreenHeight() / 2);
    }

    public static void makeGif(File src, File dest) throws IOException {
        GifImage gifImage = GifDecoder.decode(src);// 创建一个GifImage对象.
        makeGif(src, dest, gifImage.getScreenWidth() / 2, gifImage.getScreenHeight() / 2);
    }

    public static void makeGif(String src, String dest) throws IOException {
        makeGif(new File(src), new File(dest));
    }

    /**
     * 在图片中加水印
     *
     * @param src
     * @param watermarkText
     * @param dest
     * @throws IOException
     */
    public static void addTextWatermarkToGif(File src, String watermarkText, File dest) throws IOException {
        //水印初始化、设置(字体、样式、大小、颜色)
        TextPainter textPainter = new TextPainter(new Font("黑体", Font.ITALIC, 12));
        textPainter.setOutlinePaint(Color.WHITE);
        BufferedImage renderedWatermarkText = textPainter.renderString(watermarkText, true);

        //图片对象
        GifImage gf = GifDecoder.decode(src);

        //获取图片大小
        int iw = gf.getScreenWidth();
        int ih = gf.getScreenHeight();

        //获取水印大小
        int tw = renderedWatermarkText.getWidth();
        int th = renderedWatermarkText.getHeight();

        //水印位置
        Point p = new Point();
        p.x = iw - tw - 5;
        p.y = ih - th - 4;

        //加水印
        Watermark watermark = new Watermark(renderedWatermarkText, p);
        gf = watermark.apply(GifDecoder.decode(src), true);
        //输出
        GifEncoder.encode(gf, dest);
    }

    public static void addTextWatermarkToGif(GifImage gf, String watermarkText, File dest, String watermarkPosition) throws IOException {
        //水印初始化、设置(字体、样式、大小、颜色)
        TextPainter textPainter = new TextPainter(new Font("微软雅黑", Font.PLAIN, 28));
        textPainter.setOutlinePaint(Color.WHITE);
        BufferedImage renderedWatermarkText = textPainter.renderString(watermarkText, true);

        //获取图片大小
        int iw = gf.getScreenWidth();
        int ih = gf.getScreenHeight();

        //获取水印大小
        int tw = renderedWatermarkText.getWidth();
        int th = renderedWatermarkText.getHeight();

        //水印位置
        Point p = getTextPosition(iw, ih, tw, th, watermarkPosition);

        //加水印
        Watermark watermark = new Watermark(renderedWatermarkText, p);
        gf = watermark.apply(gf, true);
        //输出
        GifEncoder.encode(gf, dest);
    }

    private static Point getTextPosition(int iw, int ih, int tw, int th, String watermarkPosition) {
        Point p = new Point();
        if (watermarkPosition.equals(AdPasterGifReq.LEFT_UP)) {
            p.x = 5;
            p.y = 4;
        } else if (watermarkPosition.equals(AdPasterGifReq.LEFT_DOWN)) {
            p.x = 5;
            p.y = ih - th - 4;
        } else if (watermarkPosition.equals(AdPasterGifReq.RIGHT_UP)) {
            p.x = iw - tw - 5;
            p.y = 4;
        } else if (watermarkPosition.equals(AdPasterGifReq.RIGHT_DOWN)) {
            p.x = iw - tw - 5;
            p.y = ih - th - 4;
        }
        return p;
    }

    /**
     * 对gif图剪切，参数是坐标和宽高
     */
    public static void gifCut(File src, File dest, int x, int y, int w, int h) throws IOException {
        Rectangle rectangle = new Rectangle(x, y, w, h);
        GifImage srcImg = GifDecoder.decode(src);
        GifImage cropImg = GifTransformer.crop(srcImg, rectangle);
        GifEncoder.encode(cropImg, dest);
    }

    public static void gifCutAndAddText(File src, File dest, String text, int x, int y, int w, int h, String watermarkPosition) throws IOException {
        Rectangle rectangle = new Rectangle(x, y, w, h);
        GifImage srcImg = GifDecoder.decode(src);
        GifImage cropImg = GifTransformer.crop(srcImg, rectangle);
        addTextWatermarkToGif(cropImg, text, dest, watermarkPosition);
    }

    /**
     * 在图片中加图片水印
     *
     * @param src
     * @param watermarkPath
     * @param dest
     * @throws IOException
     */
    public static void addImageWatermarkToGif(File src, String watermarkPath, File dest, String watermarkPosition) throws IOException {
        BufferedImage renderedWatermarkText = ImageIO.read(ImgUtil.class.getResourceAsStream(watermarkPath));

        //图片对象
        GifImage gf = GifDecoder.decode(src);
        //获取图片大小
        int iw = gf.getScreenWidth();
        int ih = gf.getScreenHeight();
        //获取水印大小
        int tw = renderedWatermarkText.getWidth();
        int th = renderedWatermarkText.getHeight();
        //水印位置
        Point p = getTextPosition(iw, ih, tw, th, watermarkPosition);

        //加水印
        Watermark watermark = new Watermark(renderedWatermarkText, p);
        //水印透明度
        watermark.setTransparency(1);
        gf = watermark.apply(GifDecoder.decode(src), false);

        //输出
        GifEncoder.encode(gf, dest);
    }
}
