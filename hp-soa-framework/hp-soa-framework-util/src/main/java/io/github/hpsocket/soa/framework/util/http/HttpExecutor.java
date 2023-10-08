
package io.github.hpsocket.soa.framework.util.http;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.ConnectTimeoutException;
import org.apache.hc.client5.http.auth.AuthSchemeFactory;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.CredentialsStore;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieSpecFactory;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.entity.GzipDecompressingEntity;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.ContentBody;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.impl.auth.BasicSchemeFactory;
import org.apache.hc.client5.http.impl.auth.DigestSchemeFactory;
import org.apache.hc.client5.http.impl.auth.KerberosSchemeFactory;
import org.apache.hc.client5.http.impl.auth.NTLMSchemeFactory;
import org.apache.hc.client5.http.impl.auth.SPNegoSchemeFactory;
import org.apache.hc.client5.http.impl.auth.SystemDefaultCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.impl.cookie.RFC6265CookieSpecFactory;
import org.apache.hc.client5.http.impl.cookie.RFC6265CookieSpecFactory.CompatibilityLevel;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;

import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.core.mdc.MdcRunnable;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.core.util.Pair;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class HttpExecutor
{
    public static final int DEFAULT_RETRIES                     = 1;
    public static final int DEFAULT_MAX_REDIRECTS               = 30;
    public static final int DEFAULT_MAX_CONNECTION_TOTAL        = 100;
    public static final int DEFAULT_MAX_CONNECTION_PER_ROUTE    = 50;
    public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT  = 5 * 1000;
    public static final int DEFAULT_CONNECTION_TIMEOUT          = 10 * 1000;
    public static final int DEFAULT_RESPONSE_TIMEOUT            = 20 * 1000;
    public static final int DEFAULT_SOCKET_TIMEOUT              = 30 * 1000;
    public static final String DEFAULT_ENCODING                 = "UTF-8";
    
    private static final int MAX_LOG_CONTENT_LENGTH             = 5 * 1000;
    private static final String MONITOR_LOGGER_NAME             = "SOA-MONITOR";
    private static final String MONITOR_LOG_TYPE                = "MONITOR-EGRESS";
    private static final Logger MONITOR_LOGGER                  = LoggerFactory.getLogger(MONITOR_LOGGER_NAME);
    
    private static final ThreadPoolExecutor LOG_EXECUTOR = new ThreadPoolExecutor(
                                                                                    1,
                                                                                    8,
                                                                                    30,
                                                                                    TimeUnit.SECONDS,
                                                                                    new LinkedBlockingDeque<>(2000),
                                                                                    new ThreadPoolExecutor.CallerRunsPolicy());                                                                         

    private TlsConfig defaultTlsConfig;
    private RequestConfig defaultRequestConfig;
    private HttpClientContext httpContext;
    private CloseableHttpClient httpClient;
    private PoolingHttpClientConnectionManager connManager;
    
    private boolean useCookie               = true;
    private boolean relaxedCookieSpec       = true;
    private boolean useSystemProperties     = true;
    private boolean redirectsEnabled        = true;
    private boolean circularRedirectsAllowed= true;
    private boolean retainHeaderCookie      = true;
    private boolean trustAllCerts           = true;
    private boolean credentialsSupported    = false;
    private boolean logRequest              = false;
    private int retries                     = DEFAULT_RETRIES;
    private int maxRedirects                = DEFAULT_MAX_REDIRECTS;
    private int maxConnectionTotal          = DEFAULT_MAX_CONNECTION_TOTAL;
    private int maxConnectionPerRoute       = DEFAULT_MAX_CONNECTION_PER_ROUTE;
    private int connectionRequestTimeout    = DEFAULT_CONNECTION_REQUEST_TIMEOUT;
    private int connectTimeout              = DEFAULT_CONNECTION_TIMEOUT;
    private int responseTimeout             = DEFAULT_RESPONSE_TIMEOUT;
    private int socketTimeout               = DEFAULT_SOCKET_TIMEOUT;
    private String encoding                 = DEFAULT_ENCODING;
    
    private HttpHost httpProxy;
    private String[] supportedProtocols;
    
    private String sslKeyStoreType;
    private String sslKeyStoreFile;
    private String sslKeyStorePassword;
    private String sslKeyPassword;

    public HttpExecutor initialize()
    {
        if(httpClient == null)
        {
            synchronized(this)
            {
                if(httpClient == null)
                {
                    RequestConfig.Builder requestBuilder = RequestConfig.custom()
                                                            .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectionRequestTimeout))
                                                            .setResponseTimeout(Timeout.ofMilliseconds(responseTimeout))
                                                            .setRedirectsEnabled(redirectsEnabled)
                                                            .setCircularRedirectsAllowed(circularRedirectsAllowed)
                                                            .setMaxRedirects(maxRedirects)
                                                            .setCookieSpec(relaxedCookieSpec ? StandardCookieSpec.RELAXED : StandardCookieSpec.STRICT);
                                        
                    defaultRequestConfig = requestBuilder.build();

                    ConnectionConfig.Builder connBuilder = ConnectionConfig.custom()
                                                            .setConnectTimeout(Timeout.ofMilliseconds(connectTimeout))
                                                            .setSocketTimeout(Timeout.ofMilliseconds(socketTimeout));
                    
                    ConnectionConfig connCfg = connBuilder.build();
                    
                    PoolingHttpClientConnectionManagerBuilder cmBuilder = PoolingHttpClientConnectionManagerBuilder
                                                                            .create()
                                                                            .setMaxConnTotal(maxConnectionTotal)
                                                                            .setMaxConnPerRoute(maxConnectionPerRoute)
                                                                            .setDefaultConnectionConfig(connCfg);
                    if(defaultTlsConfig != null)
                        cmBuilder.setDefaultTlsConfig(defaultTlsConfig);
                    if(trustAllCerts)
                        cmBuilder.setSSLSocketFactory(createSSLConnectionSocketFactory());
                                                                            
                    connManager = cmBuilder.build();
                    
                    HttpClientBuilder clientBuilder    = HttpClients.custom()
                                                        .setConnectionManager(connManager)
                                                        .setDefaultRequestConfig(defaultRequestConfig);
                    
                    if(useSystemProperties)
                        clientBuilder.useSystemProperties();
                    if(httpProxy != null)
                        clientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(httpProxy));

                    httpContext = HttpClientContext.create();

                    if(useCookie)
                    {
                        Registry<CookieSpecFactory> registry = RegistryBuilder.<CookieSpecFactory> create()
                                                                    .register(StandardCookieSpec.RELAXED, new RFC6265CookieSpecFactory())
                                                                    .register(StandardCookieSpec.STRICT, new RFC6265CookieSpecFactory(CompatibilityLevel.STRICT, null))
                                                                    .build();
                        
                        httpContext.setCookieSpecRegistry(registry);
                        httpContext.setCookieStore(new BasicCookieStore());
                    }
                    else
                        clientBuilder.disableCookieManagement();
                    
                    if(credentialsSupported)
                    {
                        httpContext.setCredentialsProvider(new SystemDefaultCredentialsProvider());
                        httpContext.setAuthCache(new BasicAuthCache());
                        httpContext.setAuthSchemeRegistry(RegistryBuilder.<AuthSchemeFactory> create()
                                                            .register(StandardAuthScheme.BASIC, BasicSchemeFactory.INSTANCE)
                                                            .register(StandardAuthScheme.DIGEST, DigestSchemeFactory.INSTANCE)
                                                            .register(StandardAuthScheme.NTLM, NTLMSchemeFactory.INSTANCE)
                                                            .register(StandardAuthScheme.SPNEGO, SPNegoSchemeFactory.DEFAULT)
                                                            .register(StandardAuthScheme.KERBEROS, KerberosSchemeFactory.DEFAULT)
                                                            .build());
                    }
                    
                    httpClient = clientBuilder.build();
                }
            }
        }
        
        return this;
    }
    
    private SSLContext createTrustSSLContext()
    {
        try
        {
            SSLContextBuilder builder = new SSLContextBuilder().loadTrustMaterial(new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
                {
                    return true;
                }
            });
            
            if(GeneralHelper.isStrNotEmpty(sslKeyStoreFile))
            {
                if(GeneralHelper.isStrNotEmpty(sslKeyStoreType))
                    builder.setKeyStoreType(sslKeyStoreType);
                
                builder.loadKeyMaterial(new File(sslKeyStoreFile),
                                        GeneralHelper.safeString(sslKeyStorePassword).toCharArray(),
                                        GeneralHelper.safeString(sslKeyPassword).toCharArray());
            }
            
            return builder.build();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private SSLConnectionSocketFactory createSSLConnectionSocketFactory()
    {
        return new SSLConnectionSocketFactory(createTrustSSLContext(), supportedProtocols, null, NoopHostnameVerifier.INSTANCE);
    }
    
    public final boolean hasInitialized()
    {
        return httpClient != null;
    }
    
    public final void shutdown()
    {
        if(connManager != null)
        {
            connManager.close();
            connManager = null;
        }
        
        if(httpClient != null)
        {
            try
            {
                httpClient.close();
                httpClient = null;
            }
            catch(IOException e)
            {
            }
        }
    }
    
    @Override
    protected void finalize()
    {
        shutdown();
    }

    public Response executeGet(String uri)
    {
        return executeGet(uri, null, null);
    }

    public Response executeGet(String uri, RequestConfig requestConfig)
    {
        return executeGet(uri, requestConfig, null);
    }

    public Response executeGet(String uri, List<NameValuePair> headers)
    {
        return executeGet(uri, null, headers);
    }

    public Response executeGet(String uri, RequestConfig requestConfig, List<NameValuePair> headers)
    {
        HttpGet httpGet = createGetEntity(uri);
        return execute(httpGet, requestConfig, headers);
    }

    public Response executeFormPost(String uri, List<NameValuePair> data)
    {
        return executeFormPost(uri, data, null, null);
    }

    public Response executeFormPost(String uri, List<NameValuePair> data, RequestConfig requestConfig)
    {
        return executeFormPost(uri, data, requestConfig, null);
    }

    public Response executeFormPost(String uri, List<NameValuePair> data, List<NameValuePair> headers)
    {
        return executeFormPost(uri, data, null, headers);
    }

    public Response executeFormPost(String uri, List<NameValuePair> data, RequestConfig requestConfig, List<NameValuePair> headers)
    {
        HttpPost httpPost = createFormPostEntity(uri, data);

        return execute(httpPost, requestConfig, headers);
    }

    public Response executeStringPost(String uri, String data)
    {
        return executeStringPost(uri, data, null, null, null);
    }

    public Response executeStringPost(String uri, String data, String mimeType)
    {
        return executeStringPost(uri, data, mimeType, null, null);
    }

    public Response executeStringPost(String uri, String data, String mimeType, List<NameValuePair> headers)
    {
        return executeStringPost(uri, data, mimeType, null, headers);
    }

    public Response executeStringPost(String uri, String data, String mimeType, RequestConfig requestConfig)
    {
        return executeStringPost(uri, data, mimeType, requestConfig, null);
    }

    public Response executeStringPost(String uri, String data, String mimeType, RequestConfig requestConfig, List<NameValuePair> headers)
    {
        HttpPost httpPost = createStringPostEntity(uri, data, mimeType);

        return execute(httpPost, requestConfig, headers);
    }
    
    public Response executeBytesPost(String uri, byte[] data)
    {
        return executeBytesPost(uri, data, null, null, null);
    }

    public Response executeBytesPost(String uri, byte[] data, String mimeType)
    {
        return executeBytesPost(uri, data, mimeType, null, null);
    }

    public Response executeBytesPost(String uri, byte[] data, String mimeType, List<NameValuePair> headers)
    {
        return executeBytesPost(uri, data, mimeType, null, headers);
    }

    public Response executeBytesPost(String uri, byte[] data, String mimeType, RequestConfig requestConfig)
    {
        return executeBytesPost(uri, data, mimeType, requestConfig, null);
    }

    public Response executeBytesPost(String uri, byte[] data, String mimeType, RequestConfig requestConfig, List<NameValuePair> headers)
    {
        HttpPost httpPost = createBytesPostEntity(uri, data, mimeType);

        return execute(httpPost, requestConfig, headers);
    }
    
    public Response executeInputStreamPost(String uri, InputStream data)
    {
        return executeInputStreamPost(uri, data, null, null, null);
    }

    public Response executeInputStreamPost(String uri, InputStream data, String mimeType)
    {
        return executeInputStreamPost(uri, data, mimeType, null, null);
    }

    public Response executeInputStreamPost(String uri, InputStream data, String mimeType, List<NameValuePair> headers)
    {
        return executeInputStreamPost(uri, data, mimeType, null, headers);
    }

    public Response executeInputStreamPost(String uri, InputStream data, String mimeType, RequestConfig requestConfig)
    {
        return executeInputStreamPost(uri, data, mimeType, requestConfig, null);
    }

    public Response executeInputStreamPost(String uri, InputStream data, String mimeType, RequestConfig requestConfig, List<NameValuePair> headers)
    {
        HttpPost httpPost = createInputStreamPostEntity(uri, data, mimeType);

        return execute(httpPost, requestConfig, headers);
    }
    
    public Response executeFormUpload(String uri, List<Pair<String, ContentBody>> data)
    {
        return executeFormUpload(uri, data, null, null);
    }

    public Response executeFormUpload(String uri, List<Pair<String, ContentBody>> data, RequestConfig requestConfig)
    {
        return executeFormUpload(uri, data, requestConfig, null);
    }

    public Response executeFormUpload(String uri, List<Pair<String, ContentBody>> data, List<NameValuePair> headers)
    {
        return executeFormUpload(uri, data, null, headers);
    }

    public Response executeFormUpload(String uri, List<Pair<String, ContentBody>> data, RequestConfig requestConfig, List<NameValuePair> headers)
    {
        HttpPost httpPost = createFormUploadEntity(uri, data);

        return execute(httpPost, requestConfig, headers);
    }

    public Response downloadGet(String fileName, String uri)
    {
        return downloadGet(fileName, uri, null, null);
    }

    public Response downloadGet(String fileName, String uri, RequestConfig requestConfig)
    {
        return downloadGet(fileName, uri, requestConfig, null);
    }

    public Response downloadGet(String fileName, String uri, List<NameValuePair> headers)
    {
        return downloadGet(fileName, uri, null, headers);
    }

    public Response downloadGet(String fileName, String uri, RequestConfig requestConfig, List<NameValuePair> headers)
    {
        HttpGet httpGet = createGetEntity(uri);
        
        return execute(httpGet, requestConfig, headers, fileName);
    }

    public Response downloadFormPost(String fileName, String uri, List<NameValuePair> data)
    {
        return downloadFormPost(fileName, uri, data, null, null);
    }

    public Response downloadFormPost(String fileName, String uri, List<NameValuePair> data, RequestConfig requestConfig)
    {
        return downloadFormPost(fileName, uri, data, requestConfig, null);
    }

    public Response downloadFormPost(String fileName, String uri, List<NameValuePair> data, List<NameValuePair> headers)
    {
        return downloadFormPost(fileName, uri, data, null, headers);
    }

    public Response downloadFormPost(String fileName, String uri, List<NameValuePair> data, RequestConfig requestConfig, List<NameValuePair> headers)
    {
        HttpPost httpPost = createFormPostEntity(uri, data);

        return execute(httpPost, requestConfig, headers, fileName);
    }

    public Response downloadStringPost(String fileName, String uri, String data)
    {
        return downloadStringPost(fileName, uri, data, null, null, null);
    }

    public Response downloadStringPost(String fileName, String uri, String data, String mimeType)
    {
        return downloadStringPost(fileName, uri, data, mimeType, null, null);
    }

    public Response downloadStringPost(String fileName, String uri, String data, String mimeType, List<NameValuePair> headers)
    {
        return downloadStringPost(fileName, uri, data, mimeType, null, headers);
    }

    public Response downloadStringPost(String fileName, String uri, String data, String mimeType, RequestConfig requestConfig)
    {
        return downloadStringPost(fileName, uri, data, mimeType, requestConfig, null);
    }

    public Response downloadStringPost(String fileName, String uri, String data, String mimeType, RequestConfig requestConfig, List<NameValuePair> headers)
    {
        HttpPost httpPost = createStringPostEntity(uri, data, mimeType);

        return execute(httpPost, requestConfig, headers, fileName);
    }

    public Response downloadBytesPost(String fileName, String uri, byte[] data)
    {
        return downloadBytesPost(fileName, uri, data, null, null, null);
    }

    public Response downloadBytesPost(String fileName, String uri, byte[] data, String mimeType)
    {
        return downloadBytesPost(fileName, uri, data, mimeType, null, null);
    }

    public Response downloadBytesPost(String fileName, String uri, byte[] data, String mimeType, List<NameValuePair> headers)
    {
        return downloadBytesPost(fileName, uri, data, mimeType, null, headers);
    }

    public Response downloadBytesPost(String fileName, String uri, byte[] data, String mimeType, RequestConfig requestConfig)
    {
        return downloadBytesPost(fileName, uri, data, mimeType, requestConfig, null);
    }

    public Response downloadBytesPost(String fileName, String uri, byte[] data, String mimeType, RequestConfig requestConfig, List<NameValuePair> headers)
    {
        HttpPost httpPost = createBytesPostEntity(uri, data, mimeType);

        return execute(httpPost, requestConfig, headers, fileName);
    }

    public Response downloadInputStreamPost(String fileName, String uri, InputStream data)
    {
        return downloadInputStreamPost(fileName, uri, data, null, null, null);
    }

    public Response downloadInputStreamPost(String fileName, String uri, InputStream data, String mimeType)
    {
        return downloadInputStreamPost(fileName, uri, data, mimeType, null, null);
    }

    public Response downloadInputStreamPost(String fileName, String uri, InputStream data, String mimeType, List<NameValuePair> headers)
    {
        return downloadInputStreamPost(fileName, uri, data, mimeType, null, headers);
    }

    public Response downloadInputStreamPost(String fileName, String uri, InputStream data, String mimeType, RequestConfig requestConfig)
    {
        return downloadInputStreamPost(fileName, uri, data, mimeType, requestConfig, null);
    }

    public Response downloadInputStreamPost(String fileName, String uri, InputStream data, String mimeType, RequestConfig requestConfig, List<NameValuePair> headers)
    {
        HttpPost httpPost = createInputStreamPostEntity(uri, data, mimeType);

        return execute(httpPost, requestConfig, headers, fileName);
    }

    private HttpGet createGetEntity(String uri)
    {
        return new HttpGet(uri);
    }

    private HttpPost createFormPostEntity(String uri, List<NameValuePair> data)
    {
        HttpPost httpPost = new HttpPost(uri);
        HttpEntity entity = createFormEntity(data);
        
        httpPost.setEntity(entity);
        
        return httpPost;
    }

    private HttpPost createStringPostEntity(String uri, String data, String mimeType)
    {
        if(GeneralHelper.isStrEmpty(mimeType))
            mimeType = ContentType.APPLICATION_JSON.getMimeType();
        
        HttpPost httpPost = new HttpPost(uri);
        HttpEntity entity = createStringEntity(data, mimeType);
        
        httpPost.setEntity(entity);
        
        return httpPost;
    }
    
    private HttpPost createBytesPostEntity(String uri, byte[] data, String mimeType)
    {
        if(GeneralHelper.isStrEmpty(mimeType))
            mimeType = ContentType.APPLICATION_OCTET_STREAM.getMimeType();
        
        HttpPost httpPost = new HttpPost(uri);
        HttpEntity entity = createBytesEntity(data, mimeType);
        
        httpPost.setEntity(entity);
        
        return httpPost;
    }
    
    private HttpPost createInputStreamPostEntity(String uri, InputStream data, String mimeType)
    {
        if(GeneralHelper.isStrEmpty(mimeType))
            mimeType = ContentType.APPLICATION_OCTET_STREAM.getMimeType();
        
        HttpPost httpPost = new HttpPost(uri);
        HttpEntity entity = createInputStreamEntity(data, mimeType);
        
        httpPost.setEntity(entity);
        
        return httpPost;
    }
    
    private HttpPost createFormUploadEntity(String uri, List<Pair<String, ContentBody>> data)
    {
        HttpPost httpPost = new HttpPost(uri);
        HttpEntity entity = createMultipartEntity(data);
        
        httpPost.setEntity(entity);
        
        return httpPost;
    }

    private HttpEntity createFormEntity(List<NameValuePair> params)
    {
        return new UrlEncodedFormEntity(params);
    }

    private HttpEntity createStringEntity(String content, String mimeType)
    {
        ContentType contentType = ContentType.create(mimeType, Charset.forName(encoding));
        return new StringEntity(content, contentType);
    }
    
    private HttpEntity createBytesEntity(byte[] content, String mimeType)
    {
        ContentType contentType = ContentType.create(mimeType, Charset.forName(encoding));
        return new ByteArrayEntity(content, contentType);
    }
    
    private HttpEntity createInputStreamEntity(InputStream content, String mimeType)
    {
        ContentType contentType = ContentType.create(mimeType, Charset.forName(encoding));
        return new InputStreamEntity(content, contentType);
    }
    
    private HttpEntity createMultipartEntity(List<Pair<String, ContentBody>> data)
    {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        
        builder.setMode(HttpMultipartMode.EXTENDED);
        
        for(Pair<String, ContentBody> entry : data)
            builder.addPart(entry.getFirst(), entry.getSecond());
        
        return builder.build();
    }

    private void setHttpRequestHeaders(HttpUriRequestBase request, List<NameValuePair> headers)
    {
        if(headers != null)
        {
            for(NameValuePair nv : headers)
            {
                if(!nv.getName().equals("Cookie") || (!useCookie && retainHeaderCookie))
                    request.setHeader(nv.getName(), nv.getValue());
                else if(useCookie)
                {
                    String value = GeneralHelper.safeString(nv.getValue());
                    String[] cks = value.split("\\;");
    
                    for(String ck : cks)
                    {
                        String[] c = ck.split("\\=", 2);
                        
                        if(c.length == 2)
                            addCookie(new BasicClientCookie(c[0].trim(), c[1].trim()));
                    }
                }
            }
        }
    }
    
    private Response execute(HttpUriRequestBase request, RequestConfig requestConfig, List<NameValuePair> headers)
    {
        return execute(request, requestConfig, headers, null);
    }

    private Response execute(HttpUriRequestBase request, RequestConfig requestConfig, List<NameValuePair> headers, String fileName)
    {
        HttpEntity respEntity    = null;
        Response response        = null;
        boolean isDownload        = GeneralHelper.isStrNotEmpty(fileName);
        long startTime            = System.currentTimeMillis();

        if(requestConfig != null)
            request.setConfig(requestConfig);
        if(headers != null)
            setHttpRequestHeaders(request, headers);
        
        int i = 0;
        for(; i <= retries; i++)
        {            
            final Response rs = new Response();
            response = rs;
            
            try
            {
                respEntity = httpClient.execute(request, httpContext, new HttpClientResponseHandler<HttpEntity>()
                {
                    @Override
                    public HttpEntity handleResponse(ClassicHttpResponse resp) throws HttpException, IOException
                    {
                        HttpEntity entity    = resp.getEntity();
                        int code            = resp.getCode();
            
                        rs.setStatusCode(code);
                        rs.setVersion(resp.getVersion().toString());
                        rs.setHeaders(resp.getHeaders());
                        rs.setEncoding(parseEncoding(resp));
            
                        if((code >= 200 && code < 300) || (code >= 100 && code < 200))
                        {
                            rs.setResultCode(Response.Success);
                            
                            if(isDownload)
                                saveToFile(fileName, entity, rs, resp);
                            else
                                readContent(entity, rs, resp);
                        }
                        else if(code >= 300 && code < 400)
                        {
                            rs.setResultCode(Response.Redirect);
                            rs.setContent(resp.getFirstHeader("Location").getValue().getBytes());
                        }
                        else
                        {
                            rs.setResultCode(Response.ResponseError);
                            rs.setErrorInfo(resp.getReasonPhrase());
                            readContent(entity, rs, resp);
                        }
                        
                        return entity;
                    }
    
                });
                
                break;
            }
            catch(Exception e)
            {
                if(i < retries
                    && (e instanceof IOException)
                    && !(e instanceof SSLException)
                    && !(e instanceof ClientProtocolException))
                    continue;
                
                if(e instanceof TimeoutException)
                    response.setResultCode(Response.ConnectionPoolTimeoutException);
                else if(e instanceof ConnectTimeoutException)
                    response.setResultCode(Response.ConnectTimeoutException);
                else if(e instanceof SocketTimeoutException)
                    response.setResultCode(Response.SocketTimeoutException);
                else if(e instanceof ClientProtocolException)
                    response.setResultCode(Response.ClientProtocolException);
                else if(e instanceof IOException)
                    response.setResultCode(Response.IOException);
                else
                    response.setResultCode(Response.Exception);
                
                response.setErrorInfo(e.getMessage());
                response.setCause(e);
                
                break;
            }
            finally
            {
                if(request != null)
                {
                    request.reset();
                }
                if(respEntity != null)
                {
                    try
                    {
                        EntityUtils.consume(respEntity);
                    }
                    catch(IOException e)
                    {
                    }
                }
            }
        }
        
        if(logRequest)
            logRequest(i, System.currentTimeMillis() - startTime, request, response, isDownload);
        
        return response;
    }

    private void logRequest(final int i, final long costTime, final HttpUriRequestBase request, final Response response, final boolean isDownload)
    {
        Runnable task = new MdcRunnable()
        {
            @Override
            protected void doRun()
            {
                JSONObject jsonLog = new JSONObject();
                        
                jsonLog.put("monitor_type", MONITOR_LOG_TYPE);
                jsonLog.put("retries", i);
                jsonLog.put("costTime", costTime);
                
                JSONObject jsonReq = new JSONObject();
                jsonLog.put("httpRequest", jsonReq);
                
                jsonReq.put("isDownload", isDownload);
                jsonReq.put("method", request.getMethod());
                
                String uri  = null;
                String path = null;

                try
                {
                    URI u = request.getUri();
                    uri   = u.toString();
                    path  = u.getPath();
                }
                catch(URISyntaxException e)
                {
                }

                int i = uri.indexOf('?');
                
                if(i != -1)
                {
                    jsonReq.put("param", uri.substring(i + 1));
                }
                
                jsonReq.put("uri", uri);
                jsonReq.put("path", path);
                
                parseHeaders(jsonReq, request.getHeaders(), "headers");
                
                ProtocolVersion reqProtocolVersion = request.getVersion();
                
                if(reqProtocolVersion != null)
                    jsonReq.put("protocolVersion", reqProtocolVersion.toString());            
                
                    
                HttpEntity entity = request.getEntity();
                
                if(entity != null)
                {
                    JSONObject jsonEntity = new JSONObject();
                    jsonReq.put("entity", jsonEntity);
                    
                    int contentLength        = (int)entity.getContentLength();
                    String contentType        = entity.getContentType();
                    String contentEncoding    = entity.getContentEncoding();
                    
                    jsonEntity.put("contentLength", contentLength);
                    
                    if(GeneralHelper.isStrNotEmpty(contentType))
                        jsonEntity.put("contentType", contentType);
                    if(GeneralHelper.isStrNotEmpty(contentEncoding))
                        jsonEntity.put("contentEncoding", contentEncoding);
                    
                    if(contentLength > 0 && contentLength <= MAX_LOG_CONTENT_LENGTH)
                    {
                        InputStream is = null;
                        
                        try
                        {
                            byte[] content = new byte[contentLength];
                            is = entity.getContent();
                            is.read(content);
                            
                            jsonEntity.put("content", new String(content, Charset.forName(encoding)));
                        }
                        catch(IOException e)
                        {
                            log.error("parse HttpExecutor 'requestEntityContent' fail", e);
                        }
                        finally
                        {
                            if(is != null) try {is.close();} catch(Exception e) {}
                        }
                    }
                }
                
                JSONObject jsonResp = new JSONObject();
                jsonLog.put("httpResponse", jsonResp);
                
                boolean isSuccess        = response.isSuccess();
                boolean isNoError        = response.isNoError();
                boolean isNoExecption    = response.isNoExecption();
                boolean isRedirect        = response.isRedirect();
                
                jsonResp.put("isSuccess", isSuccess);
                jsonResp.put("isNoError", isNoError);
                jsonResp.put("isNoExecption", isNoExecption);
                jsonResp.put("isRedirect", isRedirect);
                jsonResp.put("encoding", response.getEncoding());
                jsonResp.put("statusCode", response.getStatusCode());
                jsonResp.put("resultCode", response.getResultCode());
                
                parseHeaders(jsonResp, response.getHeaders(), "headers");
                
                if(!isNoError)
                    jsonResp.put("errorInfo", response.getErrorInfo());
                
                if(!isNoExecption)
                    jsonResp.put("exceptionCause", response.getCause().toString());
                else
                {
                    jsonResp.put("reasonPhrase", response.getErrorInfo());
                    
                    String respProtocolVersion = response.getVersion();
                    
                    if(GeneralHelper.isStrNotEmpty(respProtocolVersion))
                        jsonResp.put("protocolVersion", respProtocolVersion);
                }
                
                byte[] respContent = response.getContent();
                
                if(respContent != null)
                {
                    int respContentLength = respContent.length;
                    
                    jsonResp.put("contentLength", respContentLength);
                    
                    if(respContentLength > 0 && respContentLength <= MAX_LOG_CONTENT_LENGTH)
                        jsonResp.put("content", response.toString());                        
                }
                
                String msg = jsonLog.toJSONString();
                
                if(isNoError)
                    MONITOR_LOGGER.info(msg);
                else
                    MONITOR_LOGGER.error(msg);
            }
        };
            
        try
        {
            LOG_EXECUTOR.execute(task);
        }
        catch(Exception e)
        {
            log.error("async write {} log fail", MONITOR_LOG_TYPE, e);
        }
    }

    @SuppressWarnings("unchecked")
    private void parseHeaders(JSONObject jsonLog, Header[] httpHeaders, String headerName)
    {
        if(httpHeaders != null)
        {
            Map<String, Object> headers = new HashMap<>(httpHeaders.length);
            
            for(Header h : httpHeaders)
            {
                String key = h.getName();
                String val = h.getValue();
                Object curVal = headers.get(key);
                
                if(curVal == null)
                    headers.put(key, val);
                else
                {                                
                    if(curVal instanceof List)
                        ((List<String>)curVal).add(val);
                    else
                    {
                        List<String> vals = new ArrayList<>();
                        vals.add((String)curVal);
                        vals.add(val);
                        
                        headers.remove(key);
                        headers.put(key, vals);
                    }
                }
            }
            
            jsonLog.put(headerName, headers);                        
        }
    }

    private String parseEncoding(HttpResponse resp)
    {
        final String CONTENT_TYPE_HEADER = "Content-Type";
        final String CHARSET_FLAG         = "charset=";
        
        String enc            = encoding;
        Header contentType    = resp.getFirstHeader(CONTENT_TYPE_HEADER);
        
        if(contentType != null)
        {
            String value = GeneralHelper.safeTrimString(contentType.getValue());
            
            if(GeneralHelper.isStrNotEmpty(value))
            {                
                int i = value.indexOf(CHARSET_FLAG);
                
                if(i >= 0)
                {
                    value = value.substring(i + CHARSET_FLAG.length());
                    
                    int j = value.indexOf(';');
                    
                    if(j < 0) j = value.length();
                    
                    enc = value.substring(0, j);
                    
                    if(GeneralHelper.isStrNotEmpty(enc))
                    {
                        try
                        {
                            Charset.forName(enc);
                        }
                        catch(IllegalArgumentException e)
                        {
                            enc = encoding;
                        }
                    }
                }
            }
        }
        
        return enc;
    }

    private static final void readContent(HttpEntity respEntity, Response response, HttpResponse resp) throws IOException
    {
        HttpEntity entity = toFinalEntity(respEntity);
        response.setContent(EntityUtils.toByteArray(entity));
    }
    
    private static final void saveToFile(String fileName, HttpEntity respEntity, Response response, HttpResponse resp) throws IOException
    {
        File file = new File(fileName);
        HttpEntity entity = toFinalEntity(respEntity);
        
        BufferedOutputStream out = null;
        
        try
        {
            out = new BufferedOutputStream(new FileOutputStream(file));
            entity.writeTo(out);

            response.setContent(fileName.getBytes(response.getEncoding()));
        }
        finally
        {
            if(out != null) try{out.close();} catch(Exception e) {}
        }
    }
    
    private static final HttpEntity toFinalEntity(HttpEntity respEntity)
    {
        return isGZip(respEntity) ? new GzipDecompressingEntity(respEntity) : respEntity;
    }

    private static boolean isGZip(HttpEntity respEntity)
    {
        String enc = respEntity.getContentEncoding();
        return ("gzip".equals(enc));
    }
    
    public CookieStore getCookieStore()
    {
        return httpContext.getCookieStore();
    }

    public void addCookie(Cookie cookie)
    {
        getCookieStore().addCookie(cookie);
    }

    public void addCookie(String name, String value)
    {
        getCookieStore().addCookie(new BasicClientCookie(name, value));
    }
    
    public void addCookies(List<Cookie> cookies)
    {
        for(Cookie cookie : cookies)
            addCookie(cookie);
    }

    public void clearCookie()
    {
        getCookieStore().clear();
    }
    
    public void resetCookie(List<Cookie> cookies)
    {
        clearCookie();
        addCookies(cookies);
    }
    
    public List<Cookie> getCookies()
    {
        return getCookieStore().getCookies();
    }
    
    public Cookie getCookie(String name)
    {
        for(Cookie cookie : getCookies())
        {
            if(cookie.getName().equals(name))
                return cookie;
        }
        
        return null;
    }
    
    public CredentialsProvider getCredentialsProvider()
    {
        return httpContext.getCredentialsProvider();
    }
    
    public void setCredentials(AuthScope authscope, Credentials credentials)
    {
        ((CredentialsStore)getCredentialsProvider()).setCredentials(authscope, credentials);
    }
    
    public void setCredentials(HttpHost httpHost, String userName, String password)
    {
        setCredentials(new AuthScope(httpHost), new UsernamePasswordCredentials(userName, GeneralHelper.safeString(password).toCharArray()));
    }
    
    public void setCredentials(HttpHost httpHost, Credentials credentials)
    {
        setCredentials(new AuthScope(httpHost), credentials);
    }
    
    public void setCredentials(String host, int port, String userName, String password)
    {
        setCredentials(new AuthScope(host, port), new UsernamePasswordCredentials(userName, GeneralHelper.safeString(password).toCharArray()));
    }
    
    public Credentials getCredentials(AuthScope authscope)
    {
        return getCredentialsProvider().getCredentials(authscope, httpContext);
    }
    
    public Credentials getCredentials(HttpHost httpHost)
    {
        return getCredentials(new AuthScope(httpHost));
    }
    
    public Credentials getCredentials(String host, int port)
    {
        return getCredentials(new AuthScope(host, port));
    }
    
    public void setCredentialsIfAbsent(AuthScope authscope, Credentials credentials)
    {
        Credentials obj = getCredentials(authscope);
        
        if(obj == null)
            setCredentials(authscope, credentials);
    }
    
    public void setCredentialsIfAbsent(HttpHost httpHost, String userName, String password)
    {
        setCredentialsIfAbsent(new AuthScope(httpHost), new UsernamePasswordCredentials(userName, GeneralHelper.safeString(password).toCharArray()));
    }
    
    public void setCredentialsIfAbsent(HttpHost httpHost, Credentials credentials)
    {
        setCredentialsIfAbsent(new AuthScope(httpHost), credentials);
    }
    
    public void setCredentialsIfAbsent(String host, int port, String userName, String password)
    {
        setCredentialsIfAbsent(new AuthScope(host, port), new UsernamePasswordCredentials(userName, GeneralHelper.safeString(password).toCharArray()));
    }
    
    public RequestConfig.Builder copyDefaultRequestConfig()
    {
        return RequestConfig.copy(defaultRequestConfig);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response
    {
        public static final int Success                            = 0;
        public static final int Redirect                        = 1;
        public static final int ResponseError                    = 2;
        public static final int ConnectionPoolTimeoutException    = 3;
        public static final int ConnectTimeoutException            = 4;
        public static final int SocketTimeoutException            = 5;
        public static final int ClientProtocolException            = 6;
        public static final int IOException                        = 7;
        public static final int Exception                        = 8;

        public String encoding;
        public String version;
        public int statusCode = -1;
        public int resultCode = -1;
        public String errorInfo;
        @JSONField(serialize=false)
        public byte[] content;
        public Header[] headers;
        @JSONField(serialize=false)
        public Throwable cause;

        private void setCause(Throwable cause)
        {
            this.cause = cause;
        }

        public boolean isSuccess()
        {
            return    resultCode == Response.Success;
        }

        public boolean isRedirect()
        {
            return    resultCode == Response.Redirect;
        }

        public boolean isNoError()
        {
            return    resultCode == Response.Success        ||
                    resultCode == Response.Redirect        ;
        }

        public boolean isNoExecption()
        {
            return cause == null;
        }
        
        public int getContentLength()
        {
            if(content == null)
                return 0;
            
            return content.length;
        }

        @Override
        public String toString()
        {
            if(content == null)
                return "";
            
            return new String(content, Charset.forName(encoding));
        }
    }

    public <Q, R> R postJsonRequest(String uri, Q request, Class<R> clazz)
    {
        return postJsonRequest(uri, request, clazz, null);
    }

    @SuppressWarnings("unchecked")
    public <Q, R> R postJsonRequest(String uri, Q request, Class<R> clazz, List<NameValuePair> extraHeaders)
    {
        R response                    = null;
        List<NameValuePair> headers    = new ArrayList<NameValuePair>();
        
        headers.add(new BasicNameValuePair("Accept"                , "application/json"));
        headers.add(new BasicNameValuePair("Accept-Encoding"    , "gzip, deflate"));
        headers.add(new BasicNameValuePair("Accept-Language"    , "zh-CN,zh;q=0.8,en-us;q=0.5,en;q=0.3,*;q=0.2"));
        headers.add(new BasicNameValuePair("Connection"            , "keep-alive"));
        headers.add(new BasicNameValuePair("Cache-Control"        , "max-age=0"));
        headers.add(new BasicNameValuePair("Pragma"                , "no-cache"));
        headers.add(new BasicNameValuePair("User-Agent"            , "Mozilla/5.0"));
        headers.add(new BasicNameValuePair("Content-Type"       , "application/json"));
        
        if(GeneralHelper.isNotNullOrEmpty(extraHeaders))
            headers.addAll(extraHeaders);
        
        try
        {
            String data = JSON.toJSONString(request);
            HttpExecutor.Response resp = executeStringPost(uri, data, null, headers); 
            
            if(resp.isSuccess())
            {
                if(clazz.equals(byte[].class))
                    response = (R)resp.getContent();
                else
                {
                    String result = resp.toString();
                    
                    if(clazz.equals(String.class))
                        response = (R)result;
                    else
                        response = JSON.parseObject(result, clazz);
                }
            }
            else if(!resp.isNoError()){
                throw new ServiceException(resp.getErrorInfo(), ServiceException.OUTER_API_CALL_FAIL);
            }
            else
                throw new IllegalStateException(String.format("unexpected status code '%d'", resp.statusCode));
            
        }
        catch(Exception e)
        {
            throw ServiceException.wrapServiceException(e);
        }
        
        return response;
    }
    
    public <Q, R> R postFormRequest(String uri, Map<String, String> params, Class<R> clazz)
    {
        return postFormRequest(null, uri, params, clazz);
    }
    
    @SuppressWarnings("unchecked")
    public <Q, R> R postFormRequest(List<NameValuePair> headers, String uri, Map<String, String> params, Class<R> clazz)
    {
        R response = null;
        
        if(GeneralHelper.isNullOrEmpty(headers))
        {
            headers    = new ArrayList<NameValuePair>();
            headers.add(new BasicNameValuePair("Accept"                , "text/html"));
            headers.add(new BasicNameValuePair("Accept-Encoding"    , "gzip, deflate"));
            headers.add(new BasicNameValuePair("Accept-Language"    , "zh-CN,zh;q=0.8,en-us;q=0.5,en;q=0.3,*;q=0.2"));
            headers.add(new BasicNameValuePair("Connection"            , "keep-alive"));
            headers.add(new BasicNameValuePair("Cache-Control"        , "max-age=0"));
            headers.add(new BasicNameValuePair("Pragma"                , "no-cache"));
            headers.add(new BasicNameValuePair("User-Agent"            , "Mozilla/5.0"));
        }
        
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        
        if(params != null && !params.isEmpty())
        {
            for (Map.Entry<String, String> entry : params.entrySet())
            {
                String value = entry.getValue();
                if (value != null)
                {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
        }
        
        try
        {
            HttpExecutor.Response resp = executeFormPost(uri, pairs, headers);
            
            if(resp.isSuccess())
            {
                if(clazz.equals(byte[].class))
                    response = (R)resp.getContent();
                else
                {
                    String result = resp.toString();
                    
                    if(clazz.equals(String.class))
                        response = (R)result;
                    else
                        response = JSON.parseObject(result, clazz);
                }
            }
            else if(!resp.isNoError())
                throw new ServiceException(resp.getErrorInfo(), ServiceException.OUTER_API_CALL_FAIL);
            else
                throw new IllegalStateException(String.format("unexpected status code '%d'", resp.statusCode));
            
        }
        catch(Exception e)
        {
            throw ServiceException.wrapServiceException(e);
        }
        
        return response;
    }
    
    public final <Q, R> R getRequest(String uri, Class<R> clazz)
    {
        return getRequest(uri, clazz, null);
    }
    
    @SuppressWarnings("unchecked")
    public <Q, R> R getRequest(String uri, Class<R> clazz, List<NameValuePair> extraHeaders)
    {
        R response                    = null;
        List<NameValuePair> headers    = new ArrayList<NameValuePair>();
        
        headers.add(new BasicNameValuePair("Accept"                , "*/*"));
        headers.add(new BasicNameValuePair("Accept-Encoding"    , "gzip, deflate"));
        headers.add(new BasicNameValuePair("Accept-Language"    , "zh-CN,zh;q=0.8,en-us;q=0.5,en;q=0.3,*;q=0.2"));
        headers.add(new BasicNameValuePair("Connection"            , "keep-alive"));
        headers.add(new BasicNameValuePair("Cache-Control"        , "max-age=0"));
        headers.add(new BasicNameValuePair("Pragma"                , "no-cache"));
        headers.add(new BasicNameValuePair("User-Agent"            , "Mozilla/5.0"));
        
        if(GeneralHelper.isNotNullOrEmpty(extraHeaders))
            headers.addAll(extraHeaders);
        
        try
        {
            HttpExecutor.Response resp = executeGet(uri, headers);
            
            if(resp.isSuccess())
            {
                if(clazz.equals(byte[].class))
                    response = (R)resp.getContent();
                else
                {
                    String result = resp.toString();
                    
                    if(clazz.equals(String.class))
                        response = (R)result;
                    else
                        response = JSON.parseObject(result, clazz);
                }
            }
            else if(!resp.isNoError())
                throw new ServiceException(resp.getErrorInfo(), ServiceException.OUTER_API_CALL_FAIL);
            else
                throw new IllegalStateException(String.format("unexpected status code '%d'", resp.statusCode));
            
        }
        catch(Exception e)
        {
            throw ServiceException.wrapServiceException(e);
        }
        
        return response;
    }
    
    public static final HttpExecutor getHttpExecutorInstance(
        int connectionRequestTimeout, int connectTimeout, int socketTimeout,
        int maxConnectionTotal, int maxConnectionPerRoute, int retries, boolean useCookie, HttpHost httpProxy)
    {
        HttpExecutor executor = new HttpExecutor();
        
        executor.setConnectionRequestTimeout(connectionRequestTimeout);
        executor.setConnectTimeout(connectTimeout);
        executor.setSocketTimeout(socketTimeout);
        executor.setMaxConnectionTotal(maxConnectionTotal);
        executor.setMaxConnectionPerRoute(maxConnectionPerRoute);
        executor.setRetries(retries);
        executor.setUseCookie(useCookie);
        executor.setHttpProxy(httpProxy);
        executor.setLogRequest(true);
        
        return executor.initialize();
    }
    
    public static final HttpExecutor getHttpExecutorInstance(
        int connectionRequestTimeout, int connectTimeout, int socketTimeout,
        int maxConnectionTotal, int maxConnectionPerRoute, boolean useCookie, HttpHost httpProxy)
    {
        return getHttpExecutorInstance(connectionRequestTimeout, connectTimeout, socketTimeout, maxConnectionTotal, maxConnectionPerRoute, DEFAULT_RETRIES, useCookie, httpProxy);
    }

    public static final HttpExecutor getHttpExecutorInstance(
        int connectionRequestTimeout, int connectTimeout, int socketTimeout,
        int maxConnectionTotal, int maxConnectionPerRoute, boolean useCookie)
    {
        return getHttpExecutorInstance(connectionRequestTimeout, connectTimeout, socketTimeout, maxConnectionTotal, maxConnectionPerRoute, useCookie, true);
    }
    
    public static final HttpExecutor getHttpExecutorInstance(int connectionRequestTimeout, int connectTimeout, int socketTimeout,
                                        int maxConnectionTotal, int maxConnectionPerRoute, boolean useCookie, boolean useProxy)
    {
        HttpHost httpProxy = useProxy ? getSystemHttpProxy() : null;
        return getHttpExecutorInstance(connectionRequestTimeout, connectTimeout, socketTimeout, maxConnectionTotal, maxConnectionPerRoute, useCookie, httpProxy);
    }
    
    public static final HttpExecutor getHttpExecutorInstance(
        int connectionRequestTimeout, int connectTimeout, int socketTimeout,
        int maxConnectionTotal, int maxConnectionPerRoute, int retries, boolean useCookie)
    {
        return getHttpExecutorInstance(connectionRequestTimeout, connectTimeout, socketTimeout, maxConnectionTotal, maxConnectionPerRoute, retries, useCookie, true);
    }
    
    public static final HttpExecutor getHttpExecutorInstance(
        int connectionRequestTimeout, int connectTimeout, int socketTimeout,
        int maxConnectionTotal, int maxConnectionPerRoute, int retries, boolean useCookie, boolean useProxy)
    {
        HttpHost httpProxy = useProxy ? getSystemHttpProxy() : null;
        return getHttpExecutorInstance(connectionRequestTimeout, connectTimeout, socketTimeout, maxConnectionTotal, maxConnectionPerRoute, retries, useCookie, httpProxy);
    }
    
    public static final HttpExecutor getHttpExecutorInstance()
    {
        return getHttpExecutorInstance(false);
    }
    
    public static final HttpExecutor getHttpExecutorInstance(boolean useCookie)
    {
        return getHttpExecutorInstance(DEFAULT_RETRIES, useCookie);
    }
    
    public static final HttpExecutor getHttpExecutorInstance(int retries, boolean useCookie)
    {
        return getHttpExecutorInstance(retries, useCookie, false);
    }
    
    public static final HttpExecutor getHttpExecutorInstance(boolean useCookie, boolean useProxy)
    {
        return getHttpExecutorInstance(DEFAULT_RETRIES, useCookie, useProxy);
    }
    
    public static final HttpExecutor getHttpExecutorInstance(int retries, boolean useCookie, boolean useProxy)
    {
        return getHttpExecutorInstance(DEFAULT_CONNECTION_REQUEST_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_SOCKET_TIMEOUT, DEFAULT_MAX_CONNECTION_TOTAL, DEFAULT_MAX_CONNECTION_PER_ROUTE, retries, useCookie, useProxy);
    }
    
    private static final HttpHost getSystemHttpProxy()
    {
        if(!"true".equalsIgnoreCase(System.getProperty("http.proxySet")))
            return null;
        
        return new HttpHost(System.getProperty("http.proxyHost"), GeneralHelper.str2Int(System.getProperty("http.proxyPort"), -1));
    }

}
