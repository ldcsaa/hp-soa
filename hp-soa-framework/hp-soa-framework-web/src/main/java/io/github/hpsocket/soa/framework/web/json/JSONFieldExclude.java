package io.github.hpsocket.soa.framework.web.json;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Optional;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** <b>Json 属性排除注解</b><br>
 * 序列化时排除 {@linkplain Exclude} 注解指定的属性
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.FIELD)
public @interface JSONFieldExclude
{
	Exclude value() default Exclude.ALWAYS;
	
	/** <b>Json 属性排除类型</b> */
	public static enum Exclude
	{
		/** 如果属性值为 null 时，不序列化该属性 */
		NULL,
		/** 如果属性值为 null 空 {@linkplain Optional} 时，不序列化该属性 */
		ABSENT,
		/** 如果属性值为 null、空 {@linkplain Optional}、空字符串、空数组、空 {@linkplain java.util.Collection Collection}、空 {@linkplain java.util.Map Map} 时，不序列化该属性 */
		EMPTY,
		/** 任何情况下都不序列化该属性 */
		ALWAYS
	}
}
