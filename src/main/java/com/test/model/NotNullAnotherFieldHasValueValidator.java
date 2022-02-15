package com.test.model;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

/**
 * Implementation of {@link NotNullIfAnotherFieldHasValue} validator.
 **/
public class NotNullAnotherFieldHasValueValidator
    implements ConstraintValidator<NotNullIfAnotherFieldHasValue, Object> {

    private String fieldName;
    private String[] dependFieldName;
    private String dependFieldObject;
    private String expectedFieldValue;
    @Override
    public void initialize(NotNullIfAnotherFieldHasValue annotation) {
        fieldName          = annotation.fieldName();
        dependFieldName    = annotation.dependFieldName();
        dependFieldObject =   annotation.dependFieldObject();
        expectedFieldValue= annotation.fieldValue();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext ctx) {

        if (value == null) {
            return true;
        }
        boolean isPassed = false;
        BeanWrapperImpl wrapper = new BeanWrapperImpl(value);
        Object fieldValue =  wrapper.getPropertyValue(fieldName);
        if(!expectedFieldValue.equalsIgnoreCase("null")&&fieldValue instanceof TestModel.X &&
                expectedFieldValue.equalsIgnoreCase(((TestModel.X) fieldValue).getVal())) {
            isPassed = isPassed(isPassed, wrapper);
        }else if(expectedFieldValue.equalsIgnoreCase("null")){
            isPassed = isPassed(isPassed, wrapper);
        }
            if (isPassed) {
                ctx.disableDefaultConstraintViolation();
                ctx.buildConstraintViolationWithTemplate(ctx.getDefaultConstraintMessageTemplate())
                        .addPropertyNode(Arrays.toString(dependFieldName))
                        .addConstraintViolation();
                return false;
            }

        return true;
    }

    private boolean isPassed(boolean isPassed, BeanWrapperImpl wrapper) {
        Object dependFieldValue;
        if (!dependFieldObject.equalsIgnoreCase("null")) {
            Object dependFieldObjectwrapper = wrapper.getPropertyValue(dependFieldObject);
            if (dependFieldObjectwrapper != null) {
                BeanWrapperImpl wrapper1 = new BeanWrapperImpl(dependFieldObjectwrapper);
                for (String s : dependFieldName) {
                    dependFieldValue = wrapper1.getPropertyValue(s);
                    if (dependFieldValue == null) {
                        isPassed = true;
                        break;
                    }
                }
            }
        } else {
            for (String s : dependFieldName) {
                dependFieldValue = wrapper.getPropertyValue(s);
                if (dependFieldValue == null) {
                    isPassed = true;
                    break;
                }
            }

        }
        return isPassed;
    }

}