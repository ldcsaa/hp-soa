package io.github.hpsocket.soa.starter.web.config;

import io.github.hpsocket.soa.framework.web.aspect.AccessVerificationInspector;
import io.github.hpsocket.soa.framework.web.aspect.SiteLocalInspector;
import io.github.hpsocket.soa.framework.web.propertries.IAccessVerificationProperties;
import io.github.hpsocket.soa.framework.web.service.AccessVerificationService;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/** <b>HP-SOA Web Aspect 配置</b> */
@AutoConfiguration
@Import({
            SiteLocalInspector.class
        })
public class AspectAopConfig
{
    /** {@linkplain AccessVerificationService} HTTP 请求校验拦截器配置 */
    @Bean
    @ConditionalOnProperty(name="hp.soa.web.access-verification.enabled", havingValue="true", matchIfMissing = true)
    AccessVerificationInspector accessVerificationInspector(
        IAccessVerificationProperties accessVerificationProperties,
        AccessVerificationService accessVerificationService)
    {
        return new AccessVerificationInspector(accessVerificationProperties.getDefaultAccessPolicyEnum(), accessVerificationService);
    }    
}
