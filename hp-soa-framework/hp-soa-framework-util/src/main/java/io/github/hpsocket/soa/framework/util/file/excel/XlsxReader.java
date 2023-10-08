package io.github.hpsocket.soa.framework.util.file.excel;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import lombok.Getter;

/** <b>Excel 文件读取器</b><br>
 * 逐行、快速处理 Excel 文件，读取事件通知给 {@linkplain XlsxReaderListener} 
 */
public class XlsxReader
{
    private enum CellDataType {BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER, DATE, NULL};
    
    @Getter
    private final XlsxReaderListener listener;
    
    public XlsxReader(XlsxReaderListener listener)
    {
        this.listener = listener;
    }
    
    public void parseAll(String filePath)
    {
        parse(filePath, -1, false, false);
    }
    
    public void parseAll(File file)
    {
        parse(file, -1, false, false);
    }
    
    public void parseAll(String filePath, boolean parseBlankRow)
    {
        parse(filePath, -1, parseBlankRow, false);
    }
    
    public void parseAll(File file, boolean parseBlankRow)
    {
        parse(file, -1, parseBlankRow, false);
    }
    
    public void parseAll(String filePath, boolean parseBlankRow, boolean nullAsEmpty)
    {
        parse(filePath, -1, parseBlankRow, nullAsEmpty);
    }
    
    public void parseAll(File file, boolean parseBlankRow, boolean nullAsEmpty)
    {
        parse(file, -1, parseBlankRow, nullAsEmpty);
    }
    
    public void parseOne(String filePath, int sheetIndex)
    {
        parse(filePath, sheetIndex, false, false);
    }
    
    public void parseOne(File file, int sheetIndex)
    {
        parse(file, sheetIndex, false, false);
    }
    
    public void parseOne(String filePath, int sheetIndex, boolean parseBlankRow)
    {
        parse(filePath, sheetIndex, parseBlankRow, false);
    }
    
    public void parseOne(File file, int sheetIndex, boolean parseBlankRow)
    {
        parse(file, sheetIndex, parseBlankRow, false);
    }
    
    public void parseOne(String filePath, int sheetIndex, boolean parseBlankRow, boolean nullAsEmpty)
    {
        parse(filePath, sheetIndex, parseBlankRow, nullAsEmpty);
    }
    
    public void parseOne(File file, int sheetIndex, boolean parseBlankRow, boolean nullAsEmpty)
    {
        parse(file, sheetIndex, parseBlankRow, nullAsEmpty);
    }
    
    private void parse(String filePath, int sheetIndex, boolean parseBlankRow, boolean nullAsEmpty)
    {
        parse(new File(filePath), sheetIndex, parseBlankRow, nullAsEmpty);
    }

    private void parse(File file, int sheetIndex, boolean parseBlankRow, boolean nullAsEmpty)
    {
        Parser parser = new Parser(file, sheetIndex, parseBlankRow, nullAsEmpty);
        parser.parse();
    }

    public class Parser extends DefaultHandler
    {
        private XSSFReader xss;
        private XMLReader reader;
        private SharedStrings sst;
        private StylesTable stt;
        
        private CellDataType dataType;  
        private DataFormatter formatter;  
      
        private short formatIndex;
        private String formatString; 
        
        private StringBuilder buffer;
        private List<String> datas;
        private List<String> blankDatas;

        private boolean hasVElement;
        private int hasContentCols;
        
        private int lastRowNumber;
        private int lastColNumber;
        
        @Getter        
        private int maxCols;        
        @Getter
        private int curSheetIndex;
        @Getter
        private int curRowNumber;
        @Getter
        private int curColNumber;
        
        @Getter
        private File file;
        @Getter
        private int sheetIndex;
        @Getter
        private boolean parseAllSheets;
        @Getter
        private boolean parseBlankRow;
        @Getter
        private boolean nullAsEmpty;
        
        private final String EMPTY;
        
        public Parser(File file, int sheetIndex, boolean parseBlankRow, boolean nullAsEmpty)
        {
            this.file           = file;
            this.sheetIndex     = sheetIndex < 0 ? -1 : sheetIndex;
            this.parseAllSheets = this.sheetIndex < 0;
            this.parseBlankRow  = parseBlankRow;
            this.nullAsEmpty    = nullAsEmpty;
            this.EMPTY          = nullAsEmpty ? null : "";
        }
        
        public void parse()
        {
            initParser(file);
            
            try
            {
                listener.onStartDocumnet(this);
                
                Iterator<InputStream> sheets = xss.getSheetsData();
                
                while(sheets.hasNext())
                {
                    ++curSheetIndex;
                    
                    curRowNumber    = 0;
                    curColNumber    = 0;
                    lastRowNumber   = 0;
                    lastColNumber   = 0;
                    maxCols         = 0;
                    hasContentCols  = 0;
                    blankDatas      = null;
                    
                    InputStream sheet = sheets.next();
                    
                    if(parseAllSheets || sheetIndex == curSheetIndex)
                    {
                        try
                        {
                            listener.onStartSheet(this, curSheetIndex);
                            InputSource source = new InputSource(sheet); 
                            reader.parse(source);
                        }
                        finally
                        {
                            listener.onEndSheet(this, curSheetIndex);
                        }
                        
                        if(!parseAllSheets)
                            break;
                    }
                }
            }
            catch(Exception e)
            {
                String msg = String.format("【%s - %d (%d, %d)】parse excel fail", file.getName(), curSheetIndex, curRowNumber, curColNumber);        
                throw new RuntimeException(msg, e);
            }
            finally
            {
                listener.onEndDocumnet(this);
            }
        }
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {
            if(qName.equals("v"))
                hasVElement = true;
            else if(qName.equals("c"))
            {
                curColNumber = covertCellIdtoInt(attributes.getValue("r"));
                int gap = curColNumber - lastColNumber - 1;
                
                for(int i = 0; i < gap; i++)
                    datas.add(EMPTY);
                
                setDataType(attributes);
            }
            else if(qName.equals("row"))
            {
                curRowNumber    = Integer.parseInt(attributes.getValue("r"));
                curColNumber    = 0;
                lastColNumber    = 0;
                datas            = new ArrayList<>(maxCols);
            }
            else if(qName.equals("dimension"))
            {
                String dimension = attributes.getValue("ref");
                maxCols = covertCellIdtoInt(dimension.substring(dimension.indexOf(":") + 1));
            }
        }
        
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException
        {
            if(qName.equals("v"))
            {
                String value = getDataValue(buffer.toString());
                
                datas.add(value);
            }
            else if(qName.equals("c"))
            {
                if(hasVElement)
                    hasVElement = false;
                else
                    datas.add(EMPTY);
                
                buffer.setLength(0);
                
                lastColNumber = curColNumber;
            }
            else if(qName.equals("row"))
            {
                if(parseBlankRow)
                {
                    int gap = curRowNumber - lastRowNumber - 1;
                    
                    if(gap > 0)
                    {
                        makeBlankDatas();
                        
                        for(int i = 0; i < gap; i++)
                            listener.onParseRow(this, curSheetIndex, lastRowNumber + i + 1, blankDatas);
                    }
                }
                
                if(parseBlankRow || hasContentCols > 0)
                {
                    int count = maxCols - datas.size();
                    
                    for(int i = 0; i < count; i++)
                        datas.add(EMPTY);                    
                    
                    listener.onParseRow(this, curSheetIndex, curRowNumber, datas);
                }
                
                datas.clear();
                
                lastRowNumber  = curRowNumber;
                hasContentCols = 0;
            }
        }

        private void makeBlankDatas()
        {
            if(blankDatas == null)
            {
                blankDatas = new ArrayList<>(maxCols);
                
                for(int i = 0; i < maxCols; i++)
                    blankDatas.add(EMPTY);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException
        {
            if(hasVElement)
                buffer.append(ch, start, length);
        }
        
        @Override
        public void error(SAXParseException e)
        {
            listener.onParseError(this, curSheetIndex, curRowNumber, e, false);
        }
        
        @Override
        public void fatalError(SAXParseException e)
        {
            listener.onParseError(this, curSheetIndex, curRowNumber, e, true);
        }
        
        private void initParser(File file)
        {
            this.curSheetIndex    = 0;
            this.curRowNumber    = 0;
            this.curColNumber    = 0;
            this.lastRowNumber    = 0;
            this.lastColNumber    = 0;
            this.maxCols        = 0;
            this.hasContentCols    = 0;
            this.blankDatas        = null;
            this.buffer            = new StringBuilder();
            this.formatter        = new DataFormatter();

            try
            {
                OPCPackage pkg = OPCPackage.open(file);
                
                xss        = new XSSFReader(pkg);
                sst        = xss.getSharedStringsTable();
                stt        = xss.getStylesTable();
                reader    = SAXParserFactory.newDefaultInstance().newSAXParser().getXMLReader();
                
                reader.setContentHandler(this);
            }
            catch(Exception e)
            {
                throw new RuntimeException("create excel parser fail", e);
            }
        }

        private int covertCellIdtoInt(String cellId)
        {
            StringBuilder sb = new StringBuilder();

            for(char c : cellId.toCharArray())
            {
                if(!Character.isAlphabetic(c))
                    break;

                sb.append(c);
            }
            
            int value      = 0;
            String colNum = sb.toString().toUpperCase();
            
            for(char c : colNum.toCharArray())
            {
                value = value * 26 + (c - 'A') + 1;
            }
            
            return value;
        }

        public void setDataType(Attributes attributes)
        {
            dataType     = CellDataType.NUMBER;
            formatIndex  = -1;
            formatString = null;
            
            String cellType        = attributes.getValue("t");
            String cellStyleStr    = attributes.getValue("s");

            if("b".equals(cellType))
                dataType = CellDataType.BOOL;
            else if("e".equals(cellType))
                dataType = CellDataType.ERROR;
            else if("inlineStr".equals(cellType))
                dataType = CellDataType.INLINESTR;
            else if("s".equals(cellType))
                dataType = CellDataType.SSTINDEX;
            else if("str".equals(cellType))
                dataType = CellDataType.FORMULA;

            if(cellStyleStr != null)
            {
                int styleIndex        = Integer.parseInt(cellStyleStr);
                XSSFCellStyle style    = stt.getStyleAt(styleIndex);
                
                formatIndex     = style.getDataFormat();
                formatString = style.getDataFormatString();

                if(formatString == null)
                {
                    dataType     = CellDataType.NULL;
                    formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
                }
                else if(formatString.contains("/yy") || formatString.contains("yy/") ||
                        formatString.contains("-yy") || formatString.contains("yy-"))
                {
                    dataType     = CellDataType.DATE;
                    formatString = "yyyy-MM-dd hh:mm:ss";
                }
            }
        }

        public String getDataValue(String value)
        {
            String rs;
            
            switch(dataType)
            {
            case BOOL:
                char first = value.charAt(0);
                rs = first == '0' ? "false" : "true";
                break;
            case ERROR:
                rs = EMPTY;
                break;
            case FORMULA:
                rs = value;
                break;
            case INLINESTR:
                rs = new XSSFRichTextString(value).toString();
                break;
            case SSTINDEX:
                int idx = Integer.parseInt(value);
                rs = sst.getItemAt(idx).toString();
                break;
            case NUMBER:
                if(formatString == null)
                    rs = value;
                else
                    rs = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString);
                break;
            case DATE:
                rs = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString);
                break;
            default:
                rs = EMPTY;
            }
            
            if(rs != null)
                rs = rs.trim();
            if(rs != null && !rs.isEmpty())
                ++hasContentCols;

            return rs;
        }

    }
}
