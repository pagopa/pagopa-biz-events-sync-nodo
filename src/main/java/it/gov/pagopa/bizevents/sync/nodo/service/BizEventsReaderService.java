package it.gov.pagopa.bizevents.sync.nodo.service;

import it.gov.pagopa.bizevents.sync.nodo.exception.BizEventSyncException;
import it.gov.pagopa.bizevents.sync.nodo.model.bizevent.ReceiptEventInfo;
import it.gov.pagopa.bizevents.sync.nodo.repository.BizEventsRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.receipt.PositionReceiptRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.receipt.RtRepository;
import it.gov.pagopa.bizevents.sync.nodo.util.Constants;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BizEventsReaderService {

  private final BizEventsRepository bizEventsRepository;

  private final PositionReceiptRepository positionReceiptRepository;

  private final RtRepository rtRepository;

  @Autowired
  public BizEventsReaderService(
      BizEventsRepository bizEventsRepository,
      PositionReceiptRepository positionReceiptRepository,
      RtRepository rtRepository) {

    this.bizEventsRepository = bizEventsRepository;
    this.positionReceiptRepository = positionReceiptRepository;
    this.rtRepository = rtRepository;
  }

  /**
   * Check whether there are any discrepancies between the biz events and the receipts stored by
   * NdP, searching in a past time slot.
   *
   * @param lowerBoundDate the lower bound of the timeslot
   * @param upperBoundDate the upper bound of the timeslot
   * @return true if the number of Biz Events are lower than the number of receipts stored by NdP in
   *     the time slot.
   */
  public boolean checkIfMissingBizEventsAtTimeSlot(
      LocalDateTime lowerBoundDate, LocalDateTime upperBoundDate) {

    // Retrieve the count of BizEvents for the passed time slot
    long numberOfBizEvents = 0;
    List<Long> countOfBizEventByTimeSlot =
        this.bizEventsRepository.countBizEventsInTimeSlot(
            lowerBoundDate.format(Constants.BIZ_EVENT_DATE_FORMATTER),
            upperBoundDate.format(Constants.BIZ_EVENT_DATE_FORMATTER));
    if (!countOfBizEventByTimeSlot.isEmpty()) {
      numberOfBizEvents = countOfBizEventByTimeSlot.get(0);
    }
    log.info(
        "Found [{}] BizEvents in time slot [{} - {}]",
        numberOfBizEvents,
        lowerBoundDate,
        upperBoundDate);

    // Retrieve the count of receipts generated on the first occurrence for old payment models for
    // the time slot passed
    long numberOfFirstPayOldModelReceipts =
        this.rtRepository.countFirstRPTsByTimeSlot(lowerBoundDate, upperBoundDate);

    // Retrieve the count of receipts generated on the retried occurrence for old payment models for
    // the time slot passed
    long numberOfRetriedOldModelReceipts =
        this.rtRepository.countRetriedRPTsByTimeSlot(lowerBoundDate, upperBoundDate);

    // Calculate the count of receipts useful for old payment models for the time slot passed
    long numberOfOldModelReceipts =
        numberOfFirstPayOldModelReceipts - numberOfRetriedOldModelReceipts;
    log.info(
        "Found [{}] receipts for old model in time slot [{} - {}]",
        numberOfOldModelReceipts,
        lowerBoundDate,
        upperBoundDate);

    // Retrieve the count of receipts generated for new payment models for the time slot passed
    long numberOfNewModelReceipts =
        this.positionReceiptRepository.countByTimeSlot(lowerBoundDate, upperBoundDate);
    log.info(
        "Found [{}] receipts for new model in time slot [{} - {}]",
        numberOfNewModelReceipts,
        lowerBoundDate,
        upperBoundDate);

    return ((numberOfNewModelReceipts + numberOfOldModelReceipts) - numberOfBizEvents) > 0;
  }

  public Set<ReceiptEventInfo> retrieveReceiptsNotConvertedInBizEvents(
      LocalDateTime lowerBoundDate, LocalDateTime upperBoundDate) {

    Set<ReceiptEventInfo> ndpReceipts = new HashSet<>();
    Set<ReceiptEventInfo> bizEvents;

    try {

      //
      Set<ReceiptEventInfo> newModelReceipts =
          this.positionReceiptRepository.readReceiptsInTimeSlot(lowerBoundDate, upperBoundDate);
      log.info("Found [{}] new model receipts in analyzed time slot...", newModelReceipts.size());
      Set<ReceiptEventInfo> oldModelReceipts =
          this.rtRepository.readReceiptsInTimeSlot(lowerBoundDate, upperBoundDate);
      log.info("Found [{}] old model receipts in analyzed time slot...", oldModelReceipts.size());

      //
      ndpReceipts.addAll(oldModelReceipts);
      ndpReceipts.addAll(newModelReceipts);
      ndpReceipts.forEach(
          e -> {
            e.setLowerBoundTimeSlot(lowerBoundDate);
            e.setUpperBoundTimeSlot(upperBoundDate);
          });

      //
      String formattedLowerBoundDate = lowerBoundDate.format(Constants.BIZ_EVENT_DATE_FORMATTER);
      String formattedUpperBoundDate = upperBoundDate.format(Constants.BIZ_EVENT_DATE_FORMATTER);

      //
      Set<Map<String, Object>> rawResults =
          this.bizEventsRepository.readBizEventsInTimeSlot(
              formattedLowerBoundDate, formattedUpperBoundDate);
      bizEvents = rawResults.stream().map(ReceiptEventInfo::new).collect(Collectors.toSet());
      log.info("Found [{}] BizEvents in analyzed time slot...", bizEvents.size());

      //
      ndpReceipts.removeAll(bizEvents);
      log.info(
          "Removed all extracted BizEvents from NdP new-and-old receipts: found [{}] receipts that"
              + " must be synchronized as BizEvents!",
          ndpReceipts.size());

    } catch (Exception e) {
      String msg =
          String.format(
              "An error occurred while searching receipts/BizEvents on time slot [%s - %s].",
              lowerBoundDate, upperBoundDate);
      throw new BizEventSyncException(msg, e);
    }

    return ndpReceipts;
  }
}
