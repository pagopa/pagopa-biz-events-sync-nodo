package it.gov.pagopa.bizevents.sync.nodo.config.client;

import feign.RequestInterceptor;
import it.gov.pagopa.bizevents.sync.nodo.util.Constants;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;

public abstract class AuthorizationClientConfig {

  protected String subscriptionKey;

  @Bean
  public RequestInterceptor requestIdInterceptor() {
    return requestTemplate ->
        requestTemplate
            .header(Constants.HEADER_REQUEST_ID, MDC.get("requestId"))
            .header(Constants.HEADER_SUBSCRIPTION_KEY, subscriptionKey);
  }
}
