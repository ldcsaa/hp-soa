package io.github.hpsocket.soa.framework.core.util;

/** <b>通用常量</b> */
public class Constant implements Comparable<Constant>
{
    /** 代码 */
    public final int CODE;
    /** 描述 */
    public final String DESC;
    
    public Constant()
    {
        this(0);
    }    
    
    public Constant(int code)
    {
        this(code, "");
    }

    public Constant(int code, String desc)
    {
        CODE    = code;
        DESC    = desc;
    }

    public Constant(Number code)
    {
        this(code.intValue());
    }

    public Constant(Number code, String desc)
    {
        this(code.intValue(), desc);
    }

    public final int INT_VAL()
    {
        return CODE;
    }

    public final short SHORT_VAL()
    {
        return (short)CODE;
    }

    public final byte BYTE_VAL()
    {
        return (byte)CODE;
    }

    public final String STR_VAL()
    {
        return Integer.toString(CODE);
    }
    
    public boolean equals(Number value)
    {
        return GeneralHelper.equals(value, CODE);
    }

    public boolean notEquals(Number value)
    {
        return !equals(value);
    }
    
    @Override
    public int compareTo(Constant other)
    {
        return Integer.compare(CODE, other.CODE);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        else if(obj instanceof Constant)
            return CODE == ((Constant)obj).CODE;
        else if(obj instanceof Number)
            return equals((Number)obj);
        
        return false;
    }
    
    @Override
    public int hashCode()
    {
        return CODE;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append('{').append(CODE).append(", ").append("\"").append(DESC).append("\"}");
        
        return sb.toString();
    }

    /* *********************************************************************************************************************************** */
    /* ****************************************************** Begin: Common Constant ***************************************************** */
    /* *********************************************************************************************************************************** */

    /**
     * 通用激活标识
     */
    public static class ActivatedFlag
    {
        public static final Constant AF_INACTIVATED = new Constant(0, "未激活");
        public static final Constant AF_ACTIVATED = new Constant(1, "已激活");
    }

    /**
     * 通用允许标识
     */
    public static class PermitFlag
    {
        public static final Constant PF_FORBID = new Constant(0, "禁止");
        public static final Constant PF_PERMIT = new Constant(1, "允许");
    }

    /**
     * 通用成功标识
     */
    public static class SuccessFlag
    {
        public static final Constant SF_FAIL = new Constant(0, "失败");
        public static final Constant SF_SUCCESS = new Constant(1, "成功");
        public static final Constant SF_EXCEPTION = new Constant(-1, "异常");
    }

    /**
     * 通用存在标识
     */
    public static class ExistFlag
    {
        public static final Constant EF_NOT_EXIST = new Constant(0, "不存在");
        public static final Constant EF_EXIST = new Constant(1, "存在");
    }

    /**
     * 通用是否标识
     */
    public static class YesNoFlag
    {
        public static final Constant YNF_NO = new Constant(0, "否");
        public static final Constant YNF_YES = new Constant(1, "是");
    }

    /**
     * 通用删除标识
     */
    public static class DeleteFlag
    {
        public static final Constant DF_NOT_DELETED = new Constant(0, "未删除");
        public static final Constant DF_HAD_DELETED = new Constant(1, "已删除");
    }

    /**
     * 通用状态标识
     */
    public static class StatusFlag
    {
        public static final Constant STF_DISABLED = new Constant(0, "未开启");
        public static final Constant STF_ENABLED = new Constant(1, "已开启");
        public static final Constant STF_DELETED = new Constant(-1, "已删除");
    }
}
