package it.gov.pagopa.bizevents.sync.nodo.service;

import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.TransactionDetails;
import it.gov.pagopa.bizevents.sync.nodo.exception.BizEventSyncException;
import it.gov.pagopa.bizevents.sync.nodo.model.bizevent.ReceiptEventInfo;
import it.gov.pagopa.bizevents.sync.nodo.model.enumeration.PaymentModelVersion;
import it.gov.pagopa.bizevents.sync.nodo.model.mapper.BizEventMapper;
import it.gov.pagopa.bizevents.sync.nodo.model.sync.*;
import it.gov.pagopa.bizevents.sync.nodo.util.CommonUtility;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
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

  @Value("${synchronization-process.time-slot.size.minutes}")
  private int defaultSlotSizeInMinutes;

  @Value("${historic.historicization-after.days}")
  private int historicizationAfterInDays;

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

  public SyncReport executeSynchronizationForSingleReceipt(
      @NotNull LocalDateTime lowerLimitDate,
      @NotNull LocalDateTime upperLimitDate,
      String domainId,
      String noticeNumber) {

    SyncOutcome status = SyncOutcome.GENERATED;
    List<BizEvent> allBizEventsAnalyzed = new LinkedList<>();
    Set<ReceiptEventInfo> receiptsNotConvertedInBizEvents = new HashSet<>();

    boolean isBizEventMissing =
        bizEventsReaderService.checkIfMissingBizEvent(
            lowerLimitDate, upperLimitDate, domainId, noticeNumber);
    if (isBizEventMissing) {
      try {

        // Retrieve receipts from Nodo's DB that are not converted in BizEvents
        receiptsNotConvertedInBizEvents =
            this.bizEventsReaderService.retrieveSingleReceiptNotConvertedInBizEvents(
                lowerLimitDate, upperLimitDate, domainId, noticeNumber);

        // If there are receipts not converted in biz events, call conversion method
        if (!receiptsNotConvertedInBizEvents.isEmpty()) {
          generateNewBizEventsFromReceipts(
              lowerLimitDate,
              upperLimitDate,
              receiptsNotConvertedInBizEvents,
              allBizEventsAnalyzed);
        }

      } catch (BizEventSyncException e) {
        log.error(e.getCustomMessage(), e);
        status = SyncOutcome.GENERATION_ERROR;
      }
    } else {
        log.info("A valid BizEvent for domainId [{}] and notice number [{}] is already present!", domainId, noticeNumber);
        status = SyncOutcome.RECEIPT_NOT_FOUND;
    }

    // Generate final report
    return generateReport(
        status,
        allBizEventsAnalyzed,
        receiptsNotConvertedInBizEvents,
        lowerLimitDate,
        upperLimitDate,
        mustSendEventToEvent,
        true);
  }

  public SyncReport executeSynchronization(
      LocalDateTime lowerLimitDate,
      LocalDateTime upperLimitDate,
      int overriddenTimeSlotSize,
      boolean showEventData) {

    SyncOutcome status = SyncOutcome.GENERATED;
    List<BizEvent> allBizEventsAnalyzed = new LinkedList<>();
    Set<ReceiptEventInfo> receiptsNotConvertedInBizEvents = new HashSet<>();

    //
    List<Pair<LocalDateTime, LocalDateTime>> timeSlots =
        getTimeSlotThatRequireSynchronization(
            lowerLimitDate, upperLimitDate, overriddenTimeSlotSize);
    log.info("Found [{}] time slots to be synchronized: [{}]", timeSlots.size(), timeSlots);

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

        // If there are receipts not converted in biz events, call conversion method
        if (!receiptsNotConvertedInBizEvents.isEmpty()) {
          generateNewBizEventsFromReceipts(
              lowerDateBound,
              upperDateBound,
              receiptsNotConvertedInBizEvents,
              allBizEventsAnalyzed);
        }

      } catch (BizEventSyncException e) {
        log.error(e.getCustomMessage(), e);
        status = SyncOutcome.GENERATION_ERROR;
      }
    }

    // Generate final report
    return generateReport(
        status,
        allBizEventsAnalyzed,
        receiptsNotConvertedInBizEvents,
        lowerLimitDate,
        upperLimitDate,
        mustSendEventToEvent,
        showEventData);
  }

  private void generateNewBizEventsFromReceipts(
      LocalDateTime lowerDateBound,
      LocalDateTime upperDateBound,
      Set<ReceiptEventInfo> receiptsNotConvertedInBizEvents,
      List<BizEvent> allBizEventsAnalyzed) {

    log.error(
        "[BIZ-EVENTS-SYNC-NODO] Found [{}] payments from NdP not converted as BizEvents in"
            + " time slot [{} - {}].",
        receiptsNotConvertedInBizEvents.size(),
        lowerDateBound,
        upperDateBound);

    // Generate the BizEvents from receipts retrieved from Nodo's DB
    List<BizEvent> bizEventsToSend =
        generateBizEventsFromNodoReceipts(receiptsNotConvertedInBizEvents);
    allBizEventsAnalyzed.addAll(bizEventsToSend);

    // Send events to EventHub, but only if feature flag is active
    if (mustSendEventToEvent) {
      log.info("Sending [{}] BizEvents to the EventHub...", bizEventsToSend.size());
      this.eventHubSenderService.sendBizEventsToEventHub(bizEventsToSend);
    } else {
      log.info(
          "Skipped sending BizEvents to the EventHub because the 'send-to-eventhub' flag is"
              + " false!");
    }
  }

  private List<Pair<LocalDateTime, LocalDateTime>> getTimeSlotThatRequireSynchronization(
      LocalDateTime lowerLimitDate, LocalDateTime upperLimitDate, int overriddenTimeSlotSize) {

    List<Pair<LocalDateTime, LocalDateTime>> timeSlotsInError = new ArrayList<>();

    int slotSize = overriddenTimeSlotSize > 0 ? overriddenTimeSlotSize : defaultSlotSizeInMinutes;
    if (bizEventsReaderService.isHistoricizedReceipt(upperLimitDate) && slotSize > defaultSlotSizeInMinutes) {
        log.warn("Set slot size from [{}] to default value [{}] because of search in historical DB", slotSize, defaultSlotSizeInMinutes);
        slotSize = defaultSlotSizeInMinutes;
    }

    List<LocalDateTime> timeSlots =
        CommonUtility.splitInSlots(lowerLimitDate, upperLimitDate, slotSize);
    log.info(
        "Split [{} - {}] time slot in different sections: {}",
        lowerLimitDate,
        upperLimitDate,
        timeSlots);
    for (int index = 0; index < timeSlots.size() - 1; index++) {

      // Extracting upper and lower date boundaries
      LocalDateTime minDate = timeSlots.get(index);
      LocalDateTime maxDate = timeSlots.get(index + 1);

      // Count differences between receipts stored from NdP and elaborated BizEvents
      log.info("Searching missing BizEvents for time slot [{} - {}]", minDate, maxDate);
      boolean areThereMissingBizEvent =
          this.bizEventsReaderService.checkIfMissingBizEventsAtTimeSlot(minDate, maxDate);

      // If there are missing BizEvents, add the time slot boundaries in the returned list
      if (areThereMissingBizEvent) {

        log.info(
            "Time slot [{} - {}] has missing BizEvents! They will be synchronized in the next"
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

      LocalDateTime insertedTimestamp = receiptEvent.getInsertedTimestamp();
      boolean isHistoricized =
          LocalDateTime.now().minusDays(historicizationAfterInDays).isAfter(insertedTimestamp);

      try {

        BizEvent convertedBizEvent;
        if (PaymentModelVersion.OLD.equals(receiptEvent.getVersion())) {

          convertedBizEvent =
              isHistoricized
                  ? this.paymentPositionReaderService.readOldModelPaymentPositionFromHistoric(
                      receiptEvent)
                  : this.paymentPositionReaderService.readOldModelPaymentPosition(receiptEvent);

        } else {

          convertedBizEvent =
              isHistoricized
                  ? this.paymentPositionReaderService.readNewModelPaymentPositionFromHistoric(
                      receiptEvent)
                  : this.paymentPositionReaderService.readNewModelPaymentPosition(receiptEvent);
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
      SyncOutcome status,
      List<BizEvent> events,
      Set<ReceiptEventInfo> receiptEvents,
      LocalDateTime lowerLimitDate,
      LocalDateTime upperLimitDate,
      boolean sentToEventHub,
      boolean showEventData) {

    log.info("Synchronization ended! Generating report...");
    List<SyncReportRecord> records = new LinkedList<>();
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
              .syncStatus(SyncSingleRecordStatus.GENERATED)
              .build());
    }

    // if no Biz Event was inserted with the tuple defined by receiptEvent, it was not inserted
    for (ReceiptEventInfo receiptEvent : receiptEvents) {
      long bizEventsInsertedWithThisTriple =
          records.stream()
              .filter(
                  rec ->
                      (receiptEvent
                                  .getIuv()
                                  .equals(
                                      rec.getEvent() != null
                                          ? rec.getEvent().getDebtorPosition().getNoticeNumber()
                                          : null)
                              || receiptEvent.getIuv().equals(rec.getIuv()))
                          && receiptEvent.getDomainId().equals(rec.getDomainId())
                          && receiptEvent.getPaymentToken().equals(rec.getPaymentToken()))
              .count();
      if (bizEventsInsertedWithThisTriple == 0) {
        status = status != SyncOutcome.GENERATION_ERROR ? SyncOutcome.PARTIALLY_GENERATED : status;
        records.add(
            SyncReportRecord.builder()
                .iuv(receiptEvent.getIuv())
                .domainId(receiptEvent.getDomainId())
                .paymentToken(receiptEvent.getPaymentToken())
                .modelVersion(receiptEvent.getVersion())
                .syncStatus(SyncSingleRecordStatus.NOT_INSERTED)
                .build());
      }
    }

    return SyncReport.builder()
        .status(status)
        .executionTimeSlot(
            SyncReportTimeSlot.builder().from(lowerLimitDate).to(upperLimitDate).build())
        .totalRecords(records.size())
        .sentToEventHub(sentToEventHub)
        .records(records)
        .build();
  }
}
