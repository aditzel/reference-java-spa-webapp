package com.allanditzel.dashboard.test.dbunit;

import com.allanditzel.dashboard.test.AbstractDaoTest;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.FlatDtdWriter;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileWriter;
import java.util.Arrays;
import java.util.Map;

/**
 * Uses the {@link AbstractDaoTest} underpinnings to build the schema in the target database, then uses DbUnit's
 * {@code FlatDtdWriter} to produce an XML DTD describing the schema. The emitted DTD can then be used in the DbUnit
 * XML datasets to ensure the table and column names used are correct, and to get auto-complete assistance from IDEs
 * during development.
 * <p>
 * This "test" can be run from your editor of choice, and will write the schema DTD to {@code stash-schema.dtd} in
 * the working directory.
 * <p>
 * Note: The lower-casing classes nested here are designed around the {@code FlatDtdWriter}. They apply lowercase
 * conversions to the names of elements based on the methods the DTD writer actually calls on {@code IDataSet}. That
 * means there are still code paths in them which will return names in their actual case. They're not intended to be
 * drop-in replacements for the real thing, but rather to help this class emit a DTD which is easy to use and correct
 * to the way the Liquibase changesets are written (which always use lowercase names for everything).
 *
 * @author Bryan Turner
 * @since 1.0
 */
@Ignore("DTD generation should only be performed when the schema changes")
public class SchemaDtdGenerator extends AbstractDaoTest {

    @Autowired
    private DatabaseConnectionFactory connectionFactory;

    @Test
    public void writeDtd() throws Exception {
        IDatabaseConnection connection = connectionFactory.newConnection();
        try {
            IDataSet dataSet = new LowerCaseDataSet(connection.createDataSet());

            try (FileWriter fileWriter = new FileWriter("schema.dtd")) {
                FlatDtdWriter writer = new FlatDtdWriter(fileWriter);
                writer.setContentModel(FlatDtdWriter.CHOICE);
                writer.write(dataSet);
            }
        } finally {
            connection.close();
        }
    }

    /**
     * A delegating implementation of DbUnit's {@code IDataSet} interface which converts all table names to lowercase.
     * {@code ITableMetadata} instances returned are wrapped in {@link LowerCaseTableMetaData} to lowercase column and
     * primary key names as well.
     */
    private static class LowerCaseDataSet implements IDataSet {

        private final IDataSet delegate;
        private final String[] names;
        private final Map<String, String> tableNames;

        public LowerCaseDataSet(IDataSet delegate) throws DataSetException {
            this.delegate = delegate;

            tableNames = Maps.newHashMap();
            names = Iterables.toArray(Collections2.transform(
                    //Filter out Flyway's schema_version table; we should never populate that
                    Collections2.filter(Arrays.asList(delegate.getTableNames()), s -> !"schema_version".equalsIgnoreCase(s)),
                    //Map out the lowercase names to their original casing for metadata lookup later
                    name -> {
                        tableNames.put(name.toLowerCase(), name);

                        return name.toLowerCase();
                    }), String.class);
        }

        @Override
        public ITable getTable(String tableName) throws DataSetException {
            return delegate.getTable(tableNames.get(tableName));
        }

        @Override
        public ITableMetaData getTableMetaData(String tableName) throws DataSetException {
            ITableMetaData metaData = delegate.getTableMetaData(tableNames.get(tableName));

            return new LowerCaseTableMetaData(tableName, metaData);
        }

        @Override
        public String[] getTableNames() throws DataSetException {
            return names;
        }

        @Deprecated
        @Override
        @SuppressWarnings("deprecation")
        public ITable[] getTables() throws DataSetException {
            return delegate.getTables();
        }

        @Override
        public boolean isCaseSensitiveTableNames() {
            return false;
        }

        @Override
        public ITableIterator iterator() throws DataSetException {
            return delegate.iterator();
        }

        @Override
        public ITableIterator reverseIterator() throws DataSetException {
            return delegate.reverseIterator();
        }
    }

    /**
     * A delegating implementation of DbUnit's {@code ITableMetaData} interface which converts all column and primary
     * key names to lowercase.
     */
    private static class LowerCaseTableMetaData implements ITableMetaData {

        private final Map<String, String> columnNames;
        private final Column[] columns;
        private final ITableMetaData metaData;
        private final Column[] primaryKeys;
        private final String tableName;

        public LowerCaseTableMetaData(String tableName, ITableMetaData metaData) throws DataSetException {
            this.metaData = metaData;
            this.tableName = tableName;

            columnNames = Maps.newHashMap();
            columns = transformColumns(metaData.getColumns());
            primaryKeys = transformColumns(metaData.getPrimaryKeys());
        }

        @Override
        public int getColumnIndex(String columnName) throws DataSetException {
            return metaData.getColumnIndex(columnNames.get(columnName));
        }

        @Override
        public Column[] getColumns() throws DataSetException {
            return columns;
        }

        @Override
        public Column[] getPrimaryKeys() throws DataSetException {
            return primaryKeys;
        }

        @Override
        public String getTableName() {
            return tableName;
        }

        private Column[] transformColumns(Column[] columns) {
            return Iterables.toArray(Lists.transform(Arrays.asList(columns), column -> {
                String columnName = column.getColumnName();
                columnNames.put(columnName.toLowerCase(), columnName);

                return new Column(columnName.toLowerCase(), column.getDataType(),
                        column.getSqlTypeName(), column.getNullable(), column.getDefaultValue(),
                        column.getRemarks(), column.getAutoIncrement());
            }), Column.class);
        }
    }
}
