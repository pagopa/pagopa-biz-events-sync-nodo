package it.gov.pagopa.bizevents.sync.nodo.config.datasource;

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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {
      "it.gov.pagopa.bizevents.sync.nodo.repository.primary.payment",
      "it.gov.pagopa.bizevents.sync.nodo.repository.primary.receipt"
    },
    entityManagerFactoryRef = "primaryEntityManagerFactory",
    transactionManagerRef = "primaryTransactionManager")
public class PrimaryDataSourceConfig {

  @Bean(name = "primaryDataSource")
  @Primary
  // @ConfigurationProperties("spring.datasource")
  /*public HikariDataSource dataSource() {
      return new HikariDataSource();
  }*/
  public DataSource dataSource(
      @Value("${spring.datasource.url}") String url,
      @Value("${spring.datasource.username}") String username,
      @Value("${spring.datasource.password}") String password,
      @Value("${spring.datasource.driver-class-name}") String driverClassName,
      @Value("${spring.datasource.hikari.maxLifetime}") Long maxLifetime,
      @Value("${spring.datasource.hikari.keepaliveTime}") Long keepaliveTime,
      @Value("${spring.datasource.hikari.connectionTimeout}") Long connectionTimeout) {
    HikariDataSource ds = new HikariDataSource();
    ds.setJdbcUrl(url);
    ds.setUsername(username);
    ds.setPassword(password);
    ds.setDriverClassName(driverClassName);
    ds.setMaxLifetime(maxLifetime);
    ds.setKeepaliveTime(keepaliveTime);
    ds.setConnectionTimeout(connectionTimeout);
    return ds;
  }

  @Bean(name = "primaryEntityManagerFactory")
  @Primary
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      EntityManagerFactoryBuilder builder, @Qualifier("primaryDataSource") DataSource dataSource) {
    return builder
        .dataSource(dataSource)
        .packages(
            "it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel",
            "it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel")
        .persistenceUnit("primary")
        .build();
  }

  @Bean(name = "primaryTransactionManager")
  @Primary
  public PlatformTransactionManager transactionManager(
      @Qualifier("primaryEntityManagerFactory") EntityManagerFactory emf) {
    return new JpaTransactionManager(emf);
  }
}
