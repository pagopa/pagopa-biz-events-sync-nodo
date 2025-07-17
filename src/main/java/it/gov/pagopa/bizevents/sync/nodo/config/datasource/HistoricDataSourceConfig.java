package it.gov.pagopa.bizevents.sync.nodo.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "it.gov.pagopa.bizevents.sync.nodo.repository.historic",
    entityManagerFactoryRef = "historicEntityManagerFactory",
    transactionManagerRef = "historicTransactionManager")
public class HistoricDataSourceConfig {

  @Bean(name = "historicDataSource")
  /*
  @ConfigurationProperties(prefix = "historic.datasource")
  public HikariDataSource dataSource() {
      return new HikariDataSource();
  }*/
  public DataSource dataSource(
      @Value("${historic.datasource.url}") String url,
      @Value("${historic.datasource.username}") String username,
      @Value("${historic.datasource.password}") String password,
      @Value("${historic.datasource.driver-class-name}") String driverClassName) {
    HikariDataSource ds = new HikariDataSource();
    ds.setJdbcUrl(url);
    ds.setUsername(username);
    ds.setPassword(password);
    ds.setDriverClassName(driverClassName);
    return ds;
  }

  @Bean(name = "historicEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      EntityManagerFactoryBuilder builder, @Qualifier("historicDataSource") DataSource dataSource) {
    return builder
        .dataSource(dataSource)
        .packages(
            "it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel",
            "it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel")
        .persistenceUnit("historic")
        .build();
  }

  @Bean(name = "historicTransactionManager")
  public PlatformTransactionManager transactionManager(
      @Qualifier("historicEntityManagerFactory") EntityManagerFactory emf) {
    return new JpaTransactionManager(emf);
  }
}
