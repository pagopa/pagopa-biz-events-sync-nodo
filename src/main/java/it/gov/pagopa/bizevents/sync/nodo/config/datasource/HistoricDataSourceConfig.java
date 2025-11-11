package it.gov.pagopa.bizevents.sync.nodo.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {
        "it.gov.pagopa.bizevents.sync.nodo.repository.historic"
    },
    entityManagerFactoryRef = "historicEntityManagerFactory",
    transactionManagerRef = "historicTransactionManager")
@ConditionalOnProperty(value = "spring.datasource.historic.enabled") //, havingValue = "false")
public class HistoricDataSourceConfig {



    @Value("${spring.datasource.historic.hibernate.dialect}")
    private String historicDialect;

    @Bean
    @ConfigurationProperties("spring.datasource.historic")
    public DataSourceProperties historicDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.historic")
    public DataSource historicDataSource() {
        HikariDataSource build = historicDatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        build.setPoolName("historic");
        return build;
    }

    @Bean(name = "historicEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean historicEntityManagerFactory(EntityManagerFactoryBuilder builder) {

        Map<String, Object> jpaProps = new HashMap<>();
        jpaProps.put("hibernate.dialect", historicDialect);
        jpaProps.put("hibernate.hbm2ddl.auto", "none");
        jpaProps.put("hibernate.archive.autodetection", "none");
        jpaProps.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");

        return builder
                .dataSource(historicDataSource())
                .packages(
                        "it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel",
                        "it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel")
                .properties(jpaProps)
                .build();
    }

    @Bean(name = "historicTransactionManager")
    public PlatformTransactionManager historicTransactionManager(final @Qualifier("historicEntityManagerFactory") LocalContainerEntityManagerFactoryBean historicEntityManagerFactory) {
        return new JpaTransactionManager(historicEntityManagerFactory.getObject());
    }

    /*
    @Value("${spring.datasource.historic.url}")
    private String url;

    @Value("${spring.datasource.historic.username}")
    private String username;

    @Value("${spring.datasource.historic.password}")
    private String password;

    @Value("${spring.datasource.historic.schema}")
    private String defaultSchema;

    @Value("${spring.datasource.historic.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.historic.connectionTimeout}")
    private String connectionTimeout;

    @Value("${spring.datasource.historic.maxLifetime}")
    private String maxLifetime;

    @Value("${spring.datasource.historic.keepaliveTime}")
    private String keepaliveTime;

    @Value("${spring.datasource.historic.hibernate-dialect}")
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
     */
}