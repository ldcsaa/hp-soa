package io.github.hpsocket.soa.framework.core.util;

import java.io.Serializable;

/** <b>通用值对</b><br>
 * 
 * {@link Pair#first} - 第一个值 <br>
 * {@link Pair#second} - 第二个值
 * 
 * */
@SuppressWarnings("serial")
public class Pair<F, S> implements Serializable
{    
    private F first;
    private S second;
    
    public Pair()
    {
    }
    
    public Pair(F first)
    {
        set(first, null);
    }

    public Pair(F first, S second)
    {
        set(first, second);
    }
    
    public Pair(Pair<F, S> other)
    {
        set(other.first, other.second);
    }

    public F getFirst()
    {
        return first;
    }

    public void setFirst(F first)
    {
        this.first = first;
    }

    public S getSecond()
    {
        return second;
    }

    public void setSecond(S second)
    {
        this.second = second;
    }
    
    public void set(F first, S second)
    {
        this.first    = first;
        this.second    = second;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        
        if(obj instanceof Pair)
        {
            Pair<?, ?> other = (Pair<?, ?>)obj;
            
            if(first == other.first && second == other.second)
                return true;    
            
            if(first != null && !first.equals(other.first))
                return false;
            else if(first == null && other.first != null)
                return false;
            
            if(second != null && !second.equals(other.second))
                return false;
            else if(second == null && other.second != null)
                return false;
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public int hashCode()
    {
        return (first != null ? first.hashCode() : 0) ^ (second != null ? second.hashCode() : 0);
    }
    
    @Override
    public String toString()
    {
        return String.format("{%s, %s}", first, second);
    }

}
