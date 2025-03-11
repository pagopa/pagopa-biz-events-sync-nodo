package it.gov.pagopa.bizevents.sync.nodo.service.impl;

import it.gov.pagopa.bizevents.sync.nodo.model.bizevent.PaymentPositionEvent;
import it.gov.pagopa.bizevents.sync.nodo.model.bizevent.ReceiptEventInfo;
import it.gov.pagopa.bizevents.sync.nodo.repository.PaymentPositionRepository;
import it.gov.pagopa.bizevents.sync.nodo.service.PaymentPositionReaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentPositionReaderServiceImpl implements PaymentPositionReaderService {

  private final PaymentPositionRepository paymentPositionRepository;

  public PaymentPositionReaderServiceImpl(PaymentPositionRepository paymentPositionRepository) {
    this.paymentPositionRepository = paymentPositionRepository;
  }

  @Override
  public PaymentPositionEvent readNewModelPaymentPosition(ReceiptEventInfo receiptEvent) {
    return null;
  }

  @Override
  public PaymentPositionEvent readOldModelPaymentPosition(ReceiptEventInfo receiptEvent) {
    return null;
  }
}
