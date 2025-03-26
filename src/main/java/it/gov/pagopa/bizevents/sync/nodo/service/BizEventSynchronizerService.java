package it.gov.pagopa.bizevents.sync.nodo.service;

import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.TransactionDetails;
import it.gov.pagopa.bizevents.sync.nodo.exception.BizEventSyncException;
import it.gov.pagopa.bizevents.sync.nodo.model.bizevent.ReceiptEventInfo;
import it.gov.pagopa.bizevents.sync.nodo.model.enumeration.PaymentModelVersion;
import it.gov.pagopa.bizevents.sync.nodo.model.mapper.BizEventMapper;
import it.gov.pagopa.bizevents.sync.nodo.model.sync.SyncReport;
import it.gov.pagopa.bizevents.sync.nodo.model.sync.SyncReportRecord;
import it.gov.pagopa.bizevents.sync.nodo.model.sync.SyncReportTimeSlot;
import it.gov.pagopa.bizevents.sync.nodo.util.CommonUtility;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BizEventSynchronizerService {

  private final BizEventsReaderService bizEventsReaderService;

  private final PaymentPositionReaderService paymentPositionReaderService;

  private final EcommerceHelpdeskReaderService ecommerceHelpdeskReaderService;

  private final EventHubSenderService eventHubSenderService;

  @Value("${synchronization-process.send-to-eventhub.activation}")
  private boolean mustSendEventToEvent;

  @Autowired
  public BizEventSynchronizerService(
      BizEventsReaderService bizEventsReaderService,
      PaymentPositionReaderService paymentPositionReaderService,
      EcommerceHelpdeskReaderService ecommerceHelpdeskReaderService,
      EventHubSenderService eventHubSenderService) {

    this.bizEventsReaderService = bizEventsReaderService;
    this.paymentPositionReaderService = paymentPositionReaderService;
    this.ecommerceHelpdeskReaderService = ecommerceHelpdeskReaderService;
    this.eventHubSenderService = eventHubSenderService;
  }

  public SyncReport executeSynchronization(
      LocalDateTime lowerLimitDate, LocalDateTime upperLimitDate, boolean showEventData) {

    List<BizEvent> bizEventsToSend = new LinkedList<>();
    Set<ReceiptEventInfo> receiptsNotConvertedInBizEvents = new HashSet<>();
    boolean errorDuringComputation = false;

    //
    List<Pair<LocalDateTime, LocalDateTime>> timeSlots =
        getTimeSlotThatRequireSynchronization(lowerLimitDate, upperLimitDate);
    log.debug("Found [{}] time slots to be synchronized: [{}]", timeSlots.size(), timeSlots);

    //
    for (Pair<LocalDateTime, LocalDateTime> timeSlotToSynchronize : timeSlots) {

      try {

        //
        LocalDateTime lowerDateBound = timeSlotToSynchronize.getLeft();
        LocalDateTime upperDateBound = timeSlotToSynchronize.getRight();
        log.info("Analyzing time slot [{} - {}]...", lowerDateBound, upperDateBound);

        //
        receiptsNotConvertedInBizEvents =
            this.bizEventsReaderService.retrieveReceiptsNotConvertedInBizEvents(
                lowerDateBound, upperDateBound);

        //
        if (!receiptsNotConvertedInBizEvents.isEmpty()) {

          //
          log.error(
              "[BIZ-EVENTS-SYNC-NODO] Found [{}] payments from NdP not converted as BizEvents in"
                  + " time slot [{} - {}].",
              receiptsNotConvertedInBizEvents.size(),
              lowerDateBound,
              upperDateBound);

          //
          bizEventsToSend = generateBizEventsFromNodoReceipts(receiptsNotConvertedInBizEvents);

          //
          if (mustSendEventToEvent) {
            log.info("Sending [{}] BizEvents to the EventHub...", bizEventsToSend.size());
            this.eventHubSenderService.sendBizEventsToEventHub(bizEventsToSend);
          } else {
            log.info(
                "Skipped sending BizEvents to the EventHub because the 'send-to-eventhub' flag is"
                    + " false!");
          }
        }

      } catch (BizEventSyncException e) {
        log.error(e.getCustomMessage(), e);
        errorDuringComputation = true;
      }
    }

    //
    log.debug("Synchronization ended! Generating report...");
    return generateReport(
        bizEventsToSend,
        receiptsNotConvertedInBizEvents,
        lowerLimitDate,
        upperLimitDate,
        mustSendEventToEvent,
        showEventData,
        errorDuringComputation);
  }

  private List<Pair<LocalDateTime, LocalDateTime>> getTimeSlotThatRequireSynchronization(
      LocalDateTime lowerLimitDate, LocalDateTime upperLimitDate) {

    List<Pair<LocalDateTime, LocalDateTime>> timeSlotsInError = new ArrayList<>();

    List<LocalDateTime> timeSlots = CommonUtility.splitInSlots(lowerLimitDate, upperLimitDate, 1);
    for (int index = 0; index < timeSlots.size() - 1; index++) {

      // Extracting upper and lower date boundaries
      LocalDateTime minDate = timeSlots.get(index);
      LocalDateTime maxDate = timeSlots.get(index + 1);

      // Count differences between receipts stored from NdP and elaborated BizEvents
      boolean areThereMissingBizEvent =
          this.bizEventsReaderService.checkIfMissingBizEventsAtTimeSlot(minDate, maxDate);

      // If there are missing BizEvents, add the time slot boundaries in the returned list
      if (areThereMissingBizEvent) {

        log.debug(
            "Time slot [{} - {}] has missing BizEvents. They will be synchronized in the next"
                + " step.",
            minDate,
            maxDate);
        timeSlotsInError.add(Pair.of(minDate, maxDate));

      } else {

        log.debug(
            "No missing BizEvents in the time slot [{} - {}]. Skipping synchronization for this"
                + " time slot.",
            minDate,
            maxDate);
      }
    }

    return timeSlotsInError;
  }

  private List<BizEvent> generateBizEventsFromNodoReceipts(
      Set<ReceiptEventInfo> receiptsNotConvertedInBizEvents) {

    List<BizEvent> newlyGeneratedBizEvents = new LinkedList<>();
    for (ReceiptEventInfo receiptEvent : receiptsNotConvertedInBizEvents) {

      try {

        BizEvent convertedBizEvent;
        if (PaymentModelVersion.OLD.equals(receiptEvent.getVersion())) {

          //
          convertedBizEvent =
              this.paymentPositionReaderService.readOldModelPaymentPosition(receiptEvent);

        } else {

          //
          convertedBizEvent =
              this.paymentPositionReaderService.readNewModelPaymentPosition(receiptEvent);
        }

        //
        TransactionDetails transactionDetails = null;
        if (convertedBizEvent.getTransactionDetails() == null) {
          transactionDetails =
              this.ecommerceHelpdeskReaderService.getTransactionDetails(
                  receiptEvent.getPaymentToken());
        }

        //
        BizEventMapper.finalize(convertedBizEvent, transactionDetails);
        newlyGeneratedBizEvents.add(convertedBizEvent);

      } catch (BizEventSyncException e) {
        log.error(e.getCustomMessage(), e);
      }
    }

    return newlyGeneratedBizEvents;
  }

  private SyncReport generateReport(
      List<BizEvent> events,
      Set<ReceiptEventInfo> receiptEvents,
      LocalDateTime lowerLimitDate,
      LocalDateTime upperLimitDate,
      boolean sentToEventHub,
      boolean showEventData,
      boolean errorDuringComputation) {

    List<SyncReportRecord> records = new LinkedList<>();
    boolean onError = errorDuringComputation;
    for (BizEvent bizEvent : events) {

      String paymentToken = bizEvent.getPaymentInfo().getPaymentToken();
      String domainId = bizEvent.getCreditor().getIdPA();
      String iuv = bizEvent.getDebtorPosition().getIuv();
      String noticeNumber = bizEvent.getDebtorPosition().getNoticeNumber();
      ReceiptEventInfo relatedInfo =
          receiptEvents.stream()
              .filter(
                  event ->
                      paymentToken.equals(event.getPaymentToken())
                          && domainId.equals(event.getDomainId())
                          && (event.getIuv().equals(iuv) || event.getIuv().equals(noticeNumber)))
              .findFirst()
              .orElse(null);

      records.add(
          SyncReportRecord.builder()
              .bizEventId(bizEvent.getId())
              .iuv(iuv)
              .domainId(domainId)
              .paymentToken(paymentToken)
              .event(showEventData ? bizEvent : null)
              .modelVersion(relatedInfo != null ? relatedInfo.getVersion() : null)
              .timeSlot(
                  relatedInfo == null
                      ? null
                      : SyncReportTimeSlot.builder()
                          .from(relatedInfo.getLowerBoundTimeSlot())
                          .to(relatedInfo.getUpperBoundTimeSlot())
                          .build())
              .syncStatus("GENERATED")
              .build());
    }

    // if no Biz Event was inserted with the tuple defined by receiptEvent, it was not inserted
    for (ReceiptEventInfo receiptEvent : receiptEvents) {
      long bizEventsInsertedWithThisTriple =
          records.stream()
              .filter(
                  rec ->
                      receiptEvent.getIuv().equals(rec.getIuv())
                          && receiptEvent.getDomainId().equals(rec.getDomainId())
                          && receiptEvent.getPaymentToken().equals(rec.getPaymentToken()))
              .count();
      if (bizEventsInsertedWithThisTriple == 0) {
        onError = true;
        records.add(
            SyncReportRecord.builder()
                .iuv(receiptEvent.getIuv())
                .domainId(receiptEvent.getDomainId())
                .paymentToken(receiptEvent.getPaymentToken())
                .modelVersion(receiptEvent.getVersion())
                .syncStatus("NOT_INSERTED")
                .build());
      }
    }

    return SyncReport.builder()
        .executionTimeSlot(
            SyncReportTimeSlot.builder().from(lowerLimitDate).to(upperLimitDate).build())
        .totalRecords(records.size())
        .sentToEventHub(sentToEventHub)
        .errorDuringComputation(onError)
        .records(records)
        .build();
  }
}
