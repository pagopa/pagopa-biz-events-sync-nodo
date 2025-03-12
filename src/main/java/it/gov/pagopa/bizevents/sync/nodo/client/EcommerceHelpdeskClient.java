package it.gov.pagopa.bizevents.sync.nodo.client;

import feign.FeignException;
import it.gov.pagopa.bizevents.sync.nodo.config.EcommerceHelpdeskClientConfig;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.request.SearchTransactionRequest;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response.SearchTransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    value = "ecommerce-helpdesk",
    url = "${client.ecommerce-helpdesk.host}",
    configuration = EcommerceHelpdeskClientConfig.class)
public interface EcommerceHelpdeskClient {

  @Retryable(
      noRetryFor = FeignException.FeignClientException.class,
      maxAttemptsExpression = "${client.ecommerce-helpdesk.retry.max-attempts}",
      backoff = @Backoff(delayExpression = "${client.ecommerce-helpdesk.retry.max-delay}"))
  @PostMapping(
      value = "${client.ecommerce-helpdesk.search-transaction.path}",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  SearchTransactionResponse searchTransactionByPaymentToken(
      @RequestBody SearchTransactionRequest body);
}
