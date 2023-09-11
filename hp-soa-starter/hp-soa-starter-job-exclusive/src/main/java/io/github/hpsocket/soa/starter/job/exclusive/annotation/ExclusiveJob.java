
package io.github.hpsocket.soa.starter.job.exclusive.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/** <b>分布式独占 Job 注解</b> */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scheduled
public @interface ExclusiveJob
{
	/** JOB 唯一名称（必填参数） */
	String jobName();
	
	/** 默认值：${spring.application.name}，JOB 完整名称为：${prefix}:${jobName} */
	String prefix() default "";
	
	/** 锁定时间值
	 * {@linkplain ExclusiveJob#maxLockTime maxLockTime} <= 0 会开启自动续期功能，当连接异常断开 30 秒后自动解锁<br>
	 * {@linkplain ExclusiveJob#maxLockTime maxLockTime} > 0 不会开启自动续期功能，当超过 {@linkplain ExclusiveJob#maxLockTime maxLockTime} 后自动解锁
	 */
	long maxLockTime() default -1L;
	/** {@linkplain ExclusiveJob#maxLockTime maxLockTime} 时间单位（默认：毫秒） */
	TimeUnit lockTimeUnit() default TimeUnit.MILLISECONDS;
	
	/** 参考 {@linkplain Scheduled#cron} */
	@AliasFor(annotation = Scheduled.class)
	String cron() default "";
	/** 参考 {@linkplain Scheduled#zone} */
	@AliasFor(annotation = Scheduled.class)
	String zone() default "";
	/** 参考 {@linkplain Scheduled#fixedDelay} */
	@AliasFor(annotation = Scheduled.class)
	long fixedDelay() default -1;
	/** 参考 {@linkplain Scheduled#fixedDelayString} */
	@AliasFor(annotation = Scheduled.class)
	String fixedDelayString() default "";
	/** 参考 {@linkplain Scheduled#fixedRate} */
	@AliasFor(annotation = Scheduled.class)
	long fixedRate() default -1;
	/** 参考 {@linkplain Scheduled#fixedRateString} */
	@AliasFor(annotation = Scheduled.class)
	String fixedRateString() default "";
	/** 参考 {@linkplain Scheduled#initialDelay} */
	@AliasFor(annotation = Scheduled.class)
	long initialDelay() default -1;
	/** 参考 {@linkplain Scheduled#initialDelayString} */
	@AliasFor(annotation = Scheduled.class)
	String initialDelayString() default "";
	/** 参考 {@linkplain Scheduled#timeUnit} */
	@AliasFor(annotation = Scheduled.class)
	TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
