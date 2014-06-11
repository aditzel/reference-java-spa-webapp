/*
 * Copyright 2014 Allan Ditzel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.allanditzel.dashboard.config;

import com.allanditzel.dashboard.test.dbunit.DatabaseConnectionFactory;
import com.allanditzel.dashboard.test.spring.ChainTestExecutionListener;
import com.allanditzel.dashboard.test.spring.DaoTestExecutionListener;
import com.allanditzel.dashboard.test.spring.DataSetTestExecutionListener;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.sql.DataSource;

/**
 * Test specific JPA configuration.
 */
@Configuration
public class TestsJpaConfig extends JpaConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public DatabaseConnectionFactory databaseConnectionFactory() {
        return new DatabaseConnectionFactory(dataSource());
    }

    @Override
    @Bean
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return new TransactionAwareDataSourceProxy(builder.setType(EmbeddedDatabaseType.H2).build());
    }

    @Bean
    @Primary
    public TestExecutionListener listeners() throws Exception {
        return new ChainTestExecutionListener(ImmutableList.of(
                new DependencyInjectionTestExecutionListener(),
                daoTestExecutionListener(),
                new DirtiesContextTestExecutionListener(),
                new TransactionalTestExecutionListener(),
                dataSetTestExecutionListener())
        );
    }

    private DaoTestExecutionListener daoTestExecutionListener() {
        return new DaoTestExecutionListener(applicationContext);
    }

    private DataSetTestExecutionListener dataSetTestExecutionListener() throws Exception {
        DataSetTestExecutionListener listener = new DataSetTestExecutionListener(databaseConnectionFactory(),
                entityManagerFactory(), applicationContext.getResource("classpath:dbunit/schema.dtd"));
        listener.createDataSetBuilder();

        return listener;
    }
}
