package it.gov.pagopa.bizevents.sync.nodo.repository.historic;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class HistoricHealthCheckRepository {

  EntityManager entityManager;

  @Value("${spring.datasource.historic.connection-test-query:}")
  private String query;

  public HistoricHealthCheckRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public String health() {
    Optional<Object> health =
        Optional.ofNullable(entityManager.createNativeQuery(query).getSingleResult());
    return health.isPresent() ? "up" : "down";
  }
}
