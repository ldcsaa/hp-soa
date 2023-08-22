package io.github.hpsocket.soa.starter.data.mysql.config;

import java.util.Map;

import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

public interface SoaTransactionAttributeSourceNameMapProvider
{
	Map<String, TransactionAttribute> getNameMap(RuleBasedTransactionAttribute required, RuleBasedTransactionAttribute readOnly);
}
