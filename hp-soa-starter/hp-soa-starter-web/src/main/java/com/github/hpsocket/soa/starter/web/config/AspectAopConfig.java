package com.github.hpsocket.soa.starter.web.config;

import com.github.hpsocket.soa.framework.web.aspect.AccessVerificationInspector;
import com.github.hpsocket.soa.framework.web.aspect.SiteLocalInspector;
import com.github.hpsocket.soa.framework.web.propertries.IAccessVerificationProperties;
import com.github.hpsocket.soa.framework.web.service.AccessVerificationService;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import({
			SiteLocalInspector.class
        })
public class AspectAopConfig
{
	@Bean
	@ConditionalOnProperty(name="hp.soa.web.access-verification.enabled", havingValue="true", matchIfMissing = true)
	AccessVerificationInspector accessVerificationInspector(IAccessVerificationProperties accessVerificationProperties, AccessVerificationService accessVerificationService)
	{
		return new AccessVerificationInspector(accessVerificationProperties.getDefaultAccessPolicyEnum(), accessVerificationService);
	}
}
