
package io.github.hpsocket.soa.starter.seata.config;
        
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.apache.seata.spring.boot.autoconfigure.SeataAutoConfiguration;

import static org.apache.seata.spring.boot.autoconfigure.StarterConstants.SEATA_PREFIX;

/** <b>HP-SOA Seata 配置</b> */
@AutoConfiguration
@AutoConfigureBefore({SeataAutoConfiguration.class})
@ConditionalOnProperty(prefix = SEATA_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class SoaSeataConfig
{

}
