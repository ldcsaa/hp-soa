
package io.github.hpsocket.soa.starter.sentinel.config;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.UrlCleaner;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson2.JSON;
import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.starter.sentinel.advice.SentinelExceptionAdvice;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/** <b>HP-SOA Sentinel 配置</b> */
@Slf4j
@AutoConfiguration
@Import(SentinelExceptionAdvice.class)
@ConditionalOnProperty(name = "spring.cloud.sentinel.enabled", matchIfMissing = true)
public class SoaSentinelConfig
{
	/** 资源变换器 */
	@Bean
	@ConditionalOnMissingBean(UrlCleaner.class)
	@ConditionalOnProperty(name = "spring.cloud.sentinel.filter.enabled", matchIfMissing = true)
	public UrlCleaner urlCleaner()
	{
		final Set<String> suffixSet = new HashSet<>(Arrays.asList(".js", ".css", ".html", ".ico", ".txt", ".md", ".jpg", ".png"));
		
		return new UrlCleaner()
		{
			@Override
			public String clean(String originUrl)
			{
				if(GeneralHelper.isStrEmpty(originUrl))
					return originUrl;
				
				int i = originUrl.lastIndexOf('.');
				
				if(i < 0)
					return originUrl;
				
				String suffix = originUrl.substring(i).toLowerCase();
				
				if(suffixSet.contains(suffix))
					return null;
				
				return originUrl;
			}
		};
	}
	
	/** 限流处理器 */
	@Bean
	@ConditionalOnMissingBean(BlockExceptionHandler.class)
	@ConditionalOnProperty(name = "spring.cloud.sentinel.filter.enabled", matchIfMissing = true)
	public BlockExceptionHandler blockExceptionHandler()
	{
		return new BlockExceptionHandler()
		{
			@Override
			public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception
			{
				ServiceException se = wrapServiceException(FREQUENCY_LIMIT_EXCEPTION, e);

				logServiceException(log, se.getMessage(), se);

		        response.setStatus(FREQUENCY_LIMIT_ERROR);
				response.setContentType("application/json; charset=utf-8");
		        
				try(PrintWriter out = response.getWriter())
				{
			        out.print(JSON.toJSONString(new Response<>(se)));
			        out.flush();
				}
			}
		};
	}

}
