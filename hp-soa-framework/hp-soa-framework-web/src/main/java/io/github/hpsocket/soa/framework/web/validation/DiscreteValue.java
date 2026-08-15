package io.github.hpsocket.soa.framework.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/** <b>离散值验证注解</b> */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DiscreteValueValidator.class)
public @interface DiscreteValue
{
    String message() default "Parameter value is outside the allowed discrete value range";
    int[] intValues() default {};
    String[] strValues() default {};
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
