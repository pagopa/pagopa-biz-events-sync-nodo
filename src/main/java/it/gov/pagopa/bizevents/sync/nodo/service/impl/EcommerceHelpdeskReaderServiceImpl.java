package it.gov.pagopa.bizevents.sync.nodo.service.impl;

import it.gov.pagopa.bizevents.sync.nodo.client.EcommerceHelpdeskClient;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.TransactionDetails;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.request.SearchTransactionRequest;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response.SearchTransactionResponse;
import it.gov.pagopa.bizevents.sync.nodo.service.EcommerceHelpdeskReaderService;
import it.gov.pagopa.bizevents.sync.nodo.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EcommerceHelpdeskReaderServiceImpl implements EcommerceHelpdeskReaderService {

  private final EcommerceHelpdeskClient ecommerceHelpdeskClient;

  public EcommerceHelpdeskReaderServiceImpl(EcommerceHelpdeskClient ecommerceHelpdeskClient) {

    this.ecommerceHelpdeskClient = ecommerceHelpdeskClient;
  }

  @Override
  public TransactionDetails getTransactionDetails(String paymentToken) {

    SearchTransactionRequest request =
        SearchTransactionRequest.builder()
            .type(Constants.ECOMMERCE_HELPDESK_TRANSACTION_SEARCH_ID_TYPE)
            .paymentToken(paymentToken)
            .build();
    SearchTransactionResponse response =
        this.ecommerceHelpdeskClient.searchTransactionByPaymentToken(request);

    if (response == null) {
      // TODO throw custom exception
    }

    return TransactionDetails.builder()
        // TODO populate this
        .build();
  }
}
