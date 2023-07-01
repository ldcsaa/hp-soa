package org.hpsocket.soa.framework.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static java.lang.System.out;

import static org.hpsocket.soa.framework.core.util.GeneralHelper.*;

public class CommonHelper
{
	public static final String DELIMITER_CHARS	= " ,\t\r\n\f";

	public enum AppState
	{
		STARTING, STARTED, STOPPING, STOPPED;
	}

	public static final String longList2Str(List<Long> list)
	{
		return list2Str(list);
	}
	
	public static final String intList2Str(List<Integer> list)
	{
		return list2Str(list);
	}
	
	public static final <T> String list2Str(List<T> list)
	{
		String str = null;
		
		if(list != null)
		{
			int size = list.size();
			
			if(size > 0)
			{
				StringBuilder sb = new StringBuilder();
				
				for(int i = 0; i < size; i++)
				{
					sb.append(list.get(i));
					if(i < size - 1)
						sb.append(',');
				}
				
				str = sb.toString();
			}
		}
		
		return str;
	}
	
	public static final List<Integer> str2IntList(String str)
	{
		return str2IntList(str, DELIMITER_CHARS);
	}
	
	public static final List<Integer> str2IntList(String str, String delimiters)
	{
		List<Integer> list = null;
		
		if(str != null)
		{
			StringTokenizer st = new StringTokenizer(str, delimiters);
			list = new ArrayList<Integer>(st.countTokens());
			
			while(st.hasMoreTokens())
				list.add(Integer.parseInt(st.nextToken()));
		}

		return list != null ? list : new ArrayList<Integer>();
	}
	
	public static final List<Long> str2LongList(String str)
	{
		return str2LongList(str, DELIMITER_CHARS);
	}
	
	public static final List<Long> str2LongList(String str, String delimiters)
	{
		List<Long> list = null;
		
		if(str != null)
		{
			StringTokenizer st = new StringTokenizer(str, delimiters);
			list = new ArrayList<Long>(st.countTokens());
			
			while(st.hasMoreTokens())
				list.add(Long.parseLong(st.nextToken()));
		}

		return list != null ? list : new ArrayList<Long>();
	}
	
	public static final List<String> str2StrList(String str)
	{
		return str2StrList(str, DELIMITER_CHARS);
	}
	
	public static final List<String> str2StrList(String str, String delimiters)
	{
		List<String> list = null;
		
		if(str != null)
		{
			StringTokenizer st = new StringTokenizer(str, delimiters);
			list = new ArrayList<String>(st.countTokens());
			
			while(st.hasMoreTokens())
				list.add(st.nextToken());
		}

		return list != null ? list : new ArrayList<String>();
	}
	
	public static final Integer str2IntRounding(String str)
	{
		Double v = str2Double(str);
		
		if(v == null)
			return null;
		
		return (int)(v + 0.5D);
	}
	
	public static final int str2IntRounding(String str, int def)
	{
		Integer v = str2IntRounding(str);
		
		if(v == null)
			return def;
		
		return v.intValue();
	}
	
	public static final int str2IntRounding_0(String str)
	{
		return str2IntRounding(str, 0);
	}
	
	public static final boolean isNullOrEmpty(Collection<?> c)
	{
		return c == null || c.isEmpty();
	}
	
	public static final boolean isNotNullOrEmpty(Collection<?> c)
	{
		return !isNullOrEmpty(c);
	}
	
	public static final boolean isNullOrEmpty(Map<?, ?> m)
	{
		return m == null || m.isEmpty();
	}
	
	public static final boolean isNotNullOrEmpty(Map<?, ?> m)
	{
		return !isNullOrEmpty(m);
	}
	
	public static final int compare(Number n, byte v)
	{
		if(n == null)
			return -1;
		
		return n.byteValue() - v;
	}
	
	public static final boolean equals(Number n, byte v)
	{
		return compare(n, v) == 0;
	}
	
	public static final int compare(Number n, short v)
	{
		if(n == null)
			return -1;
		
		return n.shortValue() - v;
	}
	
	public static final boolean equals(Number n, short v)
	{
		return compare(n, v) == 0;
	}
	
	public static final int compare(Number n, int v)
	{
		if(n == null)
			return -1;
		
		return n.intValue() - v;
	}
	
	public static final boolean equals(Number n, int v)
	{
		return compare(n, v) == 0;
	}
	
	public static final long compare(Number n, long v)
	{
		if(n == null)
			return -1;
		
		return n.longValue() - v;
	}
	
	public static final boolean equals(Number n, long v)
	{
		return compare(n, v) == 0;
	}

	public static final String formatNumber(Number n)
	{
		if(n == null)
			return "";
		
		long lv = n.longValue();
		double dv = (double)lv;
		
		if(n.doubleValue() == dv)
			return Long.toString(lv);
		
		return n.toString();
	}
	
	public static final int roundInt(Number n)
	{
		return (int)(double)(n.doubleValue() + 0.5D);
	}
	
	public static final <K> Integer addMapValue(Map<K, Integer> map, K key, int value)
	{
		Integer v = map.get(key);
		
		if(v == null)
			return map.put(key, value);
		else
			return map.put(key, v + value);
	}
	
	public static final void copyFile(String src, String dest) throws IOException
	{
		copyFile(new File(src), new File(dest));
	}
	
	public static final void copyFile(File src, File dest) throws IOException
	{
		FileInputStream fi	= null;
		FileOutputStream fo	= null;
		FileChannel in		= null;
		FileChannel out		= null;

		try
		{
			fi	= new FileInputStream(src);
			fo	= new FileOutputStream(dest);
			in	= fi.getChannel();
			out	= fo.getChannel();
			
			in.transferTo(0, in.size(), out);
		}
		finally
		{
			try
			{
				if(fi != null) fi.close();
				if(in != null) in.close();
				if(fo != null) fo.close();
				if(out != null) out.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static final boolean deleteFile(String filePath)
	{
		File outputFile = new File(filePath);
		
		if(!outputFile.isFile())
			return false;
		
		if(!outputFile.delete())
			throw new RuntimeException(String.format("删除文件失败（可能已被其它应用程序打开）：'%s'", filePath));
		
		return true;
	}
	
	public static void printStep(int i, boolean start)
	{
		if(start)
			printlnf("STEP #%d ->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->", i);
		else
			printlnf("STEP #%d <-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-", i);
	}
	
	public static void printlnf(String format, Object ... params)
	{
		StringBuilder sb = new StringBuilder(String.format("[%tT.%<tL] ", new Date()));
		sb.append(format).append('\n');
		
		out.printf(sb.toString(), params);
	}
	
	public static void printf(String format, Object ... params)
	{
		StringBuilder sb = new StringBuilder(String.format("[%tT.%<tL] ", new Date()));
		sb.append(format);
	
		out.printf(sb.toString(), params);
	}
	
}
