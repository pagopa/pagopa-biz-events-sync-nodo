package it.gov.pagopa.bizevents.sync.nodo.client;

import feign.FeignException;
import it.gov.pagopa.bizevents.sync.nodo.config.client.APIConfigCacheClientConfig;
import it.gov.pagopa.bizevents.sync.nodo.model.client.apiconfig.ConfigDataV1;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
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
  @PostMapping(
      value = "${client.apiconfig-cache.get-cache-by-keys.path}",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  ConfigDataV1 getCacheByKeys(@RequestParam(value = "keys") String keys);
}
