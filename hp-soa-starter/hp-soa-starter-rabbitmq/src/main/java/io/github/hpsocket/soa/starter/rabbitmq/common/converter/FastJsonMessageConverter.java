
package io.github.hpsocket.soa.starter.rabbitmq.common.converter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

import com.alibaba.fastjson2.JSON;

import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.rabbitmq.producer.entity.DomainEvent;

public class FastJsonMessageConverter extends AbstractMessageConverter
{

	@Override
	public Object fromMessage(Message message) throws MessageConversionException
	{
		return new String(message.getBody(), WebServerHelper.DEFAULT_CHARSET_OBJ);
	}

	@Override
	protected Message createMessage(Object object, MessageProperties messageProperties)
	{
		if(object instanceof DomainEvent event)
			return event.toMessage(messageProperties);
		
		byte[] bytes = JSON.toJSONBytes(object);
		messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
		messageProperties.setContentEncoding(WebServerHelper.DEFAULT_CHARSET);
		messageProperties.setContentLength(bytes.length);
		
		return new Message(bytes, messageProperties);
	}

}
