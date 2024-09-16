package io.github.hpsocket.soa.starter.kafka.producer.interceptor;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;
import org.slf4j.MDC;

import io.github.hpsocket.soa.framework.core.id.IdGenerator;
import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.kafka.util.KafkaConstant;
import io.github.hpsocket.soa.starter.kafka.util.KafkaHelper;

public class ProducerMdcInterceptor<K, V> implements ProducerInterceptor<K, V>
{
    @Override
    public void configure(Map<String, ?> configs)
    {
        
    }
    
    @Override
    public ProducerRecord<K, V> onSend(ProducerRecord<K, V> rc)
    {
        WebServerHelper.assertAppIsNotReadOnly();
        
        Headers headers = rc.headers();
        
        if(headers != null)
        {
            byte[] messageId       = KafkaHelper.getHeaderRowValue(headers, KafkaConstant.HEADER_MSG_ID);
            byte[] sourceRequestId = KafkaHelper.getHeaderRowValue(headers, KafkaConstant.HEADER_SOURCE_REQUEST_ID);
            
            if(messageId == null || messageId.length == 0)
                KafkaHelper.addHeader(headers, KafkaConstant.HEADER_MSG_ID, IdGenerator.nextIdStr());
            if(sourceRequestId == null || sourceRequestId.length == 0)
                KafkaHelper.addHeader(headers, KafkaConstant.HEADER_SOURCE_REQUEST_ID, MDC.get(MdcAttr.MDC_REQUEST_ID_KEY));
        }
        
        return rc;
    }
    
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception)
    {
    
    }
    
    @Override
    public void close()
    {
        
    }
}
