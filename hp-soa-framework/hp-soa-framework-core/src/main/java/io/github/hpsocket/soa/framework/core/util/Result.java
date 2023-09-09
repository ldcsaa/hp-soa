package io.github.hpsocket.soa.framework.core.util;

import java.util.Date;

/** <b>通用操作结果</b><br>
 * 
 * {@link Result#flag} - 结果状态标志 <br>
 * {@link Result#value} - 结果值
 * 
 * */
public class Result<F, V>
{
	/** 获取一个 {@link Result} 对象初始值：{{@link Boolean#FALSE}, null} */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <V> Result<Boolean, V> initialBoolean()
	{
		return new Result(Boolean.FALSE);
	}
	
	/** 获取一个 {@link Result} 对象初始值：{byte(0), null} */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <V> Result<Byte, V> initialByte()
	{
		return new Result(0);
	}
	
	/** 获取一个 {@link Result} 对象初始值：{char(0), null} */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <V> Result<Character, V> initialChar()
	{
		return new Result(0);
	}
	
	/** 获取一个 {@link Result} 对象初始值：{short(0), null} */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <V> Result<Short, V> initialShort()
	{
		return new Result(0);
	}
	
	/** 获取一个 {@link Result} 对象初始值：{int(0), null} */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <V> Result<Integer, V> initialInt()
	{
		return new Result(0);
	}
	
	/** 获取一个 {@link Result} 对象初始值：{long(0), null} */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <V> Result<Long, V> initialLong()
	{
		return new Result(0L);
	}
	
	/** 获取一个 {@link Result} 对象初始值：{{@link Double#NaN}, null} */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <V> Result<Float, V> initialFloat()
	{
		return new Result(Float.NaN);
	}
	
	/** 获取一个 {@link Result} 对象初始值：{{@link Double#NaN}, null} */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <V> Result<Double, V> initialDouble()
	{
		return new Result(Double.NaN);
	}
	
	/** 获取一个 {@link Result} 对象初始值：{"", null} */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <V> Result<String, V> initialString()
	{
		return new Result("");
	}
	
	/** 获取一个 {@link Result} 对象初始值：{Date("1970-1-1 00:00:00"), null} */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <V> Result<Date, V> initialDate()
	{
		return new Result(new Date(0));
	}
	
	private F flag;
	private V value;
	
	public Result()
	{
	}
	
	public Result(F flag)
	{
		set(flag, null);
	}

	public Result(F flag, V value)
	{
		set(flag, value);
	}
	
	public Result(Result<F, V> other)
	{
		set(other.flag, other.value);
	}

	public F getFlag()
	{
		return flag;
	}

	public void setFlag(F flag)
	{
		this.flag = flag;
	}

	public V getValue()
	{
		return value;
	}

	public void setValue(V value)
	{
		this.value = value;
	}
	
	public void set(F flag, V value)
	{
		this.flag	= flag;
		this.value	= value;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		
		if(obj instanceof Result)
		{
			Result<?, ?> other = (Result<?, ?>)obj;
			
			if(flag == other.flag && value == other.value)
				return true;	
			
			if(flag != null && !flag.equals(other.flag))
				return false;
			else if(flag == null && other.flag != null)
				return false;
			
			if(value != null && !value.equals(other.value))
				return false;
			else if(value == null && other.value != null)
				return false;
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return (flag != null ? flag.hashCode() : 0) ^ (value != null ? value.hashCode() : 0);
	}
	
	@Override
	public String toString()
	{
		return String.format("{%s, %s}", flag, value);
	}

}
