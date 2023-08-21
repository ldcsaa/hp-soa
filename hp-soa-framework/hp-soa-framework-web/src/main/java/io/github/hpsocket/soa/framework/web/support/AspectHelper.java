
package io.github.hpsocket.soa.framework.web.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.google.common.collect.Maps;

public class AspectHelper
{
	private static ExpressionParser expressionParser = new SpelExpressionParser();
	private static ConcurrentMap<String, Expression> expressionMap = Maps.newConcurrentMap();

	public static final EvaluationContext buildContext(ProceedingJoinPoint point, Object result)
	{
		EvaluationContext context = buildContext(point);
		context.setVariable("result", result);
		
		return context;
	}

	public static final EvaluationContext buildContext(ProceedingJoinPoint point)
	{
		EvaluationContext context = new StandardEvaluationContext();
		MethodSignature ms = (MethodSignature)point.getSignature();
		
		String[] parameterNames = ms.getParameterNames();
		Object[] args = point.getArgs();
		
		for(int i = 0; i < parameterNames.length; i++)
			context.setVariable(parameterNames[i], args[i]);

		return context;
	}

	public static final Method getMethod(ProceedingJoinPoint point)
	{
		Class<?> targetCls = point.getTarget().getClass();
		MethodSignature ms = (MethodSignature)point.getSignature();
		
		try
		{
			return targetCls.getDeclaredMethod(ms.getName(), ms.getParameterTypes());
		}
		catch(NoSuchMethodException e)
		{
			return null;
		}
	}

	public static final <T extends Annotation> T getMethodAnnotation(ProceedingJoinPoint point, Class<T> annotationType)
	{
		Method targetMethod = getMethod(point);
		
		if(targetMethod == null)
			return null;
		
		return targetMethod.getAnnotation(annotationType);
	}

	public static final Annotation[] getMethodAnnotations(ProceedingJoinPoint point)
	{
		Method targetMethod = getMethod(point);
		
		if(targetMethod == null)
			return new Annotation[0];
		
		return targetMethod.getAnnotations();
	}

	public static final Class<?> getMethodReturnType(ProceedingJoinPoint point)
	{
		Method targetMethod = getMethod(point);
		
		if(targetMethod == null)
			return null;
		
		return targetMethod.getReturnType();
	}

	public static final <T extends Annotation> T getClassAnnotation(ProceedingJoinPoint point, Class<T> annotationType)
	{
		Class<?> targetCls = point.getTarget().getClass();
		return targetCls.getAnnotation(annotationType);
	}

	public static final <T extends Annotation> T getMethodOrClassAnnotation(ProceedingJoinPoint point, Class<T> annotationType)
	{
		T annotation = getMethodAnnotation(point, annotationType);
		
		if(annotation == null)
			annotation = getClassAnnotation(point, annotationType);
		
		return annotation;
	}

	public static final Expression getExpressionByKey(String key)
	{
		if(expressionMap.containsKey(key))
			return expressionMap.get(key);

		Expression expression = expressionParser.parseExpression(key);
		expressionMap.putIfAbsent(key, expression);
		
		return expression;
	}

	public static final Map<String, Object> getParameters(ProceedingJoinPoint point)
	{
		MethodSignature ms = (MethodSignature)point.getSignature();
		String[] parameterNames = ms.getParameterNames();
		Object[] args = point.getArgs();
		
		Map<String, Object> paramMap = Maps.newHashMapWithExpectedSize(parameterNames.length);
		
		for(int i = 0; i < parameterNames.length; i++)
			paramMap.put(parameterNames[i], args[i]);

		return paramMap;
	}

}
