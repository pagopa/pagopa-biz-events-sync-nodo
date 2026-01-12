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

    @Value("${spring.datasource.historic.schema}")
    private String historicSchema;

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
        build.setSchema(historicSchema);
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
}