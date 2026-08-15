package io.github.hpsocket.soa.framework.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/** <b>离散值验证器</b> */
public class DiscreteValueValidator implements ConstraintValidator<DiscreteValue, Object>
{
    private Set<String> allowedValues;

    @Override
    public void initialize(DiscreteValue constraintAnnotation)
    {
        allowedValues = Arrays.stream(constraintAnnotation.intValues()).mapToObj(String::valueOf).collect(Collectors.toSet());
        allowedValues.addAll(Arrays.asList(constraintAnnotation.strValues()));
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context)
    {
        if(value == null)
            return true;

        return allowedValues.contains(String.valueOf(value));
    }
}
