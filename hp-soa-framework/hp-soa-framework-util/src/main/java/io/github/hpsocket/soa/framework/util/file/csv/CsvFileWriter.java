package io.github.hpsocket.soa.framework.util.file.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/** <b>CSV 文件写入器</b> */
public class CsvFileWriter extends BufferedWriter
{
    public static final char SEPARATOR          = ',';
    public static final String UTF8_CHARSET     = "UTF-8";
    public static final String GB18030_CHARSET  = "GB18030";
    public static final String DEFAULT_CHARSET  = UTF8_CHARSET;
    
    public CsvFileWriter(String fileName) throws IOException
    {
        this(fileName, false);
    }
    
    public CsvFileWriter(String fileName, boolean append) throws IOException
    {
        this(new File(fileName), append);
    }
    
    public CsvFileWriter(File file) throws IOException
    {
        this(file, false);
    }
    
    public CsvFileWriter(File file, boolean append) throws IOException
    {
        this(file, DEFAULT_CHARSET, append);
    }
    
    public CsvFileWriter(String fileName, String charset) throws IOException
    {
        this(new File(fileName), charset, false);
    }
    
    public CsvFileWriter(File file, String charset) throws IOException
    {
        this(file, charset, false);
    }
    
    public CsvFileWriter(File file, String charset, boolean append) throws IOException
    {
        super(new OutputStreamWriter(new FileOutputStream(file, append), charset));
    }
    
    public void writeSeparator() throws IOException
    {
        append(SEPARATOR);
    }
    
    public void safeWrite(Object obj) throws IOException
    {
        if(obj != null)
        {
            String str = obj.toString().replaceAll("\"", "\"\"");
            append('\"').append(str).append('\"');
        }
    }
    
    public void writeWithSeparator(Object obj) throws IOException
    {
        if(obj != null)
            append(obj.toString());

        append(SEPARATOR);
    }
    
    public void safeWriteWithSeparator(Object obj) throws IOException
    {
        safeWrite(obj);
        append(SEPARATOR);
    }
    
    public void writeWithNewLine(Object obj) throws IOException
    {
        if(obj != null)
            append(obj.toString());

        newLine();
    }
    
    public void safeWriteWithNewLine(Object obj) throws IOException
    {
        safeWrite(obj);
        newLine();
    }
    
    public void writeLine(Object ... value) throws IOException
    {
        for(int i = 0; i < value.length; i++)
        {
            Object obj = value[i];
            
            if(obj != null)
                append(obj.toString());
            
            if(i < value.length - 1)
                append(SEPARATOR);
            else
                newLine();
        }
    }
        
    public void safeWriteLine(Object ... value) throws IOException
    {
        for(int i = 0; i < value.length; i++)
        {
            Object obj = value[i];
            
            safeWrite(obj);
            
            if(i < value.length - 1)
                append(SEPARATOR);
            else
                newLine();
        }
    }
        
}
