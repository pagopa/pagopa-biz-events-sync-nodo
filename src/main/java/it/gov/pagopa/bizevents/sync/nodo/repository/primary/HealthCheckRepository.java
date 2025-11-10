package it.gov.pagopa.bizevents.sync.nodo.repository.primary;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class HealthCheckRepository {

  EntityManager entityManager;

  @Value("${primary.datasource.health-check.query}")
  private String query;

  public HealthCheckRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public String health() {
    Optional<Object> health =
        Optional.ofNullable(entityManager.createNativeQuery(query).getSingleResult());
    return health.isPresent() ? "up" : "down";
  }
}
