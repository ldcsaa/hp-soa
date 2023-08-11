package com.github.hpsocket.soa.framework.web.json;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(ElementType.FIELD)
public @interface JSONFieldExclude
{
	Exclude value() default Exclude.ALWAYS;
	
	public static enum Exclude
	{
		NULL,
		ABSENT,
		EMPTY,
		ALWAYS
	}
}
