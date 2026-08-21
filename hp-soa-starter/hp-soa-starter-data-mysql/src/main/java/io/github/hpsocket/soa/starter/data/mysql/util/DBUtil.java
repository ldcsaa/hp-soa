package io.github.hpsocket.soa.starter.data.mysql.util;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.spring.service.IService;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Bruce
 * @date 2026/7/2 21:47
 */
public class DBUtil
{

    public static <T> boolean isFieldDuplicate(IService<T> service, SFunction<T, ?> field, Object value)
    {
        return service.lambdaQuery()
                        .eq(field, value)
                        .last("LIMIT 1")
                        .count() > 0;
    }

    public static <T> boolean isFieldDuplicate(IService<T> service, SFunction<T, ?> field, Object value, SFunction<T, ?> excludedField, Object excludedValue)
    {
        if(excludedValue == null)
            return isFieldDuplicate(service, field, value);

        return service.lambdaQuery()
                        .eq(field, value)
                        .ne(excludedField, excludedValue)
                        .last("LIMIT 1")
                        .count() > 0;
    }

    public static <T> boolean isFieldDuplicate(IService<T> service, Map<SFunction<T, ?>, Object> fieldValues)
    {
        LambdaQueryChainWrapper<T> queryWrapper = service.lambdaQuery();
        fieldValues.forEach(queryWrapper::eq);
        queryWrapper.last("LIMIT 1");

        return queryWrapper.count() > 0;
    }

    public static <T> boolean isFieldDuplicate(IService<T> service, Map<SFunction<T, ?>, Object> fieldValues, SFunction<T, ?> excludedField, Object excludedValue)
    {
        if(excludedValue == null)
            return isFieldDuplicate(service, fieldValues);

        LambdaQueryChainWrapper<T> queryWrapper = service.lambdaQuery();
        fieldValues.forEach(queryWrapper::eq);

        return queryWrapper
                .ne(excludedField, excludedValue)
                .last("LIMIT 1")
                .count() > 0;
    }

    public static <T> boolean isFieldDuplicate2(IService<T> service, SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2)
    {
        return service.lambdaQuery()
                        .eq(field1, value1)
                        .eq(field2, value2)
                        .last("LIMIT 1")
                        .count() > 0;
    }

    public static <T> boolean isFieldDuplicate2(IService<T> service, SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2, SFunction<T, ?> excludedField, Object excludedValue)
    {
        if(excludedValue == null)
            return isFieldDuplicate2(service, field1, value1, field2, value2);

        return service.lambdaQuery()
                        .eq(field1, value1)
                        .eq(field2, value2)
                        .ne(excludedField, excludedValue)
                        .last("LIMIT 1")
                        .count() > 0;
    }

    public static <T> Long getAvailableId(IService<T> service, SFunction<T, ?> idField, int length)
    {
        long min = (long)Math.pow(10, length - 1);
        long max = (long)Math.pow(10, length);

        while(true)
        {
            long id = ThreadLocalRandom.current().nextLong(min, max);
            long count = service.lambdaQuery()
                                .eq(idField, id)
                                .count();

            if(count == 0)
                return id;
        }
    }

    public static void afterTransaction(Runnable afterCommit)
    {
        afterTransaction(afterCommit, null);
    }

    public static void afterTransaction(Runnable afterCommit, Runnable afterCompletion)
    {
        if(!TransactionSynchronizationManager.isSynchronizationActive())
        {
            if(afterCommit != null)
                afterCommit.run();

            if(afterCompletion != null)
                afterCompletion.run();

            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
        {
            @Override
            public void afterCommit()
            {
                if(afterCommit != null)
                    afterCommit.run();
            }

            @Override
            public void afterCompletion(int status)
            {
                if(afterCompletion != null)
                    afterCompletion.run();
            }
        });
    }

    public static <T> void afterTransaction(Consumer<T> afterCommit, T argAfterCommit)
    {
        afterTransaction(afterCommit, argAfterCommit, null, null);
    }

    public static <X, Y> void afterTransaction(Consumer<X> afterCommit, X argAfterCommit, Consumer<Y> afterCompletion, Y argAfterCompletion)
    {
        if(!TransactionSynchronizationManager.isSynchronizationActive())
        {
            if(afterCommit != null)
                afterCommit.accept(argAfterCommit);

            if(afterCompletion != null)
                afterCompletion.accept(argAfterCompletion);

            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
        {
            @Override
            public void afterCommit()
            {
                if(afterCommit != null)
                    afterCommit.accept(argAfterCommit);
            }

            @Override
            public void afterCompletion(int status)
            {
                if(afterCompletion != null)
                    afterCompletion.accept(argAfterCompletion);
            }
        });
    }

    public static <X1, X2> void afterTransaction(BiConsumer<X1, X2> afterCommit, X1 argAfterCommit1, X2 argAfterCommit2)
    {
        afterTransaction(afterCommit, argAfterCommit1, argAfterCommit2, null, null, null);
    }

    public static <X1, X2, Y1, Y2> void afterTransaction(BiConsumer<X1, X2> afterCommit, X1 argAfterCommit1, X2 argAfterCommit2, BiConsumer<Y1, Y2> afterCompletion, Y1 argAfterCompletion1, Y2 argAfterCompletion2)
    {
        if(!TransactionSynchronizationManager.isSynchronizationActive())
        {
            if(afterCommit != null)
                afterCommit.accept(argAfterCommit1, argAfterCommit2);

            if(afterCompletion != null)
                afterCompletion.accept(argAfterCompletion1, argAfterCompletion2);

            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
        {
            @Override
            public void afterCommit()
            {
                if(afterCommit != null)
                    afterCommit.accept(argAfterCommit1, argAfterCommit2);
            }

            @Override
            public void afterCompletion(int status)
            {
                if(afterCompletion != null)
                    afterCompletion.accept(argAfterCompletion1, argAfterCompletion2);
            }
        });
    }

}
