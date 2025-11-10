package it.gov.pagopa.bizevents.sync.nodo.config.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {
        "it.gov.pagopa.bizevents.sync.nodo.repository.historic"
    },
    entityManagerFactoryRef = "historicEntityManagerFactory",
    transactionManagerRef = "historicTransactionManager")
@ConditionalOnProperty(value = "historic.datasource.enabled")
public class HistoricDataSourceConfig {

    @Value("${historic.datasource.url}")
    private String url;

    @Value("${historic.datasource.username}")
    private String username;

    @Value("${historic.datasource.password}")
    private String password;

    @Value("${historic.datasource.schema}")
    private String defaultSchema;

    @Value("${historic.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${historic.datasource.hikari.connectionTimeout}")
    private String connectionTimeout;

    @Value("${historic.datasource.hikari.maxLifetime}")
    private String maxLifetime;

    @Value("${historic.datasource.hikari.keepaliveTime}")
    private String keepaliveTime;

    @Value("${historic.datasource.hibernate-dialect}")
    private String hibernateDialect;

    @Bean(name = "historicDataSource")
    public DataSource historyDataSource() {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setConnectionTimeout(Long.parseLong(connectionTimeout));
        hikariConfig.setMaxLifetime(Long.parseLong(maxLifetime));
        hikariConfig.setKeepaliveTime(Long.parseLong(keepaliveTime));
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "historicEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean historyEntityManagerFactoryBean (
        EntityManagerFactoryBuilder builder,
        @Qualifier("historicDataSource") DataSource dataSource) {

        // Setting entity manager properties
        LocalContainerEntityManagerFactoryBean entityManager = builder
                .dataSource(dataSource)
                .packages(
                        "it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel",
                        "it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel"
                )
                .persistenceUnit("historicPersistenceUnit")
                .build();
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManager.setJpaVendorAdapter(vendorAdapter);

        // Setting JPA properties
        Properties props = new Properties();
        props.put("hibernate.dialect", hibernateDialect);
        props.put("hibernate.database-platform", hibernateDialect);
        props.put("hibernate.ddl-auto", "none");
        props.put("hibernate.hbm2ddl.auto", "none");
        props.put("hibernate.default_schema", defaultSchema);
        props.put("hibernate.jdbc.lob.non_contextual_creation", "true");
        props.put("hibernate.archive.autodetection", "none");
        props.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        entityManager.setJpaProperties(props);

        return entityManager;
    }

    @Bean(name = "historicTransactionManager")
    public PlatformTransactionManager historyTransactionManager (@Qualifier("historicEntityManagerFactory") EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }
}