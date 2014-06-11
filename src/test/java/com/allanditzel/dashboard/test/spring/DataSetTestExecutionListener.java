package com.allanditzel.dashboard.test.spring;

import com.allanditzel.dashboard.test.annotation.DataSet;
import com.allanditzel.dashboard.test.dbunit.DatabaseConnectionFactory;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Cache;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Applies the {@link DataSet} annotation during test execution.
 * <p/>
 * Note: The methods on this class do full resource management but do not do <i>any</i> exception handling. Any issues
 * related to creating and inserting/deleting data sets can, and should, fail the associated test.
 *
 * @author Bryan Turner
 * @since 1.0
 */
public class DataSetTestExecutionListener extends AbstractTestExecutionListener {

    private final DatabaseConnectionFactory connectionFactory;
    private final Resource dtdResource;
    private final EntityManagerFactory entityManagerFactory;

    private DataSetBuilder dataSetBuilder;

    @SuppressWarnings("unused") //Someday I'm going to make a library out of this...
    public DataSetTestExecutionListener(DatabaseConnectionFactory connectionFactory,
                                        EntityManagerFactory entityManagerFactory) {
        this(connectionFactory, entityManagerFactory, null);
    }

    public DataSetTestExecutionListener(DatabaseConnectionFactory connectionFactory,
                                        EntityManagerFactory entityManagerFactory,
                                        Resource dtdResource) {
        this.connectionFactory = connectionFactory;
        this.dtdResource = dtdResource;
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * {@link #clearCache() Clears} the L2 cache after each test class has been run.
     *
     * @param testContext ignored
     */
    @Override
    public void afterTestClass(TestContext testContext) {
        clearCache();
    }

    /**
     * After the test method has completed, if the {@link DataSet} annotation is present and has been marked for
     * {@link DataSet#delete() deletion}, deletes the rows defined in the data set(s).
     *
     * @param testContext the test execution context
     * @throws Exception if resources cannot be loaded, a connection cannot be established, rows cannot be deleted or
     *                   any other issue prevents proper execution
     */
    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        DataSet annotation = getDataSetAnnotation(testContext);
        if (annotation != null && annotation.delete()) {
            applyAnnotation(testContext, annotation, DatabaseOperation.DELETE, true);
        }

        // if we loaded a data set just for the test method, we need to clear
        // out the L2 cache so that subsequent tests don't accidentally load
        // entities that were constructed previously.
        if (testContext.getTestMethod().isAnnotationPresent(DataSet.class)) {
            clearCache();
        }
    }

    /**
     * Before the test method runs, if the {@link DataSet} annotation is present and has been marked for
     * {@link DataSet#insert() insertion}, inserts the rows defined in the data set(s).
     *
     * @param testContext the test execution context
     * @throws Exception if resources cannot be loaded, a connection cannot be established, rows cannot be inserted
     *                   or any other issue prevents proper execution
     */
    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        DataSet annotation = getDataSetAnnotation(testContext);
        if (annotation != null && annotation.insert()) {
            applyAnnotation(testContext, annotation, DatabaseOperation.INSERT, false);
        }
    }

    /**
     * Creates the {@link DataSetBuilder} which will be used to transform Spring {@code Resource}s into DbUnit
     * {@code IDataSet} implementations. If a DTD {@code Resource} was supplied during construction, the builder
     * will use that DTD to provide metadata when constructing {@code IDataSet}s. Otherwise, it will attempt to
     * "sense" that metadata from the XML used as input.
     *
     * @throws DataSetException if the DTD resource cannot be transformed into an {@code IDataSet}
     * @throws java.io.IOException if a stream cannot be opened from the DTD resource
     */
    @PostConstruct
    public void createDataSetBuilder() throws DataSetException, IOException {
        if (dtdResource == null) {
            dataSetBuilder = new NoDtdDataSetBuilder();
        } else {
            dataSetBuilder = new DtdDataSetBuilder(new FlatDtdDataSet(dtdResource.getInputStream()));
        }
    }

    private void applyAnnotation(TestContext testContext, DataSet annotation, DatabaseOperation operation,
                                 boolean commit) throws Exception {
        ApplicationContext applicationContext = testContext.getApplicationContext();
        String[] locations = computeLocations(testContext, annotation);
        Resource[] resources = resolveResources(applicationContext, locations);
        IDataSet dataSet = createDataSet(resources);

        IDatabaseConnection connection = connectionFactory.newConnection();
        try {
            operation.execute(connection, dataSet);
            if (commit) {
                connection.getConnection().commit();
            }
        } finally {
            connection.close();
        }
    }

    /**
     * Evicts everything from L2 cache, so that tests don't interfere with each other.
     */
    private void clearCache() {
        Cache cache = entityManagerFactory.getCache().unwrap(Cache.class);
        //Evict all entities from the L2 cache (Ehcache) as well
        cache.evictEntityRegions();
        //Also evict any collections that have been cached
        cache.evictCollectionRegions();
    }

    private String[] computeLocations(TestContext testContext, DataSet dataSet) {
        String[] locations = dataSet.value();
        String[] defaultLocations = (String[]) AnnotationUtils.getDefaultValue(dataSet);
        if (Arrays.equals(locations, defaultLocations)) {
            Class<?> testClass = testContext.getTestClass();

            return new String[]{"classpath:dbunit/" + testClass.getSimpleName() + ".xml"};
        }
        return locations;
    }

    private IDataSet createDataSet(Resource[] resources) throws DataSetException, IOException {
        IDataSet[] dataSets = new IDataSet[resources.length];
        for (int i = 0; i < dataSets.length; ++i) {
            dataSets[i] = dataSetBuilder.build(resources[i]);
        }

        if (dataSets.length == 1) {
            return dataSets[0];
        }
        return new CompositeDataSet(dataSets);
    }

    private DataSet getDataSetAnnotation(TestContext testContext) {
        Method testMethod = testContext.getTestMethod();

        DataSet dataSet = testMethod.getAnnotation(DataSet.class);
        if (dataSet == null) {
            Class<?> testClass = testContext.getTestClass();
            dataSet = testClass.getAnnotation(DataSet.class);
        }
        return dataSet;
    }

    private Resource[] resolveResources(ResourceLoader resourceLoader, String[] locations) {
        Resource[] resources = new Resource[locations.length];
        for (int i = 0; i < locations.length; ++i) {
            resources[i] = resourceLoader.getResource(locations[i]);
        }
        return resources;
    }

    private interface DataSetBuilder {

        public IDataSet build(Resource resource) throws DataSetException, IOException;
    }

    private static class DtdDataSetBuilder implements DataSetBuilder {

        private final IDataSet dtd;

        private DtdDataSetBuilder(IDataSet dtd) {
            this.dtd = dtd;
        }

        @Override
        public IDataSet build(Resource resource) throws DataSetException, IOException {
            return new FlatXmlDataSetBuilder()
                    .setMetaDataSet(dtd)
                    .build(resource.getInputStream());
        }
    }

    private static class NoDtdDataSetBuilder implements DataSetBuilder {

        @Override
        public IDataSet build(Resource resource) throws DataSetException, IOException {
            return new FlatXmlDataSetBuilder()
                    .setColumnSensing(true)
                    .setDtdMetadata(false)
                    .build(resource.getInputStream());
        }
    }
}
