package io.github.hpsocket.soa.starter.web.dubbo.filter;

import org.slf4j.MDC;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;

import java.util.Arrays;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcContextAttachment;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcServiceContext;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;

import static io.github.hpsocket.soa.framework.core.mdc.MdcAttr.*;

/** <b>Dubbo MDC 过滤器</b><br>
 * 为 Dubbo 服务调用注入调用链跟踪信息
 */
@Activate(group = {CONSUMER, PROVIDER}, order = (Integer.MIN_VALUE + 1000))
public class DubboMdcFilter implements Filter, Filter.Listener
{
    private static final ThreadLocal<Boolean> GEN_APP_ID = new ThreadLocal<>();
    
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException
    {
        RpcServiceContext ctx = RpcContext.getServiceContext();
        
        if(ctx.getUrl() == null)
            return invoker.invoke(invocation);
        
        boolean isConsumer = ctx.isConsumerSide();
        final RpcContextAttachment ctxAttach = isConsumer
                                             ? RpcContext.getClientAttachment()
                                             : RpcContext.getServerAttachment();
        GEN_APP_ID.set(Boolean.FALSE);
    
        try
        {
            if(isConsumer)
            {
                for(int i = 0; i < TRANSFER_MDC_KEYS.length; i++)
                {
                    String key = TRANSFER_MDC_KEYS[i];
                    String val = MDC.get(key);
                    
                    if(GeneralHelper.isStrNotEmpty(val))
                        ctxAttach.setAttachment(key, val);
                    else if(key.equals(MDC_APP_ID_KEY))
                        GEN_APP_ID.set(Boolean.TRUE);
                }
                
                ctxAttach.setAttachment(MDC_FROM_SERVICE_ID_KEY, AppConfigHolder.getAppId());
                ctxAttach.setAttachment(MDC_FROM_SERVICE_NAME_KEY, AppConfigHolder.getAppName());
                ctxAttach.setAttachment(MDC_FROM_SERVICE_ADDR_KEY, AppConfigHolder.getAppAddress());
            }
            else
            {
                for(int i = 0; i < TRANSFER_MDC_KEYS.length; i++)
                {
                    String key = TRANSFER_MDC_KEYS[i];
                    String val = ctxAttach.getAttachment(key);
                    
                    if(GeneralHelper.isStrNotEmpty(val))
                        MDC.put(key, val);
                    else if(key.equals(MDC_APP_ID_KEY))
                        GEN_APP_ID.set(Boolean.TRUE);
                }
                
                MDC.put(MDC_SERVICE_ID_KEY, AppConfigHolder.getAppId());
                MDC.put(MDC_SERVICE_NAME_KEY, AppConfigHolder.getAppName());
                MDC.put(MDC_SERVICE_ADDR_KEY, AppConfigHolder.getAppAddress());
                
                if(GeneralHelper.isStrNotEmpty(AppConfigHolder.getAppOrganization()))
                    MDC.put(MDC_ORG_KEY, AppConfigHolder.getAppOrganization());
                if(GeneralHelper.isStrNotEmpty(AppConfigHolder.getAppOwner()))
                    MDC.put(MDC_OWNER_KEY, AppConfigHolder.getAppOwner());
                
                MDC.put(MDC_FROM_SERVICE_ID_KEY, ctxAttach.getAttachment(MDC_FROM_SERVICE_ID_KEY));
                MDC.put(MDC_FROM_SERVICE_NAME_KEY, ctxAttach.getAttachment(MDC_FROM_SERVICE_NAME_KEY));
                MDC.put(MDC_FROM_SERVICE_ADDR_KEY, ctxAttach.getAttachment(MDC_FROM_SERVICE_ADDR_KEY));
            }
            
            if(GEN_APP_ID.get())
            {
                String appId    = AppConfigHolder.getAppId();
                String appName    = AppConfigHolder.getAppName();
                
                MDC.put(MDC_APP_ID_KEY, appId);
                MDC.put(MDC_APP_NAME_KEY, appName);
                ctxAttach.setAttachment(MDC_APP_ID_KEY, appId);
                ctxAttach.setAttachment(MDC_APP_NAME_KEY, appName);
            }
            
            return invoker.invoke(invocation);
        }
        finally
        {

        }
    }

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation)
    {
        afterInvoke();
    }

    @Override
    public void onError(Throwable t, Invoker<?> invoker, Invocation invocation)
    {
        afterInvoke();
    }
    
    private void afterInvoke()
    {
        RpcServiceContext ctx = RpcContext.getServiceContext();
        
        if(ctx.getUrl() == null)
            return;
        
        boolean isConsumer = ctx.isConsumerSide();
        final RpcContextAttachment ctxAttach = isConsumer
                                             ? RpcContext.getClientAttachment()
                                             : RpcContext.getServerAttachment();
        if(isConsumer)
        {
            Arrays.stream(TRANSFER_MDC_ALL_KEYS).forEach((key) -> ctxAttach.removeAttachment(key));
            
            if(GEN_APP_ID.get())
            {
                MDC.remove(MDC_APP_ID_KEY);
                MDC.remove(MDC_APP_NAME_KEY);
            }
        }
        else
        {
            Arrays.stream(TRANSFER_MDC_ALL_KEYS).forEach((key) -> MDC.remove(key));
            
            if(GEN_APP_ID.get())
            {
                ctxAttach.removeAttachment(MDC_APP_ID_KEY);
                ctxAttach.removeAttachment(MDC_APP_NAME_KEY);
            }
        }
    }

}
