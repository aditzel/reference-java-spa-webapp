package com.allanditzel.dashboard.test.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Collection;
import java.util.Map;

/**
 * Delegates to one or more {@code TestExecutionListener}s configured in the Spring {@code ApplicationContext}. This
 * allows other listeners to benefit from Spring injection, rather than requiring them to look up beans from the
 * context to prepare their internal state. It also allows both the listeners and their order to be configured in
 * Spring, rather than requiring them to be annotated on the test classes.
 *
 * @author Bryan Turner
 * @since 1.0
 */
public class DelegatingTestExecutionListener implements TestExecutionListener {

    private TestExecutionListener delegate;

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        getDelegate(testContext).afterTestClass(testContext);
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        getDelegate(testContext).afterTestMethod(testContext);
    }

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        getDelegate(testContext).beforeTestClass(testContext);
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        getDelegate(testContext).beforeTestMethod(testContext);
    }

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        getDelegate(testContext).prepareTestInstance(testContext);
    }

    protected TestExecutionListener getDelegate(TestContext context) {
        if (delegate == null || isDirty(context)) {
            //Find all TestExecutionListeners in the ApplicationContext and create a chain from them if necessary
            ApplicationContext applicationContext = context.getApplicationContext();
            Map<String, TestExecutionListener> beans = applicationContext.getBeansOfType(TestExecutionListener.class);

            Collection<TestExecutionListener> listeners = beans.values();
            if (listeners.size() == 1) {
                //Simple optimization--no point having the extra stack frame if we don't need it
                delegate = listeners.iterator().next();
            } else {
                delegate = new ChainTestExecutionListener(listeners);
            }
        }
        return delegate;
    }

    protected boolean isDirty(TestContext context) {
        //When @DirtiesContext is used, it sets this attribute for the DI listener's benefit. However, because we
        //are parked on top of the listener chain, our methods are guaranteed to be invoked first. That means we
        //have the opportunity to detect the attribute before the DI listener detects and removes it.
        return Boolean.TRUE.equals(context.getAttribute(DependencyInjectionTestExecutionListener.REINJECT_DEPENDENCIES_ATTRIBUTE));
    }
}
