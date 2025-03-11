package it.gov.pagopa.bizevents.sync.nodo.service.impl;

import it.gov.pagopa.bizevents.sync.nodo.model.ReceiptEventInfo;
import it.gov.pagopa.bizevents.sync.nodo.repository.BizEventsRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.NodoNewModelReceiptsRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.NodoOldModelReceiptsRepository;
import it.gov.pagopa.bizevents.sync.nodo.service.BizEventsSyncNodoService;
import it.gov.pagopa.bizevents.sync.nodo.util.Constants;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BizEventsSyncNodoServiceImpl implements BizEventsSyncNodoService {

  private final BizEventsRepository bizEventsRepository;

  private final NodoNewModelReceiptsRepository nodoNewModelReceiptsRepository;

  private final NodoOldModelReceiptsRepository nodoOldModelReceiptsRepository;

  @Autowired
  public BizEventsSyncNodoServiceImpl(
      BizEventsRepository bizEventsRepository,
      NodoNewModelReceiptsRepository nodoNewModelReceiptsRepository,
      NodoOldModelReceiptsRepository nodoOldModelReceiptsRepository) {

    this.bizEventsRepository = bizEventsRepository;
    this.nodoNewModelReceiptsRepository = nodoNewModelReceiptsRepository;
    this.nodoOldModelReceiptsRepository = nodoOldModelReceiptsRepository;
  }

  @Override
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

    // Retrieve the count of receipts generated on the first occurrence for old payment models for
    // the time slot passed
    long numberOfFirstPayOldModelReceipts =
        this.nodoOldModelReceiptsRepository.countFirstRPTsByTimeSlot(
            lowerBoundDate, upperBoundDate);

    // Retrieve the count of receipts generated on the retried occurrence for old payment models for
    // the time slot passed
    long numberOfRetriedOldModelReceipts =
        this.nodoOldModelReceiptsRepository.countRetriedRPTsByTimeSlot(
            lowerBoundDate, upperBoundDate);

    // Calculate the count of receipts useful for old payment models for the time slot passed
    long numberOfOldModelReceipts =
        numberOfFirstPayOldModelReceipts - numberOfRetriedOldModelReceipts;

    // Retrieve the count of receipts generated for new payment models for the time slot passed
    long numberOfNewModelReceipts =
        this.nodoNewModelReceiptsRepository.countByTimeSlot(lowerBoundDate, upperBoundDate);

    return ((numberOfNewModelReceipts + numberOfOldModelReceipts) - numberOfBizEvents) > 0;
  }

  @Override
  public Set<ReceiptEventInfo> retrieveReceiptsNotConvertedInBizEvents(
      LocalDateTime lowerBoundDate, LocalDateTime upperBoundDate) {

    //
    Set<ReceiptEventInfo> ndpReceipts = new HashSet<>();
    ndpReceipts.addAll(
        this.nodoNewModelReceiptsRepository.readReceiptsInTimeSlot(lowerBoundDate, upperBoundDate));
    ndpReceipts.addAll(
        this.nodoOldModelReceiptsRepository.readReceiptsInTimeSlot(lowerBoundDate, upperBoundDate));

    Set<ReceiptEventInfo> bizEvents = new HashSet<>();
    bizEvents.addAll(
        this.bizEventsRepository.readBizEventsOfOldModelInTimeSlot(lowerBoundDate, upperBoundDate));
    bizEvents.addAll(
        this.bizEventsRepository.readBizEventsOfNewModelInTimeSlot(lowerBoundDate, upperBoundDate));

    ndpReceipts.removeAll(bizEvents);
    return ndpReceipts;
  }
}
