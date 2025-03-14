package it.gov.pagopa.bizevents.sync.nodo.service;

import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionPayment;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionTransfer;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.Rpt;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.RptSoggetti;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.RptVersamenti;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.Rt;
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

    return BizEventMapper.fromNewModel(
        positionPayment, positionTransfers, totalNotices, configCacheService.getConfigData());
  }

  public BizEvent readOldModelPaymentPosition(ReceiptEventInfo receiptEvent) {

    // leggi rpt via IDENT_DOMINIO, IUV, CCP
    // leggi rpt_soggetti via RPT_ID e TIPO_SOGGETTO
    // leggi rpt_versamenti via FK_RPT (order by progressivo)
    // leggi rt + rt_versamenti + rt_xml via IDENT_DOMINIO, IUV, CCP

    String domainId = receiptEvent.getDomainId();
    String iuv = receiptEvent.getIuv();
    String ccp = receiptEvent.getPaymentToken();

    Optional<Rpt> rptOpt = this.rptRepository.readByUniqueIdentifier(domainId, iuv, ccp);
    if (rptOpt.isEmpty()) {
      // TODO throw custom exception
    }
    Rpt rpt = rptOpt.get();

    Optional<Rt> rtOpt = this.rtRepository.readByUniqueIdentifier(domainId, iuv, ccp);
    if (rtOpt.isEmpty()) {
      // TODO throw custom exception
    }
    Rt rt = rtOpt.get();

    Long rptId = rpt.getId();
    List<RptSoggetti> rptSubjects = this.rptSoggettiRepository.readByRptId(rptId);
    List<RptVersamenti> rptTransfers = this.rptVersamentiRepository.readByRptId(rptId);

    return BizEventMapper.fromOldModel(
        rpt, rt, rptSubjects, rptTransfers, configCacheService.getConfigData());
  }
}
