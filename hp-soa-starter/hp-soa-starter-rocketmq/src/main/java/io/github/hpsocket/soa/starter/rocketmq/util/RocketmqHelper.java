
package io.github.hpsocket.soa.starter.rocketmq.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.apache.rocketmq.client.apis.message.MessageView;

/** <b>RocketMQ 通用辅助类</b> */
public class RocketmqHelper
{
    /** {@linkplain MessageView} 消息内容转为 {@linkplain byte[]} */
    public static final byte[] getMessageViewBody(MessageView messageView)
    {
        ByteBuffer byteBuffer = messageView.getBody();
        byte[] body = new byte[byteBuffer.remaining()];
        
        byteBuffer.get(body);
        return body;
    }

    /** {@linkplain MessageView} 消息内容转为 {@linkplain String} */
    public static final String getMessageViewBodyAsString(MessageView messageView)
    {
        return StandardCharsets.UTF_8.decode(messageView.getBody()).toString();
    }
}
