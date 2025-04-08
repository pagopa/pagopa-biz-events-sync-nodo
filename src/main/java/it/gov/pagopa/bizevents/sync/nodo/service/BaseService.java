package it.gov.pagopa.bizevents.sync.nodo.service;

import it.gov.pagopa.bizevents.sync.nodo.model.AppInfo;
import it.gov.pagopa.bizevents.sync.nodo.repository.HealthCheckRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BaseService {

  private final HealthCheckRepository healthCheckRepository;

  @Value("${info.application.name}")
  private String name;

  @Value("${info.application.version}")
  private String version;

  @Value("${info.properties.environment}")
  private String environment;

  public BaseService(HealthCheckRepository healthCheckRepository) {

    this.healthCheckRepository = healthCheckRepository;
  }

  public AppInfo health() {
    return AppInfo.builder()
        .name(name)
        .version(version)
        .environment(environment)
        .db(healthCheckRepository.health())
        .build();
  }
}
