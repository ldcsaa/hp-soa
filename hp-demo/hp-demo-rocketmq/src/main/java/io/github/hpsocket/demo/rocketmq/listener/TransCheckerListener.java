package io.github.hpsocket.demo.rocketmq.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.rocketmq.client.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.apis.producer.TransactionResolution;
import org.apache.rocketmq.client.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.client.core.RocketMQTransactionChecker;

import io.github.hpsocket.soa.starter.rocketmq.util.RocketmqConstant;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** 事务检查监听器 */
@Slf4j
@Getter
@RocketMQTransactionListener(rocketMQTemplateBeanName = RocketMQAutoConfiguration.ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME)
public class TransCheckerListener implements RocketMQTransactionChecker
{
    private Set<String> bizIds = new HashSet<>();

    @Override
    public TransactionResolution check(MessageView messageView)
    {
        String bizId = messageView.getProperties().get(RocketmqConstant.HEADER_CORRELATION_ID);
        
        if(!bizIds.contains(bizId))
        {
            log.warn("'bizId' NOT found, then ROLLBACK transaction -> (internalMessageId: {}, bizId: {})", messageView.getMessageId().toString(), bizId);
            return TransactionResolution.ROLLBACK;
        }
        
        int val = ThreadLocalRandom.current().nextInt(10);
        boolean rs = ((val & 0x1) == 0);
                
        log.info("{} transaction -> (internalMessageId: {}, bizId: {})", rs ? "COMMIT" : "IGNORE", messageView.getMessageId().toString(), bizId);
        
        return rs ? TransactionResolution.COMMIT : TransactionResolution.UNKNOWN;
    }
    
    public void addBizId(String bizId)
    {
        bizIds.add(bizId);
    }

}
