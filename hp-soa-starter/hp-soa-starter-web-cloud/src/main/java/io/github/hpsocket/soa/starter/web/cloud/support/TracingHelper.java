package io.github.hpsocket.soa.starter.web.cloud.support;

import java.util.StringTokenizer;

import org.slf4j.MDC;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.CryptHelper;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;

import static io.github.hpsocket.soa.framework.core.mdc.MdcAttr.*;

public class TracingHelper
{
    public static final String HEADER_TRACING_INFO  = "X-Tracing-Info";
    public static final String TRACING_IS_ENTRY_KEY = "__isEntry";
    private static final ThreadLocal<Boolean> ENTRY = new ThreadLocal<>();
    
    public static final String createTracingInfoHeader()
    {
        StringBuilder sb = new StringBuilder(200);
        
        for(String key : TRANSFER_MDC_KEYS)
        {
            String value = MDC.get(key);
            
            if(GeneralHelper.isStrNotEmpty(value))
                sb.append(key).append('=').append(CryptHelper.urlEncode(value)).append(';');
        }
        
        sb.append(MDC_FROM_SERVICE_ID_KEY).append('=').append(CryptHelper.urlEncode(AppConfigHolder.getAppId())).append(';');
        sb.append(MDC_FROM_SERVICE_NAME_KEY).append('=').append(CryptHelper.urlEncode(AppConfigHolder.getAppName())).append(';');
        sb.append(MDC_FROM_SERVICE_ADDR_KEY).append('=').append(CryptHelper.urlEncode(AppConfigHolder.getAppAddress()));
        
        return sb.toString();
    }
    
    public static final void fillMdcAttr(MdcAttr mdcAttr, String tracingInfo)
    {
        if(GeneralHelper.isStrEmpty(tracingInfo))
            return;

        StringTokenizer st = new StringTokenizer(tracingInfo, ";");
        
        while(st.hasMoreTokens())
        {
            String field = st.nextToken();
            int i = field.indexOf('=');
            
            if(i > 0)
            {
                String key = field.substring(0, i).trim();
                
                if(GeneralHelper.isStrNotEmpty(key))
                {
                    String value = CryptHelper.urlDecode(field.substring(i + 1).trim());                
                    mdcAttr.set(key, value);;
                }
            }
        }
    }
    
    public static final boolean isEntry()
    {
        return ENTRY.get();
    }
    
    public static final Boolean isEntryOrNull()
    {
        return ENTRY.get();
    }
    
    public static final void setEntry(Boolean entry)
    {
        ENTRY.set(entry);
    }
    
    public static final void removeEntry()
    {
        setEntry(null);
    }

}
