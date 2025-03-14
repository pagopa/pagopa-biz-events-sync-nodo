package it.gov.pagopa.bizevents.sync.nodo.service;

import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.TransactionDetails;
import it.gov.pagopa.bizevents.sync.nodo.model.bizevent.ReceiptEventInfo;
import it.gov.pagopa.bizevents.sync.nodo.model.enumeration.PaymentModelVersion;
import it.gov.pagopa.bizevents.sync.nodo.model.mapper.BizEventMapper;
import it.gov.pagopa.bizevents.sync.nodo.util.CommonUtility;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BizEventSynchronizerService {

  private final BizEventsReaderService bizEventsReaderService;
  private final PaymentPositionReaderService paymentPositionReaderService;
  private final EcommerceHelpdeskReaderService ecommerceHelpdeskReaderService;
  private final EventHubSenderService eventHubSenderService;

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

  public void executeSynchronization(LocalDateTime lowerLimitDate, LocalDateTime upperLimitDate) {

    //
    List<Pair<LocalDateTime, LocalDateTime>> timeSlots =
        getTimeSlotThatRequireSynchronization(lowerLimitDate, upperLimitDate);

    //
    for (Pair<LocalDateTime, LocalDateTime> timeSlotToSynchronize : timeSlots) {

      //
      LocalDateTime lowerDateBound = timeSlotToSynchronize.getLeft();
      LocalDateTime upperDateBound = timeSlotToSynchronize.getRight();

      //
      // TODO handle errors and exceptions
      Set<ReceiptEventInfo> receiptsNotConvertedInBizEvents =
          this.bizEventsReaderService.retrieveReceiptsNotConvertedInBizEvents(
              lowerDateBound, upperDateBound);

      //
      if (!receiptsNotConvertedInBizEvents.isEmpty()) {

        //
        log.error(
            "[BIZ-EVENTS-SYNC-NODO] Found [{}] payments from NdP not converted as BizEvents in time"
                + " slot [{} - {}].",
            receiptsNotConvertedInBizEvents.size(),
            lowerDateBound,
            upperDateBound);

        // TODO remove, only for debug purpose
        receiptsNotConvertedInBizEvents =
            receiptsNotConvertedInBizEvents.stream().findFirst().stream()
                .collect(Collectors.toSet());

        //
        List<BizEvent> bizEventsToSend =
            generateBizEventsFromNodoReceipts(receiptsNotConvertedInBizEvents);

        //
        this.eventHubSenderService.sendBizEventsToEventHub(bizEventsToSend);
        log.info("");
      }
    }
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

    // TODO handle errors and exceptions
    for (ReceiptEventInfo receiptEvent : receiptsNotConvertedInBizEvents) {

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
      TransactionDetails transactionDetails =
          this.ecommerceHelpdeskReaderService.getTransactionDetails(receiptEvent.getPaymentToken());

      //
      BizEventMapper.finalize(convertedBizEvent, transactionDetails);

      newlyGeneratedBizEvents.add(convertedBizEvent);
    }

    return newlyGeneratedBizEvents;
  }
}
