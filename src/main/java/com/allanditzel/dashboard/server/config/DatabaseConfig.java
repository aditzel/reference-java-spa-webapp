package com.allanditzel.dashboard.server.config;

import com.jolbox.bonecp.BoneCPDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Database configuration class
 *
 * @author Allan Ditzel
 * @since 1.0
 */
@Configuration
public class DatabaseConfig {
    @Value("${db.connection.url}")
    private String databaseConnectionUrl;

    @Value("${db.username}")
    private String databaseUsername;

    @Value("${db.password}")
    private String databasePassword;

    @Bean
    public DataSource dataSource() {
        BoneCPDataSource dataSource = new BoneCPDataSource();
        dataSource.setDriverClass("org.mariadb.jdbc.Driver");
        dataSource.setJdbcUrl(databaseConnectionUrl);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);

        return dataSource;
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource());

        return flyway;
    }
}
