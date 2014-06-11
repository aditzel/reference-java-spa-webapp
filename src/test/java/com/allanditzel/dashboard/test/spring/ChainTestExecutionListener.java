package com.allanditzel.dashboard.test.spring;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.util.*;

/**
 * A {@code TestExecutionListener} implementation which chains one or more other listeners, applying them in configured
 * order for methods before the test is executed and in reverse order for methods executed after the test.
 *
 * @author Bryan Turner
 * @since 1.0
 */
public class ChainTestExecutionListener implements TestExecutionListener {

    //Both collections contain the same listeners, with the after list in reverse order from the before list. This is
    //necessary to honor the stack-like contract of listeners.
    private List<TestExecutionListener> afterListeners;
    private List<TestExecutionListener> beforeListeners;

    public ChainTestExecutionListener() {
    }

    public ChainTestExecutionListener(Collection<TestExecutionListener> listeners) {
        setTestExecutionListeners(listeners);
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        for (TestExecutionListener listener : afterListeners) {
            listener.afterTestClass(testContext);
        }
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        for (TestExecutionListener listener : afterListeners) {
            listener.afterTestMethod(testContext);
        }
    }

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        for (TestExecutionListener listener : beforeListeners) {
            listener.beforeTestClass(testContext);
        }
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        for (TestExecutionListener listener : beforeListeners) {
            listener.beforeTestMethod(testContext);
        }
    }

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        for (TestExecutionListener listener : beforeListeners) {
            listener.prepareTestInstance(testContext);
        }
    }

    public void setTestExecutionListener(TestExecutionListener listener) {
        setTestExecutionListeners(Arrays.asList(listener));
    }

    public void setTestExecutionListeners(Collection<TestExecutionListener> listeners) {
        beforeListeners = new ArrayList<TestExecutionListener>(listeners);

        //Reverse the listener order for the after listeners so they are unwound like a stack
        afterListeners = new ArrayList<TestExecutionListener>(listeners);
        Collections.reverse(afterListeners);
    }
}
