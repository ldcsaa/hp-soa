package io.github.hpsocket.soa.framework.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

/** <b>枚举值验证器</b> */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object>
{
    private EnumValue enumValue;

    @Override
    public void initialize(EnumValue constraintAnnotation)
    {
        this.enumValue = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context)
    {
        if(value == null)
            return true;

        Object[] enumConstants = enumValue.enumClass().getEnumConstants();

        if(enumConstants == null || enumConstants.length == 0)
            return false;

        try
        {
            Field field = null;
            for(Object enumConstant : enumConstants)
            {
                if(field == null)
                {
                    field = enumConstant.getClass().getDeclaredField(enumValue.field());
                    field.setAccessible(true);
                }

                Object enumVal = field.get(enumConstant);
                if(value.equals(enumVal) || value.toString().equals(String.valueOf(enumVal)))
                    return true;
            }
        }
        catch(Exception e)
        {
            return false;
        }

        return false;
    }
}
