package it.gov.pagopa.bizevents.sync.nodo.service;

import it.gov.pagopa.bizevents.sync.nodo.model.bizevent.PaymentPositionEvent;
import it.gov.pagopa.bizevents.sync.nodo.model.bizevent.ReceiptEventInfo;

public interface PaymentPositionReaderService {

  PaymentPositionEvent readNewModelPaymentPosition(ReceiptEventInfo receiptEvent);

  PaymentPositionEvent readOldModelPaymentPosition(ReceiptEventInfo receiptEvent);
}
