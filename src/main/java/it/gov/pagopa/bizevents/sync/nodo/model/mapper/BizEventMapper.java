package it.gov.pagopa.bizevents.sync.nodo.model.mapper;

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
import it.gov.pagopa.bizevents.sync.nodo.model.client.apiconfig.ConfigDataV1;
import it.gov.pagopa.bizevents.sync.nodo.model.client.apiconfig.CreditorInstitution;
import it.gov.pagopa.bizevents.sync.nodo.model.client.apiconfig.PaymentServiceProvider;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response.PspInfo;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response.TransactionInfo;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response.TransactionResponse;
import it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response.UserInfo;
import it.gov.pagopa.bizevents.sync.nodo.util.CommonUtility;
import it.gov.pagopa.bizevents.sync.nodo.util.Constants;
import java.util.*;
import java.util.function.Supplier;

public class BizEventMapper {

  private BizEventMapper() {}

  public static BizEvent fromNewModel(
      PositionPayment pp,
      List<PositionTransfer> transfers,
      Long totalNotices,
      ConfigDataV1 configData) {

    PositionPaymentPlan ppp = pp.getPaymentPlan();
    PositionService ps = ppp.getPositionService();
    PositionSubject debtor = ps.getDebtor();
    PositionSubject payer = pp.getPayer();

    if (ppp == null) {
      // TODO throw custom exception
    }

    if (ps == null) {
      // TODO throw custom exception
    }

    String paFiscalCode = ps.getPaFiscalCode();
    String pspId = pp.getPspId();

    BizEvent bizEvent =
        BizEvent.builder()
            .id(UUID.randomUUID().toString())
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
                            pp.getInsertedTimestamp(), Constants.BIZ_EVENT_EXTENDED_DATE_FORMATTER))
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

  public static BizEvent fromOldModel() {

    return BizEvent.builder().build();
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

    bizEvent.setTransactionDetails(transactionDetails);
    bizEvent.setMissingInfo(missingInfo);
    bizEvent.setComplete(String.valueOf(missingInfo.isEmpty()));
    return bizEvent;
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
      // TODO log an error but do not throw exception
    }

    return metadata;
  }

  private static void addOnMissingInfoIfTrue(
      List<String> missingInfo, String value, Supplier<Boolean> function) {

    if (Boolean.TRUE.equals(function.get())) {
      missingInfo.add(value);
    }
  }
}
