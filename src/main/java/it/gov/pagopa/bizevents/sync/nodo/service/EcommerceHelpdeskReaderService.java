package it.gov.pagopa.bizevents.sync.nodo.service;

import feign.FeignException;
import feign.FeignException.FeignServerException;
import it.gov.pagopa.bizevents.sync.nodo.client.EcommerceHelpdeskClient;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.TransactionDetails;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.request.SearchTransactionRequest;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response.SearchTransactionResponse;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response.TransactionResponse;
import it.gov.pagopa.bizevents.sync.nodo.model.mapper.BizEventMapper;
import it.gov.pagopa.bizevents.sync.nodo.util.Constants;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EcommerceHelpdeskReaderService {

  private final EcommerceHelpdeskClient ecommerceHelpdeskClient;

  @Autowired
  public EcommerceHelpdeskReaderService(EcommerceHelpdeskClient ecommerceHelpdeskClient) {

    this.ecommerceHelpdeskClient = ecommerceHelpdeskClient;
  }

  public TransactionDetails getTransactionDetails(String paymentToken) {

    TransactionDetails transactionDetails = null;

    SearchTransactionRequest request =
        SearchTransactionRequest.builder()
            .type(Constants.ECOMMERCE_HELPDESK_TRANSACTION_SEARCH_ID_TYPE)
            .paymentToken(paymentToken)
            .build();

    try {
      SearchTransactionResponse response =
          this.ecommerceHelpdeskClient.searchTransactionByPaymentToken(0, 10, request);

      if (response.getTransactions() == null || response.getTransactions().isEmpty()) {
        log.error(
            "[BIZ-EVENTS-SYNC-NODO] No valid response received from eCommerce Helpdesk using"
                + " request [{}]",
            request);
      } else {
        List<TransactionResponse> transactions = response.getTransactions();
        if (transactions.size() > 1) {
          log.info(
              "Multiple transaction found in response received from eCommerce Helpdesk using"
                  + " request [{}]",
              request);
        }
        transactionDetails = BizEventMapper.fromTransactionResponse(transactions.get(0));
      }

    } catch (FeignServerException e) {
      log.error(
          "[BIZ-EVENTS-SYNC-NODO] An error occurred while communicating with eCommerce Helpdesk"
              + " using request [{}]",
          request,
          e);
    } catch (FeignException e) {
      log.debug(
          "An error occurred while communicating with eCommerce Helpdesk using request [{}]",
          request,
          e);
    }

    return transactionDetails;
  }
}
