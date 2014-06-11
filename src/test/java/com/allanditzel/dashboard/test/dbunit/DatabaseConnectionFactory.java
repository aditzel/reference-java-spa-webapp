package com.allanditzel.dashboard.test.dbunit;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Adapts between DbUnit's {@code IDatabaseConnection} and a standard Java {@code DataSource}.
 * <p/>
 * To facilitate DbUnit's use of the connected database, this factory applies Hibernate-like logic for attempting to
 * use the {@code DatabaseMetaData} to determine which DbUnit {@code IDataTypeFactory} it should use. If an explicit
 * mapping from the connected database to a factory is not available, the {@code DefaultDataTypeFactory} will be used
 * automatically.
 *
 * @author Bryan Turner
 * @since 1.0
 */
public class DatabaseConnectionFactory {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConnectionFactory.class);

    private final DataSource dataSource;

    private volatile IDataTypeFactory dataTypeFactory;

    public DatabaseConnectionFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Constructs a new DbUnit {@code DatabaseConnection}, configures its {@code IDataTypeFactory} and returns it.
     * <p/>
     * If a Spring transaction is in progress, this should automatically use the same connection. See the wiring for
     * this bean in {@code test-context.xml} for more on how that works.
     *
     * @return a new DbUnit connection
     * @throws DatabaseUnitException if a new {@code DatabaseConnection} cannot be constructed from the JDBC connection
     * @throws java.sql.SQLException          if a JDBC connection cannot be opened, or {@code DatabaseMetaData} cannot be
     *                               retrieved from it
     * @see #resolveDataTypeFactory(java.sql.Connection)
     */
    public IDatabaseConnection newConnection() throws DatabaseUnitException, SQLException {
        Connection jdbcConnection = dataSource.getConnection();
        if (dataTypeFactory == null) {
            //Not synchronised because a) unit tests tend to be serial and b) it should compute the same one anyway
            dataTypeFactory = resolveDataTypeFactory(jdbcConnection);
        }

        String schema = null;
        if (dataTypeFactory instanceof OracleDataTypeFactory) {
            //In Oracle, the schema name is the username. To speed up table detection in DbUnit, and also to ensure
            //the tests run correctly on users with high permissions, we want to only look in the user's schema. If
            //the user has high permissions, they start seeing SYS and SYSTEM schema tables, among others, as well
            //as their own, which can cause strange DbUnit failures.
            String userName = jdbcConnection.getMetaData().getUserName();
            schema = userName == null ? null : userName.toUpperCase();
        }
        DatabaseConnection connection = new DatabaseConnection(jdbcConnection, schema);

        DatabaseConfig configuration = connection.getConfig();
        configuration.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
        configuration.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, Boolean.TRUE);

        return connection;
    }

    /**
     * Uses the {@code DatabaseMetaData.getDatabaseProductName()}, and optionally the major version, to try and
     * determine the correct DbUnit {@code IDataTypeFactory} to use.
     * <p/>
     * If the database name does not match any of the heuristics applied, a {@code DefaultDataTypeFactory} is returned
     * and a warning is logged. Depending on the database, the default data type factory may result in test failures.
     *
     * @param connection a JDBC connection to obtain {@code DatabaseMetaData}
     * @return the data type factory to use, which will never be {@code null}
     * @throws java.sql.SQLException if metadata cannot be retrieved
     */
    private IDataTypeFactory resolveDataTypeFactory(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String databaseName = metaData.getDatabaseProductName();

        //The structure for this is drawn from Hibernate's StandardDialectResolver. I figure if these checks are robust
        //enough for Hibernate, they should be more than robust enough for our unit tests.
        IDataTypeFactory factory;
        if ("HSQL Database Engine".equals(databaseName)) {
            log.debug("Using HSQL DataTypeFactory");
            factory = new HsqldbDataTypeFactory();
        } else if ("H2".equals(databaseName)) {
            log.debug("Using H2 DataTypeFactory");
            factory = new H2DataTypeFactory();
        } else if ("MySQL".equals(databaseName)) {
            log.debug("Using MySQL DataTypeFactory");
            factory = new MySqlDataTypeFactory();
        } else if ("PostgreSQL".equals(databaseName)) {
            log.debug("Using Postgres DataTypeFactory");
            factory = new PostgresqlDataTypeFactory();
        } else if (databaseName.startsWith("Microsoft SQL Server")) {
            log.debug("Using SQL Server DataTypeFactory");
            factory = new MsSqlDataTypeFactory();
        } else if ("Oracle".equals(databaseName)) {
            if (metaData.getDatabaseMajorVersion() < 10) {
                log.debug("Using Oracle DataTypeFactory for 10g and later");
                factory = new OracleDataTypeFactory();
            } else {
                log.debug("Using Oracle DataTypeFactory for 9i and earlier");
                factory = new Oracle10DataTypeFactory();
            }
        } else {
            log.warn("No IDataTypeFactory was resolved for {}. Using default DataTypeFactory. This may result in " +
                    "test failures. If so, please update {} with an explicit DataTypeFactory for this database.",
                    databaseName, getClass());
            return new DefaultDataTypeFactory();
        }

        return factory;
    }
}