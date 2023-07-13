package com.github.hpsocket.soa.framework.web.dubbo.filter;

import org.slf4j.MDC;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

public class DubboMdcFilter implements Filter
{
	@Override
	@Activate
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException
	{
		RpcContext ctx		= RpcContext.getServiceContext();
		boolean isConsumer	= ctx.isConsumerSide();
		String clientId		= null;
		String requestId	= null;
		String sessionId	= null;
		String appCode		= null;
		String appName		= null;
		String appId		= null;
		
		boolean genAppName	= false;
		boolean genAppId	= false;
	
		try
		{
			if(isConsumer)
			{
				clientId = MDC.get(MDC_CLIENT_ID_KEY);
				if(GeneralHelper.isStrNotEmpty(clientId))
					ctx.setAttachment(CLIENT_ID, clientId);
				
				requestId = MDC.get(MDC_REQUEST_ID_KEY);
				if(GeneralHelper.isStrNotEmpty(requestId))
					ctx.setAttachment(REQUEST_ID, requestId);
				
				sessionId = MDC.get(MDC_SESSION_ID_KEY);
				if(GeneralHelper.isStrNotEmpty(sessionId))
					ctx.setAttachment(SESSION_ID, sessionId);
				
				appCode = MDC.get(MDC_APP_CODE_KEY);
				if(GeneralHelper.isStrNotEmpty(appCode))
					ctx.setAttachment(APP_CODE, appCode);
				
				appName	= MDC.get(MDC_APP_NAME_KEY);
				appId	= MDC.get(MDC_APP_ID_KEY);
				
				if(GeneralHelper.isStrEmpty(appName))
				{
					appName		= MyApplicationContext.currentApplicationName();
					genAppName	= true;
					
					appId		= MyApplicationContext.currentApplicationId();
					
					if(GeneralHelper.isStrNotEmpty(appId))
						genAppId = true;
				}
				
				if(GeneralHelper.isStrNotEmpty(appName))
				{
					if(genAppName)
						MDC.put(MDC_APP_NAME_KEY, appName);
					
					ctx.setAttachment(APP_NAME, appName);
					
					if(GeneralHelper.isStrNotEmpty(appId))
					{
						if(genAppId)
							MDC.put(MDC_APP_ID_KEY, appId);
						
						ctx.setAttachment(APP_ID, appId);
					}
				}
			}
			else
			{
				clientId = ctx.getAttachment(CLIENT_ID);
				if(GeneralHelper.isStrNotEmpty(clientId))
					MDC.put(MDC_CLIENT_ID_KEY, clientId);
				
				requestId = ctx.getAttachment(REQUEST_ID);
				if(GeneralHelper.isStrNotEmpty(requestId))
					MDC.put(MDC_REQUEST_ID_KEY, requestId);
				
				sessionId = ctx.getAttachment(SESSION_ID);
				if(GeneralHelper.isStrNotEmpty(sessionId))
					MDC.put(MDC_SESSION_ID_KEY, sessionId);
				
				appCode = ctx.getAttachment(APP_CODE);
				if(GeneralHelper.isStrNotEmpty(appCode))
					MDC.put(MDC_APP_CODE_KEY, appCode);
				
				appName = ctx.getAttachment(APP_NAME);
				
				if(GeneralHelper.isStrEmpty(appName))
				{
					appName		= MyApplicationContext.currentApplicationName();
					genAppName	= true;
					
					appId		= MyApplicationContext.currentApplicationId();
					
					if(GeneralHelper.isStrNotEmpty(appId))
						genAppId = true;
				}
				
				if(GeneralHelper.isStrNotEmpty(appName))
				{
					if(genAppName)
						ctx.setAttachment(APP_NAME, appName);
					
					MDC.put(MDC_APP_NAME_KEY, appName);
					
					if(GeneralHelper.isStrNotEmpty(appId))
					{
						if(genAppId)
							ctx.setAttachment(APP_ID, appId);
						
						MDC.put(MDC_APP_ID_KEY, appId);
					}
				}
			}
			
			putServicePropsMDC();
			
			return invoker.invoke(invocation);
		}
		finally
		{
			if(isConsumer)
			{
				if(GeneralHelper.isStrNotEmpty(clientId))
					ctx.removeAttachment(CLIENT_ID);
				if(GeneralHelper.isStrNotEmpty(requestId))
					ctx.removeAttachment(REQUEST_ID);
				if(GeneralHelper.isStrNotEmpty(sessionId))
					ctx.removeAttachment(SESSION_ID);
				if(GeneralHelper.isStrNotEmpty(appCode))
					ctx.removeAttachment(APP_CODE);
				if(GeneralHelper.isStrNotEmpty(appName))
					ctx.removeAttachment(APP_NAME);
				if(GeneralHelper.isStrNotEmpty(appId))
					ctx.removeAttachment(APP_ID);
				
				if(genAppName)
					MDC.remove(MDC_APP_NAME_KEY);
				if(genAppId)
					MDC.remove(MDC_APP_ID_KEY);
			}
			else
			{
				if(GeneralHelper.isStrNotEmpty(clientId))
					MDC.remove(MDC_CLIENT_ID_KEY);
				if(GeneralHelper.isStrNotEmpty(requestId))
					MDC.remove(MDC_REQUEST_ID_KEY);
				if(GeneralHelper.isStrNotEmpty(sessionId))
					MDC.remove(MDC_SESSION_ID_KEY);
				if(GeneralHelper.isStrNotEmpty(appCode))
					MDC.remove(MDC_APP_CODE_KEY);
				if(GeneralHelper.isStrNotEmpty(appName))
					MDC.remove(MDC_APP_NAME_KEY);
				if(GeneralHelper.isStrNotEmpty(appId))
					MDC.remove(MDC_APP_ID_KEY);
				
				if(genAppName)
					ctx.remove(APP_NAME);
				if(genAppId)
					ctx.remove(APP_ID);
			}
		}
	}

}
