package com.example.demo.or_code;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class QRCodeDemo extends LuminanceSource {

    //二维码识别

    private final BufferedImage image;
    private final int left;
    private final int top;

    public QRCodeDemo(BufferedImage image) {
        this(image, 0, 0, image.getWidth(), image.getHeight());
    }

    public QRCodeDemo(BufferedImage image, int left, int top, int width, int height) {
        super(width, height);

        int sourceWidth = image.getWidth();
        int sourceHeight = image.getHeight();
        if (left + width > sourceWidth || top + height > sourceHeight) {
            throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
        }

        for (int y = top; y < top + height; y++) {
            for (int x = left; x < left + width; x++) {
                if ((image.getRGB(x, y) & 0xFF000000) == 0) {
                    image.setRGB(x, y, 0xFFFFFFFF); // = white
                }
            }
        }

        this.image = new BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_BYTE_GRAY);
        this.image.getGraphics().drawImage(image, 0, 0, null);
        this.left = left;
        this.top = top;
    }

    @Override
    public byte[] getRow(int y, byte[] row) {
        if (y < 0 || y >= getHeight()) {
            throw new IllegalArgumentException("Requested row is outside the image: " + y);
        }
        int width = getWidth();
        if (row == null || row.length < width) {
            row = new byte[width];
        }
        image.getRaster().getDataElements(left, top + y, width, 1, row);
        return row;
    }

    @Override
    public byte[] getMatrix() {
        int width = getWidth();
        int height = getHeight();
        int area = width * height;
        byte[] matrix = new byte[area];
        image.getRaster().getDataElements(left, top, width, height, matrix);
        return matrix;
    }

    @Override
    public boolean isCropSupported() {
        return true;
    }

    @Override
    public LuminanceSource crop(int left, int top, int width, int height) {
        return new QRCodeDemo(image, this.left + left, this.top + top, width, height);
    }

    @Override
    public boolean isRotateSupported() {
        return true;
    }

    @Override
    public LuminanceSource rotateCounterClockwise() {

        int sourceWidth = image.getWidth();
        int sourceHeight = image.getHeight();

        AffineTransform transform = new AffineTransform(0.0, -1.0, 1.0, 0.0, 0.0, sourceWidth);

        BufferedImage rotatedImage = new BufferedImage(sourceHeight, sourceWidth, BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g = rotatedImage.createGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        int width = getWidth();
        return new QRCodeDemo(rotatedImage, top, sourceWidth - (left + width), getHeight(), width);
    }
    /**
     * 解析指定路径下的二维码图片
     *
     * @param filePath 二维码图片路径
     * @return
     */
    public static String parseQRCode(String filePath) {
        String content = "";
        try {
            File file = new File(filePath);
            BufferedImage image = ImageIO.read(new URL(filePath));
            LuminanceSource source = new QRCodeDemo(image);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            MultiFormatReader formatReader = new MultiFormatReader();
            Result result = formatReader.decode(binaryBitmap, hints);

            System.out.println("result 为：" + result.toString());
            System.out.println("resultFormat 为：" + result.getBarcodeFormat());
            System.out.println("resultText 为：" + result.getText());
            //设置返回值
            content = result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    //二维码生成


    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;
    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }


    private static void writeToFile(BitMatrix matrix, String format, File file)
            throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }


    private static void writeToStream(BitMatrix matrix, String format, OutputStream stream)
            throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }
    /**
     * 根据内容，生成指定宽高、指定格式的二维码图片
     *
     * @param text   内容
     * @param width  宽
     * @param height 高
     * @param format 图片格式
     * @return 生成的二维码图片路径
     * @throws Exception
     */
    public static String generateQRCode(String text, int width, int height, String format) throws Exception {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        String pathName = "F:/new.png";
        File outputFile = new File(pathName);
        writeToFile(bitMatrix, format, outputFile);
        return pathName;
    }

    public static void main(String[] args) {
        String str = QRCodeDemo.parseQRCode("http://www.youfenba.com/qrcode/create.html?url=http%3A%2F%2Fyjxmt.youfenba.com%2FIndex%2Forder_article_details.html%3Fid%3D356001%26current_type%3D1");
        System.out.println(str);
    }
}
