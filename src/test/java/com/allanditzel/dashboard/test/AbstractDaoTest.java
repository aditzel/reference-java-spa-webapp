package com.allanditzel.dashboard.test;

import com.allanditzel.dashboard.config.PropertyConfig;
import com.allanditzel.dashboard.config.TestsJpaConfig;
import com.allanditzel.dashboard.test.spring.DelegatingTestExecutionListener;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * A base class for building DAO tests, and other tests which should be run against a database rather than using mocks.
 * <p/>
 * For simplicity in testing, this base class extends JUnit's {@code Assert} class, exposing all testing assertions
 * directly to derived types. As a result, derived types should not need to import assertions.
 * <p/>
 * The Spring application context created by this class for any tests derived from it is <i>cached</i>. That means the
 * same context will be used across all such tests. As a result, tests which irreparably change the context must be
 * marked with {@code DirtiesContext} to trigger the context to be reconstructed for the next test or they may trigger
 * cascading test failures.
 * <p/>
 * All tests are run in a transaction, and that transaction is <i>rolled back</i> when the test ends (even if the test
 * ends successfully without throwing an exception). This reduces the amount of database cleanup that has to be done
 * to keep table contents in a consistent state.
 * <p/>
 * <b>Warning</b>: It is <i>not safe</i> to assume that tests are being run against a throwaway in-memory HSQL database.
 * The test {@code ApplicationContext} allows for specifying a real database. As a result, tests <i>must</i> clean up
 * after themselves. The easiest way to accomplish this is to ensure that the test does not commit changes it makes.
 * Failing that, another approach must be used. The {@link com.allanditzel.dashboard.test.annotation.DataSet DataSet}
 * annotation includes a {@code delete()} flag which may be useful in applying cleanup in some tests. These cleanup
 * rules apply not only to rows inserted, updated or deleted, but also to any schema changes such as creating or
 * dropping tables.
 *
 * @author Bryan Turner
 * @since 1.0
 */
@ContextConfiguration(classes = { TestsJpaConfig.class, PropertyConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(DelegatingTestExecutionListener.class)
@Transactional
public abstract class AbstractDaoTest extends Assert {
}
