package it.gov.pagopa.bizevents.sync.nodo.scheduler;

import it.gov.pagopa.bizevents.sync.nodo.service.ConfigCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class ConfigDataLoaderScheduler {

  private final ConfigCacheService configCacheService;

  public ConfigDataLoaderScheduler(ConfigCacheService configCacheService) {

    this.configCacheService = configCacheService;
  }

  @Scheduled(cron = "{config-data-loader.schedule.expression}")
  @Async
  @Transactional
  void readNewestConfigData() {

    log.debug("");
    configCacheService.readConfigData();
  }
}
