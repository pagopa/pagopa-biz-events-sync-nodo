package it.gov.pagopa.bizevents.sync.nodo.service.impl;

import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionPayment;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionTransfer;
import it.gov.pagopa.bizevents.sync.nodo.model.bizevent.ReceiptEventInfo;
import it.gov.pagopa.bizevents.sync.nodo.model.mapper.BizEventMapper;
import it.gov.pagopa.bizevents.sync.nodo.repository.payment.PaymentPositionRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.payment.PositionTransferRepository;
import it.gov.pagopa.bizevents.sync.nodo.service.PaymentPositionReaderService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentPositionReaderServiceImpl implements PaymentPositionReaderService {

  private final PaymentPositionRepository paymentPositionRepository;

  private final PositionTransferRepository positionTransferRepository;

  public PaymentPositionReaderServiceImpl(
      PaymentPositionRepository paymentPositionRepository,
      PositionTransferRepository positionTransferRepository) {
    this.paymentPositionRepository = paymentPositionRepository;
    this.positionTransferRepository = positionTransferRepository;
  }

  @Override
  public BizEvent readNewModelPaymentPosition(ReceiptEventInfo receiptEvent) {

    LocalDateTime insertedTimestamp =
        receiptEvent.getInsertedTimestamp().truncatedTo(ChronoUnit.DAYS);
    LocalDateTime minDate = LocalDateTime.from(insertedTimestamp);
    LocalDateTime maxDate = LocalDateTime.from(insertedTimestamp.plusDays(1));
    String paymentToken = receiptEvent.getPaymentToken();

    Optional<PositionPayment> positionPaymentOpt =
        this.paymentPositionRepository.readByPaymentTokenInTimeSlot(minDate, maxDate, paymentToken);
    if (positionPaymentOpt.isEmpty()) {
      // TODO throw custom exception
    }

    PositionPayment positionPayment = positionPaymentOpt.get();
    Long totalNotices = 1L;
    if ("v2".equalsIgnoreCase(positionPayment.getCloseVersion())) {
      totalNotices =
          this.paymentPositionRepository.countPositionPaymentsByTransactionId(
              positionPayment.getTransactionId());
    }

    List<PositionTransfer> positionTransfers =
        positionTransferRepository.readByPositionPayment(positionPayment.getId(), minDate);
    if (positionTransfers.isEmpty()) {
      // TODO throw custom exception
    }

    return BizEventMapper.fromNewModel(positionPayment, positionTransfers, totalNotices);
  }

  @Override
  public BizEvent readOldModelPaymentPosition(ReceiptEventInfo receiptEvent) {
    return null;
  }
}
