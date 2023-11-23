package io.github.hpsocket.soa.starter.web.cloud.support;

import static io.github.hpsocket.soa.framework.core.mdc.MdcAttr.*;

import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.jboss.logging.MDC;

import brave.baggage.BaggageField;
import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;

public class TracingHelper
{
    public static final String TRACING_IS_ENTRY_KEY = "__isEntry";
    private static final ThreadLocal<Boolean> ENTRY = new ThreadLocal<>();
    
    public static final boolean checkEntry()
    {
        boolean isEntry = false;
        BaggageField bf = BaggageField.getByName(TRACING_IS_ENTRY_KEY);
        
        if(bf == null)
        {
            isEntry = true;
            bf      = BaggageField.create(TRACING_IS_ENTRY_KEY);
        }
        
        bf.updateValue(Boolean.valueOf(isEntry).toString());
        MDC.put(TRACING_IS_ENTRY_KEY, isEntry);
        ENTRY.set(isEntry);
        
        return isEntry;
    }
    
    public static final boolean isEntry()
    {
        return ENTRY.get();
    }
    
    public static final void removeEntry()
    {
        ENTRY.set(null);
    }
    
    public static final void fillBaggageByMdcAttr()
    {
        for(String key : MdcAttr.TRANSFER_MDC_ALL_KEYS)
        {
            String value = (String)MDC.get(key);
            
            if(GeneralHelper.isStrNotEmpty(value))
                BaggageField.create(key).updateValue(value);
        }
    }

    public static final void fillMdcAttrByBaggage()
    {
        Map<String, String> map = BaggageField.getAllValues();
        
        if(map.isEmpty())
            return;
        
        boolean genAppId = false;
        
        for(String key : MdcAttr.TRANSFER_MDC_ALL_KEYS)
        {
            String value = map.get(key.toLowerCase());
            
            if(GeneralHelper.isStrNotEmpty(value))
                MDC.put(key, value);
            else if(key.equals(MDC_APP_ID_KEY))
                genAppId = true;
        }
        
        MDC.put(MDC_SERVICE_ID_KEY, AppConfigHolder.getAppId());
        MDC.put(MDC_SERVICE_NAME_KEY, AppConfigHolder.getAppName());
        MDC.put(MDC_SERVICE_ADDR_KEY, AppConfigHolder.getAppAddress());
        
        if(GeneralHelper.isStrNotEmpty(AppConfigHolder.getAppOrganization()))
            MDC.put(MDC_ORG_KEY, AppConfigHolder.getAppOrganization());
        if(GeneralHelper.isStrNotEmpty(AppConfigHolder.getAppOwner()))
            MDC.put(MDC_OWNER_KEY, AppConfigHolder.getAppOwner());
        
        if(genAppId)
        {
            String appId    = AppConfigHolder.getAppId();
            String appName  = AppConfigHolder.getAppName();
            
            MDC.put(MDC_APP_ID_KEY, appId);
            MDC.put(MDC_APP_NAME_KEY, appName);
            
            BaggageField.create(MDC_APP_ID_KEY).updateValue(appId);
            BaggageField.create(MDC_APP_NAME_KEY).updateValue(appName);
        }
        
        BaggageField fromServiceId   = BaggageField.getByName(MDC_FROM_SERVICE_ID_KEY);
        BaggageField fromServiceName = BaggageField.getByName(MDC_FROM_SERVICE_NAME_KEY);
        BaggageField fromServiceAddr = BaggageField.getByName(MDC_FROM_SERVICE_ADDR_KEY);
        
        if(fromServiceId == null)
            fromServiceId = BaggageField.create(MDC_FROM_SERVICE_ID_KEY);
        if(fromServiceName == null)
            fromServiceName = BaggageField.create(MDC_FROM_SERVICE_NAME_KEY);
        if(fromServiceAddr == null)
            fromServiceAddr = BaggageField.create(MDC_FROM_SERVICE_ADDR_KEY);
        
        fromServiceId.updateValue(AppConfigHolder.getAppId());
        fromServiceName.updateValue(AppConfigHolder.getAppName());
        fromServiceAddr.updateValue(AppConfigHolder.getAppAddress());
    }
    
    public static final Object aspectInspect(ProceedingJoinPoint joinPoint) throws Throwable
    {
        try
        {
            TracingHelper.checkEntry();
            TracingHelper.fillBaggageByMdcAttr();
            
            return joinPoint.proceed();
        }
        finally
        {
            TracingHelper.removeEntry();
        }
    }
    
}
