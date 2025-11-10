package it.gov.pagopa.bizevents.sync.nodo.config.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {
                "it.gov.pagopa.bizevents.sync.nodo.repository.primary.payment",
                "it.gov.pagopa.bizevents.sync.nodo.repository.primary.receipt"
        },
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        transactionManagerRef = "primaryTransactionManager"
)
public class PrimaryDataSourceConfig {

    @Value("${primary.datasource.url}")
    private String url;

    @Value("${primary.datasource.username}")
    private String username;

    @Value("${primary.datasource.password}")
    private String password;

    @Value("${primary.datasource.schema}")
    private String defaultSchema;

    @Value("${primary.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${primary.datasource.hikari.connectionTimeout}")
    private String connectionTimeout;

    @Value("${primary.datasource.hikari.maxLifetime}")
    private String maxLifetime;

    @Value("${primary.datasource.hikari.keepaliveTime}")
    private String keepaliveTime;

    @Value("${primary.datasource.hibernate-dialect}")
    private String hibernateDialect;

    @Primary
    @Bean(name = "primaryDataSource")
    public DataSource primaryDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setConnectionTimeout(Long.parseLong(connectionTimeout));
        hikariConfig.setMaxLifetime(Long.parseLong(maxLifetime));
        hikariConfig.setKeepaliveTime(Long.parseLong(keepaliveTime));
        return new HikariDataSource(hikariConfig);
    }

    @Primary
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            @Qualifier("primaryDataSource") DataSource dataSource) {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setShowSql(false);

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setJpaVendorAdapter(vendorAdapter);
        em.setPackagesToScan(
                "it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel",
                "it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel"
        );
        em.setPersistenceUnitName("primaryPersistenceUnit");

        Properties props = new Properties();
        props.put("hibernate.dialect", hibernateDialect);
        props.put("hibernate.default_schema", defaultSchema);
        props.put("hibernate.hbm2ddl.auto", "none");
        props.put("hibernate.jdbc.lob.non_contextual_creation", "true");
        props.put("hibernate.archive.autodetection", "class"); // usa "class" per evitare Invalid Magic
        props.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        em.setJpaProperties(props);

        return em;
    }

    @Primary
    @Bean(name = "primaryTransactionManager")
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
