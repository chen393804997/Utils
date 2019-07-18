package com.example.demo.excel;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExcelService {

    private static final Logger logger = Logger.getLogger(ExcelService.class);


    public SXSSFWorkbook toExcel(SXSSFWorkbook wb, String sheetName, List<Map<String, String>> datas,
                                 String[] headers, String[] properties) {
        // 创建Excel的工作书册 Workbook,对应到一个excel文档
        // header style
        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        headerStyle.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        // 创建单元格样式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);



        Sheet sheet = wb.createSheet(sheetName);

        fillSheet(sheet, headerStyle, headers, properties, style, datas);
        return wb;
    }

    /**
     * 根据模板生成excel
     * @param is 流
     * @param datas 需要填充的数据
     * @param properties  填充的字段数组
     * @param numberRows  需要设置为数字的字段数组
     * @param dateRows  需要设置为日期的字段数组
     * @param dateFormat  日期格式：比如：yyyy-MM-dd HH:mm:ss  默认为yyyy-MM-dd（就是传null）
     * @return Workbook
     */

    public Workbook toExcel(InputStream is, List<Map<String, String>> datas, String[] properties, String[] numberRows, String[] dateRows, String dateFormat) {
        Workbook wb = getWorkbook(is);
        return toExcel(datas, properties, numberRows, dateRows, dateFormat, wb, 0);
    }

    /**
     * 根据模板生成excel
     * @param excelPath 模板全路径
     * @param datas 需要填充的数据
     * @param properties  填充的字段数组
     * @param numberRows  需要设置为数字的字段数组
     * @param dateRows  需要设置为日期的字段数组
     * @param dateFormat  日期格式：比如：yyyy-MM-dd HH:mm:ss  默认为yyyy-MM-dd（就是传null）
     * @return Workbook
     */

    public Workbook toExcel(String excelPath, List<Map<String, String>> datas, String[] properties, String[] numberRows, String[] dateRows, String dateFormat) {
        Workbook wb = getWorkbook(excelPath);
        return toExcel(datas, properties, numberRows, dateRows, dateFormat, wb, 0);
    }

    /**
     * 根据模板生成excel
     * @param datas 需要填充的数据
     * @param properties  填充的字段数组
     * @param numberRows  需要设置为数字的字段数组
     * @param dateRows  需要设置为日期的字段数组
     * @param dateFormat  日期格式：比如：yyyy-MM-dd HH:mm:ss  默认为yyyy-MM-dd（就是传null）
     * @param wb
     * @param sheetIndex
     * @return Workbook
     */

    public Workbook toExcel(List<Map<String, String>> datas, String[] properties, String[] numberRows, String[] dateRows, String dateFormat, Workbook wb, int sheetIndex) {
        int count = datas.size();
        if (count == 0) {
            return null;
        }
        CellStyle stringStyle = getCellStyle(wb, "string");
        CellStyle numberStyle = getCellStyle(wb, "number");
        CellStyle dateStyle = null;
        if (dateFormat != null && dateFormat != "") {
            dateStyle = getDateCellStyle(wb, dateFormat);
        }else {
            dateStyle = getCellStyle(wb, "date");
        }

        Sheet sheet = wb.getSheetAt(sheetIndex);
        int totalRows = sheet.getPhysicalNumberOfRows(); //获取单元格的总行数

        CellStyle demoStyle = null;
        Row row = null;
        for (Map<String, String> data : datas) {

            int colIndex = 0;
            row = sheet.createRow(totalRows++); //在模板的最后一行开始创建行

            for(String property : properties) {

                demoStyle = stringStyle;

                //判断是否应该设置为数字单元格
                if (numberRows != null) {
                    for (String string : numberRows) {
                        if (string.equals(property)) {
                            demoStyle = numberStyle;
                            break;
                        }
                    }
                }
                //判断是否应该设置为日期单元格
                if (dateRows != null) {
                    for (String string : dateRows) {
                        if (string.equals(property)) {
                            demoStyle = dateStyle;
                            break;
                        }
                    }
                }

                if (data.containsKey(property)) {
                    if (demoStyle == stringStyle) {
                        writeOneCellForString(row, demoStyle, colIndex++, data.get(property),wb);
                    }else if (demoStyle == numberStyle) {
                        writeOneCellForNumber(row, demoStyle, colIndex++, data.get(property));
                    }else if (demoStyle == dateStyle) {
                        writeOneCellForDate(row, demoStyle, colIndex++, data.get(property), dateFormat);
                    }
                } else {
                    writeOneCellForString(row, demoStyle, colIndex++, "",wb);
                }

            }
        }

        return wb;
    }

    /**
     * 写一个单元格的数据(String)
     * @param row
     * @param style
     * @param index
     * @param value
     */
    private static void writeOneCellForString(Row row, CellStyle style, int index, String value,Workbook workbook) {
        CreationHelper createHelper = workbook.getCreationHelper();
        Cell cell = row.createCell(index);
        cell.setCellStyle(style);
        cell.setCellValue(createHelper.createRichTextString(value));
    }

    /**
     * 写一个单元格的数据(double)
     * @param row
     * @param style
     * @param index
     * @param value
     */
    private static void writeOneCellForNumber(Row row, CellStyle style, int index, String value) {
        Cell cell = row.createCell(index);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellStyle(style);
        if (value == null || "".equals(value)) {
            cell.setCellValue("");
        }else {
            cell.setCellValue(Double.parseDouble(value));
        }
    }

    /**
     * 写一个单元格的数据(date)
     * @param row
     * @param style
     * @param index
     * @param value
     */
    private static void writeOneCellForDate(Row row, CellStyle style, int index, String value, String dateFormat) {
        Cell cell = row.createCell(index);
        cell.setCellStyle(style);
        if (value == null || "".equals(value)) {
            cell.setCellValue("");
        }else {
            cell.setCellValue(stringToDate(value, dateFormat));
        }
    }

    private static Date stringToDate(String str, String dateFormat) {
        if (dateFormat == null || dateFormat == "") {
            dateFormat = "yyyy-MM-dd";
        }
        DateFormat format = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取样式
     * flag :   string(字符串)、date（日期）、number（数字）
     */
    private static CellStyle getCellStyle (Workbook wb, String flag) {
        CellStyle style = wb.createCellStyle(); // 样式对象

        Font font = wb.createFont();   //设置字体格式
        font.setFontName("微软雅黑");   //---》设置字体，是什么类型例如：宋体
        font.setFontHeightInPoints((short)10);   //--->设置字体大小
        style.setFont(font);     //--->将字体格式加入到style0_1中
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);   //设置下划线，参数是黑线的宽度
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);   //设置左边框
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);   //设置有边框
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);   //设置下边框
        style.setAlignment(HSSFCellStyle.ALIGN_LEFT); //左对齐
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
        DataFormat format =  wb.createDataFormat();

        if ("number".equals(flag)) {
            style.setDataFormat(format.getFormat("@"));
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); //居中
        }else if ("date".equals(flag)) {
            style.setDataFormat(format.getFormat("yyyy-MM-dd"));
            style.setAlignment(HSSFCellStyle.ALIGN_RIGHT); //右对齐
        }

        return style;
    }

    private static CellStyle getDateCellStyle (Workbook wb, String dateFormat) {
        CellStyle style = wb.createCellStyle(); // 样式对象

        Font font = wb.createFont();   //设置字体格式
        font.setFontName("微软雅黑");   //---》设置字体，是什么类型例如：宋体
        font.setFontHeightInPoints((short)10);   //--->设置字体大小
        style.setFont(font);     //--->将字体格式加入到style0_1中
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);   //设置下划线，参数是黑线的宽度
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);   //设置左边框
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);   //设置有边框
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);   //设置下边框
        style.setAlignment(HSSFCellStyle.ALIGN_RIGHT); //右对齐
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
        DataFormat format =  wb.createDataFormat();
        style.setDataFormat(format.getFormat(dateFormat));

        return style;
    }


    public SXSSFWorkbook toExcel(List<Map<String, String>> datas,
                                 String[] headers, String[] properties) {
        int count = datas.size();
        if (count == 0) {
            return null;
        }
        // 创建Excel的工作书册 Workbook,对应到一个excel文档
        SXSSFWorkbook wb = new SXSSFWorkbook();
        // header style
        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        headerStyle.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        // 创建单元格样式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        Sheet sheet = wb.createSheet("sheet");

        fillSheet(sheet, headerStyle, headers, properties, style, datas);

        return wb;
    }

    private static void fillCell(Row row, CellStyle style, int index, String value) {
        Cell cell = row.createCell(index);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    public static void fillSheet(Sheet sheet,
                                 CellStyle headerStyle, String[] headers, String[] properties,
                                 CellStyle style, List<Map<String, String>> datas) {


        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            // 创建一个Excel的单元格
            Cell cell = row.createCell(i);
            // 给Excel的单元格设置样式和赋值
            cell.setCellStyle(headerStyle);
            cell.setCellValue(headers[i]);
        }
        int rowIndex = 1;
        for (Map<String, String> data : datas) {

            int colIndex = 0;
            row = sheet.createRow(rowIndex++);
            // 创建一个Excel的单元格
            for(String property : properties) {
                if (data.containsKey(property)) {
                    fillCell(row, style, colIndex++, data.get(property));
                } else {
                    fillCell(row, style, colIndex++, "");
                }

            }
        }
    }

    /**
     *
     * @描述：验证excel文件
     *
     * @参数：@param filePath　文件完整路径
     *
     * @参数：@return
     *
     * @返回值：boolean
     */
    private boolean validateExcel(String filePath){

        /** 检查文件名是否为空或者是否是Excel格式的文件 */

        if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath))){

            logger.error("文件名不是excel格式");

            return false;

        }

        /** 检查文件是否存在 */

        File file = new File(filePath);

        if (file == null || !file.exists())
        {

            logger.error("文件不存在");

            return false;

        }

        return true;

    }

    /**
     *
     * @描述：是否是2003的excel，返回true是2003
     *
     * @作者：chenlei
     *
     * @参数：@param filePath　文件完整路径
     *
     * @参数：@return
     *
     * @返回值：boolean
     */

    private static boolean isExcel2003(String filePath){

        return filePath.matches("^.+\\.(?i)(xls)$");

    }

    /**
     *
     * @描述：是否是2007的excel，返回true是2007
     *
     * @作者：chenlei
     *
     * @参数：@param filePath　文件完整路径
     *
     * @参数：@return
     *
     * @返回值：boolean
     */

    private static boolean isExcel2007(String filePath)
    {

        return filePath.matches("^.+\\.(?i)(xlsx)$");

    }

    /**
     * 根据版本选择创建Workbook的方式
     */
    private static Workbook getWorkbook (String filePath) {

        Workbook wb = null;
        try {
            boolean isExcel2003 = true;

            if (isExcel2007(filePath)){
                isExcel2003 = false;
            }
            InputStream is = new FileInputStream(filePath);
            if (isExcel2003){
                wb = new HSSFWorkbook(is);
            }else{
                wb = new XSSFWorkbook(is);
            }
            return wb;
        }catch (Exception e) {
            e.printStackTrace();
        }


        return wb;
    }

    /**
     * 根据版本选择创建Workbook的方式
     */
    private static Workbook getWorkbook (InputStream is) {

        Workbook wb = null;
        try {
            wb = new XSSFWorkbook(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }


}
