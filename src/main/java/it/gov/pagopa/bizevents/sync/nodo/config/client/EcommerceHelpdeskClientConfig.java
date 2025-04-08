package it.gov.pagopa.bizevents.sync.nodo.config.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EcommerceHelpdeskClientConfig extends AuthorizationClientConfig {

  @Autowired
  public EcommerceHelpdeskClientConfig(
      @Value("${client.ecommerce-helpdesk.subscription-key}") String subscriptionKey) {

    this.subscriptionKey = subscriptionKey;
  }
}
