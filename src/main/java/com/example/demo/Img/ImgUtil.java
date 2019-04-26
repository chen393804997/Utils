package com.example.demo.Img;


import com.gif4j.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    /**
     * 获取图片的帧数
     * @param imgUrl
     * @return
     */
    public int getImgFrameRate(String imgUrl){
        URL url = null;
        ImageInputStream iis = null;
        int result = 0;
        try {
            url = new URL(imgUrl);
            URLConnection connection = url.openConnection();
            // create an image input stream from the specified fileDD
            iis = ImageIO.createImageInputStream(connection.getInputStream());
            // get all currently registered readers that recognize the image format
            Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
            if (!iter.hasNext()) {
                throw new RuntimeException("No readers found!");
            }
            // get the first reader
            ImageReader reader = iter.next();
            //reader.getFormatName() ：获取图片的格式
            reader.setInput(iis, false);
            result = reader.getNumImages(true);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (iis != null){
                try {
                    iis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    private static final int[] RGB_MASKS = {0xFF0000, 0xFF00, 0xFF};
    private static final ColorModel RGB_OPAQUE = new DirectColorModel(32, RGB_MASKS[0], RGB_MASKS[1], RGB_MASKS[2]);

    /**
     * 获取网络图片流
     *
     * @param url
     * @return
     */
    public static BufferedImage getImageStream(String url) {
        BufferedImage bufferedImage = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                bufferedImage = ImageIO.read(inputStream);
            }
        } catch (Exception e) {
            System.out.println("获取网络图片出现异常，图片路径为：" + url);
            e.printStackTrace();
        }
        return bufferedImage;
    }

    /**
     * 获取网络图片流（解决图片icc信息缺失）
     * @param url
     * @return
     */
    public static BufferedImage getImageStreamByToolkit(String url){
        BufferedImage bufferedImage = null;
        try {
            Image img = Toolkit.getDefaultToolkit().createImage(new URL(url));
            PixelGrabber pg = new PixelGrabber(img, 0, 0, -1, -1, true);
            pg.grabPixels();
            int width = pg.getWidth(), height = pg.getHeight();
            DataBuffer buffer = new DataBufferInt((int[]) pg.getPixels(), pg.getWidth() * pg.getHeight());
            WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, RGB_MASKS, null);
            bufferedImage = new BufferedImage(RGB_OPAQUE, raster, false, null);
        }catch (Exception e){
            System.out.println("getImageStreamByToolkit execute error ：" + e.getMessage());
            e.printStackTrace();
        }
        return bufferedImage;
    }

    /**
     * 将图片裁剪成透明背景的圆形图片
     * @param image
     * @return
     */
    public static BufferedImage cutRoundImage(BufferedImage image)throws Exception{
        long start = System.currentTimeMillis();
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        output = g2.getDeviceConfiguration().createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect(0, 0,w, h, w, h);
        g2.setComposite(AlphaComposite.SrcIn);
        g2.drawImage(image, 0, 0, w, h, null);
        g2.dispose();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(output, "jpg", os);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
        long end = System.currentTimeMillis();
        System.out.println("将图片裁剪成透明背景的圆形图片耗时："+(end-start));
        return bufferedImage;
    }
    /**
     * 拼接图片（合成）
     * @param buffImg   底图
     * @param waterImg  叠加图片
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     * @throws IOException
     */
    public static BufferedImage mergeImage(BufferedImage buffImg, BufferedImage waterImg, int x, int y, int width, int height ) throws IOException {
        long start = System.currentTimeMillis();
        // 创建Graphics2D对象，用在底图对象上绘图
        Graphics2D g2d = buffImg.createGraphics();
        // 绘制
        g2d.drawImage(waterImg, x, y, width, height, null);
        g2d.dispose();// 释放图形上下文使用的系统资源
        long end = System.currentTimeMillis();
        System.out.println("拼接图片（合成）耗时："+(end-start));
        return buffImg;
    }
    /**
     * 添加换行文字
     * @param buffImg
     * @param content
     * @param x
     * @param y
     * @return
     * @throws IOException
     */
    public static BufferedImage addAutoScrollFont(BufferedImage buffImg, String content,int x, int y,int size,boolean center,String color,int line_length,int line_num,String font_name) throws Exception {
        double cell_num = getLength(content)/line_length;
        int num = (int)Math.ceil(cell_num);
        if(num>line_num){
            num = line_num;
        }
        for(int i =0;i<num;i++){
            String cut_content = getStringByLength(content,line_length);
            content = content.substring(cut_content.length(),content.length());
            int spacing =  size + 10;//行间距
            if(i==num-1&&cut_content.length()>(line_length-2)){//最后一行，增加省略号
                cut_content = cut_content.substring(0,line_length-2)+"...";
            }
            buffImg = addFont(buffImg,cut_content,x,y+i*spacing,size,center,color,line_length,font_name);
        }
        return buffImg;
    }
    /**
     * 右对齐添加文字
     * @param buffImg
     * @param content
     * @param x
     * @param y
     * @param size
     * @param center
     * @param color
     * @param length
     * @return
     * @throws Exception
     */
    public static BufferedImage addRightFont(BufferedImage buffImg, String content,int x, int y,int size,boolean center,String color,int length,String font_name)throws Exception {
        if(getLength(content)>length){
            content = getStringByLength(content,length-1)+"..";
        }
        x = x - (int)(getLength(content)*(size+7));
        return addFont(buffImg,content,x,y,size,center,color,length,font_name);
    }
    /**
     * 添加文字
     * @param buffImg
     * @param content
     * @param x
     * @param y
     * @return
     * @throws IOException
     */
    public static BufferedImage addFont(BufferedImage buffImg, String content,int x, int y,int size,boolean center,String color,int length,String font_name) throws Exception {
        long start = System.currentTimeMillis();
        // 创建Graphics2D对象，用在底图对象上绘图
        Graphics2D g2d = buffImg.createGraphics();
        Color font_color = new Color(68, 68, 68);
        if(!StringUtils.isEmpty(color)){
            if((color.length() ==7)){
                String str2 = color.substring(1,3);
                String str3 = color.substring(3,5);
                String str4 = color.substring(5,7);
                int red = Integer.parseInt(str2,16);
                int green = Integer.parseInt(str3,16);
                int blue = Integer.parseInt(str4,16);
                font_color = new Color(red,green,blue);
            }
        }
        g2d.setColor(font_color);
        if(getLength(content)>length&&!content.endsWith("...")){
            content = getStringByLength(content,length)+"...";
        }
        //文字居中位置显示，得到开始位置
        if(center){
            double start_ponit = buffImg.getWidth()/2 - getLength(content)*size/2;
            x = (int)start_ponit;
        }
        // 设置字体、字型、字号
        g2d.setFont(new Font(font_name, Font.BOLD, size));
        // 写入文字
        g2d.drawString(content, x, y);
        g2d.dispose();
        long end = System.currentTimeMillis();
        System.out.println("添加文字耗时："+(end-start));
        return buffImg;
    }
    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为1,英文字符长度为0.5
     * @param  s 需要得到长度的字符串
     * @return int 得到的字符串长度
     */
    public static double getLength(String s) {
        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < s.length(); i++) {
            // 获取一个字符
            String temp = s.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
        }
        //进位取整
        return  Math.ceil(valueLength);
    }
    /**
     * 获取指定长度字符串
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为1,英文字符长度为0.5
     * @param  s 需要得到长度的字符串
     * @param length 得到的字符串长度
     */
    public static String getStringByLength(String s,int length) {
        String result = "";
        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        String chinese_symbol = "[\u3002\uff1f\uff01\uff0c\u3001\uff1b\uff1a\u201c\u201d\u2018\u2019\uff08\uff09\u300a\u300b\u3008\u3009\u3010\u3011\u300e\u300f\u300c\u300d\ufe43\ufe44\u3014\u3015\u2026\u2014\uff5e\ufe4f\uffe5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < s.length(); i++) {
            // 获取一个字符
            String temp = s.substring(i, i + 1);
            // 判断是否为中文字符/中文标点符号
            if (temp.matches(chinese)||temp.matches(chinese_symbol)) {
                // 中文字符长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
            if(Math.ceil(valueLength)<=length){
                result += temp;
            }else{
                break;
            }
        }
        return result;
    }

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;
    /**
     * url转二维码
     * @param url
     * @return
     */
    public static BufferedImage urlToImage(String url) throws Exception{
        long start = System.currentTimeMillis();
        Map<com.google.zxing.EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 400, 400, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("url转二维码耗时："+(end-start));
        return image;
    }
}
