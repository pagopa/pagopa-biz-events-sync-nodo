package it.gov.pagopa.bizevents.sync.nodo.service;

import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import it.gov.pagopa.bizevents.sync.nodo.model.bizevent.ReceiptEventInfo;

public interface PaymentPositionReaderService {

  BizEvent readNewModelPaymentPosition(ReceiptEventInfo receiptEvent);

  BizEvent readOldModelPaymentPosition(ReceiptEventInfo receiptEvent);
}
