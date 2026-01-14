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
import it.gov.pagopa.bizevents.sync.nodo.repository.historic.payment.*;
import it.gov.pagopa.bizevents.sync.nodo.repository.historic.receipt.HistoricRtRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.primary.payment.*;
import it.gov.pagopa.bizevents.sync.nodo.repository.primary.receipt.RtRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

  private final HistoricPaymentPositionRepository historicPaymentPositionRepository;

  private final HistoricPositionTransferRepository historicPositionTransferRepository;

  private final HistoricRtRepository historicRtRepository;

  private final HistoricRptRepository historicRptRepository;

  private final HistoricRptSoggettiRepository historicRptSoggettiRepository;

  private final HistoricRptVersamentiRepository historicRptVersamentiRepository;

  private final ConfigCacheService configCacheService;

  public PaymentPositionReaderService(
      PaymentPositionRepository paymentPositionRepository,
      PositionTransferRepository positionTransferRepository,
      RtRepository rtRepository,
      RptRepository rptRepository,
      RptSoggettiRepository rptSoggettiRepository,
      RptVersamentiRepository rptVersamentiRepository,
      @Autowired(required = false) HistoricPaymentPositionRepository historicPaymentPositionRepository,
      @Autowired(required = false) HistoricPositionTransferRepository historicPositionTransferRepository,
      @Autowired(required = false) HistoricRtRepository historicRtRepository,
      @Autowired(required = false) HistoricRptRepository historicRptRepository,
      @Autowired(required = false) HistoricRptSoggettiRepository historicRptSoggettiRepository,
      @Autowired(required = false) HistoricRptVersamentiRepository historicRptVersamentiRepository,
      ConfigCacheService configCacheService) {

    this.paymentPositionRepository = paymentPositionRepository;
    this.positionTransferRepository = positionTransferRepository;
    this.rtRepository = rtRepository;
    this.rptRepository = rptRepository;
    this.rptSoggettiRepository = rptSoggettiRepository;
    this.rptVersamentiRepository = rptVersamentiRepository;
    this.historicPaymentPositionRepository = historicPaymentPositionRepository;
    this.historicPositionTransferRepository = historicPositionTransferRepository;
    this.historicRtRepository = historicRtRepository;
    this.historicRptRepository = historicRptRepository;
    this.historicRptSoggettiRepository = historicRptSoggettiRepository;
    this.historicRptVersamentiRepository = historicRptVersamentiRepository;
    this.configCacheService = configCacheService;
  }

  public BizEvent readNewModelPaymentPosition(ReceiptEventInfo receiptEvent) {

    BizEvent bizEvent;
    LocalDateTime insertedTimestamp =
        receiptEvent.getInsertedTimestamp().truncatedTo(ChronoUnit.DAYS);
    LocalDateTime minDate = insertedTimestamp;
    LocalDateTime maxDate = insertedTimestamp.plusDays(1);
    String paymentToken = receiptEvent.getPaymentToken();

    try {
      Optional<PositionPayment> positionPaymentOpt =
          this.paymentPositionRepository.readByPaymentTokenInTimeSlot(
              minDate, maxDate, paymentToken);
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
                minDate, maxDate, positionPayment.getTransactionId());
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

      bizEvent =
          BizEventMapper.fromNewModel(
              positionPayment, positionTransfers, totalNotices, configCacheService.getConfigData());

    } catch (DataAccessException e) {
      String msg =
          String.format(
              "An error occurred during read operation on tables for paymentToken=[%s]",
              paymentToken);
      throw new BizEventSyncException(msg, e);
    }

    return bizEvent;
  }

  public BizEvent readOldModelPaymentPosition(ReceiptEventInfo receiptEvent) {

    BizEvent bizEvent;
    String domainId = receiptEvent.getDomainId();
    String iuv = receiptEvent.getIuv();
    String ccp = receiptEvent.getPaymentToken();
    LocalDate lowerBound = receiptEvent.getInsertedTimestamp().toLocalDate();
    LocalDate upperBound = lowerBound.plusDays(1);

    try {
      Optional<Rpt> rptOpt =
          this.rptRepository.readByUniqueIdentifier(
                  lowerBound.atStartOfDay(),
                  upperBound.atStartOfDay(),
                  domainId,
                  iuv,
                  ccp);
      if (rptOpt.isEmpty()) {
        String msg =
            String.format(
                "No valid record found in RPT table for domainId=[%s] iuv=[%s] ccp=[%s]",
                domainId, iuv, ccp);
        throw new BizEventSyncException(msg);
      }
      Rpt rpt = rptOpt.get();

      Optional<Rt> rtOpt =
          this.rtRepository.readByUniqueIdentifier(
                  lowerBound.atStartOfDay(),
                  upperBound.atStartOfDay(),
                  domainId,
                  iuv,
                  ccp);
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

  public BizEvent readNewModelPaymentPositionFromHistoric(ReceiptEventInfo receiptEvent) {

    BizEvent bizEvent;
    LocalDateTime insertedTimestamp =
        receiptEvent.getInsertedTimestamp().truncatedTo(ChronoUnit.DAYS);
    LocalDateTime minDate = insertedTimestamp;
    LocalDateTime maxDate = insertedTimestamp.plusDays(1);
    String paymentToken = receiptEvent.getPaymentToken();

    try {
      Optional<PositionPayment> positionPaymentOpt =
          this.historicPaymentPositionRepository.readByPaymentTokenInTimeSlot(
              minDate, maxDate, paymentToken);
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
            this.historicPaymentPositionRepository.countPositionPaymentsByTransactionId(
                minDate, maxDate, positionPayment.getTransactionId());
      }

      List<PositionTransfer> positionTransfers =
          this.historicPositionTransferRepository.readByPositionPayment(
              positionPayment.getId(), minDate);
      if (positionTransfers.isEmpty()) {
        String msg =
            String.format(
                "No valid record found in POSITION_TRANSFER table for FK_POSITION_PAYMENT=[%s] with"
                    + " inserted timestamp > [%s]",
                positionPayment.getId(), minDate);
        throw new BizEventSyncException(msg);
      }

      bizEvent =
          BizEventMapper.fromNewModel(
              positionPayment, positionTransfers, totalNotices, configCacheService.getConfigData());

    } catch (DataAccessException e) {
      String msg =
          String.format(
              "An error occurred during read operation on tables for paymentToken=[%s]",
              paymentToken);
      throw new BizEventSyncException(msg, e);
    }

    return bizEvent;
  }

  public BizEvent readOldModelPaymentPositionFromHistoric(ReceiptEventInfo receiptEvent) {

    BizEvent bizEvent;
    String domainId = receiptEvent.getDomainId();
    String iuv = receiptEvent.getIuv();
    String ccp = receiptEvent.getPaymentToken();
    LocalDate lowerBound = receiptEvent.getInsertedTimestamp().toLocalDate();
    LocalDate upperBound = lowerBound.plusDays(1);

    try {
      Optional<Rpt> rptOpt =
          this.historicRptRepository.readByUniqueIdentifier(
              lowerBound, upperBound, domainId, iuv, ccp);
      if (rptOpt.isEmpty()) {
        String msg =
            String.format(
                "No valid record found in RPT table for domainId=[%s] iuv=[%s] ccp=[%s]",
                domainId, iuv, ccp);
        throw new BizEventSyncException(msg);
      }
      Rpt rpt = rptOpt.get();

      Optional<Rt> rtOpt =
          this.historicRtRepository.readByUniqueIdentifier(
              lowerBound, upperBound, domainId, iuv, ccp);
      if (rtOpt.isEmpty()) {
        String msg =
            String.format(
                "No valid record found in RT table for domainId=[%s] iuv=[%s] ccp=[%s]",
                domainId, iuv, ccp);
        throw new BizEventSyncException(msg);
      }
      Rt rt = rtOpt.get();

      Long rptId = rpt.getId();
      List<RptSoggetti> rptSubjects = this.historicRptSoggettiRepository.readByRptId(rptId);
      List<RptVersamenti> rptTransfers = this.historicRptVersamentiRepository.readByRptId(rptId);

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
