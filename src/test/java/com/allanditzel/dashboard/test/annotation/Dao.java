package com.allanditzel.dashboard.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a field on a test class into which the DAO under test should be injected.
 * <p/>
 * To properly apply this annotation to a field:
 * <ul>
 *     <li>If the field's type is an interface implemented by the DAO rather than the implementation class, the
 *     {@link #value()} must be specified</li>
 *     <li>If the field's type is the desired DAO implementation, {@link #value()} can be left default</li>
 * </ul>
 * <p/>
 * DAOs instantiated via this annotation must follow one of 2 construction paradigms:
 * <ul>
 *     <li>A nullary constructor paired with a writable "entityManagerFactory" property</li>
 *     <li>A single-argument constructor accepting a {@code EntityManagerFactory} as a parameter</li>
 * </ul>
 * Annotated fields will be injected with constructed instances (<i>not mocks!</i>) of the desired implementation which
 * has been initialised with the JPA {@code EntityManagerFactory} defined in the test application context.
 * <p/>
 * This annotation is intended as a simplification for building DAO DbUnit tests. In such tests, one or more
 * {@link DataSet data sets} are loaded and then a JPA DAO is tested against that data. Rather than writing
 * that initialisation code, be it in each test, in a helper method or in an {@code &#064;Before}-annotated method,
 * the field can simply be annotated {@code &#064;Dao} and the initialization will happen automatically.
 *
 * @author Bryan Turner
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Dao {

    /**
     * If the type of the annotated field is not instantiable, for example when annotating a field which is an interface
     * rather than the exact implementation, the concrete class can be specified by setting this property.
     *
     * @return the DAO class to instantiate, or {@code null} if the field's type is the desired implementation
     */
    Class<?> value() default None.class;
    
    public static final class None {
        //Placeholder class for value's default
    }
}
