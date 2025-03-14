package it.gov.pagopa.bizevents.sync.nodo.service;

import feign.FeignException;
import it.gov.pagopa.bizevents.sync.nodo.client.APIConfigCacheClient;
import it.gov.pagopa.bizevents.sync.nodo.model.client.apiconfig.ConfigDataV1;
import it.gov.pagopa.bizevents.sync.nodo.model.client.apiconfig.ConfigDataVersion;
import it.gov.pagopa.bizevents.sync.nodo.util.Constants;
import jakarta.annotation.PostConstruct;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConfigCacheService {

  private final APIConfigCacheClient apiConfigCacheClient;

  private ConfigDataV1 configData;

  public ConfigCacheService(APIConfigCacheClient apiConfigCacheClient) {

    this.apiConfigCacheClient = apiConfigCacheClient;
  }

  @PostConstruct
  public void initializeConfigData() {

    readConfigData();
  }

  public ConfigDataV1 getConfigData() {

    if (this.configData == null) {
      readConfigData();
    }
    return this.configData;
  }

  public void readConfigData() {

    boolean mustReadNewDataVersion;

    if (this.configData == null) {
      mustReadNewDataVersion = true;
    } else {
      ConfigDataVersion configDataVersion = readCurrentVersionId();

      if (configDataVersion == null) {
        mustReadNewDataVersion = true;
      } else {
        boolean isThereDifferentCacheVersion =
            !configDataVersion.getCacheVersion().equals(this.configData.getCacheVersion());
        boolean isThereNewerVersion =
            configDataVersion.getVersion().compareTo(this.configData.getVersion()) > 0;
        mustReadNewDataVersion = isThereDifferentCacheVersion || isThereNewerVersion;
      }
    }

    if (mustReadNewDataVersion) {
      readCurrentConfigData();
    }
  }

  private ConfigDataVersion readCurrentVersionId() {

    ConfigDataVersion configDataVersion = null;
    try {
      ResponseEntity<ConfigDataVersion> response = this.apiConfigCacheClient.getLatestVersion();
      if (response != null
          && response.getStatusCode().is2xxSuccessful()
          && response.getBody() != null) {
        configDataVersion = response.getBody();
        configDataVersion.setCacheVersion(
            Objects.requireNonNull(response.getHeaders().get("x-cache-id")).get(0));
      } else {
        // TODO log error
      }

    } catch (FeignException e) {
      log.error("[Cache] Error while retrieving config data from APIConfig cache.", e);
    }
    return configDataVersion;
  }

  private void readCurrentConfigData() {

    try {
      ResponseEntity<ConfigDataV1> response =
          this.apiConfigCacheClient.getCacheByKeys(Constants.APICONFIG_CACHE_REQUIRED_KEYS);
      if (response != null
          && response.getStatusCode().is2xxSuccessful()
          && response.getBody() != null) {
        this.configData = response.getBody();
        this.configData.setCacheVersion(
            Objects.requireNonNull(response.getHeaders().get("x-cache-version")).stream()
                .findFirst()
                .orElse(null));
        this.configData.setVersion(
            Objects.requireNonNull(response.getHeaders().get("x-cache-id")).stream()
                .findFirst()
                .orElse(null));
        // TODO log info
      } else {
        // TODO log error
      }

    } catch (FeignException e) {
      log.error("[Cache] Error while retrieving config data from APIConfig cache.", e);
    }
  }
}
