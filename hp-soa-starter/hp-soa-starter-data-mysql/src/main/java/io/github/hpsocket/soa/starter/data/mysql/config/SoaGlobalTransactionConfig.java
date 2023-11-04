
package io.github.hpsocket.soa.starter.data.mysql.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import static io.github.hpsocket.soa.starter.data.mysql.config.SoaDataSourceConfig.*;

/** <b>HP-SOA 全局事务配置</b><br>
 * 当 <i>${hp.soa.data.mysql.global-transaction-management.enabled}</i> 为 true 时，启用全局事务配置
 */
@AutoConfiguration
@EnableTransactionManagement
@ConditionalOnProperty(name = "hp.soa.data.mysql.global-transaction-management.enabled", matchIfMissing = false)
public class SoaGlobalTransactionConfig
{
    public static final String dynamicRoutingTransactionAdviceBeanName = "dynamicRoutingTransactionAdvice";
    public static final String soaTransactionAttributeSourceNameMapProviderBeanName = "soaTransactionAttributeSourceNameMapProvider";
    public static final String requiredRuleBasedTransactionAttributeBeanName = "requiredRuleBasedTransactionAttribute";
    public static final String readOnlyRuleBasedTransactionAttributeBeanName = "readOnlyRuleBasedTransactionAttribute";
    public static final String dynamicRoutingTransactionAdvisorBeanName = "dynamicRoutingTransactionAdvisor";
    
    @Value("${hp.soa.data.mysql.global-transaction-management.timeout:3000}")
    private int timeout;
    @Value("${hp.soa.data.mysql.global-transaction-management.isolation:ISOLATION_READ_COMMITTED}")
    private String isolation;
    @Value("${hp.soa.data.mysql.global-transaction-management.propagation:PROPAGATION_REQUIRED}")
    private String propagation;
    @Value("${hp.soa.data.mysql.global-transaction-management.rollback-for:java.lang.Exception}")
    Class<Throwable>[] rollbackFor;
    
    @Value("${hp.soa.data.mysql.global-transaction-management.read-only-timeout:3000}")
    private int readOnlyTimeout;
    @Value("${hp.soa.data.mysql.global-transaction-management.read-only-isolation:ISOLATION_READ_COMMITTED}")
    private String readOnlyIsolation;
    @Value("${hp.soa.data.mysql.global-transaction-management.read-only-propagation:PROPAGATION_REQUIRED}")
    private String readOnlyPropagation;

    @Value("${hp.soa.data.mysql.global-transaction-management.pointcut-expression:}")
    private String pointcutExpression;
    
    /** 全局事务的默认 Advice */
    @Primary
    @Bean(dynamicRoutingTransactionAdviceBeanName)
    @ConditionalOnMissingBean(name = dynamicRoutingTransactionAdviceBeanName)
    public TransactionInterceptor dynamicRoutingTransactionAdvice(
        @Qualifier(dynamicRoutingTransactionManagerBeanName) TransactionManager transactionManager,
        @Qualifier(soaTransactionAttributeSourceNameMapProviderBeanName) SoaTransactionAttributeSourceNameMapProvider nameMapProvider,
        @Qualifier(requiredRuleBasedTransactionAttributeBeanName) RuleBasedTransactionAttribute required, 
        @Qualifier(readOnlyRuleBasedTransactionAttributeBeanName) RuleBasedTransactionAttribute readOnly)
    {
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        source.setNameMap(nameMapProvider.getNameMap(required, readOnly));
        
        return new TransactionInterceptor(transactionManager, source);
    }
    
    /** 全局事务的默认普通事务属性 */
    @Primary
    @Bean(requiredRuleBasedTransactionAttributeBeanName)
    @ConditionalOnMissingBean(name = requiredRuleBasedTransactionAttributeBeanName)
    RuleBasedTransactionAttribute requiredRuleBasedTransactionAttribute()
    {
        List<RollbackRuleAttribute> ruleAttrs = new ArrayList<>(rollbackFor.length);
        
        for(Class<Throwable> e : rollbackFor)
            ruleAttrs.add(new RollbackRuleAttribute(e));
        
        RuleBasedTransactionAttribute required = new RuleBasedTransactionAttribute();
        required.setPropagationBehaviorName(propagation);
        required.setIsolationLevelName(isolation);
        required.setTimeout(timeout);
        required.setRollbackRules(ruleAttrs);
        
        return required;
    }
    
    /** 全局事务的默认只读事务属性 */
    @Primary
    @Bean(readOnlyRuleBasedTransactionAttributeBeanName)
    @ConditionalOnMissingBean(name = readOnlyRuleBasedTransactionAttributeBeanName)
    RuleBasedTransactionAttribute readOnlyRuleBasedTransactionAttribute()
    {
        RuleBasedTransactionAttribute readOnly = new RuleBasedTransactionAttribute();
        readOnly.setPropagationBehaviorName(readOnlyPropagation);
        readOnly.setIsolationLevelName(readOnlyIsolation);
        readOnly.setTimeout(readOnlyTimeout);
        readOnly.setReadOnly(true);
        
        return readOnly;
    }
    
    /** 全局事务的默认事务拦截匹配参数提供者 */
    @Primary
    @Bean(soaTransactionAttributeSourceNameMapProviderBeanName)
    @ConditionalOnMissingBean(name = soaTransactionAttributeSourceNameMapProviderBeanName)
    SoaTransactionAttributeSourceNameMapProvider soaTransactionAttributeSourceNameMapProvider()
    {
        return new SoaTransactionAttributeSourceNameMapProvider() {};
    }

    /** 全局事务的默认 Advisor */
    @Primary
    @Bean(dynamicRoutingTransactionAdvisorBeanName)
    @ConditionalOnMissingBean(name = dynamicRoutingTransactionAdvisorBeanName)
    @ConditionalOnExpression("'${hp.soa.data.mysql.global-transaction-management.pointcut-expression:}' != ''")
    public Advisor dynamicRoutingTransactionAdvisor(@Qualifier(dynamicRoutingTransactionAdviceBeanName) TransactionInterceptor txAdvice)
    {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(pointcutExpression);
        return new DefaultPointcutAdvisor(pointcut, txAdvice);
    }
    
    public static interface SoaTransactionAttributeSourceNameMapProvider
    {
        default Map<String, TransactionAttribute> getNameMap(RuleBasedTransactionAttribute required, RuleBasedTransactionAttribute readOnly)
        {
            Map<String, TransactionAttribute> attributesMap = new HashMap<>();

            attributesMap.put("save*", required);
            attributesMap.put("remove*", required);
            attributesMap.put("update*", required);
            attributesMap.put("batch*", required);
            attributesMap.put("clear*", required);
            attributesMap.put("add*", required);
            attributesMap.put("append*", required);
            attributesMap.put("modify*", required);
            attributesMap.put("edit*", required);
            attributesMap.put("insert*", required);
            attributesMap.put("del*", required);
            attributesMap.put("delete*", required);
            attributesMap.put("do*", required);
            attributesMap.put("erase*", required);
            attributesMap.put("erase*", required);
            attributesMap.put("change*", required);
            attributesMap.put("create*", required);
            attributesMap.put("import*", required);
            attributesMap.put("increase*", required);
            attributesMap.put("reduce*", required);
            attributesMap.put("refund*", required);
            attributesMap.put("confirm*", required);
            attributesMap.put("consume*", required);
            attributesMap.put("cancel*", required);
            attributesMap.put("use*", required);
            attributesMap.put("bind*", required);
            attributesMap.put("apply*", required);
            attributesMap.put("submit*", required);
            attributesMap.put("lock*", required);
            attributesMap.put("unLock*", required);
            attributesMap.put("open*", required);
            attributesMap.put("close*", required);
            attributesMap.put("mark*", required);
            attributesMap.put("set*", required);
            attributesMap.put("reset*", required);

            attributesMap.put("select*", readOnly);
            attributesMap.put("get*", readOnly);
            attributesMap.put("valid*", readOnly);
            attributesMap.put("list*", readOnly);
            attributesMap.put("count*", readOnly);
            attributesMap.put("find*", readOnly);
            attributesMap.put("load*", readOnly);
            attributesMap.put("search*", readOnly);
            
            return attributesMap;
        }
    }

}
