package io.github.hpsocket.demo.rocketmq.listener;

import java.util.HashSet;
import java.util.Set;

import org.apache.rocketmq.client.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.apis.producer.TransactionResolution;
import org.apache.rocketmq.client.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.client.core.RocketMQTransactionChecker;

import io.github.hpsocket.soa.starter.rocketmq.util.RocketmqConstant;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
            log.warn("invalid bizId: {}", bizId);
            return TransactionResolution.ROLLBACK;
        }
        
        return TransactionResolution.COMMIT;
    }
    
    public void addBizId(String bizId)
    {
        bizIds.add(bizId);
    }

}
