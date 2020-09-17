//package com.example.demo;
//
//import com.alibaba.fastjson.JSON;
//import com.example.demo.excel.ExcelService;
//import com.example.demo.or_code.QRCodeDemo;
//import com.example.demo.utils.ValidateUtil;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.util.StringUtils;
//
//import java.io.*;
//import java.net.MalformedURLException;
//import java.util.*;
//
//public class YouFenBa {
//
//    public void execute(String path){
//        List<Map<String,String>> list = getExcel(path);
//        if (!ValidateUtil.checkListIsNotEmpty(list))
//            return;
//        handleUrl(list);
//        produceExcel(list,"C:\\Users\\薛嵘锦\\Desktop\\优粉吧项目.xlsx");
//    }
//
//    public void produceExcel(List<Map<String,String>> list,String savePath){
//        String path = "C:\\Users\\薛嵘锦\\Desktop\\模板.xlsx";
//        if (!ValidateUtil.checkListIsNotEmpty(list)){
//            return;
//        }
//        InputStream inputStream = null;
//        InputStream is = null;
//        FileOutputStream fileOutputStream = null;
//        try {
//            ExcelService excelService = new ExcelService();
//            inputStream = new FileInputStream(new File(path));
//            Workbook wb = excelService.toExcel(inputStream, list, new String[]{"caseTitle","caseUrl"}, null, null, null);
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            wb.write(bos);
//            byte[] batys = bos.toByteArray();
//            is = new ByteArrayInputStream(batys);
//
//            int index;
//            byte[] bytes = new byte[1024];
//            fileOutputStream = new FileOutputStream(savePath);
//            while ((index = is.read(bytes)) != -1) {
//                fileOutputStream.write(bytes, 0, index);
//                fileOutputStream.flush();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                if (fileOutputStream != null)
//                    fileOutputStream.close();
//                if (is != null){
//                    is.close();
//                }
//                if (inputStream != null){
//                    inputStream.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    private void handleUrl(List<Map<String,String>> list){
//        for (Map<String,String> map : list){
//            String adUrl = map.get("adUrl");
//            String caseUrl = QRCodeDemo.parseQRCode(adUrl);
//            if (StringUtils.isEmpty(caseUrl))
//                continue;
//            map.put("caseUrl",caseUrl);
//        }
//    }
//
//    private List<Map<String,String>> getExcel(String path) {
//        List<Map<String,String>> resultList = new ArrayList<>();
//        InputStream is = null;
//        Workbook wookbook = null;
//        try {
//            is = new FileInputStream(new File(path));
//            try {
//                wookbook = new HSSFWorkbook(is);
//            } catch (Exception e) {
//                is = new FileInputStream(new File(path));
//                wookbook = new XSSFWorkbook(is);
//            }
//            //得到一个工作表
//            Sheet sheet = wookbook.getSheetAt(0);
//            //获得表头
//            Row rowHead = sheet.getRow(0);
//            //判断表头是否正确
//            int rowHeadNum = rowHead.getPhysicalNumberOfCells();
//            //获得数据的总行数
//            int totalRowNum = sheet.getLastRowNum();
//
//            //获得所有数据
//            for (int i = 1; i <= totalRowNum; i++) {
//                try {
//                    //获得第i行对象
//                    Row row = sheet.getRow(i);
//                    //获得获得第i行第0列的 String类型对象
//                    Cell cell = row.getCell((short) 0);
//                    String adUrl = cell == null ? null : cell.getStringCellValue().trim();
//                    cell = row.getCell((short) 1);
//                    String caseTitle = cell == null ? null : cell.getStringCellValue().trim();
//                    if (StringUtils.isEmpty(adUrl) || StringUtils.isEmpty(caseTitle))
//                        continue;
//                    Map<String,String> map = new HashMap<>();
//                    map.put("adUrl",adUrl);
//                    map.put("caseTitle",caseTitle);
//                    resultList.add(map);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return resultList;
//    }
//
//    public static void main(String[] args) {
//        YouFenBa youFenBa = new YouFenBa();
//        youFenBa.execute("C:\\Users\\薛嵘锦\\Desktop\\优粉吧文案.xlsx");
//    }
//
//}
