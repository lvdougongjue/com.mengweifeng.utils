package com.mengweifeng.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author MengWeiFeng
 */
public class ExcelUtil {
    private static final String BANK_STR = "";

    // 私有化构造函数
    private ExcelUtil() {
    }

    public static final String ERROR_EXT_NAME = "错误的扩展名";

    public static void main(String[] args) throws IOException {
        /**
         * String filePath = "test.xlsx"; List<SheetObject> sheets = new
         * ArrayList<ExcelUtil.SheetObject>(); SheetObject sheetObject = new
         * SheetObject("测试"); sheets.add(sheetObject); List<RowObject> rows =
         * new ArrayList<ExcelUtil.RowObject>(); sheetObject.setDatas(rows);
         * RowObject row = new RowObject(); rows.add(row); List<CellObject>
         * cells = new ArrayList<ExcelUtil.CellObject>(); row.setCells(cells);
         * CellObject cell = new CellObject();
         * cell.setDspCellStyleType(DspCellStyleType.ORANGETEXT);
         * cell.setValue("12342342"); cells.add(cell);
         * create07ExcelFileWithStyle(new File(filePath), sheets);
         **/

    }

    /**
     * @param filePath
     * @param sheets   key=sheet页名称 value=需要写入的sheet页的内容(string数组的形式)，String[]第一行为标题
     * @throws IOException
     * @throws IOException
     * @author mengweifeng
     * @since 2012-11-28
     */
    public static void create03ExcelFile(String filePath, Map<String, List<String[]>> sheets) throws IOException {
        File excelFile = new File(filePath);
        create03ExcelFile(excelFile, sheets);
    }

    /**
     * @param excelFile
     * @param sheets    key = sheet页名称 value =
     *                  需要写入的sheet页的内容(string数组的形式)，String[]第一行为标题
     * @throws IOException
     * @throws IOException
     * @author mengweifeng
     * @since 2012-11-28
     */
    public static void create03ExcelFile(File excelFile, Map<String, List<String[]>> sheets) throws IOException {
        Workbook wb = new HSSFWorkbook();
        if (!excelFile.getAbsolutePath().endsWith(".xls")) {
            throw new IOException(ERROR_EXT_NAME);
        }
        createExcelFile(wb, excelFile, sheets);
    }

    /**
     * 创建07格式的excel文件
     *
     * @param filePath
     * @param sheets
     * @throws IOException
     * @author mengweifeng
     * @since 2013-5-8
     */
    public static void create07ExcelFile(String filePath, Map<String, List<String[]>> sheets) throws IOException {
        File excelFile = new File(filePath);
        create07ExcelFile(excelFile, sheets);
    }

    /**
     * 创建07格式的excel文件
     *
     * @param excelFile
     * @param sheets
     * @throws IOException
     * @author mengweifeng
     * @since 2013-5-8
     */
    public static void create07ExcelFile(File excelFile, Map<String, List<String[]>> sheets) throws IOException {
        Workbook wb = new XSSFWorkbook();
        if (!excelFile.getAbsolutePath().endsWith(".xlsx")) {
            throw new IOException(ERROR_EXT_NAME);
        }
        createExcelFile(wb, excelFile, sheets);
    }

    /**
     * 创建带样式的2007版本excel文件
     *
     * @param excelFile
     * @param sheets
     * @throws IOException
     */
    public static void create07ExcelFileWithStyle(File excelFile, List<SheetObject> sheets) throws IOException {
        Workbook wb = new XSSFWorkbook();
        if (!excelFile.getAbsolutePath().endsWith(".xlsx")) {
            throw new IOException(ERROR_EXT_NAME);
        }
        createExcelFile(wb, excelFile, sheets);
    }

    public static void createExcelFile(Workbook wb, File excelFile, Map<String, List<String[]>> sheets) throws IOException {
        for (Entry<String, List<String[]>> entry : sheets.entrySet()) {
            String sheetName = entry.getKey();
            Sheet sheet = wb.createSheet(sheetName);
            List<String[]> sheetValues = entry.getValue();
            if (sheetValues == null) {
                continue;
            }
            for (int i = 0; i < sheetValues.size(); i++) {
                String[] values = sheetValues.get(i);
                Row row = sheet.createRow(i);
                for (int j = 0; j < values.length; j++) {
                    Cell cell = row.createCell(j);
                    String value = values[j];
                    if (value == null || value.length() == 0) {
                        value = BANK_STR;
                    }
                    try {
                        /**
                         * if (NumberUtil.isInteger(value)) { //Integer
                         * numberValue = Integer.parseInt(value); Long
                         * numberValue = Long.valueOf(value);
                         * cell.setCellValue(numberValue); } else {
                         * cell.setCellValue(value); }
                         **/
                        cell.setCellValue(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                        cell.setCellValue(values[j]);
                    }
                }
            }
        }
        OutputStream os = new FileOutputStream(excelFile);
        wb.write(os);
    }

    public static void createExcelFile(Workbook wb, File excelFile, List<SheetObject> sheets) throws IOException {
        Map<CellStyleType, CellStyle> cellStyleMap = createCellStyleMap(wb);
        for (SheetObject sheetObject : sheets) {
            String sheetName = sheetObject.getSheetName();
            Sheet sheet = wb.createSheet(sheetName);
            RowObject titleRow = sheetObject.getTitles();
            int i = 0;
            if (titleRow != null) {
                Row row = sheet.createRow(i);
                fillCellData(cellStyleMap, row, titleRow);
                i++;
            }
            List<RowObject> dataRowObjects = sheetObject.getDatas();
            if (dataRowObjects != null) {
                for (int j = 0; j < dataRowObjects.size(); j++) {
                    Row row = sheet.createRow(j + i);
                    RowObject dataRowObject = dataRowObjects.get(j);
                    fillCellData(cellStyleMap, row, dataRowObject);
                }
            }
        }
        OutputStream os = new FileOutputStream(excelFile);
        wb.write(os);
    }

    /**
     * 填充单元格数据
     *
     * @param cellStyleMap
     * @param row
     * @param dataRowObject
     */
    private static void fillCellData(Map<CellStyleType, CellStyle> cellStyleMap, Row row, RowObject dataRowObject) {
        List<CellObject> cellObjects = dataRowObject.getCells();
        for (int j = 0; j < cellObjects.size(); j++) {
            Cell cell = row.createCell(j);
            CellObject cellObject = cellObjects.get(j);
            CellStyleType cellStyleType = cellObject.getCellStyleType();
            CellStyle cellStyle = cellStyleMap.get(cellStyleType);
            cell.setCellStyle(cellStyle);
            Object valueObject = cellObject.getValue();
            if (valueObject == null) {
                cell.setCellValue("-");
                continue;
            }
            switch (cellStyleType) {
                case BLUETITLE:
                case YELLOWTITLE:
                case REDTEXT:
                case ORANGETEXT:
                case YELLOTEXT:
                case GREENTEXT:
                case TEXT:
                    // 字符串类
                    cell.setCellValue(valueObject.toString());
                    break;
                case INTEGER:
                case DOUBLE:
                case PERCENT:
                case MONEY:
                    // 数字类
                    Double doubleValue = Double.valueOf(valueObject.toString());
                    cell.setCellValue(doubleValue);
                    break;
                case DATE:
                    // 日期时间类
                    Date date = (Date) valueObject;
                    cell.setCellValue(date);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 生成单元格样式集合
     *
     * @param wb
     * @return
     */
    private static Map<CellStyleType, CellStyle> createCellStyleMap(Workbook wb) {
        Map<CellStyleType, CellStyle> cellStyleMap = new HashMap<CellStyleType, CellStyle>();
        XSSFDataFormat df = (XSSFDataFormat) wb.createDataFormat();

        // 蓝色底标题样式
        CellStyle blueTitleCellStyle = getCellStyle(wb);
        blueTitleCellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
        blueTitleCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font whiteTitleFont = wb.createFont();
        whiteTitleFont.setColor(HSSFColor.WHITE.index);
        whiteTitleFont.setFontName("微软雅黑");
        whiteTitleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        blueTitleCellStyle.setFont(whiteTitleFont);
        cellStyleMap.put(CellStyleType.BLUETITLE, blueTitleCellStyle);

        // 蓝色底标题样式
        CellStyle yellowTitleCellStyle = getCellStyle(wb);
        yellowTitleCellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
        yellowTitleCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font blackTitleFont = wb.createFont();
        blackTitleFont.setColor(HSSFColor.BLACK.index);
        blackTitleFont.setFontName("微软雅黑");
        blackTitleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        yellowTitleCellStyle.setFont(blackTitleFont);
        cellStyleMap.put(CellStyleType.YELLOWTITLE, yellowTitleCellStyle);

        // 日期格式
        CellStyle statDateCellStyle = getCellStyle(wb);
        statDateCellStyle.setDataFormat(df.getFormat("yyyy-mm-dd"));
        cellStyleMap.put(CellStyleType.DATE, statDateCellStyle);

        // 普通字符串
        CellStyle textCellStyle = getCellStyle(wb);
        cellStyleMap.put(CellStyleType.TEXT, textCellStyle);

        // 红色文本
        CellStyle redTextCellStyle = getCellStyle(wb);
        Font redTitleFont = wb.createFont();
        redTitleFont.setColor(HSSFColor.RED.index);
        redTitleFont.setFontName("微软雅黑");
        redTitleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        redTextCellStyle.setFont(redTitleFont);
        cellStyleMap.put(CellStyleType.REDTEXT, redTextCellStyle);

        // 橙色文本
        CellStyle orangeTextCellStyle = getCellStyle(wb);
        Font orangeTitleFont = wb.createFont();
        orangeTitleFont.setColor(HSSFColor.ORANGE.index);
        orangeTitleFont.setFontName("微软雅黑");
        orangeTitleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        orangeTextCellStyle.setFont(orangeTitleFont);
        cellStyleMap.put(CellStyleType.ORANGETEXT, orangeTextCellStyle);

        // 黄色文本
        CellStyle yelloTextCellStyle = getCellStyle(wb);
        Font yelloTitleFont = wb.createFont();
        yelloTitleFont.setColor(HSSFColor.YELLOW.index);
        yelloTitleFont.setFontName("微软雅黑");
        yelloTitleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        yelloTextCellStyle.setFont(yelloTitleFont);
        cellStyleMap.put(CellStyleType.YELLOTEXT, yelloTextCellStyle);

        // 绿色文本
        CellStyle greenTextCellStyle = getCellStyle(wb);
        Font greenTitleFont = wb.createFont();
        greenTitleFont.setColor(HSSFColor.GREEN.index);
        greenTitleFont.setFontName("微软雅黑");
        greenTitleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        greenTextCellStyle.setFont(greenTitleFont);
        cellStyleMap.put(CellStyleType.GREENTEXT, greenTextCellStyle);

        // 整数
        CellStyle integerCellStyle = getCellStyle(wb);
        integerCellStyle.setDataFormat(df.getFormat("#,##0"));
        cellStyleMap.put(CellStyleType.INTEGER, integerCellStyle);
        // 小数
        CellStyle doubleCellStyle = getCellStyle(wb);
        doubleCellStyle.setDataFormat(df.getFormat("#,##0.00"));
        cellStyleMap.put(CellStyleType.DOUBLE, doubleCellStyle);
        // 百分数
        CellStyle percentCellStyle = getCellStyle(wb);
        percentCellStyle.setDataFormat(df.getFormat("0.00%"));
        cellStyleMap.put(CellStyleType.PERCENT, percentCellStyle);
        // 金额
        CellStyle moneyCellStyle = getCellStyle(wb);
        moneyCellStyle.setDataFormat(df.getFormat("¥#,##0.00"));
        cellStyleMap.put(CellStyleType.MONEY, moneyCellStyle);
        return cellStyleMap;
    }

    /**
     * 获取单元格基本样式
     *
     * @return
     * @author mengweifeng
     * @since 2013-6-18
     */
    private static CellStyle getCellStyle(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        Font font = wb.createFont();
        font.setFontName("微软雅黑");
        cellStyle.setFont(font);
        return cellStyle;
    }

    /**
     * 读取exce文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    @SuppressWarnings("resource")
    public static Workbook getWorkbook(File file) throws Exception {
        Workbook wb = null;
        InputStream is = new FileInputStream(file);
        String fileName = file.getName();
        String extendName = FileUtil.getExtendName(fileName);
        if ("xls".equals(extendName)) {
            // 2003格式
            wb = new HSSFWorkbook(is);
        } else if ("xlsx".equals(extendName)) {
            // 2007格式
            wb = new XSSFWorkbook(is);
        } else {
            // 不是excel文件
            throw new Exception("不是EXCLE文件");
        }
        return wb;
    }
//
//    /**
//     * 读取excel内容
//     *
//     * @param file
//     * @param fileContents
//     * @return
//     */
//    public static List<String> readExcelFile(CommonsMultipartFile file, List<String> fileContents) {
//        if (null == file) {
//            throw new RuntimeException("未获取到excel文件,请查看!");
//        }
//        InputStream input = null;
//        Workbook workbook = null;
//        try {
//            input = file.getFileItem().getInputStream();
//            workbook = getWorkbook(file.getOriginalFilename(), input);
//            Sheet sheet = workbook.getSheetAt(Constants.NUM_ZERO);
//            for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
//                Row row = sheet.getRow(i);
//                if (null == row) {
//                    continue;
//                }
//                // 只获取第一列的值
//                Cell cell = row.getCell(Constants.NUM_ZERO);
//                if (null == cell) {
//                    continue;
//                }
//                String value = null;
//                switch (cell.getCellType()) {
//                    case HSSFCell.CELL_TYPE_STRING:
//                        value = cell.getStringCellValue();
//                        break;
//                    case HSSFCell.CELL_TYPE_NUMERIC:
//                        value = String.valueOf(cell.getNumericCellValue());
//                        break;
//                }
//                if (StringUtils.isNotEmpty(value) && !fileContents.contains(value)) {
//                    fileContents.add(value);
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        } finally {
//            if (null != input) {
//                try {
//                    input.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return fileContents;
//    }

    /**
     * 获取WorkBook
     *
     * @param fileName
     * @param is
     * @return
     * @throws Exception
     */
    public static Workbook getWorkbook(String fileName, InputStream is) throws Exception {
        Workbook wb = null;
        // InputStream is = new FileInputStream(file);
        // String fileName = file.getName();
        String extendName = FileUtil.getExtendName(fileName);
        if ("xls".equals(extendName)) {
            // 2003格式
            wb = new HSSFWorkbook(is);
        } else if ("xlsx".equals(extendName)) {
            // 2007格式
            wb = new XSSFWorkbook(is);
        } else {
            // 不是excel文件
            throw new Exception("不是EXCLE文件");
        }
        return wb;
    }

    /**
     * 获取单元格的内容
     *
     * @param cell
     * @return
     * @author mengweifeng
     * @since 2013-2-20
     */
    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        String value = "";
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:

                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    if (date != null) {
                        value = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
                    }
                } else {
                    value = new DecimalFormat("0").format(cell.getNumericCellValue());
                }
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                // 导入时如果为公式生成的数据则无值
                if (!cell.getStringCellValue().equals("")) {
                    value = cell.getStringCellValue();
                } else {
                    value = cell.getNumericCellValue() + "";
                }
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                break;
            case HSSFCell.CELL_TYPE_ERROR:
                value = "";
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                value = (cell.getBooleanCellValue() == true ? "Y" : "N");
                break;
            default:
                value = "";
        }
        value = rightTrim(value);
        return value;
    }

    /**
     * 去掉字符串右边的空格
     *
     * @param str 要处理的字符串
     * @return 处理后的字符串
     */

    private static String rightTrim(String str) {
        if (str == null) {
            return "";
        }
        str = str.trim().replaceAll("\r", "").replaceAll("\r\n", "").replaceAll("\n", "");
        int length = str.length();
        for (int i = length - 1; i >= 0; i--) {
            if (str.charAt(i) != 0x20 && str.charAt(i) != (char) 160) {
                break;
            }
            length--;
        }
        return str.substring(0, length);
    }

    public static class SheetObject {
        private String sheetName;
        private RowObject titles;
        private List<RowObject> datas;

        public SheetObject() {

        }

        public SheetObject(String sheetName) {
            super();
            this.sheetName = sheetName;
        }

        public void addRow(RowObject row) {
            if (datas == null) {
                datas = new ArrayList<RowObject>();
            }
            datas.add(row);
        }

        public String getSheetName() {
            return sheetName;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
        }

        public RowObject getTitles() {
            return titles;
        }

        public void setTitles(RowObject titles) {
            this.titles = titles;
        }

        public List<RowObject> getDatas() {
            return datas;
        }

        public void setDatas(List<RowObject> datas) {
            this.datas = datas;
        }

    }

    public static class RowObject {
        List<CellObject> cells;

        public RowObject() {

        }

        public void add(CellObject cell) {
            if (cells == null) {
                cells = new ArrayList<CellObject>();
            }
            cells.add(cell);
        }

        public RowObject(List<CellObject> cells) {
            this.cells = cells;
        }

        public List<CellObject> getCells() {
            return cells;
        }

        public void setCells(List<CellObject> cells) {
            this.cells = cells;
        }
    }

    public static class CellObject {
        private Object value;
        private CellStyleType cellStyleType;

        public CellObject() {

        }

        public CellObject(Object value, CellStyleType cellStyleType) {
            super();
            this.value = value;
            this.cellStyleType = cellStyleType;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public CellStyleType getCellStyleType() {
            return cellStyleType;
        }

        public void setCellStyleType(CellStyleType cellStyleType) {
            this.cellStyleType = cellStyleType;
        }

    }

    public  enum CellStyleType {
        BLUETITLE, YELLOWTITLE, DATE, REDTEXT, YELLOTEXT, ORANGETEXT, GREENTEXT, TEXT, INTEGER, DOUBLE, PERCENT, MONEY
    }
}
