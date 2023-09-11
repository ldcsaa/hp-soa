package io.github.hpsocket.soa.starter.job.xxljob.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/** <b>HP-SOA XxlJob 属性</b> */
@Getter
@Setter
@ConfigurationProperties(prefix = "xxl.job")
@ConditionalOnProperty(name = "xxl.job.enabled", matchIfMissing = true)
public class SoaXxlJobProperties
{
	private boolean enabled = true;

    private String accessToken;

    Admin admin = new Admin();
    Executor executor = new Executor();
    
    @Getter
    @Setter
    public static class Admin
    {
        private String addresses;    	
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
