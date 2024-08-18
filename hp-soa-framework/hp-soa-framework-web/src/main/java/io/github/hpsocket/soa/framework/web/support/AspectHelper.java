
package io.github.hpsocket.soa.framework.web.support;

import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/** <b>Aspect 拦截信息检索辅助类</b> */
public class AspectHelper
{
    private static ExpressionParser expressionParser = new SpelExpressionParser();
    private static ConcurrentMap<String, Expression> expressionMap = new ConcurrentHashMap<>();

    public static final EvaluationContext buildContext(JoinPoint point, Object result)
    {
        EvaluationContext context = buildContext(point);
        context.setVariable("result", result);
        
        return context;
    }

    public static final EvaluationContext buildContext(JoinPoint point)
    {
        EvaluationContext context = new StandardEvaluationContext();
        MethodSignature ms = (MethodSignature)point.getSignature();
        
        String[] parameterNames = ms.getParameterNames();
        Object[] args = point.getArgs();
        
        for(int i = 0; i < parameterNames.length; i++)
            context.setVariable(parameterNames[i], args[i]);

        return context;
    }

    public static final Method getMethod(JoinPoint point)
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

    public static final Class<?> getMethodReturnType(JoinPoint point)
    {
        Method targetMethod = getMethod(point);
        
        if(targetMethod == null)
            return null;
        
        return targetMethod.getReturnType();
    }

    public static final <T extends Annotation> T getMethodAnnotation(JoinPoint point, Class<T> annotationType)
    {
        Method targetMethod = getMethod(point);
        
        if(targetMethod == null)
            return null;
        
        return MergedAnnotations.from(targetMethod, SearchStrategy.INHERITED_ANNOTATIONS).get(annotationType).synthesize(MergedAnnotation::isPresent).orElse(null);
    }

    public static final Annotation[] getMethodAnnotations(JoinPoint point)
    {
        Method targetMethod = getMethod(point);
        
        if(targetMethod == null)
            return new Annotation[0];
        
        return MergedAnnotations.from(targetMethod, SearchStrategy.INHERITED_ANNOTATIONS).stream().map(MergedAnnotation::synthesize).toArray(Annotation[]::new);
    }

    public static final <T extends Annotation> T getClassAnnotation(JoinPoint point, Class<T> annotationType)
    {
        Class<?> targetCls = point.getTarget().getClass();
        
        return MergedAnnotations.from(targetCls, SearchStrategy.INHERITED_ANNOTATIONS).get(annotationType).synthesize(MergedAnnotation::isPresent).orElse(null);
    }

    public static final Annotation[] getClassAnnotations(JoinPoint point)
    {
        Class<?> targetCls = point.getTarget().getClass();
        
        return MergedAnnotations.from(targetCls, SearchStrategy.INHERITED_ANNOTATIONS).stream().map(MergedAnnotation::synthesize).toArray(Annotation[]::new);
    }

    public static final <T extends Annotation> T getMethodOrClassAnnotation(JoinPoint point, Class<T> annotationType)
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

    public static final Map<String, Object> getParameters(JoinPoint point)
    {
        MethodSignature ms = (MethodSignature)point.getSignature();
        String[] parameterNames = ms.getParameterNames();
        Object[] args = point.getArgs();
        
        Map<String, Object> paramMap = new HashMap<>(parameterNames.length);
        
        for(int i = 0; i < parameterNames.length; i++)
            paramMap.put(parameterNames[i], args[i]);

        return paramMap;
    }

    public static final Object[] getArgs(JoinPoint point)
    {
        return point.getArgs();
    }

    @SuppressWarnings("unchecked")
    public static final <T> T findFirstArgByType(JoinPoint point, Class<? extends T> clazz)
    {
        Object[] args = getArgs(point);
        
        for(Object arg : args)
        {
            if(clazz.isInstance(arg))
                return (T)arg;
        }
        
        return null;
    }
    
    public static final Object findFirstArgByTypes(JoinPoint point, Class<?> ... clazzes)
    {
        Object[] args = getArgs(point);
        
        for(Object arg : args)
        {
            for(Class<?> clazz : clazzes)
            {
                if(clazz.isInstance(arg))
                    return arg;            
            }
        }
        
        return null;
    }
    
    /** <b>注解信息持有者</b> */
    abstract public static class AnnotationHolder<T extends Annotation>
    {
        private Map<Object, SoftReference<T>> map = new ConcurrentHashMap<>();
        
        public AnnotationHolder() {}
        
        @SuppressWarnings("unchecked")
        public T findAnnotation(JoinPoint jp, Function<JoinPoint, Object> funcKey, BiFunction<JoinPoint, Class<T>, T> funcFind)
        {
            Object key = funcKey.apply(jp);
            T val = Optional.ofNullable(map.get(key)).map(SoftReference::get).orElse(null);
            
            if(val == null)
            {
                ParameterizedType type = (ParameterizedType)this.getClass().getGenericSuperclass();
                Class<T> tClazz = (Class<T>)type.getActualTypeArguments()[0];
                
                val = funcFind.apply(jp, tClazz);
                
                if(val != null)
                    map.put(key, new SoftReference<>(val));
            }
            
            return val;
        }
        
        public T findAnnotationByMethod(JoinPoint jp)
        {
            return findAnnotation(jp, (j) -> ((MethodSignature)j.getSignature()).getMethod(), (j, cls) -> getMethodAnnotation(j, cls));
        }
        
        public T findAnnotationByMethodOrClass(JoinPoint jp)
        {
            return findAnnotation(jp, (j) -> ((MethodSignature)j.getSignature()).getMethod(), (j, cls) -> getMethodOrClassAnnotation(j, cls));
        }
        
        public T findAnnotationByClass(JoinPoint jp)
        {
            return findAnnotation(jp, (j) -> j.getTarget().getClass(), (j, cls) -> getClassAnnotation(j, cls));
        }
    }

}
