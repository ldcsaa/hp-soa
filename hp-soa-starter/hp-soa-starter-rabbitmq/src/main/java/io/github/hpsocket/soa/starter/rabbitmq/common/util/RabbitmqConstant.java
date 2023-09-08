package io.github.hpsocket.soa.starter.rabbitmq.common.util;

public class RabbitmqConstant
{
	public static final String HEADER_DOMAIN_NAME		= "x-domain-name";
	public static final String HEADER_EVENT_NAME		= "x-event-name";
	public static final String HEADER_MSG_ID			= "x-msg-id";
	public static final String HEADER_SOURCE_REQUEST_ID	= "x-source-request-id";
	public static final String HEADER_CORRELA_DATA_ID	= "amqp_correlationId";
	public static final String HEADER_AMQP_MESSAGE_ID	= "amqp_messageId";
	public static final String HEADER_AMQP_DELIVERY_TAG	= "amqp_deliveryTag";
	
	
	public static final int SF_NOT_SEND		= 0;
	public static final int SF_SENDING		= 1;
	public static final int SF_SEND_SUCC	= 2;
	public static final int SF_SEND_FAIL	= 3;


}
