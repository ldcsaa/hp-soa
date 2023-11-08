package io.github.hpsocket.soa.framework.util.file.excel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static io.github.hpsocket.soa.framework.core.util.GeneralHelper.*;

/** <b>POI 辅助器</b><br>
 * 简化 Excel 文件处理操作
 */
public class PoiHelper
{
    public static final boolean isExcelFile(File f)
    {
        if(f.isFile())
        {
            String name = f.getName();
            
            if(name.endsWith(".xlsx") || name.endsWith(".xls"))
                return true;
        }
        
        return false;
    }

    public static final boolean isXlsxFile(File f)
    {
        if(f.isFile())
        {
            String name = f.getName();
            
            if(name.endsWith(".xlsx"))
                return true;
        }
        
        return false;
    }

    public static final boolean isXlsFile(File f)
    {
        if(f.isFile())
        {
            String name = f.getName();
            
            if(name.endsWith(".xls"))
                return true;
        }
        
        return false;
    }

    public static final boolean isCsvFile(File f)
    {
        if(f.isFile())
        {
            String name = f.getName();
            
            if(name.endsWith(".csv"))
                return true;
        }
        
        return false;
    }

    public static final boolean isExcelFile(String fileName)
    {
        return isExcelFile(new File(fileName));
    }
    
    public static final boolean isXlsxFile(String fileName)
    {
        return isXlsxFile(new File(fileName));
    }
    
    public static final boolean isXlsFile(String fileName)
    {
        return isXlsFile(new File(fileName));
    }
    
    public static final File[] listExcelFiles(String strPath)
    {
        File path = new File(strPath);
        
        File[] files = path.listFiles(new FilenameFilter() {
            
            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".xlsx") && name.indexOf("~$") == -1;
            }
        });
        
        return files;
    }

    public static final String modifyFileName(String fileName, String suffix, String timeStamp)
    {
        int i        = fileName.lastIndexOf('.');
        int len     = fileName.length();
        
        if(len == 0 || i <= 0 || i >= len -1)
            throw new IllegalArgumentException(String.format("illegal file name '%s'", fileName));
        
        StringBuilder sb = new StringBuilder();
        sb.append(fileName.substring(0, i))
        .append('_').append(suffix);
        
        if(timeStamp != null)
            sb.append('_').append(timeStamp);
        
        sb.append(fileName.substring(i));
        
        return sb.toString();
    }
    
    public static final Cell getCell(Sheet sheet, int rownum, int cellnum, boolean create)
    {
        Row row = sheet.getRow(rownum);
        
        if(create)
        {
            if(row == null) row = sheet.createRow(rownum);
            return row.getCell(cellnum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        }
        else
        {
            if(row == null) return null;
            return row.getCell(cellnum);
        }
    }
    
    public static final float getCellValueFloat(Sheet sheet, int rownum, int cellnum)
    {
        return str2Float(getCellValue(sheet, rownum, cellnum), 0);
    }
    
    public static final Float getCellValueFloatOrNull(Sheet sheet, int rownum, int cellnum)
    {
        return str2Float(getCellValue(sheet, rownum, cellnum));
    }
    
    public static final Float getCellValueFloatOrNull_0(Sheet sheet, int rownum, int cellnum)
    {
        String val = getCellValue(sheet, rownum, cellnum);
        
        if(isStrEmpty(val))
            return 0F;
        
        return str2Float(val);
    }
    
    public static final double getCellValueDouble(Sheet sheet, int rownum, int cellnum)
    {
        return str2Double(getCellValue(sheet, rownum, cellnum), 0);
    }
    
    public static final Double getCellValueDoubleOrNull(Sheet sheet, int rownum, int cellnum)
    {
        return str2Double(getCellValue(sheet, rownum, cellnum));
    }
    
    public static final Double getCellValueDoubleOrNull_0(Sheet sheet, int rownum, int cellnum)
    {
        String val = getCellValue(sheet, rownum, cellnum);
        
        if(isStrEmpty(val))
            return 0D;
        
        return str2Double(val);
    }
    
    public static final int getCellValueInt(Sheet sheet, int rownum, int cellnum)
    {
        double f = getCellValueDouble(sheet, rownum, cellnum);
        
        return Double.valueOf(f + 0.5D).intValue();
    }
    
    public static final Integer getCellValueIntOrNull(Sheet sheet, int rownum, int cellnum)
    {
        Double f = getCellValueDoubleOrNull(sheet, rownum, cellnum);

        if(f == null) return null;
        
        return Double.valueOf(f + 0.5D).intValue();
    }
    
    public static final Integer getCellValueIntOrNull_0(Sheet sheet, int rownum, int cellnum)
    {
        Double f = getCellValueDoubleOrNull_0(sheet, rownum, cellnum);

        if(f == null) return null;
        
        return Double.valueOf(f + 0.5D).intValue();
    }
    
    public static final String getCellValue(Sheet sheet, int rownum, int cellnum)
    {
        Row row = sheet.getRow(rownum);
        if(row == null) return "";
        Cell cell = row.getCell(cellnum);
        if(cell == null) return "";
        
        return getCellValue(cell);
    }
    
    public static final String getCellValue(Cell cell)
    {
        String rs = null;
        
        try
        {
            switch(cell.getCellType())
            {
            case BLANK:
                rs = "";
                break;
            case STRING:
                rs = cell.getRichStringCellValue().getString();
                break;
            case NUMERIC:
                if(!DateUtil.isCellDateFormatted(cell))
                    rs = NumberToTextConverter.toText(cell.getNumericCellValue());
                else
                {
                    Date theDate = cell.getDateCellValue();
                    rs = date2Str(theDate, "yyyy-MM-dd HH:mm:ss");
                }
                break;
            case BOOLEAN:
                rs = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                rs = NumberToTextConverter.toText(cell.getNumericCellValue());
                break;
            case ERROR:
                rs = null;
                break;
            default:
                rs = null;
            }
        }
        catch(Exception e)
        {

        }

        return trimChars(rs, " ");
    }
    
    public static String trimChars(String str, String chars)
    {
        if(isStrEmpty(str))
            return safeString(str);
        if(chars == null)
            chars = "";
        
        int len    = str.length();
        int st    = 0;
        
        while(st < len)
        {
            char c = str.charAt(st);
            
            if(c <= ' ' || chars.indexOf(c) >= 0)
                ++st;
            else
                break;
        }

        while(st < len)
        {
            char c = str.charAt(len - 1);
            
            if(c <= ' ' || chars.indexOf(c) >= 0)
                --len;
            else
                break;
        }

        return ((st > 0) || (len < str.length())) ? str.substring(st, len) : str;
    }
    
    public static final <T> void setCellValue(Sheet sheet, int rownum, int cellnum, T value)
    {
        Cell cell = getCell(sheet, rownum, cellnum, true);
        
        if(value == null)
        {
            CellType cellType = cell.getCellType();
            
            if(cellType == CellType.FORMULA || cellType == CellType.ERROR)
                cell.setCellValue("");
            else
                cell.setBlank();
        }
        else if(value instanceof String)
            cell.setCellValue(safeTrimString((String)value));
        else if(value instanceof RichTextString)
            cell.setCellValue((RichTextString)value);
        else if(value instanceof Number)
            cell.setCellValue(((Number)value).doubleValue());
        else if(value instanceof Boolean)
            cell.setCellValue(((Boolean)value));
        else if(value instanceof Date)
            cell.setCellValue(((Date)value));
        else if(value instanceof LocalDate)
            cell.setCellValue(Date.from(((LocalDate)value).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        else if(value instanceof LocalDateTime dt)
            cell.setCellValue(GregorianCalendar.from(ZonedDateTime.of((dt), ZoneId.systemDefault())));
        else if(value instanceof OffsetDateTime dt)
            cell.setCellValue(GregorianCalendar.from(dt.toZonedDateTime()));
        else if(value instanceof ZonedDateTime dt)
            cell.setCellValue(GregorianCalendar.from(dt));
        else if(value instanceof Calendar)
            cell.setCellValue(((Calendar)value));
        else
            throw new RuntimeException(String.format("invalid cell value type '%s'", value.getClass().getName()));
    }
    
    public static final <T> void setCellValue(Sheet sheet, int rownum, int cellnum, String value, Class<?> clazz)
    {
        if(value == null || clazz == null)
            setCellValue(sheet, rownum, cellnum, value);
        else if(String.class.isAssignableFrom(clazz))
            setCellValue(sheet, rownum, cellnum, value);
        else if(RichTextString.class.isAssignableFrom(clazz))
            setCellValue(sheet, rownum, cellnum, new XSSFRichTextString(value));
        else if(Number.class.isAssignableFrom(clazz) || (clazz.isPrimitive() && clazz != Void.TYPE && clazz != Boolean.TYPE && clazz != Character.TYPE))
            setCellValue(sheet, rownum, cellnum, str2Double(value));
        else if(Boolean.class.isAssignableFrom(clazz) || Boolean.TYPE == clazz)
            setCellValue(sheet, rownum, cellnum, str2Boolean(value));
        else if(Date.class.isAssignableFrom(clazz))
            setCellValue(sheet, rownum, cellnum, str2Date(value));
        else if(Calendar.class.isAssignableFrom(clazz))
        {
            Calendar c = Calendar.getInstance();
            c.setTime(str2Date(value));
            setCellValue(sheet, rownum, cellnum, c);
        }
        else
            throw new RuntimeException(String.format("invalid cell value type '%s'", clazz.getName()));
    }
    
    public static final boolean copyCellValue(Sheet fromSheet, int fromRowNum, int fromCellNum, Sheet toSheet, int toRowNum, int toCellNum, Class<?> clazz, boolean breakIfEmpty)
    {
        String value = getCellValue(fromSheet, fromRowNum, fromCellNum);
        boolean rs     = !breakIfEmpty || isStrNotEmpty(value);
        
        if(rs)
            setCellValue(toSheet, toRowNum, toCellNum, value, clazz);
        
        return rs;
    }

    public static final <T> void setCellFormula(Sheet sheet, int rownum, int cellnum, String formula)
    {
        Cell cell = getCell(sheet, rownum, cellnum, true);
        cell.setCellFormula(formula);
    }
    
    public static final CellStyle createCellStyle(Workbook wb, String fontName, short fontHeightInPoints, short fontColor, boolean isBlockFont, short fillForegroundColor, HorizontalAlignment horizontalAlignment)
    {
        Font font        = wb.createFont();
        CellStyle style    = wb.createCellStyle();
        
        font.setFontHeightInPoints(fontHeightInPoints);
        font.setFontName(fontName);
        font.setColor(fontColor);
        font.setBold(isBlockFont);
        
        style.setFillForegroundColor(fillForegroundColor);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(horizontalAlignment);
        style.setFont(font);
        
        return style;
    }
    
    public static final <T> void setCellStyle(Sheet sheet, int rownum, int cellnum, CellStyle style)
    {
        Cell cell = getCell(sheet, rownum, cellnum, true);        
        cell.setCellStyle(style);
    }
    
    public static final <T> void setCellComment(Sheet sheet, int rownum, int cellnum, String text)
    {
        Cell cell = getCell(sheet, rownum, cellnum, true);
        
        cell.removeCellComment();
        
        if(isStrEmpty(text))
            return;
        else
        {
            
            Drawing<?> p            = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor    = new XSSFClientAnchor(100, 100, 100, 100, cellnum, rownum, cellnum + 2, rownum + 2);
            Comment comment            = p.createCellComment(anchor);
            XSSFRichTextString rt    = new XSSFRichTextString(text);
            
            rt.applyFont(((XSSFCellStyle)cell.getCellStyle()).getFont());
            comment.setString(rt);
            cell.setCellComment(comment);
        }
    }
    
    public static final CellRangeAddress getCellMergedRegion(Sheet sheet, int row, int column)
    {
        int mergeCount = sheet.getNumMergedRegions();
        
        for(int i = 0; i < mergeCount; i++)
        {
            CellRangeAddress range    = sheet.getMergedRegion(i);
            int firstColumn            = range.getFirstColumn();
            int lastColumn            = range.getLastColumn();
            int firstRow            = range.getFirstRow();
            int lastRow                = range.getLastRow();
            
            if(row >= firstRow && row <= lastRow)
            {
                if(column >= firstColumn && column <= lastColumn)
                {
                    return range;
                }
            }
        }
        
        return null;
    }
    
    public static final CellRangeAddress addCellMergedRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol)
    {
        CellRangeAddress region = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        sheet.addMergedRegion(region);
        
        return region;
    }

    public static final CellRangeAddress addCellMergedRegionUnsafe(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol)
    {
        CellRangeAddress region = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        sheet.addMergedRegionUnsafe(region);
        
        return region;
    }
    
    public static final void setRegionBorder(Sheet sheet, CellRangeAddress region, BorderStyle border)
    {
        RegionUtil.setBorderTop(border, region, sheet);
        RegionUtil.setBorderLeft(border, region, sheet);
        RegionUtil.setBorderRight(border, region, sheet);
        RegionUtil.setBorderBottom(border, region, sheet);
    }
    
    public static final void autoSizeColumns(Sheet sheet, int ... cols)
    {
        for(int i = 0; i < cols.length; i++)
            sheet.autoSizeColumn(cols[i]);
    }

    public static final void autoSizeColumns(Sheet sheet, boolean useMergedCells, int ... cols)
    {
        for(int i = 0; i < cols.length; i++)
            sheet.autoSizeColumn(cols[i], useMergedCells);
    }

    public static final XSSFWorkbook openWorkbook(String filePath) throws IOException
    {
        return openWorkbook(new File(filePath));
    }

    public static final XSSFWorkbook openWorkbook(File file) throws IOException
    {
        InputStream is = null;
        
        try
        {
            is = new BufferedInputStream(new FileInputStream(file));
            return new XSSFWorkbook(is);
        }
        finally
        {
            if(is != null)
            {
                try
                {
                    is.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static final void saveWorkbook(Workbook wb, String filePath) throws IOException
    {
        saveWorkbook(wb, new File(filePath));
    }
    
    public static final void saveWorkbook(Workbook wb, File file) throws IOException
    {
        OutputStream os = null;
        
        try
        {
            os = new BufferedOutputStream(new FileOutputStream(file));
            wb.write(os);
        }
        finally
        {
            if(os != null)
            {
                try
                {
                    os.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
