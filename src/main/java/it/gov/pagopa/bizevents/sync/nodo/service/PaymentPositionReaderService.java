package it.gov.pagopa.bizevents.sync.nodo.service;

import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionPayment;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionTransfer;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.Rpt;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.RptSoggetti;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.RptVersamenti;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.Rt;
import it.gov.pagopa.bizevents.sync.nodo.exception.BizEventSyncException;
import it.gov.pagopa.bizevents.sync.nodo.model.bizevent.ReceiptEventInfo;
import it.gov.pagopa.bizevents.sync.nodo.model.mapper.BizEventMapper;
import it.gov.pagopa.bizevents.sync.nodo.repository.payment.PaymentPositionRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.payment.PositionTransferRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.payment.RptRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.payment.RptSoggettiRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.payment.RptVersamentiRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.receipt.RtRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentPositionReaderService {

  private final PaymentPositionRepository paymentPositionRepository;

  private final PositionTransferRepository positionTransferRepository;

  private final RtRepository rtRepository;

  private final RptRepository rptRepository;

  private final RptSoggettiRepository rptSoggettiRepository;

  private final RptVersamentiRepository rptVersamentiRepository;

  private final ConfigCacheService configCacheService;

  public PaymentPositionReaderService(
      PaymentPositionRepository paymentPositionRepository,
      PositionTransferRepository positionTransferRepository,
      RtRepository rtRepository,
      RptRepository rptRepository,
      RptSoggettiRepository rptSoggettiRepository,
      RptVersamentiRepository rptVersamentiRepository,
      ConfigCacheService configCacheService) {

    this.paymentPositionRepository = paymentPositionRepository;
    this.positionTransferRepository = positionTransferRepository;
    this.rtRepository = rtRepository;
    this.rptRepository = rptRepository;
    this.rptSoggettiRepository = rptSoggettiRepository;
    this.rptVersamentiRepository = rptVersamentiRepository;
    this.configCacheService = configCacheService;
  }

  public BizEvent readNewModelPaymentPosition(ReceiptEventInfo receiptEvent) {

    LocalDateTime insertedTimestamp =
        receiptEvent.getInsertedTimestamp().truncatedTo(ChronoUnit.DAYS);
    LocalDateTime minDate = LocalDateTime.from(insertedTimestamp);
    LocalDateTime maxDate = LocalDateTime.from(insertedTimestamp.plusDays(1));
    String paymentToken = receiptEvent.getPaymentToken();

    Optional<PositionPayment> positionPaymentOpt =
        this.paymentPositionRepository.readByPaymentTokenInTimeSlot(minDate, maxDate, paymentToken);
    if (positionPaymentOpt.isEmpty()) {
      String msg =
          String.format(
              "No valid record found in POSITION_PAYMENT table for paymentToken=[%s] in"
                  + " range=[%s-%s]",
              paymentToken, minDate, maxDate);
      throw new BizEventSyncException(msg);
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
      String msg =
          String.format(
              "No valid record found in POSITION_TRANSFER table for FK_POSITION_PAYMENT=[%s] with"
                  + " inserted timestamp > [%s]",
              positionPayment.getId(), minDate);
      throw new BizEventSyncException(msg);
    }

    return BizEventMapper.fromNewModel(
        positionPayment, positionTransfers, totalNotices, configCacheService.getConfigData());
  }

  public BizEvent readOldModelPaymentPosition(ReceiptEventInfo receiptEvent) {

    BizEvent bizEvent;
    String domainId = receiptEvent.getDomainId();
    String iuv = receiptEvent.getIuv();
    String ccp = receiptEvent.getPaymentToken();

    try {
      Optional<Rpt> rptOpt = this.rptRepository.readByUniqueIdentifier(domainId, iuv, ccp);
      if (rptOpt.isEmpty()) {
        String msg =
            String.format(
                "No valid record found in RPT table for domainId=[%s] iuv=[%s] ccp=[%s]",
                domainId, iuv, ccp);
        throw new BizEventSyncException(msg);
      }
      Rpt rpt = rptOpt.get();

      Optional<Rt> rtOpt = this.rtRepository.readByUniqueIdentifier(domainId, iuv, ccp);
      if (rtOpt.isEmpty()) {
        String msg =
            String.format(
                "No valid record found in RT table for domainId=[%s] iuv=[%s] ccp=[%s]",
                domainId, iuv, ccp);
        throw new BizEventSyncException(msg);
      }
      Rt rt = rtOpt.get();

      Long rptId = rpt.getId();
      List<RptSoggetti> rptSubjects = this.rptSoggettiRepository.readByRptId(rptId);
      List<RptVersamenti> rptTransfers = this.rptVersamentiRepository.readByRptId(rptId);

      bizEvent =
          BizEventMapper.fromOldModel(
              rpt, rt, rptSubjects, rptTransfers, configCacheService.getConfigData());

    } catch (DataAccessException e) {
      String msg =
          String.format(
              "An error occurred during read operation on tables for domainId=[%s] iuv=[%s]"
                  + " ccp=[%s]",
              domainId, iuv, ccp);
      throw new BizEventSyncException(msg, e);
    }
    return bizEvent;
  }
}
