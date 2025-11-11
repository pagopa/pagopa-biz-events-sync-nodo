package it.gov.pagopa.bizevents.sync.nodo.config.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.*;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.*;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

    @Value("${spring.datasource.primary.hibernate.dialect}")
    private String primaryDialect;

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.primary")
    public DataSourceProperties primaryDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource primaryDataSource() {
        HikariDataSource build = primaryDatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        build.setPoolName("primary");
        return build;
    }

    @Primary
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(EntityManagerFactoryBuilder builder) {

        Map<String, Object> jpaProps = new HashMap<>();
        jpaProps.put("hibernate.dialect", primaryDialect);
        jpaProps.put("hibernate.hbm2ddl.auto", "none");
        jpaProps.put("hibernate.archive.autodetection", "none");
        //jpaProps.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        jpaProps.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");

        return builder
                .dataSource(primaryDataSource())
                /*.packages(
                        "it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel",
                        "it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel")*/
                .classes( // NewModel
                        PositionPayment.class,
                        PositionPaymentPlan.class,
                        PositionReceipt.class,
                        PositionService.class,
                        PositionSubject.class,
                        PositionTransfer.class,
                        PositionTransferMBD.class,
                        // OldModel
                        Rpt.class,
                        RptSoggetti.class,
                        RptVersamenti.class,
                        Rt.class,
                        RtVersamenti.class,
                        RtXml.class
                )
                .properties(jpaProps)
                .build();
    }

    @Primary
    @Bean(name = "primaryTransactionManager")
    public PlatformTransactionManager primaryTransactionManager(final @Qualifier("primaryEntityManagerFactory") LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory) {
        return new JpaTransactionManager(primaryEntityManagerFactory.getObject());
    }
}
