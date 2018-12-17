package com.example.demo.pdf;

import com.itextpdf.text.pdf.BaseFont;
import org.springframework.core.io.ClassPathResource;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: czw
 * @create: 2018-12-17 15:45
 **/
public class PDFUtils {
    /** 字体相对路径*/
    public static final String FONT_PATH = "font/";

    public static String DATE_FORMAT_DATE = "yyyy-MM-dd";

    public static boolean downloadFile(String filePath, HttpServletResponse response) throws UnsupportedEncodingException {
        File file = new File(filePath);
        if(file.exists()){ //判断文件父目录是否存在
//			response.setContentType("application/force-download");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=utf-8''" + fileName);

            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;

            OutputStream os = null; //输出流
            try {
                os = response.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while(i != -1){
                    os.write(buffer);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if(null != bis) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                if(null != fis) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        }else{
            return false;
        }
        return true;
    }

    /**
     * 没有图片相对路径的方法
     * @param html
     * @param pdfPath
     * @param fontBasePath
     * @return
     */
    public static boolean createPdf(String html, String pdfPath, String fontBasePath){
        boolean flag = false;
        try {
            createFile(pdfPath);
            OutputStream os = new FileOutputStream(pdfPath);
            ITextRenderer renderer = new ITextRenderer();
            //解决中文支持
            ITextFontResolver fontResolver = renderer.getFontResolver();
            fontResolver.addFont(FONT_PATH +"simfang.ttf", BaseFont.IDENTITY_H,
                    BaseFont.NOT_EMBEDDED);
            fontResolver.addFont(FONT_PATH +"simhei.ttf", BaseFont.IDENTITY_H,
                    BaseFont.NOT_EMBEDDED);
            renderer.setDocumentFromString(html);

            // 解决图片的相对路径问题
//	        renderer.getSharedContext().setBaseURL("file:/" + application.getRealPath("UserFiles/Image") + "/");

            renderer.layout();
            renderer.createPDF(os);
            os.close();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 有相对图片路径的方法
     * @param html
     * @param pdfPath
     * @param fontBasePath
     * @param imagePath
     * @return
     */
    public static boolean createPdf(String html, String pdfPath, String fontBasePath, String imagePath){
        boolean flag = false;
        try {
            createFile(pdfPath);
            OutputStream os = new FileOutputStream(pdfPath);
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);

            //解决中文支持
            ITextFontResolver fontResolver = renderer.getFontResolver();
            fontResolver.addFont(FONT_PATH +"simfang.ttf", BaseFont.IDENTITY_H,
                    BaseFont.NOT_EMBEDDED);
            fontResolver.addFont(FONT_PATH +"simhei.ttf", BaseFont.IDENTITY_H,
                    BaseFont.NOT_EMBEDDED);
            // 解决图片的相对路径问题
            renderer.getSharedContext().setBaseURL("file:///" + imagePath);

            renderer.layout();
            renderer.createPDF(os);
            os.close();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    public static boolean DeleteFolder(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (!file.exists()) {  // 不存在返回 false
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) {  // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else {  // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }

    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if(file.exists()) {
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            return false;
        }
        //判断目标文件所在的目录是否存在
        if(!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            if(!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        //创建目标文件
        try {
            if (file.createNewFile()) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    private static boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag){
                    break;
                }
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    public static void getPdf(String htmlUrl, String pdfUrl){
        try {
            // step 1
            String url = new File(htmlUrl).toURI().toURL().toString();
            // step 2
            OutputStream os = new FileOutputStream(pdfUrl);
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(url);

            // step 3 解决中文支持
            ITextFontResolver fontResolver = renderer
                    .getFontResolver();

            ClassPathResource resource = new ClassPathResource("/ttf/simfang.ttf");
            String path = resource.getFile().getPath();
            String fontBasePath = path.substring(0, path.indexOf("target")).replace("\\", "/") + "src/main/resources/ttf/";

            fontResolver.addFont(FONT_PATH + "simfang.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            fontResolver.addFont(FONT_PATH + "simhei.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            renderer.layout();
            renderer.createPDF(os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static String getPdfPath() {
        String path = "";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DATE);
        String[] date = sdf.format(new Date()).split("-");
        String year = date[0];
        String month = date[1];
        String day = date[2];
        path += "/pdf" + "/"+ year + "/"+ month + "/" + day + "/";
        return path;
    }

    public static String getPdfPathTemporary() {
        String path = "";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DATE);
        String[] date = sdf.format(new Date()).split("-");
        String year = date[0];
        String month = date[1];
        String day = date[2];
        path += "/pdf" + "/" + "temporary" + "/"+ year + "/"+ month + "/" + day + "/";
        return path;
    }

    public static void main(String[] args) throws IOException {
        String html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd \"><html xmlns=\"http://www.w3.org/1999/xhtml \"><head>"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><style>body{font-family:FangSong}p.MsoNormal{mso-style-parent:\"\";margin:0;margin-bottom:.0001pt;font-size:12pt}span.1{font-family:'Times New Roman'}span.15{font-family:'Times New Roman';color:#00f;text-decoration:underline;text-underline:single}p.p{mso-style-noshow:yes;margin-top:5pt;margin-bottom:5pt;mso-margin-top-alt:auto;mso-margin-bottom-alt:auto;font-size:12pt}span.msoIns{mso-style-type:export-only;mso-style-name:\"\";text-decoration:underline;text-underline:single;color:#00f}span.msoDel{mso-style-type:export-only;mso-style-name:\"\";text-decoration:line-through;color:red}table.MsoNormalTable{mso-style-parent:\"\";mso-style-noshow:yes;mso-tstyle-rowband-size:0;mso-tstyle-colband-size:0;mso-padding-alt:0 5.4pt 0 5.4pt;mso-para-margin:0;mso-para-margin-bottom:.0001pt;mso-pagination:widow-orphan;font-size:10pt;mso-ansi-language:#0400;mso-fareast-language:#0400;mso-bidi-language:#0400}</style></head>"
                + "<body style=\"tab-interval:21pt;text-justify-trim:punctuation\"><div class=\"Section0\" style=\"layout-grid:15.6pt\"><p class=\"MsoNormal\" align=\"center\" style=\"text-align:center\"><b><span style=\"font-weight:700;font-size:16pt\">结算信息单</span></b><b><span style=\"font-weight:700;font-size:16pt\"></span></b>"
                + "</p><p class=\"MsoNormal\" align=\"center\" style=\"text-align:center\"><span style=\"font-size:12pt\">&nbsp;</span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\"><i>尊敬的</i></span><b><span style=\"font-weight:700;font-size:11.5pt;font-family:SimHei\">"
                + "lihaifeng@newrank.cn"
                + "</span></b><span style=\"font-size:10.5pt\">您好:</span><span style=\"font-size:10.5pt\"></span></p><p class=\"MsoNormal\" style=\"text-indent:21pt\"><span style=\"font-size:10.5pt\">您于"
                + "2016-04-22&nbsp;10:18:20"
                + "提交的结算申请已被受理，请确认您的信息：</span><span style=\"font-size:10.5pt\"></span></p><p class=\"MsoNormal\" style=\"text-indent:21pt\"><span style=\"font-size:10.5pt\">&nbsp;</span></p><p class=\"MsoNormal\"><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">企业名称："
                + "测试"
                + "</span></b><b><span style=\"font-weight:700;font-size:10.5pt\"></span></b></p><p class=\"MsoNormal\"><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">结算银行</span></b><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">:"
                + "中国银行"
                + "</span></b><b><span style=\"font-weight:700;font-size:10.5pt\"></span></b></p><p class=\"MsoNormal\"><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">结算账号</span></b><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">:"
                + "2322434343221"
                + "</span></b><b><span style=\"font-weight:700;font-size:10.5pt\"></span></b></p><p class=\"MsoNormal\"><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">您此次申请结算金额为</span></b><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">:"
                + "1000元"
                + "。</span></b><b><span style=\"font-weight:700;font-size:10.5pt\"></span></b></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\">&nbsp;</span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\">凡经认定为一般纳税人的企业，必须通过防伪税控系统开具增值税专用发票。</span><span style=\"font-size:10.5pt\"></span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\">增值税专用发票：</span><span style=\"font-size:10.5pt\"></span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\">购货单位名称必须为本公司名称全称、“地址、电话、税务登记号、开户行及账号”等项目填写必须正确。</span><span style=\"font-size:10.5pt\"></span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\"><span>应同时取得发票联（第二联）及抵扣联（第三联），各联均加盖销货单位发票专用章或财务专用章，字轨号码一</span> <span>致，字迹清楚，不得涂改，各项目填写齐全、正确无误，票面金额与实际支付的金额相符，两联的内容与金额一</span>致；</span><span style=\"font-size:10.5pt\"></span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\">&nbsp;</span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\">开票信息请见以下：</span><span style=\"font-size:10.5pt\"></span></p><p class=\"MsoNormal\">"
                + "<span style=\"font-size:10.5pt\">&nbsp;</span></p><img src=\"http://images2015.cnblogs.com/blog/4758/201612/4758-20161223094714557-1228302646.png\" alt=\"323\"/><p class=\"MsoNormal\"><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">开票内容：服务费</span></b><b><span style=\"font-weight:700;font-size:10.5pt\"></span></b></p><p class=\"MsoNormal\"><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">开票金额</span></b><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">："
                + "1000元"
                + "</span></b><b><span style=\"font-weight:700;font-size:10.5pt\"></span></b></p><p class=\"MsoNormal\"><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">开票抬头：上海看榜信息科技有限公司</span></b><b><span style=\"font-weight:700;font-size:10.5pt\"></span></b></p><p class=\"MsoNormal\"><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">开户行：中国工商银行上海市浦星路支行</span></b><b><span style=\"font-weight:700;font-size:10.5pt\"></span></b></p><p class=\"MsoNormal\"><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">账号：</span></b><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">1001056909000009948</span></b><b><span style=\"font-weight:700;font-size:10.5pt\"></span></b></p><p class=\"MsoNormal\"><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">税号：</span></b><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">310230590424777</span></b><b><span style=\"font-weight:700;font-size:10.5pt\"></span></b></p><p class=\"MsoNormal\"><b>"
                + "<span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">地址：上海市陈行路</span></b><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">2388号2号楼402室&nbsp;&nbsp;电话：021-64293929</span></b><b><span style=\"font-weight:700;font-size:10.5pt\"></span></b></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\">&nbsp;</span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\"><span>附则：本制度由上海看榜信息科技有限公司财务部制定，由总裁核准签发，自2015年8月10日起实施执行，未尽</span><span>事宜以财务部发文为准。</span></span><span style=\"font-size:10.5pt\"></span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\">&nbsp;</span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\">请将</span><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">发票</span></b><span style=\"font-size:10.5pt\">和</span><b><span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">结算信息单</span></b><span style=\"font-size:10.5pt\">（打印）</span><span style=\"font-size:10.5pt\">一并寄往：</span><b>"
                + "<span style=\"font-weight:700;font-size:10.5pt;font-family:SimHei\">上海市陈行路2388号浦江科技广场2号楼301，财务负责人收，电话：021-64293929</span></b><b><span style=\"font-weight:700;font-size:10.5pt\"></span></b></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\">&nbsp;</span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\">我们会在收到发票后十五个工作日内处理汇款，请您留意。</span><span style=\"font-size:10.5pt\"></span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\">&nbsp;</span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\"><span>感谢您光临中国内容创业服务平台——，这是一个综合评估微信、微博以及其它移动互联网渠道的内容价值</span> <span>标准体系，秉承独立公正传统出品，公认最早最权威。</span></span><span style=\"font-size:10.5pt\"></span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\">&nbsp;</span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\">现在就登录吧</span><span style=\"font-size:10.5pt\">!http://newrank.cn</span><span style=\"font-size:10.5pt\"></span></p><p class=\"MsoNormal\">"
                +"<img style=\"width: 435px; height: 150px;margin-top: 2px;\" src=\"approve/card/2016/08/18/F1AC3A9D4A239469A84A0803559068E6.jpg\" />"
                + "<span style=\"font-size:10.5pt\">&nbsp;</span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\"></span><span style=\"font-size:10.5pt\">&nbsp;敬启</span><span style=\"font-size:10.5pt\"></span></p><p class=\"MsoNormal\"><span style=\"font-size:10.5pt\">&nbsp;</span></p></div></body></html>";
        // getPdf("E:/sdsd.html", "E:/aa.pdf");
        createPdf(html, "/Users/Mac/idea/redis-demo/结算信息单asdhjadaklk.pdf", null );

    }
}
