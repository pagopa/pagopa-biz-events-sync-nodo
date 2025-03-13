package it.gov.pagopa.bizevents.sync.nodo.service.impl;

import it.gov.pagopa.bizevents.sync.nodo.client.EcommerceHelpdeskClient;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.TransactionDetails;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.request.SearchTransactionRequest;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response.SearchTransactionResponse;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response.TransactionResponse;
import it.gov.pagopa.bizevents.sync.nodo.model.mapper.BizEventMapper;
import it.gov.pagopa.bizevents.sync.nodo.service.EcommerceHelpdeskReaderService;
import it.gov.pagopa.bizevents.sync.nodo.util.Constants;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EcommerceHelpdeskReaderServiceImpl implements EcommerceHelpdeskReaderService {

  private final EcommerceHelpdeskClient ecommerceHelpdeskClient;

  @Autowired
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
        this.ecommerceHelpdeskClient.searchTransactionByPaymentToken(0, 10, request);

    if (response == null) {
      // TODO throw custom exception
    }

    if (response.getTransactions() == null || response.getTransactions().isEmpty()) {
      // TODO throw custom exception
    }

    List<TransactionResponse> transactions = response.getTransactions();
    if (transactions.size() > 1) {
      // TODO log info
    }

    return BizEventMapper.fromTransactionResponse(transactions.get(0));
  }
}
