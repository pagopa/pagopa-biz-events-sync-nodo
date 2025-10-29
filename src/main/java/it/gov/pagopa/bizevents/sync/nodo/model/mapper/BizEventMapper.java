package it.gov.pagopa.bizevents.sync.nodo.model.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonSyntaxException;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.DebtorPosition;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.MapEntry;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.PaymentInfo;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.subject.Debtor;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.subject.Payer;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.transfer.Transfer;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.subject.Creditor;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.subject.Psp;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.InfoTransaction;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.Transaction;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.TransactionDetails;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.subject.TransactionPsp;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.subject.User;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.*;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.Rpt;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.RptSoggetti;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.RptVersamenti;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.Rt;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.rt.CtDatiSingoloPagamentoRT;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.rt.CtRicevutaTelematica;
import it.gov.pagopa.bizevents.sync.nodo.exception.BizEventSyncException;
import it.gov.pagopa.bizevents.sync.nodo.model.client.apiconfig.ConfigDataV1;
import it.gov.pagopa.bizevents.sync.nodo.model.client.apiconfig.CreditorInstitution;
import it.gov.pagopa.bizevents.sync.nodo.model.client.apiconfig.PaymentServiceProvider;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response.PspInfo;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response.TransactionInfo;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response.TransactionResponse;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response.UserInfo;
import it.gov.pagopa.bizevents.sync.nodo.util.CommonUtility;
import it.gov.pagopa.bizevents.sync.nodo.util.Constants;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;
import javax.xml.transform.stream.StreamSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BizEventMapper {

  private BizEventMapper() {}

  public static BizEvent fromNewModel(
      PositionPayment pp,
      List<PositionTransfer> transfers,
      Long totalNotices,
      ConfigDataV1 configData) {

    PositionPaymentPlan ppp = pp.getPaymentPlan();
    PositionSubject payer = pp.getPayer();
    if (ppp == null) {
      String msg =
          String.format(
              "No valid record found in POSITION_PAYMENT_PLAN table for related to PAYMENT_POSITION"
                  + " with ID=[%s]",
              pp.getId());
      throw new BizEventSyncException(msg);
    }

    PositionService ps = ppp.getPositionService();
    if (ps == null) {
      String msg =
          String.format(
              "No valid record found in POSITION_service table for related to PAYMENT_POSITION with"
                  + " ID=[%s]",
              pp.getId());
      throw new BizEventSyncException(msg);
    }

    PositionSubject debtor = ps.getDebtor();
    String paFiscalCode = ps.getPaFiscalCode();
    String pspId = pp.getPspId();

    BizEvent bizEvent;
    try {
      bizEvent =
          BizEvent.builder()
              .id(generateUniqueID())
              .version("2")
              .idPaymentManager(pp.getTransactionId())
              .receiptId(pp.getPaymentToken())
              .debtorPosition(
                  DebtorPosition.builder()
                      .modelType("2")
                      .noticeNumber(ps.getNoticeId())
                      .iuv(pp.getCreditorReferenceId())
                      .iur(pp.getPaymentToken())
                      .build())
              .creditor(
                  Creditor.builder()
                      .idPA(paFiscalCode)
                      .idBrokerPA(pp.getBrokerPaId())
                      .idStation(pp.getStationId())
                      .companyName(
                          findCI(configData, paFiscalCode)
                              .map(CreditorInstitution::getBusinessName)
                              .orElse(null))
                      .officeName(ps.getOfficeName())
                      .build())
              .psp(
                  Psp.builder()
                      .idPsp(pspId)
                      .idBrokerPsp(pp.getBrokerPspId())
                      .idChannel(pp.getChannelId())
                      .psp(
                          findPSP(configData, pspId)
                              .map(PaymentServiceProvider::getBusinessName)
                              .orElse(null))
                      .pspPartitaIVA(
                          findPSP(configData, pspId)
                              .map(PaymentServiceProvider::getVatNumber)
                              .orElse(null))
                      .pspFiscalCode(
                          findPSP(configData, pspId)
                              .map(PaymentServiceProvider::getTaxCode)
                              .orElse(null))
                      .channelDescription(pp.getPaymentChannel())
                      .build())
              .paymentInfo(
                  PaymentInfo.builder()
                      .paymentDateTime(
                          CommonUtility.formatDate(
                              pp.getInsertedTimestamp(),
                              Constants.BIZ_EVENT_EXTENDED_DATE_FORMATTER))
                      .applicationDate(
                          CommonUtility.formatDate(
                              pp.getApplicationDate(), Constants.BIZ_EVENT_REDUCED_DATE_FORMATTER))
                      .transferDate(
                          CommonUtility.formatDate(
                              pp.getTransferDate(), Constants.BIZ_EVENT_REDUCED_DATE_FORMATTER))
                      .dueDate(
                          CommonUtility.formatDate(
                              ppp.getDueDate(), Constants.BIZ_EVENT_REDUCED_DATE_FORMATTER))
                      .paymentToken(pp.getPaymentToken())
                      .amount(CommonUtility.toPlainString(pp.getAmount()))
                      .fee(CommonUtility.toPlainString(pp.getFee()))
                      .primaryCiIncurredFee(CommonUtility.toPlainString(pp.getFeePa()))
                      .idBundle(pp.getBundleId())
                      .idCiBundle(pp.getBundlePaId())
                      .totalNotice(totalNotices.toString())
                      .paymentMethod(pp.getPaymentMethod())
                      .touchpoint(pp.getPaymentChannel())
                      .remittanceInformation(ps.getDescription())
                      .iur(pp.getPaymentToken())
                      .metadata(extractMetadata(ppp.getMetadata()))
                      .build())
              .transferList(new LinkedList<>())
              .build();

      if (debtor != null) {
        bizEvent.setDebtor(
            Debtor.builder()
                .fullName(debtor.getFullName())
                .entityUniqueIdentifierType(debtor.getEntityUniqueIdentifierType())
                .entityUniqueIdentifierValue(debtor.getEntityUniqueIdentifierValue())
                .streetName(debtor.getStreetName())
                .civicNumber(debtor.getCivicNumber())
                .postalCode(debtor.getPostalCode())
                .city(debtor.getCity())
                .stateProvinceRegion(debtor.getStateProvinceRegion())
                .country(debtor.getCountry())
                .eMail(debtor.getEmail())
                .build());
      }

      if (payer != null) {
        bizEvent.setPayer(
            Payer.builder()
                .fullName(payer.getFullName())
                .entityUniqueIdentifierType(payer.getEntityUniqueIdentifierType())
                .entityUniqueIdentifierValue(payer.getEntityUniqueIdentifierValue())
                .streetName(payer.getStreetName())
                .civicNumber(payer.getCivicNumber())
                .postalCode(payer.getPostalCode())
                .city(payer.getCity())
                .stateProvinceRegion(payer.getStateProvinceRegion())
                .country(payer.getCountry())
                .eMail(payer.getEmail())
                .build());
      }

      List<PositionTransfer> pts =
          transfers.stream()
              .sorted(Comparator.comparing(PositionTransfer::getTransferIdentifier))
              .toList();
      for (PositionTransfer pt : pts) {
        bizEvent
            .getTransferList()
            .add(
                Transfer.builder()
                    .idTransfer(pt.getTransferIdentifier())
                    .fiscalCodePA(pt.getPaFiscalCodeSecondary())
                    .companyName(
                        findCI(configData, pt.getPaFiscalCodeSecondary())
                            .map(CreditorInstitution::getBusinessName)
                            .orElse(null))
                    .amount(CommonUtility.toPlainString(pt.getAmount()))
                    .transferCategory(pt.getTransferCategory())
                    .remittanceInformation(pt.getRemittanceInformation())
                    .iban(pt.getIban())
                    .mbdAttachment(
                        pt.getPositionTransferMBD() != null
                            ? CommonUtility.convertBlob(pt.getPositionTransferMBD().getXmlContent())
                            : null)
                    .metadata(extractMetadata(pt.getMetadata()))
                    .build());
      }

      generateTransactionDetailFromPMInfo(pp, bizEvent);

    } catch (Exception e) {
      String msg =
          String.format(
              "An error occurred during mapping of new model payment with ID=[%s] to Biz Event",
              pp.getId());
      throw new BizEventSyncException(msg, e);
    }

    return bizEvent;
  }

  public static BizEvent fromOldModel(
      Rpt rpt,
      Rt rt,
      List<RptSoggetti> rptSubjects,
      List<RptVersamenti> rptTransfers,
      ConfigDataV1 configData) {

    if (rptSubjects.isEmpty()) {
      String msg =
          String.format("No valid record found in RPT_SOGGETTI table for RPT_ID=[%s]", rpt.getId());
      throw new BizEventSyncException(msg);
    }

    if (rptTransfers.isEmpty()) {
      String msg =
          String.format(
              "No valid record found in RPT_VERSAMENTI table for FK_RPT=[%s]", rpt.getId());
      throw new BizEventSyncException(msg);
    }

    CtRicevutaTelematica decodedRT = extractRT(rt.getRtXml().getXmlContent());
    List<CtDatiSingoloPagamentoRT> datiSingoloPagamento = new ArrayList<>();
    if (decodedRT != null) {
      datiSingoloPagamento = decodedRT.getDatiPagamento().getDatiSingoloPagamento();
    }

    if (datiSingoloPagamento.isEmpty()) {
      String msg =
          String.format(
              "No valid CtDatiSingoloPagamentoRT extracted from RT_XML table for ID=[%s]",
              rpt.getId());
      throw new BizEventSyncException(msg);
    }

    BizEvent bizEvent;
    try {
      RptSoggetti pagatore =
          rptSubjects.stream()
              .filter(e -> "P".equalsIgnoreCase(e.getId().getTipoSoggetto()))
              .findFirst()
              .orElse(null);

      RptSoggetti versante =
          rptSubjects.stream()
              .filter(e -> "V".equalsIgnoreCase(e.getId().getTipoSoggetto()))
              .findFirst()
              .orElse(null);

      RptSoggetti beneficiario =
          rptSubjects.stream()
              .filter(e -> "B".equalsIgnoreCase(e.getId().getTipoSoggetto()))
              .findFirst()
              .orElse(null);

      String domainId = rpt.getIdentDominio();
      String pspId = rpt.getPsp();

      bizEvent =
          BizEvent.builder()
              .id(generateUniqueID())
              .version("2")
              .idPaymentManager("Y".equalsIgnoreCase(rpt.getWisp2()) ? rpt.getIdSessione() : null)
              .debtorPosition(
                  DebtorPosition.builder()
                      .modelType("1")
                      .iuv(rpt.getIuv())
                      .iur(datiSingoloPagamento.get(0).getIdentificativoUnivocoRiscossione())
                      .build())
              .creditor(
                  Creditor.builder()
                      .idPA(domainId)
                      .idBrokerPA(rpt.getIntermediariopa())
                      .idStation(rpt.getStazIntermediariopa())
                      .companyName(beneficiario != null ? beneficiario.getAnagrafica() : null)
                      .build())
              .psp(
                  Psp.builder()
                      .idPsp(pspId)
                      .idBrokerPsp(rpt.getIntermediarioPsp())
                      .idChannel(rpt.getCanale())
                      .psp(
                          findPSP(configData, pspId)
                              .map(PaymentServiceProvider::getBusinessName)
                              .orElse(null))
                      .pspPartitaIVA(
                          findPSP(configData, pspId)
                              .map(PaymentServiceProvider::getVatNumber)
                              .orElse(null))
                      .pspFiscalCode(
                          findPSP(configData, pspId)
                              .map(PaymentServiceProvider::getTaxCode)
                              .orElse(null))
                      .build())
              .paymentInfo(
                  PaymentInfo.builder()
                      .paymentDateTime(
                          CommonUtility.formatDate(
                              rt.getDataRicevuta(), Constants.BIZ_EVENT_EXTENDED_DATE_FORMATTER))
                      .paymentToken(rpt.getCcp())
                      .amount(CommonUtility.toPlainString(rpt.getSommaVersamenti()))
                      .fee(
                          CommonUtility.toPlainString(
                              datiSingoloPagamento.stream()
                                  .mapToDouble(
                                      paymentData ->
                                          Optional.ofNullable(
                                                  paymentData.getCommissioniApplicatePSP())
                                              .orElse(BigDecimal.ZERO)
                                              .doubleValue())
                                  .sum()))
                      .paymentMethod(rpt.getTipoVersamento())
                      .iur(datiSingoloPagamento.get(0).getIdentificativoUnivocoRiscossione())
                      .build())
              .transferList(new LinkedList<>())
              .build();

      if (pagatore != null) {
        bizEvent.setDebtor(
            Debtor.builder()
                .fullName(pagatore.getAnagrafica())
                .entityUniqueIdentifierType(pagatore.getTipoIdentificativoUnivoco())
                .entityUniqueIdentifierValue(pagatore.getCodiceIdentificativoUnivoco())
                .streetName(pagatore.getIndirizzo())
                .civicNumber(pagatore.getCivico())
                .postalCode(pagatore.getCap())
                .city(pagatore.getLocalita())
                .stateProvinceRegion(pagatore.getProvincia())
                .country(pagatore.getNazione())
                .eMail(pagatore.getEmail())
                .build());
      }

      if (versante != null) {
        bizEvent.setPayer(
            Payer.builder()
                .fullName(versante.getAnagrafica())
                .entityUniqueIdentifierType(versante.getTipoIdentificativoUnivoco())
                .entityUniqueIdentifierValue(versante.getCodiceIdentificativoUnivoco())
                .streetName(versante.getIndirizzo())
                .civicNumber(versante.getCivico())
                .postalCode(versante.getCap())
                .city(versante.getLocalita())
                .stateProvinceRegion(versante.getProvincia())
                .country(versante.getNazione())
                .eMail(versante.getEmail())
                .build());
      }

      List<RptVersamenti> rptTransfersSorted =
          rptTransfers.stream()
              .sorted(Comparator.comparing(RptVersamenti::getProgressivo))
              .toList();
      for (RptVersamenti rptvers : rptTransfersSorted) {
        CtDatiSingoloPagamentoRT ctDatiSingoloPagamento =
            datiSingoloPagamento.get((int) (rptvers.getProgressivo() - 1));
        bizEvent
            .getTransferList()
            .add(
                Transfer.builder()
                    .fiscalCodePA(rpt.getIdentDominio())
                    .companyName(beneficiario != null ? beneficiario.getAnagrafica() : null)
                    .amount(CommonUtility.toPlainString(rptvers.getImporto()))
                    .transferCategory(rptvers.getDatiSpecificiRiscossione())
                    .iur(ctDatiSingoloPagamento.getIdentificativoUnivocoRiscossione())
                    .remittanceInformation(rptvers.getCausaleVersamento())
                    .iban(rptvers.getIban())
                    .build());
      }
    } catch (Exception e) {
      String msg =
          String.format(
              "An error occurred during mapping of old model payment with ID=[%s] to Biz Event",
              rpt.getId());
      throw new BizEventSyncException(msg, e);
    }

    return bizEvent;
  }

  public static TransactionDetails fromTransactionResponse(TransactionResponse transaction) {

    TransactionInfo transactionInfo = transaction.getTransactionInfo();
    it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response.PaymentInfo paymentInfo =
        transaction.getPaymentInfo();
    PspInfo pspInfo = transaction.getPspInfo();
    UserInfo userInfo = transaction.getUserInfo();

    return TransactionDetails.builder()
        .transaction(
            Transaction.builder()
                .grandTotal(transactionInfo.getGrandTotal())
                .amount(transactionInfo.getAmount())
                .fee(transactionInfo.getFee())
                .creationDate(
                    CommonUtility.formatDate(
                        transactionInfo.getCreationDate(),
                        Constants.BIZ_EVENT_EXTENDED_DATE_FORMATTER))
                .numAut(transactionInfo.getAuthorizationOperationId())
                .transactionStatus(transactionInfo.getStatus())
                .authorizationCode(transactionInfo.getAuthorizationCode())
                // .paymentGateway(transactionInfo.getPaymentGateway())
                .rrn(transactionInfo.getRrn())
                // .timestampOperation(transactionInfo.getCreationDate())
                .transactionId(paymentInfo.getIdTransaction())
                .psp(
                    TransactionPsp.builder()
                        .idChannel(pspInfo.getIdChannel())
                        .businessName(pspInfo.getBusinessName())
                        // .idPsp(pspInfo.getPspId())
                        .build())
                .build())
        .user(
            User.builder()
                .type(userInfo.getAuthenticationType())
                .fiscalCode(userInfo.getUserFiscalCode())
                .fullName(
                    userInfo.getSurname() != null
                        ? userInfo.getName() + " " + userInfo.getSurname()
                        : null)
                .notificationEmail(userInfo.getNotificationEmail())
                .build())
        .info(
            InfoTransaction.builder()
                .brand(transactionInfo.getBrand())
                .clientId(transaction.getPaymentInfo().getOrigin())
                .paymentMethodName(transactionInfo.getPaymentMethodName())
                .build())
        .build();
  }

  public static BizEvent finalize(BizEvent bizEvent, TransactionDetails transactionDetails) {

    List<String> missingInfo = new LinkedList<>();

    //
    if (bizEvent.getTransactionDetails() == null) {
      bizEvent.setTransactionDetails(transactionDetails);
    }

    addOnMissingInfoIfTrue(
        missingInfo, "idPaymentManager", () -> bizEvent.getIdPaymentManager() == null);
    addOnMissingInfoIfTrue(missingInfo, "receiptId", () -> bizEvent.getReceiptId() == null);
    addOnMissingInfoIfTrue(
        missingInfo, "creditor.companyName", () -> bizEvent.getCreditor().getCompanyName() == null);
    addOnMissingInfoIfTrue(
        missingInfo, "creditor.officeName", () -> bizEvent.getCreditor().getOfficeName() == null);

    //
    Psp psp = bizEvent.getPsp();
    addOnMissingInfoIfTrue(missingInfo, "psp.psp", () -> psp.getPsp() == null);
    addOnMissingInfoIfTrue(missingInfo, "psp.pspPartitaIVA", () -> psp.getPspPartitaIVA() == null);
    addOnMissingInfoIfTrue(missingInfo, "psp.pspFiscalCode", () -> psp.getPspFiscalCode() == null);
    addOnMissingInfoIfTrue(
        missingInfo, "psp.channelDescription", () -> psp.getChannelDescription() == null);

    //
    Debtor debtor = bizEvent.getDebtor();
    addOnMissingInfoIfTrue(missingInfo, "debtor.streetName", () -> debtor.getStreetName() == null);
    addOnMissingInfoIfTrue(
        missingInfo, "debtor.civicNumber", () -> debtor.getCivicNumber() == null);
    addOnMissingInfoIfTrue(missingInfo, "debtor.postalCode", () -> debtor.getPostalCode() == null);
    addOnMissingInfoIfTrue(missingInfo, "debtor.city", () -> debtor.getCity() == null);
    addOnMissingInfoIfTrue(
        missingInfo, "debtor.stateProvinceRegion", () -> debtor.getStateProvinceRegion() == null);
    addOnMissingInfoIfTrue(missingInfo, "debtor.country", () -> debtor.getCountry() == null);
    addOnMissingInfoIfTrue(missingInfo, "debtor.eMail", () -> debtor.getEMail() == null);

    //
    Payer payer = bizEvent.getPayer();
    addOnMissingInfoIfTrue(missingInfo, "payer", () -> payer == null);

    if (payer != null) {
      addOnMissingInfoIfTrue(missingInfo, "payer.streetName", () -> payer.getStreetName() == null);
      addOnMissingInfoIfTrue(
          missingInfo, "payer.civicNumber", () -> payer.getCivicNumber() == null);
      addOnMissingInfoIfTrue(missingInfo, "payer.postalCode", () -> payer.getPostalCode() == null);
      addOnMissingInfoIfTrue(missingInfo, "payer.city", () -> payer.getCity() == null);
      addOnMissingInfoIfTrue(
          missingInfo, "payer.stateProvinceRegion", () -> payer.getStateProvinceRegion() == null);
      addOnMissingInfoIfTrue(missingInfo, "payer.country", () -> payer.getCountry() == null);
      addOnMissingInfoIfTrue(missingInfo, "payer.eMail", () -> payer.getEMail() == null);
    }

    //
    PaymentInfo paymentInfo = bizEvent.getPaymentInfo();
    addOnMissingInfoIfTrue(
        missingInfo, "paymentInfo.description", () -> paymentInfo.getDescription() == null);
    addOnMissingInfoIfTrue(
        missingInfo, "paymentInfo.applicationDate", () -> paymentInfo.getApplicationDate() == null);
    addOnMissingInfoIfTrue(
        missingInfo, "paymentInfo.transferDate", () -> paymentInfo.getTransferDate() == null);
    addOnMissingInfoIfTrue(
        missingInfo, "paymentInfo.dueDate", () -> paymentInfo.getDueDate() == null);
    addOnMissingInfoIfTrue(
        missingInfo,
        "paymentInfo.primaryCiIncurredFee",
        () -> paymentInfo.getPrimaryCiIncurredFee() == null);
    addOnMissingInfoIfTrue(
        missingInfo, "paymentInfo.idBundle", () -> paymentInfo.getIdBundle() == null);
    addOnMissingInfoIfTrue(
        missingInfo, "paymentInfo.idCiBundle", () -> paymentInfo.getIdCiBundle() == null);
    addOnMissingInfoIfTrue(
        missingInfo, "paymentInfo.totalNotice", () -> paymentInfo.getTotalNotice() == null);
    addOnMissingInfoIfTrue(
        missingInfo, "paymentInfo.paymentMethod", () -> paymentInfo.getPaymentMethod() == null);
    addOnMissingInfoIfTrue(
        missingInfo, "paymentInfo.touchpoint", () -> paymentInfo.getTouchpoint() == null);
    addOnMissingInfoIfTrue(
        missingInfo,
        "paymentInfo.remittanceInformation",
        () -> paymentInfo.getRemittanceInformation() == null);
    addOnMissingInfoIfTrue(
        missingInfo, "paymentInfo.metadata", () -> paymentInfo.getMetadata() == null);

    //
    addOnMissingInfoIfTrue(
        missingInfo, "transactionDetails", () -> bizEvent.getTransactionDetails() == null);

    //
    bizEvent.setMissingInfo(missingInfo);
    bizEvent.setComplete(String.valueOf(missingInfo.isEmpty()));

    return bizEvent;
  }

  private static Optional<CreditorInstitution> findCI(
      ConfigDataV1 configData, String paFiscalCode) {

    Optional<CreditorInstitution> ciOpt = Optional.empty();
    if (paFiscalCode != null && configData.getCreditorInstitutions() != null) {
      ciOpt = Optional.ofNullable(configData.getCreditorInstitutions().get(paFiscalCode));
    }
    return ciOpt;
  }

  private static Optional<PaymentServiceProvider> findPSP(ConfigDataV1 configData, String pspId) {

    Optional<PaymentServiceProvider> pspOpt = Optional.empty();
    if (pspId != null && configData.getPsps() != null) {
      pspOpt = Optional.ofNullable(configData.getPsps().get(pspId));
    }
    return pspOpt;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static List<MapEntry> extractMetadata(String rawString) {

    List<MapEntry> metadata = null;
    try {
      List rawEntryMap = Constants.GSON_PARSER.fromJson(rawString, List.class);
      if (rawEntryMap != null) {
        metadata = new LinkedList<>();
        for (Object rawEntry : rawEntryMap) {
          Map<String, String> entry = (Map<String, String>) rawEntry;
          metadata.add(MapEntry.builder().key(entry.get("key")).value(entry.get("value")).build());
        }
      }
    } catch (ClassCastException | JsonSyntaxException e) {
      log.error(
          "Impossible to generate metadata from string [{}]. Skipping metadata generation.",
          rawString,
          e);
    }

    return metadata;
  }

  private static void addOnMissingInfoIfTrue(
      List<String> missingInfo, String value, Supplier<Boolean> function) {

    if (Boolean.TRUE.equals(function.get())) {
      missingInfo.add(value);
    }
  }

  private static CtRicevutaTelematica extractRT(String blob) {
    CtRicevutaTelematica rt;
    try {
      String formattedBlob = blob.replaceAll("xmlns=[\"'][^\"']+[\"']", "");
      Unmarshaller unmarshaller = Constants.RT_JAXB_CONTEXT.createUnmarshaller();
      StringReader reader = new StringReader(formattedBlob);
      JAXBElement<CtRicevutaTelematica> root =
          unmarshaller.unmarshal(new StreamSource(reader), CtRicevutaTelematica.class);
      rt = root.getValue();
    } catch (JAXBException e) {
      throw new BizEventSyncException(
          "Error while extracting CtRicevutaTelematica from blob string", e);
    }
    return rt;
  }

  private static void generateTransactionDetailFromPMInfo(PositionPayment pp, BizEvent bizEvent) {
    try {
      Blob rawBlob = pp.getPmInfo();
      if (rawBlob != null) {
          String pmInfo = CommonUtility.convertBlob(rawBlob.getBytes(1, (int) rawBlob.length()));
          if (pmInfo != null) {
              bizEvent.setTransactionDetails(
                      new ObjectMapper().readValue(pmInfo, TransactionDetails.class));
          }
      }
    } catch (JsonProcessingException | SQLException e) {
      log.warn("Failed to generate transaction details from PM_INFO. Skipping it.", e);
    }
  }

  private static String generateUniqueID() {
    return "resync-" + UUID.randomUUID();
  }
}
