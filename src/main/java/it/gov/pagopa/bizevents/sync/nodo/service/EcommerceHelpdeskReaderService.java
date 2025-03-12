package it.gov.pagopa.bizevents.sync.nodo.service;

import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.TransactionDetails;

public interface EcommerceHelpdeskReaderService {

  TransactionDetails getTransactionDetails(String paymentToken);
}
