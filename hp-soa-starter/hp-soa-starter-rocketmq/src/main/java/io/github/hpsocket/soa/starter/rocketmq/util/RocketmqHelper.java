
package io.github.hpsocket.soa.starter.rocketmq.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.apache.rocketmq.client.apis.message.MessageView;

public class RocketmqHelper
{
    public static final byte[] getMessageViewBody(MessageView messageView)
    {
        ByteBuffer byteBuffer = messageView.getBody();
        byte[] body = new byte[byteBuffer.remaining()];
        
        byteBuffer.get(body);
        return body;
    }

    public static final String getMessageViewBodyAsString(MessageView messageView)
    {
        return StandardCharsets.UTF_8.decode(messageView.getBody()).toString();
    }
}
