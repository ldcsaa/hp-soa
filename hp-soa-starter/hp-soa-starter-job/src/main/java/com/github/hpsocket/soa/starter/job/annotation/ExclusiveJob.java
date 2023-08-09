
package com.github.hpsocket.soa.starter.job.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scheduled
public @interface ExclusiveJob
{
	String jobName();
	String prefix() default "";
	long maxLockTime() default 50L;
	TimeUnit lockTimeUnit() default TimeUnit.MILLISECONDS;
	
	@AliasFor(annotation = Scheduled.class)
	String cron() default "";
	@AliasFor(annotation = Scheduled.class)
	String zone() default "";
	@AliasFor(annotation = Scheduled.class)
	long fixedDelay() default -1;
	@AliasFor(annotation = Scheduled.class)
	String fixedDelayString() default "";
	@AliasFor(annotation = Scheduled.class)
	long fixedRate() default -1;
	@AliasFor(annotation = Scheduled.class)
	String fixedRateString() default "";
	@AliasFor(annotation = Scheduled.class)
	long initialDelay() default -1;
	@AliasFor(annotation = Scheduled.class)
	String initialDelayString() default "";
	@AliasFor(annotation = Scheduled.class)
	TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
