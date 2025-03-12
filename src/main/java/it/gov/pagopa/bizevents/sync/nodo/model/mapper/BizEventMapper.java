package it.gov.pagopa.bizevents.sync.nodo.model.mapper;

import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.DebtorPosition;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.PaymentInfo;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.subject.Debtor;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.subject.Payer;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.transfer.MBD;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.transfer.Transfer;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.subject.Creditor;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.subject.Psp;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionPayment;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionPaymentPlan;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionService;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionSubject;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionTransfer;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class BizEventMapper {

  private BizEventMapper() {}

  public static BizEvent fromNewModel(
      PositionPayment pp, List<PositionTransfer> transfers, Long totalNotices) {

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

    BizEvent bizEvent =
        BizEvent.builder()
            .version("2")
            .complete(null) // TODO
            .missingInfo(null) // TODO
            .idPaymentManager(pp.getTransactionId())
            .receiptId(pp.getPaymentToken())
            .debtorPosition(
                DebtorPosition.builder()
                    .modelType("2")
                    .noticeNumber(ps.getNoticeId())
                    .iuv(pp.getCreditorReferenceId())
                    .build())
            .creditor(
                Creditor.builder()
                    .idPA(ps.getPaFiscalCode())
                    .idBrokerPA(pp.getBrokerPaId())
                    .idStation(pp.getStationId())
                    .companyName(null) // TODO from Cache
                    .officeName(ps.getOfficeName())
                    .build())
            .psp(
                Psp.builder()
                    .idPsp(pp.getPspId())
                    .idBrokerPsp(pp.getBrokerPspId())
                    .idChannel(pp.getChannelId())
                    .psp(null) // TODO from Cache
                    .pspPartitaIVA(null) // TODO from Cache
                    .pspFiscalCode(null) // TODO from Cache
                    .channelDescription(pp.getPaymentChannel())
                    .build())
            .paymentInfo(
                PaymentInfo.builder()
                    .paymentDateTime(null) // TODO format -> (pp.getInsertedTimestamp())
                    .applicationDate(null) // TODO format -> (pp.getApplicationDate())
                    .transferDate(null) // TODO format -> (pp.getTransferDate())
                    .dueDate(null) // TODO format -> (ppp.getDueDate())
                    .paymentToken(pp.getPaymentToken())
                    .amount(null) // TODO format -> (pp.getAmount())
                    .fee(null) // TODO format -> (pp.getFee())
                    .primaryCiIncurredFee(null) // TODO format -> (pp.getFeePa())
                    .idBundle(pp.getBundleId())
                    .idCiBundle(pp.getBundlePaId())
                    .totalNotice(totalNotices.toString())
                    .paymentMethod(pp.getPaymentMethod())
                    .touchpoint(pp.getPaymentChannel())
                    .remittanceInformation(ps.getDescription())
                    .iur(pp.getPaymentToken())
                    .metadata(null) // TODO format -> (ppp.getMetadata())
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
                  .companyName(null) // TODO from Cache
                  .amount(null) // TODO format -> (pt.getAmount())
                  .transferCategory(pt.getTransferCategory())
                  .remittanceInformation(pt.getRemittanceInformation())
                  .iban(pt.getIban())
                  .mbd(MBD.builder().build()) // TODO generate MBD
                  .metadata(null) // TODO format -> (pt.getMetadata())
                  .build());
    }

    return bizEvent;
  }

  public static BizEvent fromOldModel() {

    return BizEvent.builder().build();
  }

  public static BizEvent setTransactionDetails(BizEvent bizEvent, String transactionDetail) {

    return bizEvent;
  }
}
