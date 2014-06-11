package com.allanditzel.dashboard.test.spring;

import com.google.common.base.Throwables;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Base class to simplify building {@code TestExecutionListener}s which process the fields on the test class to apply
 * some behaviour to them based on the presence of an annotation.
 *
 * @author Bryan Turner
 * @since 1.0
 */
public abstract class AbstractAnnotationTestExecutionListener<T extends Annotation>
        extends AbstractTestExecutionListener {
    
    private final Class<T> annotationClass;

    protected AbstractAnnotationTestExecutionListener(Class<T> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public void prepareTestInstance(final TestContext testContext) throws Exception {
        Class<?> testClass = testContext.getTestClass();

        ReflectionUtils.doWithFields(testClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) {
                try {
                    T annotation = getAnnotation(field, annotationClass);
                    if (annotation != null) {
                        validateAnnotation(field, annotation);
                        applyAnnotation(testContext, field, annotation);
                    }
                } catch (Exception e) {
                    throw Throwables.propagate(e);
                }
            }
        });
    }
    
    protected void applyAnnotation(TestContext testContext, Field field, T annotation) throws Exception {
        Object value = createValue(field, annotation);
        
        field.setAccessible(true);
        field.set(testContext.getTestInstance(), value);
    }
    
    protected abstract Object createValue(Field field, T annotation) throws Exception;
    
    protected T getAnnotation(Field field, Class<T> annotationClass) {
        return field.getAnnotation(annotationClass);
    }
    
    protected void validateAnnotation(Field field, T annotation) {
        //No-op
    }
}
