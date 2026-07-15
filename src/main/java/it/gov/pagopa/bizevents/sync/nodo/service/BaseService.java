package it.gov.pagopa.bizevents.sync.nodo.service;

import it.gov.pagopa.bizevents.sync.nodo.model.AppInfo;
import it.gov.pagopa.bizevents.sync.nodo.repository.historic.HistoricHealthCheckRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.primary.HealthCheckRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BaseService {

  private final HealthCheckRepository healthCheckRepository;

  private final HistoricHealthCheckRepository historicDBHealthCheckRepository;

  @Value("${info.application.name}")
  private String name;

  @Value("${info.application.version}")
  private String version;

  @Value("${info.properties.environment}")
  private String environment;

  public BaseService(
      HealthCheckRepository healthCheckRepository,
      @Autowired(required = false) HistoricHealthCheckRepository historicDBHealthCheckRepository) {

    this.healthCheckRepository = healthCheckRepository;
    this.historicDBHealthCheckRepository = historicDBHealthCheckRepository;
  }

  public AppInfo health() {
    return AppInfo.builder()
        .name(name)
        .version(version)
        .environment(environment)
        .primaryDb(healthCheckRepository == null ? "disabled" : healthCheckRepository.health())
        .historicDb(historicDBHealthCheckRepository == null ? "disabled" : historicDBHealthCheckRepository.health())
        .build();
  }
}
