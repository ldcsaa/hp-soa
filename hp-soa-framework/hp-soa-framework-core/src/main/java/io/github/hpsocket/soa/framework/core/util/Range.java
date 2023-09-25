package io.github.hpsocket.soa.framework.core.util;

/** <b>通用数值范围类</b> */
public class Range<T extends Number>
{
    private T begin;
    private T end;
    
    public Range()
    {
        
    }
    
    public Range(T begin, T end)
    {
        this.begin  = begin;
        this.end    = end;
    }

    public T getBegin()
    {
        return begin;
    }

    public void setBegin(T begin)
    {
        this.begin = begin;
    }

    public T getEnd()
    {
        return end;
    }

    public void setEnd(T end)
    {
        this.end = end;
    }
    
    public byte byteSize()
    {
        return (byte)(end.byteValue() - begin.byteValue());
    }
    
    public short shortSize()
    {
        return (short)(end.shortValue() - begin.shortValue());
    }
    
    public int intSize()
    {
        return end.intValue() - begin.intValue();
    }
    
    public long longSize()
    {
        return end.longValue() - begin.longValue();
    }
    
    public float floatSize()
    {
        return end.floatValue() - begin.floatValue();
    }
    
    public double doubleSize()
    {
        return end.doubleValue() - begin.doubleValue();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        
        if(obj instanceof Range)
        {
            Range<?> other = (Range<?>)obj;
            
            if(begin == other.begin && end == other.end)
                return true;    
            
            if(begin != null && !begin.equals(other.begin))
                return false;
            else if(begin == null && other.begin != null)
                return false;
            
            if(end != null && !end.equals(other.end))
                return false;
            else if(end == null && other.end != null)
                return false;
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public int hashCode()
    {
        return (begin != null ? begin.hashCode() : 0) ^ (end != null ? end.hashCode() : 0);
    }
    
    @Override
    public String toString()
    {
        return String.format("{%s - %s}", begin, end);
    }
}
