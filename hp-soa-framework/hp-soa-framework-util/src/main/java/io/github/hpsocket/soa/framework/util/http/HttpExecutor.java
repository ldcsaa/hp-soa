
package io.github.hpsocket.soa.framework.util.http;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
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

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.ConnectTimeoutException;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.CredentialsStore;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieOrigin;
import org.apache.hc.client5.http.cookie.CookieSpec;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.cookie.MalformedCookieException;
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
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;

import io.github.hpsocket.soa.framework.core.mdc.MdcRunnable;
import io.github.hpsocket.soa.framework.core.thread.AsyncThreadPoolExecutor;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.core.util.Pair;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class HttpExecutor
{
	public static final int DEFAULT_RETRIES						= 0;
	public static final int DEFAULT_MAX_CONNECTION_TOTAL		= 100;
	public static final int DEFAULT_MAX_CONNECTION_PER_ROUTE	= 30;
	public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT	= 50 * 1000;
	public static final int DEFAULT_CONNECTION_TIMEOUT			= 10 * 1000;
	public static final int DEFAULT_RESPONSE_TIMEOUT			= 20 * 1000;
	public static final int DEFAULT_SOCKET_TIMEOUT				= 30 * 1000;
	public static final String DEFAULT_ENCODING					= "UTF-8";
	public static final String LAX_COOKIE_SPEC					= "lax";
	
	private static final int MAX_LOG_CONTENT_LENGTH				= 5 * 1000;
	private static final String MONITOR_LOGGER_NAME				= "SOA-MONITOR";
	private static final String MONITOR_LOG_TYPE				= "MONITOR-EGRESS";
	private static final Logger MONITOR_LOGGER					= LoggerFactory.getLogger(MONITOR_LOGGER_NAME);
	
	private static final ThreadPoolExecutor LOG_EXECUTOR = new ThreadPoolExecutor(
																					4,
																					16,
																					30,
																					TimeUnit.SECONDS,
																					new LinkedBlockingDeque<>(2000),
																					new ThreadPoolExecutor.CallerRunsPolicy());																		 

	private RequestConfig defaultRequestConfig;
	private HttpClientContext httpContext;
	private CloseableHttpClient httpClient;
	private PoolingHttpClientConnectionManager connManager;
	
	private boolean useCookie				= true;
	private boolean redirectsEnabled		= true;
	private boolean retainHeaderCookie		= true;
	private boolean trustAllCerts			= true;
	private boolean credentialsSupported	= false;
	private boolean logRequest				= false;
	private int retries						= DEFAULT_RETRIES;
	private int maxConnectionTotal			= DEFAULT_MAX_CONNECTION_TOTAL;
	private int maxConnectionPerRoute		= DEFAULT_MAX_CONNECTION_PER_ROUTE;
	private int connectionRequestTimeout	= DEFAULT_CONNECTION_REQUEST_TIMEOUT;
	private int connectTimeout				= DEFAULT_CONNECTION_TIMEOUT;
	private int responseTimeout				= DEFAULT_RESPONSE_TIMEOUT;
	private int socketTimeout				= DEFAULT_SOCKET_TIMEOUT;
	private String encoding					= DEFAULT_ENCODING;
	
	private HttpHost httpProxy;
	private String[] supportedProtocols;
	
	private String sslKeyStoreType;
	private String sslKeyStoreFile;
	private String sslKeyStorePassword;
	private String sslKeyPassword;

	public HttpExecutor()
	{

	}
	
	private static class LaxCookieSpec extends DefaultCookieSpec
	{
		private static final LaxCookieSpec INSTANCE = new LaxCookieSpec();
		
		@Override
		public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException
		{
			// pass any ~
		}
	}

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
															.setCircularRedirectsAllowed(redirectsEnabled);
										
					defaultRequestConfig = requestBuilder.build();

					ConnectionConfig.Builder connBuilder = ConnectionConfig.custom()
															.setConnectTimeout(Timeout.ofMilliseconds(connectTimeout))
															.setSocketTimeout(Timeout.ofMilliseconds(socketTimeout))
															.setCharset(Charset.forName(encoding));
					
					ConnectionConfig connCfg = connBuilder.build();
					
					connManager = new PoolingHttpClientConnectionManager();
					connManager.setMaxTotal(maxConnectionTotal);
					connManager.setDefaultMaxPerRoute(maxConnectionPerRoute);
					connManager.setDefaultConnectionConfig(connCfg);
					
					HttpClientBuilder clientBuilder	= HttpClients.custom()
														.setConnectionManager(connManager)
														.setDefaultRequestConfig(defaultRequestConfig);
					
					if(httpProxy != null)
						clientBuilder.setProxy(httpProxy);

					httpContext = HttpClientContext.create();

					if(useCookie)
					{
						Registry<CookieSpecProvider> registry = RegistryBuilder.<CookieSpecProvider> create()
																	.register(CookieSpecs.DEFAULT, new DefaultCookieSpecProvider())
																	.register(LAX_COOKIE_SPEC, new DefaultCookieSpecProvider() {
																		@Override
																		public CookieSpec create(HttpContext context)
																		{
																			return LaxCookieSpec.INSTANCE;
																		}
																	})
																	.build();
						httpContext.setCookieSpecRegistry(registry);
						httpContext.setCookieStore(new BasicCookieStore());
					}
					else
						clientBuilder.disableCookieManagement();
					
					if(trustAllCerts)
					{
						clientBuilder.setSSLSocketFactory(createSSLConnectionSocketFactory());
					}
					
					if(credentialsSupported)
					{
						httpContext.setCredentialsProvider(new SystemDefaultCredentialsProvider());
						httpContext.setAuthCache(new BasicAuthCache());
						httpContext.setAuthSchemeRegistry(RegistryBuilder.<AuthSchemeProvider> create()
															.register(AuthSchemes.BASIC, new BasicSchemeFactory())
															.register(AuthSchemes.DIGEST, new DigestSchemeFactory())
															.register(AuthSchemes.NTLM, new NTLMSchemeFactory())
															.register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory())
															.register(AuthSchemes.KERBEROS, new KerberosSchemeFactory())
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
		try
		{
			if(connManager != null)
				connManager.close();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
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
		HttpEntity respEntity	= null;
		Response response		= null;
		boolean isDownload		= GeneralHelper.isStrNotEmpty(fileName);
		long startTime			= System.currentTimeMillis();

		if(requestConfig != null)
			request.setConfig(requestConfig);
		if(headers != null)
			setHttpRequestHeaders(request, headers);
		
		int i = 0;
		for(; i <= retries; i++)
		{
			response	= new Response();
			
			try
			{
				CloseableHttpResponse resp	= httpClient.execute(request, httpContext);
				respEntity					= resp.getEntity();
				int code					= resp.getCode();
	
				response.setStatusCode(code);
				response.setVersion(resp.getVersion().toString());
				response.setHeaders(resp.getHeaders());
				response.setEncoding(parseEncoding(resp));
	
				if((code >= 200 && code < 300) || (code >= 100 && code < 200))
				{
					response.setResultCode(Response.Success);
					
					if(isDownload)
						saveFile(fileName, respEntity, response, resp);
					else
						readContent(respEntity, response, resp);
				}
				else if(code >= 300 && code < 400)
				{
					response.setResultCode(Response.Redirect);
					response.setContent(resp.getFirstHeader("Location").getValue().getBytes());
				}
				else
				{
					response.setResultCode(Response.ResponseError);
					response.setErrorInfo(resp.getReasonPhrase());
					readContent(respEntity, response, resp);
				}
				
				break;
			}
			catch(Exception e)
			{
				if(i < retries && (e instanceof IOException) && !(e instanceof ClientProtocolException))
					continue;
				
				if(e instanceof ConnectTimeoutException)
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
				JSONObject jsonLog	= new JSONObject();
						
				jsonLog.put("monitor_type", MONITOR_LOG_TYPE);
				jsonLog.put("retries", i);
				jsonLog.put("costTime", costTime);
				
				jsonLog.put("requestIsDownload", isDownload);
				jsonLog.put("requestMethod", request.getMethod());
				
				String uriFull	= request.getRequestUri();
				String uri		= uriFull;
				int i			= uriFull.indexOf('?');
				
				if(i != -1)
				{
					uri = uriFull.substring(0, i);
					jsonLog.put("requestUriQuery", uriFull.substring(i + 1));
				}
				
				jsonLog.put("requestUri", uri);
				jsonLog.put("requestUriFull", uriFull);
				
				parseHeaders(jsonLog, request.getHeaders(), "requestHeaders");
				
				ProtocolVersion reqProtocolVersion = request.getVersion();
				
				if(reqProtocolVersion != null)
					jsonLog.put("requestProtocolVersion", reqProtocolVersion.toString());			
				
					
				HttpEntity entity = request.getEntity();
				
				if(entity != null)
				{
					int contentLength		= (int)entity.getContentLength();
					String contentType		= entity.getContentType();
					String contentEncoding	= entity.getContentEncoding();
					
					jsonLog.put("requestEntityContentLength", contentLength);
					
					if(GeneralHelper.isStrNotEmpty(contentType))
						jsonLog.put("requestEntityContentType", contentType);
					if(GeneralHelper.isStrNotEmpty(contentEncoding))
						jsonLog.put("requestEntityContentEncoding", contentEncoding);
					
					if(contentLength > 0 && contentLength <= MAX_LOG_CONTENT_LENGTH)
					{
						InputStream is = null;
						
						try
						{
							byte[] content = new byte[contentLength];
							is = entity.getContent();
							is.read(content);
							
							jsonLog.put("requestEntityContent", new String(content, Charset.forName(encoding)));
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
				
				boolean isSuccess		= response.isSuccess();
				boolean isNoError		= response.isNoError();
				boolean isNoExecption	= response.isNoExecption();
				boolean isRedirect		= response.isRedirect();
				
				jsonLog.put("responseIsSuccess", isSuccess);
				jsonLog.put("responseIsNoError", isNoError);
				jsonLog.put("responseIsNoExecption", isNoExecption);
				jsonLog.put("responseIsRedirect", isRedirect);
				jsonLog.put("responseEncoding", response.getEncoding());
				jsonLog.put("responseStatusCode", response.getStatusCode());
				jsonLog.put("responseResultCode", response.getResultCode());
				
				parseHeaders(jsonLog, response.getHeaders(), "responseHeaders");
				
				if(!isNoError)
					jsonLog.put("responseErrorInfo", response.getErrorInfo());
				
				if(!isNoExecption)
					jsonLog.put("responseExceptionCause", response.getCause().toString());
				else
				{
					jsonLog.put("responseReasonPhrase", response.getErrorInfo());
					
					String respProtocolVersion = response.getVersion();
					
					if(GeneralHelper.isStrNotEmpty(respProtocolVersion))
						jsonLog.put("responseProtocolVersion", respProtocolVersion);
				}
				
				byte[] respContent = response.getContent();
				
				if(respContent != null)
				{
					int respContentLength = respContent.length;
					
					jsonLog.put("responseContentLength", respContentLength);
					
					if(respContentLength > 0 && respContentLength <= MAX_LOG_CONTENT_LENGTH)
						jsonLog.put("responseContent", response.toString());						
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
			log.error("async write {} log fail", e);
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
		final String CHARSET_FLAG		 = "charset=";
		
		String enc			= encoding;
		Header contentType	= resp.getFirstHeader(CONTENT_TYPE_HEADER);
		
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

	private void readContent(HttpEntity respEntity, Response response, HttpResponse resp) throws IOException
	{
		InputStream is = respEntity.getContent();
		byte[] content = HttpHelper.readBytes(is);
		
		if(isGZip(respEntity))
			content = HttpHelper.unGZip(content);

		response.setContent(content);
	}
	
	private void saveFile(String fileName, HttpEntity respEntity, Response response, HttpResponse resp) throws IOException
	{
		File file = new File(fileName);
		BufferedOutputStream out = null;
		
		try
		{
			out = new BufferedOutputStream(new FileOutputStream(file));

			if(isGZip(respEntity))
				HttpHelper.unGZip(respEntity.getContent(), out);
			else
				respEntity.writeTo(out);

			response.setContent(fileName.getBytes(response.getEncoding()));
		}
		finally
		{
			if(out != null) try{out.close();} catch(Exception e) {}
		}
	}

	private boolean isGZip(HttpEntity respEntity)
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
	
	public RequestConfig.Builder createLaxCookieRequestConfig()
	{
		return copyDefaultRequestConfig().setCookieSpec(LAX_COOKIE_SPEC);
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class Response
	{
		public static final int Success							= 0;
		public static final int Redirect						= 1;
		public static final int ResponseError					= 2;
		public static final int ConnectionPoolTimeoutException	= 3;
		public static final int ConnectTimeoutException			= 4;
		public static final int SocketTimeoutException			= 5;
		public static final int ClientProtocolException			= 6;
		public static final int IOException						= 7;
		public static final int Exception						= 8;

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
			return	resultCode == Response.Success;
		}

		public boolean isRedirect()
		{
			return	resultCode == Response.Redirect;
		}

		public boolean isNoError()
		{
			return	resultCode == Response.Success		||
					resultCode == Response.Redirect		;
		}

		public boolean isNoExecption()
		{
			return cause == null;
		}

		@Override
		public String toString()
		{
			if(content == null)
				return "";
			
			return new String(content, Charset.forName(encoding));
		}
	}

	public void close()
	{
		if(httpClient != null)
		{
			try
			{
				httpClient.close();
			}
			catch(IOException e)
			{
			}
		}
	}
	
}
