package com.allanditzel.dashboard.test.spring;

import com.allanditzel.dashboard.test.annotation.Dao;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;

/**
 * Applies the {@link Dao} annotation during test execution.
 *
 * @author Bryan Turner
 * @since 1.0
 */
public class DaoTestExecutionListener
        extends AbstractAnnotationTestExecutionListener<Dao> {

    private static final Class<?> DEFAULT_DAO_CLASS = (Class) AnnotationUtils.getDefaultValue(Dao.class);

    private final AutowireCapableBeanFactory beanFactory;

    public DaoTestExecutionListener(ApplicationContext applicationContext) {
        super(Dao.class);

        beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createValue(Field field, Dao annotation) throws IllegalAccessException {
        try {
            //Try to get the existing bean, if possible. This allows for more sophisticated wiring, because the
            //DAO can be manally defined in the ApplicationContext, complete with anonymous nested beans
            return beanFactory.getBean(field.getType());
        } catch (NoSuchBeanDefinitionException e) {
            //Otherwise, create one on the fly for this test. This assumes the DAO has a pretty straightforward
            //constructor, really only requiring a SessionFactory
            return beanFactory.createBean(getDaoClass(field, annotation), AutowireCapableBeanFactory.AUTOWIRE_NO, false);
        }
    }

    private Class<?> getDaoClass(Field field, Dao annotation) {
        return DEFAULT_DAO_CLASS.equals(annotation.value()) ? field.getType() : annotation.value();
    }
}
