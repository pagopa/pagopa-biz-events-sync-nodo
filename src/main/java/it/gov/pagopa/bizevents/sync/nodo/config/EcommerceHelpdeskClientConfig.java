package it.gov.pagopa.bizevents.sync.nodo.config;

import feign.RequestInterceptor;
import it.gov.pagopa.bizevents.sync.nodo.util.Constants;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class EcommerceHelpdeskClientConfig {

  protected String subscriptionKey;

  @Autowired
  public EcommerceHelpdeskClientConfig(@Value("${}") String subscriptionKey) {
    this.subscriptionKey = subscriptionKey;
  }

  @Bean
  public RequestInterceptor requestIdInterceptor() {
    return requestTemplate ->
        requestTemplate
            .header(Constants.HEADER_REQUEST_ID, MDC.get("requestId"))
            .header(Constants.HEADER_SUBSCRIPTION_KEY, subscriptionKey);
  }
}
