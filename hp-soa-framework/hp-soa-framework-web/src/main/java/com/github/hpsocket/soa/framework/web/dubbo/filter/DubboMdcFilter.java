package com.github.hpsocket.soa.framework.web.dubbo.filter;

import org.slf4j.MDC;

import com.github.hpsocket.soa.framework.core.util.GeneralHelper;
import com.github.hpsocket.soa.framework.web.holder.AppConfigHolder;

import java.util.Arrays;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcContextAttachment;
import org.apache.dubbo.rpc.RpcException;

import static com.github.hpsocket.soa.framework.core.mdc.MdcAttr.*;

public class DubboMdcFilter implements Filter
{
	@Override
	@Activate
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException
	{
		RpcContext ctx		= RpcContext.getServiceContext();
		boolean isConsumer	= ctx.isConsumerSide();
		boolean genAppId	= false;
	
		try
		{
			if(isConsumer)
			{				
				for(int i = 0; i < TRANSFER_MDC_KEYS.length; i++)
				{
					String key = TRANSFER_MDC_KEYS[i];
					String val = MDC.get(key);
					
					if(GeneralHelper.isStrNotEmpty(val))
						ctx.setAttachment(key, val);
					else if(key.equals(MDC_APP_ID_KEY))
						genAppId = true;
				}
			}
			else
			{
				RpcContextAttachment ctxAttach = RpcContext.getClientAttachment();
				
				for(int i = 0; i < TRANSFER_MDC_KEYS.length; i++)
				{
					String key = TRANSFER_MDC_KEYS[i];
					String val = ctxAttach.getAttachment(key);
					
					if(GeneralHelper.isStrNotEmpty(val))
						MDC.put(key, val);
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
			}
			
			if(genAppId)
			{
				String appId	= AppConfigHolder.getAppId();
				String appName	= AppConfigHolder.getAppName();
				
				MDC.put(MDC_APP_ID_KEY, appId);
				MDC.put(MDC_APP_NAME_KEY, appName);
				ctx.setAttachment(MDC_APP_ID_KEY, appId);
				ctx.setAttachment(MDC_APP_NAME_KEY, appName);
			}
			
			return invoker.invoke(invocation);
		}
		finally
		{
			if(isConsumer)
			{
				Arrays.stream(TRANSFER_MDC_KEYS).forEach((key) -> ctx.removeAttachment(key));

				if(genAppId)
				{
					MDC.remove(MDC_APP_ID_KEY);
					MDC.remove(MDC_APP_NAME_KEY);
				}
			}
			else
			{
				Arrays.stream(TRANSFER_MDC_KEYS).forEach((key) -> MDC.remove(key));
				
				if(genAppId)
				{
					ctx.removeAttachment(MDC_APP_ID_KEY);
					ctx.removeAttachment(MDC_APP_NAME_KEY);
				}
			}
		}
	}

}
