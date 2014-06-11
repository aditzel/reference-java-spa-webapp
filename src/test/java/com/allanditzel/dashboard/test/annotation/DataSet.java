package com.allanditzel.dashboard.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a list of 1 or more resources which reference DbUnit XML data sets. The data defined in each set is inserted
 * into the database before each individual test is run and rolled back after the test completes. This means that, at
 * the start of each test, the database contents are guaranteed to match <i>exactly</i> the unified contents of all of
 * the defined data sets.
 * <p/>
 * This annotation can be applied to a test class or a specific {@code &#064;Test} method. Annotations applied at the
 * method level <i>supersede and replace</i> an annotation at the class level--they are not merged. An annotation at
 * the class level can be thought of as a default list of data sets applied if the test does not explicitly define any
 * data sets of its own.
 * <p/>
 * Spring's {@code Resource} abstraction is used to load the data sets, providing the full power of Spring's excellent
 * resource support code. This means values like "classpath:dbunit/tenant.xml" will be correctly resolved using Spring's
 * {@code ClassPathResource}.
 *
 * @author Bryan Turner
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DataSet {

    /**
     * Retrieves a flag indicating whether the rows from the {@link #value() referenced data set(s)} should be deleted
     * after the test ends.
     * <p/>
     * By default, because tests operate within a transaction which will be rolled back, this is {@code false}. However,
     * for tests which do end up inserting and committing data, this annotation should be applied with this flag set to
     * {@code true} so that such rows will be cleaned up after the test. This ensures consistent state for other tests.
     *
     * @return {@code true} if rows from the specified data sets should be deleted; otherwise, {@code false}
     */
    boolean delete() default false;

    /**
     * Retrieves a flag indicating whether the rows from the {@link #value() referenced data set(s)} should be inserted
     * before the test starts.
     * <p/>
     * By default, because it is assumed most annotated tests will be trying to apply a set of test data, this is
     * {@code true}. However, for tests which mutate the database in some other way, it may be useful to set this to
     * {@code false} and use the annotation to list data sets for {@link #delete() cleanup} after the test completes.
     *
     * @return {@code true} if rows from the specified data sets should be inserted; otherwise, {@code false}
     */
    boolean insert() default true;

    /**
     * Defines 1 or more resources which reference DbUnit XML data sets. If no explicit value is set, the "default"
     * value is interpreted as {@code "classpath:dbunit/&lt;TestClassSimpleName&gt;.xml}.
     *
     * @return 1 or more data set XML resources
     */
    String[] value() default "default";
}