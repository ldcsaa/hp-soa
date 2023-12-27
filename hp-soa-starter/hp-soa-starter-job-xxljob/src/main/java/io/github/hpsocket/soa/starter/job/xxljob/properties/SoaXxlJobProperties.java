package io.github.hpsocket.soa.starter.job.xxljob.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/** <b>HP-SOA XxlJob 属性</b> */
@Getter
@Setter
@ConfigurationProperties(prefix = "hp.soa.job.xxl")
@ConditionalOnProperty(name = "hp.soa.job.xxl.enabled", matchIfMissing = true)
public class SoaXxlJobProperties
{
    private boolean enabled = true;

    Admin admin = new Admin();
    Executor executor = new Executor();
    
    @Getter
    @Setter
    public static class Admin
    {
        private String addresses;        
        private String accessToken;
    }
    
    @Getter
    @Setter
    public static class Executor
    {
        private String appname;
        private String address;
        private String ip;
        private int port;
        private String logPath;
        private int logRetentionDays;        
    }

}
