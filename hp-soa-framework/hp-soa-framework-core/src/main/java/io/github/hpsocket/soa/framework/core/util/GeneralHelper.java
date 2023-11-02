package io.github.hpsocket.soa.framework.core.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/** <b>通用方法帮助类</b> */
public class GeneralHelper
{
    private static final String DELIMITERR_CHARS    = " ,\t\r\n\f";
    

    private static final Pattern PATTERN_NUMERIC    = Pattern.compile("^0$|^\\-?[1-9]+[0-9]*$");
    private static final Pattern PATTERN_EMAIL_ADDR = Pattern.compile("^[a-z0-9_\\-]+(\\.[_a-z0-9\\-]+)*@([_a-z0-9\\-]+\\.)+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel)$");
    private static final Pattern PATTERN_IP_ADDR    = Pattern.compile("^([1-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(\\.([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])){3}$");
    private static final Pattern PATTERN_LINK       = Pattern.compile("<a[^>]*href=\\\"[^\\s\\\"]+\\\"[^>]*>[^<]*<\\/a>");
    private static final Pattern PATTERN_HTTP_URL   = Pattern.compile("^(https?:\\/\\/)?([a-z]([a-z0-9\\-]*\\.)+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel)|(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))(\\/[a-z0-9_\\-\\.~]+)*(\\/([a-z0-9_\\-\\.]*)(\\?[a-z0-9+_\\-\\.%=&amp;]*)?)?(#[a-z][a-z0-9_]*)?$");
    private static final Pattern PATTERN_XML_ESCAPES= Pattern.compile(".*[&|\"|\'|<|>].*");
    
    private static final String[] SHORT_DATE_PATTERN                = {"yyyy-MM-dd", "yyyy/MM/dd", "yyyy\\MM\\dd", "yyyyMMdd"};
    private static final String[] LONG_DATE_PATTERN                 = {"yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyy\\MM\\dd HH:mm:ss", "yyyyMMddHHmmss"};
    private static final String[] LONG_DATE_PATTERN_WITH_MILSEC     = {"yyyy-MM-dd HH:mm:ss.SSS", "yyyy/MM/dd HH:mm:ss.SSS", "yyyy\\MM\\dd HH:mm:ss.SSS", "yyyyMMddHHmmssSSS"};

    private static final Map<String, Locale> AVAILABLE_LOCALES      = new HashMap<String, Locale>();
    private static final char[][] XML_ESCAPE_CHARS                  = new char[63][];
    
    /** 空字符串 */
    public static final String EMPTY_STRING         = "";
    /** 默认字符编码 */
    public static final String DEFAULT_ENCODING     = "UTF-8";
    /** 当前操作系统平台 */
    public static final String OS_PLATFORM          = getOSName();
    /** 当前操作系统平台是否为 Windows */
    public static final boolean IS_WINDOWS_PLATFORM = isWindowsPlatform();
    /** 当前操作系统平台的换行符 */
    public static final String NEWLINE_CHAR         = IS_WINDOWS_PLATFORM ? "\r\n" : "\n";
    
    static
    {
        Locale[] locales = Locale.getAvailableLocales();
        for(Locale locale : locales)
            AVAILABLE_LOCALES.put(locale.toString(), locale);
        
        XML_ESCAPE_CHARS[38] = "&amp;"  .toCharArray();
        XML_ESCAPE_CHARS[60] = "&lt;"   .toCharArray();
        XML_ESCAPE_CHARS[62] = "&gt;"   .toCharArray();
        XML_ESCAPE_CHARS[34] = "&quot;" .toCharArray();
        XML_ESCAPE_CHARS[39] = "&apos;" .toCharArray();
    }
    
    /** 字符串转换为字节数组 */
    public static byte[] strGetBytes(String content, String charset)
    {
        try
        {
            return content.getBytes(charset);
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /** 字节数组转换为字符串 */
    public static String strFromBytes(byte[] content, String charset)
    {
        try
        {
            return new String(content, charset);
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /** 获取系统支持的所有 {@link Locale} */
    public final static Map<String, Locale> getAvailableLocales()
    {
        return AVAILABLE_LOCALES;
    }
    
    /** 获取系统支持的指定名称的 {@link Locale} */
    public final static Locale getAvailableLocale(String locale)
    {
        return AVAILABLE_LOCALES.get(locale);
    }
    
    /** 检查字符串不为 null 或空字符串 */
    public final static boolean isStrNotEmpty(String str)
    {
        return str != null && str.length() != 0;
    }

    /** 检查字符串不为 null 、空字符串或只包含空格 */
    public final static boolean isTrimStrNotEmpty(String str)
    {
        boolean result = isStrNotEmpty(str);
        return result ? isStrNotEmpty(str.trim()) : result;
    }

    /** 检查字符串为 null 或空字符串 */
    public final static boolean isStrEmpty(String str)
    {
        return !isStrNotEmpty(str);
    }

    /** 检查字符串为 null 、空字符串或只包含空格 */
    public final static boolean isTrimStrEmpty(String str)
    {
        boolean result = isStrEmpty(str);
        return result ?  result : isStrEmpty(str.trim());
    }

    /** 把参数 str 转换为安全字符串：如果 str = null，则把它转换为空字符串 */
    public final static String safeString(String str)
    {
        if(str == null)
            str = "";
        
        return str;
    }

    /** 把参数 obj 转换为安全字符串：如果 obj = null，则把它转换为空字符串 */
    public final static String safeString(Object obj)
    {
        if(obj == null)
            return "";
        
        return obj.toString();
    }

    /** 把参数 str 转换为安全字符串并执行去除前后空格：如果 str = null，则把它转换为空字符串 */
    public final static String safeTrimString(String str)
    {
        return safeString(str).trim();
    }

    /** 消除字符串 str 左侧的子字符串 word */
    public static final String trimLeft(String str, String word)
    {
        if(word != null && word.length() > 0)
        {
            int index    = -1;
            int length    = word.length();
            
            int i = 0;
            while(str.indexOf(word, i) == i)
            {
                index = i;
                i += length;
            }
            
            if(index != -1)
            {
                index += length;
                str = str.substring(index);
            }
        }
        
        return str;
    }
    
    /** 消除字符串 str 右侧的子字符串 word */
    public static final String trimRight(String str, String word)
    {
        if(word != null && word.length() > 0)
        {
            int index    = -1;
            int length    = word.length();
            
            int j = -1;
            int i = str.length() - 1;
            while((j = i - (length - 1)) >= 0 && str.lastIndexOf(word, i) == j)
            {
                index = i;
                i -= length;
            }
            
            if(index != -1)
            {
                index -= (length - 1);
                str = str.substring(0, index);
            }
        }
        
        return str;
    }
    
    /** 消除字符串 str 左右两侧的子字符串 word */
    public static final String trim(String str, String word)
    {
        str = trimLeft(str, word);
        str = trimRight(str, word);
        
        return str;
    }

    /** 消除字符串 str 左右两侧的指定字符 */
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

    /** 检查字符串是否符合整数格式 */
    public final static boolean isStrNumeric(String str)
    {
        return PATTERN_NUMERIC.matcher(safeString(str)).matches();
    }

    /** 检查字符串是否符合电子邮件格式 */
    public final static boolean isStrEmailAddress(String str)
    {
        return PATTERN_EMAIL_ADDR.matcher(safeString(str)).matches();
    }

    /** 检查字符串是否符合 IP 地址格式 */
    public final static boolean isStrIPAddress(String str)
    {
        return PATTERN_IP_ADDR.matcher(safeString(str)).matches();
    }

    /** 检查字符串是否符合 HTML 超链接元素格式 */
    public final static boolean isStrLink(String str)
    {
        return PATTERN_LINK.matcher(safeString(str)).matches();
    }

    /** 检查字符串是否符合 URL 格式 */
    public final static boolean isStrURL(String str)
    {
        return PATTERN_HTTP_URL.matcher(safeString(str)).matches();
    }
    
    /** 置换常见的 XML 特殊字符 */
    public final static String escapeXML(String str)
    {
        if(!PATTERN_XML_ESCAPES.matcher(str).matches())
            return str;
        
        char[] src       = str.toCharArray();
        StringBuilder sb = new StringBuilder(src.length);
        
        for(char c : src)
        {
            if(c > '>' || c < '"')
                sb.append(c);
            else
            {
                char[] dest = XML_ESCAPE_CHARS[c];
                
                if(dest == null)
                    sb.append(c);
                else
                    sb.append(dest);
            }
        }
        
        return sb.toString();
    }
    
    /** 屏蔽正则表达式的转义字符（但不屏蔽 ignores 参数中包含的字符） */
    public static final String escapeRegexChars(String str, char ... ignores)
    {
        final char ESCAPE_CHAR   = '\\';
        final char[] REGEX_CHARS = {'.', ',', '?', '+', '-', '*', '^', '$', '|', '&', '{', '}', '[', ']', '(', ')', '\\'};
        
        char[] regex_chars = REGEX_CHARS;
        
        if(ignores.length > 0)
        {
            Set<Character> cs = new HashSet<Character>(REGEX_CHARS.length);
            
            for(int i = 0; i < REGEX_CHARS.length; i++)
                cs.add(REGEX_CHARS[i]);
            for(int i = 0; i < ignores.length; i++)
                cs.remove(ignores[i]);
                
            int i        = 0;
            regex_chars = new char[cs.size()];
            Iterator<Character> it = cs.iterator();
            
            while(it.hasNext())
                regex_chars[i++] = it.next();                
        }
        
        StringBuilder sb = new StringBuilder();
        
        for(int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);
            
            for(int j = 0; j < regex_chars.length; j++)
            {
                if(c == regex_chars[j])
                {
                    sb.append(ESCAPE_CHAR);
                    break;
                }
            }
            
            sb.append(c);
        }
        
        return sb.toString();
    }

    /** 符分割字符串（分割符：" \t\n\r\f,;"） */
    public final static String[] splitStr(String str)
    {
        return splitStr(str, " \t\n\r\f,;");
    }
    
    /** 符分割字符串（分割符：由 delim 参数指定） */
    public final static String[] splitStr(String str, String delim)
    {
        StringTokenizer st    = new StringTokenizer(str, delim);
        String[] array        = new String[st.countTokens()];
        
        int i = 0;
        while(st.hasMoreTokens())
            array[i++] = st.nextToken();
        
        return array;
    }

    /** 调用 {@linkplain Thread#sleep(long)} 方法使当前线程睡眠 period 毫秒 <br>
     * 
     * 如果 {@linkplain Thread#sleep(long)} 方法被中断则返回 false
     * 
     */
    public final static boolean waitFor(long period)
    {
        if(period > 0)
        {
            try
            {
                Thread.sleep(period);
            }
            catch(Exception e)
            {
                return false;
            }
        }
        else
            Thread.yield();
        
        return true;
    }

    /** 参考：{@linkplain GeneralHelper#waitFor(long) waitFor(long)} */
    public final static boolean waitFor(long period, TimeUnit unit)
    {
        return waitFor(unit.toMillis(period));
    }

    /** 参考：{@linkplain GeneralHelper#waitFor(long) waitFor(long)} */
    public final static boolean waitFor(Duration duration)
    {
        return waitFor(duration.toMillis());
    }

    /** String -> Integer，如果转换不成功则返回 null */
    public final static Integer str2Int(String s)
    {
        Integer returnVal;
        try {
            returnVal = Integer.decode(safeTrimString(s));
        } catch(Exception e) {
            returnVal = null;
        }
        return returnVal;
    }

    /** String -> int，如果转换不成功则返回默认值 d */
    public final static int str2Int(String s, int d)
    {
        int returnVal;
        try {
            returnVal = Integer.parseInt(safeTrimString(s));
        } catch(Exception e) {
            returnVal = d;
        }
        return returnVal;
    }

    /** String -> int，如果转换不成功则返回 0 */
    public final static int str2Int_0(String s)
    {
        return str2Int(s, 0);
    }

    /** String -> Short，如果转换不成功则返回 null */
    public final static Short str2Short(String s)
    {
        Short returnVal;
        try {
            returnVal = Short.decode(safeTrimString(s));
        } catch(Exception e) {
            returnVal = null;
        }
        return returnVal;
    }

    /** String -> short，如果转换不成功则返回默认值 d */
    public final static short str2Short(String s, short d)
    {
        short returnVal;
        try {
            returnVal = Short.parseShort(safeTrimString(s));
        } catch(Exception e) {
            returnVal = d;
        }
        return returnVal;
    }

    /** String -> short，如果转换不成功则返回 0 */
    public final static short str2Short_0(String s)
    {
        return str2Short(s, (short)0);
    }

    /** String -> Long，如果转换不成功则返回 null */
    public final static Long str2Long(String s)
    {
        Long returnVal;
        try {
            returnVal = Long.decode(safeTrimString(s));
        } catch(Exception e) {
            returnVal = null;
        }
        return returnVal;
    }

    /** String -> long，如果转换不成功则返回默认值 d */
    public final static long str2Long(String s, long d)
    {
        long returnVal;
        try {
            returnVal = Long.parseLong(safeTrimString(s));
        } catch(Exception e) {
            returnVal = d;
        }
        return returnVal;
    }

    /** String -> long，如果转换不成功则返回 0 */
    public final static long str2Long_0(String s)
    {
        return str2Long(s, 0L);
    }

    /** String -> Float，如果转换不成功则返回 null */
    public final static Float str2Float(String s)
    {
        Float returnVal;
        try {
            returnVal = Float.valueOf(safeTrimString(s));
        } catch(Exception e) {
            returnVal = null;
        }
        return returnVal;
    }

    /** String -> float，如果转换不成功则返回默认值 d */
    public final static float str2Float(String s, float d)
    {
        float returnVal;
        try {
            returnVal = Float.parseFloat(safeTrimString(s));
        } catch(Exception e) {
            returnVal = d;
        }
        return returnVal;
    }

    /** String -> float，如果转换不成功则返回 0 */
    public final static float str2Float_0(String s)
    {
        return str2Float(s, 0F);
    }

    /** String -> Double，如果转换不成功则返回 null */
    public final static Double str2Double(String s)
    {
        Double returnVal;
        try {
            returnVal = Double.valueOf(safeTrimString(s));
        } catch(Exception e) {
            returnVal = null;
        }
        return returnVal;
    }

    /** String -> double，如果转换不成功则返回默认值 d */
    public final static double str2Double(String s, double d)
    {
        double returnVal;
        try {
            returnVal = Double.parseDouble(safeTrimString(s));
        } catch(Exception e) {
            returnVal = d;
        }
        return returnVal;
    }

    /** String -> double，如果转换不成功则返回 0.0 */
    public final static double str2Double_0(String s)
    {
        return str2Double(s, 0D);
    }

    /** String -> Byte，如果转换不成功则返回 null */
    public final static Byte str2Byte(String s)
    {
        Byte returnVal;
        try {
            returnVal = Byte.decode(safeTrimString(s));
        } catch(Exception e) {
            returnVal = null;
        }
        return returnVal;
    }

    /** String -> byte，如果转换不成功则返回默认值 d */
    public final static byte str2Byte(String s, byte d)
    {
        byte returnVal;
        try {
            returnVal = Byte.parseByte(safeTrimString(s));
        } catch(Exception e) {
            returnVal = d;
        }
        return returnVal;
    }

    /** String -> byte，如果转换不成功则返回 0 */
    public final static byte str2Byte_0(String s)
    {
        return str2Byte(s, (byte)0);
    }

    /** String -> Character，如果转换不成功则返回 null */
    public final static Character str2Char(String s)
    {
        Character returnVal;
        try {
            returnVal = safeTrimString(s).charAt(0);
        } catch(Exception e) {
            returnVal = null;
        }
        return returnVal;
    }

    /** String -> char，如果转换不成功则返回默认值 d */
    public final static char str2Char(String s, char d)
    {
        char returnVal;
        try {
            returnVal = safeTrimString(s).charAt(0);
        } catch(Exception e) {
            returnVal = d;
        }
        return returnVal;
    }

    /** String -> char，如果转换不成功则返回 0 */
    public final static char str2Char_0(String s)
    {
        return str2Char(s, Character.MIN_VALUE);
    }

    /** String -> Boolean，如果转换不成功则返回 null */
    public final static Boolean str2Boolean(String s)
    {
        return Boolean.valueOf(safeTrimString(s));
    }

    /** String -> boolean，如果转换不成功则返回默认值 d */
    public final static boolean str2Boolean(String s, boolean d)
    {
        s = safeTrimString(s);
        
        if(s.equalsIgnoreCase("true"))
            return true;
        else if(s.equalsIgnoreCase("false"))
            return false;
        
        return d;
    }

    /** String -> boolean，如果转换不成功则返回 0 */
    public final static boolean str2Boolean_False(String s)
    {
        return str2Boolean(s, false);
    }

    /** String -> java.util.Date， str 的格式由 format  定义 */
    public final static Date str2Date(String str, String format)
    {
        Date date = null;
        
        try
        {
            DateFormat df = new SimpleDateFormat(format);
            date = df.parse(safeTrimString(str));
        }
        catch(Exception e)
        {

        }

        return date;
    }

    /** String -> java.util.Date，由函数自身判断 str 的格式 */
    public final static Date str2Date(String str)
    {
        Date date = null;

        try
        {
            final char SEPARATOR    = '-';
            final String[] PATTERN  = {"yyyy", "MM", "dd", "HH", "mm", "ss", "SSS"};
            String[] values         = safeTrimString(str).split("\\D");
            String[] element        = new String[values.length];
            
            int length = 0;
            for(String e : values)
            {
                e = e.trim();
                if(e.length() != 0)
                {
                    element[length++] = e;
                    if(length == PATTERN.length)
                        break;
                }
            }

            if(length > 0)
            {
                StringBuilder value = new StringBuilder();

                if(length > 1)
                {
                    for(int i = 0; i < length; ++i)
                    {
                        value.append(element[i]);
                        value.append(SEPARATOR);
                    }
                }
                else
                {
                    String src  = element[0];
                    int remain  = src.length();
                    int pos     = 0;
                    int i       = 0;

                    for(i = 0; remain > 0 && i < PATTERN.length; ++i)
                    {
                        int p_length    = PATTERN[i].length();
                        int v_length    = Math.min(p_length, remain);
                        String v        = src.substring(pos, pos + v_length);
                        pos            += v_length;
                        remain         -= v_length;

                        value.append(v);
                        value.append(SEPARATOR);
                    }

                    length = i;
                }

                 StringBuilder format = new StringBuilder();

                 for(int i = 0; i < length; ++i)
                {
                    format.append(PATTERN[i]);
                    format.append(SEPARATOR);
                }

                date = str2Date(value.toString(), format.toString());
            }
        }
        catch(Exception e)
        {

        }

        return date;
    }
    
    /** String -> java.util.Date，由 Patterns 指定可能的日期格式 */
    public final static Date str2Date(String str, String[] Patterns)
    {
        Date date = null;
        
        for(int i = 0; i < Patterns.length; ++i)
        {
            date = str2Date(str, Patterns[i]);

            if( date != null)
                break;
        }

        return date;
    }
    
    /** String -> java.util.Date，由 GeneralHelper.SHORT_DATE_PATTERN 指定可能的日期格式 */
    public final static Date str2ShortDate(String str)
    {
        return str2Date(str, SHORT_DATE_PATTERN);
    }

    /** String -> java.util.Date，由 GeneralHelper.LONG_DATE_PATTERN 指定可能的日期格式 */
    public final static Date str2LongDate(String str)
    {
        return str2Date(str, LONG_DATE_PATTERN);
    }

    /** String -> java.util.Date，由 GeneralHelper.LONG_DATE_PATTERN_WITH_MILSEC 指定可能的日期格式 */
    public final static Date str2LongDateWithMilliSecond(String str)
    {
        return str2Date(str, LONG_DATE_PATTERN_WITH_MILSEC);
    }
    
    /** 类型转换处理器接口 */
    public static interface TypeHandler<T>
    {
        T handle(String v);
    }
    
    /** String -> Any，字符串转换为 8 种基础数据类型、及其包装类 {@link Date}、 或 {@link String} 
     * 
     * @param type    : 目标类型的 {@link Class} 对象
     * @param v       : 要转换的字符串
     * @return        : 转换结果，如果转换不成功返回 null
     * @throws        : 如果目标类型不支持抛出 {@link IllegalArgumentException}
     * 
     */
    public static final <T> T str2Object(Class<T> type, String v)
    {
        return str2Object(type, v, null);
    }
    
    /** String -> Any，如果 handler 为 null 则把字符串转换为 8 种基础数据类型、及其包装类、 {@link Date} 或 {@link String}，
     *                   如果 handler 不为 null 则由 handler 执行转换 
     * 
     * @param type     : 目标类型的 {@link Class} 对象
     * @param v        : 要转换的字符串
     * @param handler  : 类型转换处理器
     * @return         : 转换结果，如果转换不成功返回 null
     * @throws         : 如果目标类型不支持抛出 {@link IllegalArgumentException}
     * 
     */
    @SuppressWarnings("unchecked")
    public static final <T> T str2Object(Class<T> type, String v, TypeHandler<T> handler)
    {
        Object param = null;
        
        if(handler != null)
            return handler.handle(v);
        
        if(type == String.class)
            param =  safeTrimString(v);
        else if(type == int.class)
            param =  str2Int_0(v);
        else if(type == long.class)
            param =  str2Long_0(v);
        else if(type == byte.class)
            param =  str2Byte_0(v);
        else if(type == char.class)
            param =  str2Char_0(v);
        else if(type == float.class)
            param =  str2Float_0(v);
        else if(type == double.class)
            param =  str2Double_0(v);
        else if(type == short.class)
            param =  str2Short_0(v);
        else if(type == boolean.class)
            param =  str2Boolean_False(v);
        else if(type == Integer.class)
            param =  str2Int(v);
        else if(type == Long.class)
            param =  str2Long(v);
        else if(type == Byte.class)
            param =  str2Byte(v);
        else if(type == Character.class)
            param =  str2Char(v);
        else if(type == Float.class)
            param =  str2Float(v);
        else if(type == Double.class)
            param =  str2Double(v);
        else if(type == Short.class)
            param =  str2Short(v);
        else if(type == Boolean.class)
            param =  str2Boolean(v);
        else if(Date.class.isAssignableFrom(type))
            param =  str2Date(v);
        else
            throw new IllegalArgumentException(String.format("object type '%s' not valid", type));
        
        return (T)param;
    }

    /** Any -> Object[] <br>
     * 
     *  obj == null                 : 返回 Object[] {null} <br>
     *  obj 为对象数组                : 强制转换为 Object[], 并返回自身 <br>
     *  obj 为基础类型数组             : 返回 Object[], 其元素类型为基础类型的包装类 <br>
     *  obj 为 {@link Collection}   : 通过 toArray() 方法返回 Object[] <br>
     *  obj 为 {@link Iterable}     : 遍历 {@link Iterable}, 并返回包含其所有元素的 Object[] <br>
     *  obj 为 {@link Iterator}     : 遍历 {@link Iterator}, 并返回包含其所有元素的 Object[] <br>
     *  obj 为 {@link Enumeration}  : 遍历 {@link Enumeration}, 并返回包含其所有元素的 Object[] <br>
     *  obj 为普通对象                : 返回 Object[] {obj} <br>
     * 
     * @param obj    : 任何对象
     * 
     */
    public static final Object[] object2Array(Object obj)
    {
        Object[] array;
        
        if(obj == null)
            array = new Object[] {obj};
        else if(obj.getClass().isArray())
        {
            Class<?> clazz = obj.getClass().getComponentType();
            
            if(Object.class.isAssignableFrom(clazz))
                array = (Object[])obj;
            else
            {
                int length = Array.getLength(obj);
                
                if(length > 0)
                {
                    array = new Object[length];
                    
                    for(int i = 0; i < length; i++)
                        array[i] = Array.get(obj, i);
                }
                else
                    array = new Object[0];
            }
        }
        else if(obj instanceof Collection<?>)
            array = ((Collection<?>)obj).toArray();
        else if(obj instanceof Iterable<?>)
        {
            List<Object> list = new ArrayList<Object>();
            Iterator<?> it = ((Iterable<?>)obj).iterator();
            
            while(it.hasNext())
                list.add(it.next());
            
            array = list.toArray();
        }
        else if(obj instanceof Iterator)
        {
            List<Object> list = new ArrayList<Object>();
            Iterator<?> it = (Iterator<?>)obj;
            
            while(it.hasNext())
                list.add(it.next());
            
            array = list.toArray();
        }
        else if(obj instanceof Enumeration<?>)
        {
            List<Object> list = new ArrayList<Object>();
            Enumeration<?> it = (Enumeration<?>)obj;
            
            while(it.hasMoreElements())
                list.add(it.nextElement());
            
            array = list.toArray();
        }
        else
            array = new Object[] {obj};
        
        return array;
    }

    /** 返回 date 加上 value 天后的日期（清除时间信息） */
    public final static Date addDate(Date date, int value)
    {
        return addDate(date, value, true);
    }

    /** 返回 date 加上 value 天后的日期，trimTime 指定是否清除时间信息 */
    public final static Date addDate(Date date, int value, boolean trimTime)
    {
        return addTime(date, Calendar.DATE, value, trimTime);

    }

    /** 返回 date 加上 value 个 field 时间单元后的日期（不清除时间信息） */
    public final static Date addTime(Date date, int field, int value)
    {
        return addTime(date, field, value, false);
    }

    /** 返回 date 加上 value 个 field 时间单元后的日期，trimTime 指定是否去除时间信息 */
    public final static Date addTime(Date date, int field, int value, boolean trimTime)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(field, value);

        if(trimTime)
        {
            c.set(Calendar.HOUR, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        }

        return c.getTime();

    }

    /** java.util.Date -> String，str 的格式由 format  定义 */
    public final static String date2Str(Date date, String format)
    {
        DateFormat df    = new SimpleDateFormat(format);
        return df.format(date);
    }

    /** 修整 SQL 语句字符串：' -> '，（includeWidlcard 指定是否对星号和问号作转换：* -> %, ? -> _） */
    public static final String regularSQLStr(String str, boolean includeWidlcard)
    {
        str = str.replace("'", "''");

        if(includeWidlcard)
        {
            str = str.replace('*', '%');
            str = str.replace('?', '_');
        }

        return str;
    }
    
    /** 获取 clazz 的 {@link ClassLoader} 对象，如果为 null 则返回当前线程的 Context {@link ClassLoader} */
    public static final ClassLoader getClassLoader(Class<?> clazz)
    {
        ClassLoader loader = clazz.getClassLoader();
        
        if(loader == null)
            loader = Thread.currentThread().getContextClassLoader();
        
        return loader;
    }
    
    /** 加载类名为  className 的 {@link Class} 对象，如果加载失败则返回 null */
    public static final Class<?> loadClass(String className)
    {
        Class<?> clazz     = null;
        ClassLoader loader = getClassLoader(GeneralHelper.class);
        
        try
        {
            clazz = loader.loadClass(className);
        }
        catch(ClassNotFoundException e)
        {
            
        }
        
        return clazz;
    }

    /** 用 {@linkplain Class#forName(String)} 加载 {@link Class} 对象，如果加载失败则返回 null */
    public static final Class<?> classForName(String name)
    {
        Class<?> clazz = null;
        
        try
        {
            clazz = Class.forName(name);
        }
        catch(ClassNotFoundException e)
        {
            
        }
        
        return clazz;
    }

    /** 用 {@linkplain Class#forName(String, boolean, ClassLoader)} 加载 {@link Class} 对象，如果加载失败则返回 null */
    public static final Class<?> classForName(String name, boolean initialize, ClassLoader loader)
    {
        Class<?> clazz = null;
        
        try
        {
            clazz = Class.forName(name, initialize, loader);
        }
        catch(ClassNotFoundException e)
        {
            
        }
        
        return clazz;
    }

    /** 获取 clazz 资源环境中 resPath 相对路径的 URL 对象 */
    public static final URL getClassResource(Class<?> clazz, String resPath)
    {
        URL url = clazz.getResource(resPath);
        
        if(url == null)
        {
            ClassLoader loader = clazz.getClassLoader();
            if(loader != null) url = loader.getResource(resPath);
            
            if(url == null)
            {
                loader = Thread.currentThread().getContextClassLoader();
                if(loader != null) url = loader.getResource(resPath);
            }
        }

        return url;
    }

    /** 获取 clazz 资源环境中 resPath 相对路径的 URL 对象列表 */
    public static final List<URL> getClassResources(Class<?> clazz, String resPath)
    {
        List<URL> urlList     = new ArrayList<URL>();
        Enumeration<URL> urls = null;
        
        try
        {
            ClassLoader loader = clazz.getClassLoader();
            if(loader != null) urls = loader.getResources(resPath);
            
            if(urls == null || !urls.hasMoreElements())
            {
                loader = Thread.currentThread().getContextClassLoader();
                if(loader != null) urls = loader.getResources(resPath);
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        
        if(urls != null)
        {
            while(urls.hasMoreElements())
                urlList.add(urls.nextElement());
        }

        return urlList;
    }

    /** 获取 clazz 资源环境中 resPath 的 {@link InputStream} */
    public static final InputStream getClassResourceAsStream(Class<?> clazz, String resPath)
    {
        InputStream is = clazz.getResourceAsStream(resPath);
        
        if(is == null)
        {
            ClassLoader loader = clazz.getClassLoader();
            if(loader != null) is = loader.getResourceAsStream(resPath);
            
            if(is == null)
            {
                loader = Thread.currentThread().getContextClassLoader();
                if(loader != null) is = loader.getResourceAsStream(resPath);
            }
        }

        return is;
    }

    /** 获取 clazz 资源环境中 resPath 相对路径的 URL 绝对路径（返还的绝对路径用 UTF-8 编码） */
    public static final String getClassResourcePath(Class<?> clazz, String resPath)
    {
        return getClassResourcePath(clazz, resPath, DEFAULT_ENCODING);
    }

    /** 获取 clazz 资源环境中 resPath 相对路径的 URL 绝对路径（返还的绝对路径用 pathEnc 编码） */
    public static final String getClassResourcePath(Class<?> clazz, String resPath, String pathEnc)
    {
        String path = null;

        try
        {
            URL url = getClassResource(clazz, resPath);
            
            if(url != null)
            {
                path = url.getPath();
                path = URLDecoder.decode(path, pathEnc);
            }
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }

        return path;
    }

    /** 获取 clazz 资源环境中 resPath 相对路径的 URL 绝对路径列表（返还的绝对路径用 UTF-8 编码） */
    public static final List<String> getClassResourcePaths(Class<?> clazz, String resPath)
    {
        return getClassResourcePaths(clazz, resPath, DEFAULT_ENCODING);
    }

    /** 获取 clazz 资源环境中 resPath 相对路径的 URL 绝对路径列表（返还的绝对路径用 pathEnc 编码） */
    public static final List<String> getClassResourcePaths(Class<?> clazz, String resPath, String pathEnc)
    {
        List<String> pathList = new ArrayList<String>();

        try
        {
            List<URL> urlList = getClassResources(clazz, resPath);
            
            for(URL url : urlList)
            {
                String path = URLDecoder.decode(url.getPath(), pathEnc);
                pathList.add(path);
            }
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }

        return pathList;
    }

    /** 获取 clazz 资源环境的当前 URL 绝对路径（返回的绝对路径用 pathEnc 编码） */
    public static final String getClassPath(Class<?> clazz)
    {
        return getClassResourcePath(clazz, ".");
    }

    /** 获取 resource 资源的 locale 本地化文件中名字为 key 的字符串资源，并代入 params 参数 */
    public static final String getResourceMessage(Locale locale, String resource, String key, Object ... params)
    {
        ResourceBundle bundle = ResourceBundle.getBundle(resource, locale);
        String msg = bundle.getString(key);

        if(params != null && params.length > 0)
            msg = MessageFormat.format(msg, params);

        return msg;
    }

    /** 获取 resource 资源的默认本地化文件中名字为 key 的字符串资源，并代入 params 参数 */
    public static final String getResourceMessage(String resource, String key, Object ... params)
    {
        return getResourceMessage(Locale.getDefault(), resource, key, params);
    }

    /** 获取 e 异常的堆栈信息，最大的堆栈层数由 levels 指定 */
    public static final String getExceptionMessageStackString(Throwable e, int levels)
    {
        StringBuilder sb = new StringBuilder();

        if(levels == 0)
            levels = Integer.MAX_VALUE;

        for(int i = 0; i < levels; ++i)
        {
            if(i > 0) sb.append("Caused by -> ");
            sb.append(e.getClass().getName());
            
            String msg = e.getLocalizedMessage();
            if(msg != null) sb.append(": ").append(msg);

            e = e.getCause();
            if(e == null)
                break;
        }

        return sb.toString();
    }

    /** 获取 e 异常的整个堆栈列表 */
    public static final String getExceptionMessageStackString(Throwable e)
    {
        return getExceptionMessageStackString(e, 0);
    }

    /** 获取 e 异常的堆栈列表，最大的堆栈层数由 levels 指定 */
    public static final List<String> getExceptionMessageStack(Throwable e, int levels)
    {
        List<String> list = new ArrayList<String>();

        if(levels == 0)
            levels = Integer.MAX_VALUE;

        for(int i = 0; i < levels; ++i)
        {
            StringBuilder sb = new StringBuilder();

            if(i > 0) sb.append("Caused by -> ");
            sb.append(e.getClass().getName());
            
            String msg = e.getLocalizedMessage();
            if(msg != null) sb.append(": ").append(msg);

            list.add(sb.toString());

            e = e.getCause();
            if(e == null)
                break;
        }

        return list;
    }

    /** 获取 e 异常的整个堆栈列表 */
    public static final List<String> getExceptionMessageStack(Throwable e)
    {
        return getExceptionMessageStack(e, 0);
    }

    /** 输出 e 异常的 levels 层堆栈列表到 ps 中 */
    public static final void printExceptionMessageStack(Throwable e, int levels, PrintStream ps)
    {
        List<String> list = getExceptionMessageStack(e, levels);

        for(String msg : list)
            ps.println(msg);
    }

    /** 输出 e 异常的 levels 层堆栈列表到标准错误流中 */
    public static final void printExceptionMessageStack(Throwable e, int levels)
    {
        printExceptionMessageStack(e, levels, System.err);
    }

    /** 输出 e 异常的整个堆栈列表到 ps 中 */
    public static final void printExceptionMessageStack(Throwable e, PrintStream ps)
    {
        printExceptionMessageStack(e, 0, ps);
    }

    /** 输出 e 异常的整个堆栈列表到标准错误流中 */
    public static final void printExceptionMessageStack(Throwable e)
    {
        printExceptionMessageStack(e, 0);
    }
    
    /** 把元素添加到 {@link Map} 中，不保证线程安全（不替换原值） */
    public static final <K, V> boolean tryPut(Map<K, V> map, K key, V value)
    {
        return tryPut(map, key, value, false);
    }
    
    /** 把元素添加到 {@link Map} 中，不保证线程安全 */
    public static final <K, V> boolean tryPut(Map<K, V> map, K key, V value, boolean replace)
    {
        if(replace || !map.containsKey(key))
        {
            map.put(key, value);
            return true;
        }
        
        return false;
    }

    /** 把元素添加到 {@link Map} 中，并保证线程安全（不替换原值） */
    public static final <K, V> boolean syncTryPut(Map<K, V> map, K key, V value)
    {
        return syncTryPut(map, key, value, false);
    }
    
    /** 把元素添加到 {@link Map} 中，并保证线程安全 */
    public static final <K, V> boolean syncTryPut(Map<K, V> map, K key, V value, boolean replace)
    {
        synchronized(map)
        {
            return tryPut(map, key, value, replace);
        }
    }

    /** 把元素添加到 {@link Map} 中，不保证线程安全（不替换原值） */
    public static final <K, V> int tryPutAll(Map<K, V> map, Map<K, V> src)
    {
        return tryPutAll(map, src, false);
    }
    
    /** 把元素添加到 {@link Map} 中，不保证线程安全 */
    public static final <K, V> int tryPutAll(Map<K, V> map, Map<K, V> src, boolean replace)
    {
        if(replace)
        {
            map.putAll(src);
            return src.size();
        }
        
        int count = 0;
        Set<Map.Entry<K, V>> entries = src.entrySet();
        
        for(Map.Entry<K, V> e : entries)
        {
            if(!map.containsKey(e.getKey()))
            {
                map.put(e.getKey(), e.getValue());
                ++count;
            }
        }
        
        return count;
    }

    /** 把元素添加到 {@link Map} 中，并保证线程安全（不替换原值） */
    public static final <K, V> int syncTryPutAll(Map<K, V> map, Map<K, V> src)
    {
        return syncTryPutAll(map, src, false);
    }
    
    /** 把元素添加到 {@link Map} 中，并保证线程安全 */
    public static final <K, V> int syncTryPutAll(Map<K, V> map, Map<K, V> src, boolean replace)
    {
        synchronized(map)
        {
            return tryPutAll(map, src, replace);
        }
    }

    /** 从 {@link Map} 中删除元素，不保证线程安全 */
    public static final <K, V> boolean tryRemove(Map<K, V> map, K key)
    {
        if(map.containsKey(key))
        {
            map.remove(key);
            return true;
        }
        
        return false;
    }

    /** 从 {@link Map} 中删除元素，并保证线程安全 */
    public static final <K, V> boolean syncTryRemove(Map<K, V> map, K key)
    {
        synchronized(map)
        {
            return tryRemove(map, key);
        }
    }

    /** 清空 {@link Map}，不保证线程安全 */
    public static final <K, V> void tryClear(Map<K, V> map)
    {
        map.clear();
    }

    /** 清空 {@link Map}，并保证线程安全 */
    public static final <K, V> void syncTryClear(Map<K, V> map)
    {
        synchronized(map)
        {
            tryClear(map);
        }
    }
    
    /** 获取 System Property */
    public static final String getSystemProperty(String key)
    {
        return System.getProperty(key);
    }

    /** 获取 System Property，不存在则返回 <i>def</i> 参数值 */
    public static final String getSystemProperty(String key, String def)
    {
        return System.getProperty(key, def);
    }

    /** 当 System Property 不存在时，设置 System Property */
    public static final boolean setSystemPropertyIfAbsent(String key, Object value)
    {
        return setSystemProperty(key, value, false);
    }
    
    /** 设置 System Property */
    public static final boolean setSystemProperty(String key, Object value)
    {
        return setSystemProperty(key, value, true);
    }
    
    /** 设置 System Property，<i>def</i> 参数值标识是否覆盖现有值 */
    public static final boolean setSystemProperty(String key, Object value, boolean override)
    {
        String oldVal = System.getProperty(key);
        
        if(!override && oldVal != null)
            return false;
        
        if(value == null)
            System.getProperties().remove(key);
        else
            System.setProperty(key, value.toString());
        
        return true;
    }
    
    /** 获取 System Env */
    public static final String getSystemEnv(String key)
    {
        return System.getenv(key);
    }

    /** 获取所有 System Env */
    public static final Map<String, String> getSystemEnvMap()
    {
        return System.getenv();
    }


    /** 获取当前 JVM 进程的 ID */
    public static final int getProcessId()
    {
        return Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }

    /** 获取当前 JVM 进程的 Java 版本 */
    public static final String getJavaVersion()
    {
        return getSystemProperty("java.version");
    }

    /** 获取当前操作系统的名称 */
    public static final String getOSName()
    {
        return getSystemProperty("os.name");
    }

    /** 检查当前操作系统是否为 Windows 系列 */
    public static final boolean isWindowsPlatform()
    {
        return File.pathSeparatorChar == ';';
    }
    
    /** 根据前缀和日期时间生成名称，格式：$prefix&lt;yyyyMMdd&gt; */    
    public static String genNameByDateTime(String prefix, ZonedDateTime dateTime)
    {
        return genNameByDateTime(prefix, dateTime, "yyyyMMdd");
    }
    
    /** 根据前缀和日期时间生成名称，格式：$prefix&lt;dateTime.format(dateTimePattern)&gt; */    
    public static String genNameByDateTime(String prefix, ZonedDateTime dateTime, String dateTimePattern)
    {
        return prefix + dateTime.format(DateTimeFormatter.ofPattern(dateTimePattern));
    }

    /** 按拼音排序的字符串比较器 */
    public static class PinYinComparator implements Comparator<String>
    {
        @Override
        public int compare(String o1, String o2)
        {
            java.text.Collator cmp = java.text.Collator.getInstance(java.util.Locale.CHINA);
            return cmp.compare(o1, o2);
        }
    }

    /** 按文件名称进行文件筛选的文件过滤器，构造函数参数 name 指定文件名的正则表达式 */
    public static class FileNameFileFilter implements FileFilter
    {
        protected static final int FLAGS = IS_WINDOWS_PLATFORM ? Pattern.CASE_INSENSITIVE : 0;

        Pattern pattern;

        public FileNameFileFilter(String name)
        {
            String exp = name;
            exp = exp.replace('.', '#');
            exp = exp.replaceAll("#", "\\\\.");
            exp = exp.replace('*', '#');
            exp = exp.replaceAll("#", ".*");
            exp = exp.replace('?', '#');
            exp = exp.replaceAll("#", ".?");
            exp = "^" + exp + "$";

            pattern = Pattern.compile(exp, FLAGS);
        }

        @Override
        public boolean accept(File file)
        {
            Matcher matcher = pattern.matcher(file.getName());
            return matcher.matches();
        }
    }
    
    public static final String longList2Str(List<Long> list)
    {
        return list2Str(list);
    }
    
    public static final String intList2Str(List<Integer> list)
    {
        return list2Str(list);
    }
    
    public static final String strList2Str(List<String> list)
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
    
    public static final List<Long> str2LongList(String str)
    {
        return str2LongList(str, DELIMITERR_CHARS);
    }
    
    public static final List<Long> str2LongList(String str, String delim)
    {
        List<Long> list = null;
        
        if(str != null)
        {
            StringTokenizer st = new StringTokenizer(str, delim);
            list = new ArrayList<Long>(st.countTokens());
            
            while(st.hasMoreTokens())
                list.add(Long.parseLong(st.nextToken()));
        }

        return list != null ? list : List.of();
    }
    
    public static final List<Integer> str2IntList(String str)
    {
        return str2IntList(str, DELIMITERR_CHARS);
    }
    
    public static final List<Integer> str2IntList(String str, String delim)
    {
        List<Integer> list = null;
        
        if(str != null)
        {
            StringTokenizer st = new StringTokenizer(str, delim);
            list = new ArrayList<Integer>(st.countTokens());
            
            while(st.hasMoreTokens())
                list.add(Integer.parseInt(st.nextToken()));
        }

        return list != null ? list : List.of();
    }
    
    public static final List<String> str2StrList(String str)
    {
        return str2StrList(str, DELIMITERR_CHARS);
    }
    
    public static final List<String> str2StrList(String str, String delim)
    {
        List<String> list = null;
        
        if(str != null)
        {
            StringTokenizer st = new StringTokenizer(str, delim);
            list = new ArrayList<String>(st.countTokens());
            
            while(st.hasMoreTokens())
                list.add(st.nextToken());
        }

        return list != null ? list : List.of();
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
    
    public static final boolean isNullOrZero(Byte n)
    {
        return n == null || n.byteValue() == (byte)0;
    }
    
    public static final boolean isNullOrZero(Short n)
    {
        return n == null || n.shortValue() == (short)0;
    }
    
    public static final boolean isNullOrZero(Integer n)
    {
        return n == null || n.intValue() == 0;
    }
    
    public static final boolean isNullOrZero(Long n)
    {
        return n == null || n.longValue() == 0L;
    }
    
    public static final boolean isNotNullAndZero(Byte n)
    {
        return !isNullOrZero(n);
    }
    
    public static final boolean isNotNullAndZero(Short n)
    {
        return !isNullOrZero(n);
    }
    
    public static final boolean isNotNullAndZero(Integer n)
    {
        return !isNullOrZero(n);
    }
    
    public static final boolean isNotNullAndZero(Long n)
    {
        return !isNullOrZero(n);
    }
    
    public static final boolean isNullOrNan(Float n)
    {
        return n == null || n.equals(Float.NaN);
    }
    
    public static final boolean isNullOrNan(Double n)
    {
        return n == null || n.equals(Double.NaN);
    }
    
    public static final boolean isNotNullAndNan(Float n)
    {
        return !isNullOrNan(n);
    }
    
    public static final boolean isNotNullAndNan(Double n)
    {
        return !isNullOrNan(n);
    }
    
    public static final boolean isNull(Object o)
    {
        return (o == null);
    }    
    
    public static final boolean isNotNull(Object o)
    {
        return !isNull(o);
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

    public static final String mask(String str, int size, int offset)
    {
        if(GeneralHelper.isStrEmpty(str))
            return "";
        
        int len   = str.length();
        int end   = len - offset;
        int begin = len - size - offset;
        
        if(end <= 0)
            return str;
        if(begin < 0)
            begin = 0;
        
        StringBuilder sb = new StringBuilder(len);
        
        for(int i = 0; i < len; i++)
        {
            if(i >= begin && i < end)
                sb.append('*');
            else
                sb.append(str.charAt(i));
        }
        
        return sb.toString();
    }
    
    public static final <T extends Enum<T>> T enumLookup(Class<T> enumClass, String name, boolean ignoreCase)
    {
        Stream<T> stream = Arrays.stream(enumClass.getEnumConstants());
        
        return (ignoreCase)
            ? stream.filter((e) -> e.name().equalsIgnoreCase(name)).findFirst().orElse(null)
            : stream.filter((e) -> e.name().equals(name)).findFirst().orElse(null);
    }
}
