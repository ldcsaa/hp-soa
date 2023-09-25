
package io.github.hpsocket.soa.starter.data.redis.serializer;

/** <b>Kryo 序列化器</b><br>
 * 该序列化器支持存储 null 值
 */
public class KryoRedisSerializer<T> extends BaseKryoRedisSerializer<T>
{
    @Override
    protected boolean checkSerializeParam(T t)
    {
        if(t == null)
        {
            return false;
        }
        
        return true;
    }
    
    @Override
    protected boolean checkDeserializeParam(byte[] bytes)
    {
        if(bytes == null || bytes.length == 0)
        {
            return false;
        }
        
        return true;
    }
}
