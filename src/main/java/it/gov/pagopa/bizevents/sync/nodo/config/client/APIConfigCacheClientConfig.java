package it.gov.pagopa.bizevents.sync.nodo.config.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class APIConfigCacheClientConfig extends AuthorizationClientConfig {

  @Autowired
  public APIConfigCacheClientConfig(
      @Value("${client.apiconfig-cache.subscription-key}") String subscriptionKey) {

    this.subscriptionKey = subscriptionKey;
  }
}
