
package io.github.hpsocket.soa.starter.rabbitmq.common.converter;

import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

import io.github.hpsocket.soa.framework.core.id.IdGenerator;
import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;

import static io.github.hpsocket.soa.starter.rabbitmq.common.util.RabbitmqConstant.*;

/** <b>普通文本消息转换器</b> */
public class TextMessageConverter extends AbstractMessageConverter
{
	@Override
	public Object fromMessage(Message message) throws MessageConversionException
	{
		return new String(message.getBody(), WebServerHelper.DEFAULT_CHARSET_OBJ);
	}

	@Override
	protected Message createMessage(Object object, MessageProperties messageProperties)
	{
		byte[] bytes = object.toString().getBytes(WebServerHelper.DEFAULT_CHARSET_OBJ);
		String msgId = IdGenerator.nextIdStr();
		String sourceRequestId = MDC.get(MdcAttr.MDC_REQUEST_ID_KEY);
		
		messageProperties.setMessageId(msgId);
		messageProperties.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN);
		messageProperties.setContentEncoding(WebServerHelper.DEFAULT_CHARSET);
		messageProperties.setContentLength(bytes.length);
		messageProperties.setHeader(HEADER_MSG_ID, msgId);
		messageProperties.setHeader(HEADER_SOURCE_REQUEST_ID, sourceRequestId);
		
		return new Message(bytes, messageProperties);
	}

}
