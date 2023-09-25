
package io.github.hpsocket.soa.framework.util.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import io.github.hpsocket.soa.framework.core.util.CryptHelper;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.core.util.KV;

/**
 * 
 * HTTP 帮助类
 *
 */
public class HttpHelper
{
    /** URL scheme 指示符 */
    public static final String URL_SECHEME_SUFFIX        = "://";
    /** URL 目录分隔符 */
    public static final String URL_PATH_SEPARATOR        = "/";
    /** URL 端口分隔符 */
    public static final String URL_PORT_SEPARATOR        = ":";
    /** URL 参数标识符 */
    public static final String URL_PARAM_FLAG            = "?";
    /** URL 参数分隔符 */
    public static final String URL_PARAM_SEPARATOR        = "&";
    /** HTTP URL 标识 */
    public static final String HTTP_SCHEME                = "http";
    /** HTTPS URL 标识 */
    public static final String HTTPS_SCHEME                = "https";
    /** HTTP 默认端口 */
    public static final int HTTP_DEFAULT_PORT            = 80;
    /** HTTPS 默认端口 */
    public static final int HTTPS_DEFAULT_PORT            = 443;
    /** 默认缓冲区大小 */
    private static final int DEFAULT_BUFFER_SIZE        = 8192;

    /** 获取 {@link HttpURLConnection} */
    @SafeVarargs
    public final static HttpURLConnection getHttpConnection(String url, KV<String, String> ... properties) throws IOException
    {
        return getHttpConnection(url, null, properties);
    }

    /** 获取 {@link HttpURLConnection} */
    @SafeVarargs
    public final static HttpURLConnection getHttpConnection(String url, String method, KV<String, String> ... properties) throws IOException
    {
        URL connUrl                = new URL(url);
        HttpURLConnection conn    = (HttpURLConnection)connUrl.openConnection();
        
        if(GeneralHelper.isStrNotEmpty(method))
            conn.setRequestMethod(method);
        
        conn.setDoInput(true);
        conn.setDoOutput(true);
        
        for(KV<String, String> kv : properties)
            conn.setRequestProperty(kv.getKey(), kv.getValue());

        return conn;
    }

    /** 向页面输出文本内容 */
    public final static void writeString(HttpURLConnection conn, String content, String charsetName) throws IOException
    {
        writeString(conn.getOutputStream(), content, charsetName);
    }

    /** 向页面输出文本内容 */
    public final static void writeString(OutputStream os, String content, String charsetName) throws IOException
    {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, charsetName));
        
        pw.write(content);
        pw.flush();
        pw.close();
    }

    /** 向页面输出字节内容 */
    public final static void writeBytes(HttpURLConnection conn, byte[] content) throws IOException
    {
        writeBytes(conn.getOutputStream(), content);
    }

    /** 向页面输出字节内容 */
    public final static void writeBytes(OutputStream os, byte[] content) throws IOException
    {
        BufferedOutputStream bos = new BufferedOutputStream(os);
        
        bos.write(content);
        bos.flush();
        bos.close();
    }

    /** 读取页面请求的文本内容 */
    public final static String readString(HttpURLConnection conn, boolean escapeReturnChar, String charsetName) throws IOException
    {
        return readString(conn.getInputStream(), escapeReturnChar, charsetName);
    }

    /** 读取页面请求的文本内容 */
    public final static String readString(InputStream is, boolean escapeReturnChar, String charsetName) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, charsetName));
        
        try
        {
            if(escapeReturnChar)
            {
                for(String line = null; (line = rd.readLine()) != null;)
                    sb.append(line);
             }
            else
            {
                int count        = 0;
                char[] array    = new char[DEFAULT_BUFFER_SIZE];
                
                while((count = rd.read(array)) != -1)
                    sb.append(array, 0, count);
            }
        }
        finally
        {
            rd.close();
        }
        
        return sb.toString();
    }
    
    /** 读取页面请求的字节内容 */
    public final static byte[] readBytes(HttpURLConnection conn) throws IOException
    {
        return readBytes(conn.getInputStream(), conn.getContentLength());
    }
    
    /** 读取页面请求的字节内容 */
    public final static byte[] readBytes(InputStream is) throws IOException
    {
        return readBytes(is, 0);
    }

    /** 读取页面请求的字节内容 */
    public final static byte[] readBytes(InputStream is, int length) throws IOException
    {
        if(length > 0)
            return is.readNBytes(length);
        
        return is.readAllBytes();
    }
    
    /** 根据地址和参数生成 URL */
    @SafeVarargs
    public final static String makeURL(String srcURL, KV<String, String> ... params)
    {
        return makeURL(srcURL, null, params);
    }

    /** 根据地址和参数生成 URL，并用指定字符集对地址进行编码 */
    @SafeVarargs
    public final static String makeURL(String srcURL, String charset, KV<String, String> ... params)
    {
        StringBuilder sbURL = new StringBuilder(srcURL);
        
        char token = '&';
        char firstToken = srcURL.indexOf('?') != -1 ? token : '?';
        
        for(int i = 0; i < params.length; i++)
        {
            KV<String, String> kv = params[i];
            String key = kv.getKey();
            String val = kv.getValue();
            
            if(i > 0)
                sbURL.append(token);
            else
                sbURL.append(firstToken);
            
            sbURL.append(key);
            sbURL.append('=');
            if(GeneralHelper.isStrNotEmpty(val))
                    sbURL.append(CryptHelper.urlEncode(val, charset));
            }
        
        return sbURL.toString();
    }

    /** 确保 URL 路径的前后存在 URL 路径分隔符 */
    public static final String ensurePath(String path, String defPath)
    {
        if(GeneralHelper.isStrEmpty(path))
            path = defPath;
        if(!path.startsWith(URL_PATH_SEPARATOR))
            path = URL_PATH_SEPARATOR + path;
        if(!path.endsWith(URL_PATH_SEPARATOR))
            path = path + URL_PATH_SEPARATOR;
        
        return path;
    }
    
    /** 获取 URL 地址的主机段 */
    public static final String getUrlHost(String url)
    {
        int p1 = url.indexOf(URL_SECHEME_SUFFIX);
        
        if(p1 != -1)
        {
            url = url.substring(p1 + 3);
            return getUrlHost(url);
        }
        else
        {
            int p2 = url.indexOf(URL_PATH_SEPARATOR);

            if(p2 != -1)
                return url.substring(0, p2);
            else
            {
                int p3 = url.indexOf(URL_PARAM_FLAG);
                
                if(p3 != -1)
                    return url.substring(0, p3);
                else
                    return url;
            }
        }
    }
    
    /** 获取 URL 地址的 Base Path */
    public static final String getUrlBase(String url, String scheme)
    {
        int p1 = url.indexOf(URL_SECHEME_SUFFIX);
        
        if(p1 != -1)
        {
            int p2 = url.indexOf(URL_PATH_SEPARATOR, p1 + 3);

            if(p2 != -1)
                return url.substring(0, p2);
            else
            {
                int p3 = url.indexOf(URL_PARAM_FLAG, p1 + 3);
                
                if(p3 != -1)
                    return url.substring(0, p3);
                else
                    return url;
            }
        }
        else
        {
            url = scheme + url;
            return getUrlBase(url, scheme);
        }
    }
    
    /** 获取 URL 地址的 Base Path */
    public static final String getUrlBase(String url)
    {
        return getUrlBase(url, HTTP_SCHEME + URL_SECHEME_SUFFIX);
    }
    
    /** 获取 URL 地址的非参数段 */
    public static final String truncateUrlParams(String url)
    {
        int p = url.indexOf(URL_PARAM_FLAG);
        
        if(p != -1)
            url = url.substring(0, p);

        return url;
    }
    
    /** 添加 URL 地址参数 */
    public static final String addUrlParams(String url, Object ... params)
    {
        final String ENC = CryptHelper.DEFAULT_ENCODING;
        
        int index = url.indexOf('?');
        char sep1 = (index == -1) ? '?' : '&';
        StringBuilder sb = new StringBuilder(url);
        
        for(int i = 0; i < params.length; i += 2)
        {
            String key = CryptHelper.urlEncode(params[i].toString(), ENC);
            sb.append(i == 0 ? sep1 : '&').append(key).append('=');
            
            if(i < params.length - 1)
            {
                String val = CryptHelper.urlEncode(params[i + 1].toString(), ENC);
                sb.append(val);
            }
        }
        
        return sb.toString();
    }
    
    /** 添加 URL 地址参数 */
    public static final String addUrlParams(String url, Map<String, String> map)
    {
        int i            = 0;
        Object[] params    = new Object[map.size() * 2];
        
        for(Map.Entry<String, String> entry : map.entrySet())
        {
            String key    = entry.getKey();
            String val    = entry.getValue();
            params[i++]    = key == null ? "" : key;
            params[i++]    = val == null ? "" : val;
        }
        
        return addUrlParams(url, params);
    }
    
    /** 删除 URL 地址参数 */
    public static final String deleteUrlParams(String url, String ... names)
    {
        String baseUrl = truncateUrlParams(url);
        Map<String, String> params = getUrlParamMap(url);
        
        for(String name : names)
            params.remove(name);
        
        return addUrlParams(baseUrl, params);
    }
    
    /** 获取 URL 地址参数 */
    public static final Map<String, String> getUrlParamMap(String url)
    {
        Map<String, String> map = new LinkedHashMap<String, String>();
        
        int index = url.indexOf('?');
        if(index != -1)
        {
            String str = url.substring(index + 1);
            String[] params = str.split("\\&");
            
            for(String param : params)
            {
                String[] pair    = param.split("\\=", 2);
                String name        = CryptHelper.urlDecode(pair[0]);
                String value    = pair.length == 2 ? CryptHelper.urlDecode(pair[1]) : "";
                
                if(!name.isEmpty())
                    map.put(name, value);
            }
        }
        
        return map;
    }
    
    /** 获取 URL 地址参数（不解码） */
    public static final Map<String, String> getUrlParamMapNoDecode(String url)
    {
        Map<String, String> map = new LinkedHashMap<String, String>();
        
        int index = url.indexOf('?');
        if(index != -1)
        {
            String str = url.substring(index + 1);
            String[] params = str.split("\\&");
            
            for(String param : params)
            {
                String[] pair    = param.split("\\=", 2);
                String name        = pair[0];
                String value    = pair.length == 2 ? pair[1] : "";
                
                if(!name.isEmpty())
                    map.put(name, value);
            }
        }
        
        return map;
    }
    
    /** 获取 URL 地址的指定参数 */
    public static final String getUrlParam(String url, String name)
    {
        return getUrlParamMap(url).get(name);
    }

    /** 编码 URL 地址参数 */
    public final static String encodeUrlParams(String url)
    {
        String baseUrl = truncateUrlParams(url);
        
        if(baseUrl.length() < url.length())
        {
            Map<String, String> params = getUrlParamMapNoDecode(url);
            String[] items = new String[params.size() * 2];
            
            int i = 0;
            for(Map.Entry<String, String> e : params.entrySet())
            {
                items[i++] = e.getKey();
                items[i++] = e.getValue();
            }
            
            url = addUrlParams(baseUrl, (Object[])items);
        }
        
        return url;
    }

    /** GZip 解压 */
    public final static byte[] unGZip(byte[] bytes) throws IOException
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        
        byte[] rs = unGZip(bis);
        
        bis.close();
        
        return rs;
    }

    /** GZip 解压 */
    public final static byte[] unGZip(InputStream in) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        unGZip(in, baos);
        
        byte[] rs = baos.toByteArray();
        
        baos.close();
        
        return rs;
    }

    /** GZip 解压 */
    public final static void unGZip(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer            = new byte[DEFAULT_BUFFER_SIZE];
        GZIPInputStream gzip    = new GZIPInputStream(in);

        int r;
        while((r = gzip.read(buffer, 0, buffer.length)) != -1)
            out.write(buffer, 0, r);
        
        gzip.close();
    }

}
