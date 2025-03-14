package it.gov.pagopa.bizevents.sync.nodo.client;

import feign.FeignException;
import it.gov.pagopa.bizevents.sync.nodo.config.client.APIConfigCacheClientConfig;
import it.gov.pagopa.bizevents.sync.nodo.model.client.apiconfig.ConfigDataV1;
import it.gov.pagopa.bizevents.sync.nodo.model.client.apiconfig.ConfigDataVersion;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    value = "apiconfig-cache",
    url = "${client.apiconfig-cache.host}",
    configuration = APIConfigCacheClientConfig.class)
public interface APIConfigCacheClient {

  @Retryable(
      noRetryFor = FeignException.FeignClientException.class,
      maxAttemptsExpression = "${client.apiconfig-cache.retry.max-attempts}",
      backoff = @Backoff(delayExpression = "${client.apiconfig-cache.retry.max-delay}"))
  @GetMapping(
      value = "${client.apiconfig-cache.get-cache-by-keys.path}",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<ConfigDataV1> getCacheByKeys(@RequestParam(value = "keys") String keys);

  @Retryable(
      noRetryFor = FeignException.FeignClientException.class,
      maxAttemptsExpression = "${client.apiconfig-cache.retry.max-attempts}",
      backoff = @Backoff(delayExpression = "${client.apiconfig-cache.retry.max-delay}"))
  @GetMapping(
      value = "${client.apiconfig-cache.get-cache-latest-version.path}",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<ConfigDataVersion> getLatestVersion();
}
