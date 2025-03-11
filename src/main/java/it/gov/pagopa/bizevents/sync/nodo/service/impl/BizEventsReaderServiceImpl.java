package it.gov.pagopa.bizevents.sync.nodo.service.impl;

import it.gov.pagopa.bizevents.sync.nodo.model.ReceiptEventInfo;
import it.gov.pagopa.bizevents.sync.nodo.repository.BizEventsRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.NodoNewModelReceiptsRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.NodoOldModelReceiptsRepository;
import it.gov.pagopa.bizevents.sync.nodo.service.BizEventsReaderService;
import it.gov.pagopa.bizevents.sync.nodo.util.Constants;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BizEventsReaderServiceImpl implements BizEventsReaderService {

  private final BizEventsRepository bizEventsRepository;

  private final NodoNewModelReceiptsRepository nodoNewModelReceiptsRepository;

  private final NodoOldModelReceiptsRepository nodoOldModelReceiptsRepository;

  @Autowired
  public BizEventsReaderServiceImpl(
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
    String formattedLowerBoundDate = lowerBoundDate.format(Constants.BIZ_EVENT_DATE_FORMATTER);
    String formattedUpperBoundDate = upperBoundDate.format(Constants.BIZ_EVENT_DATE_FORMATTER);

    try {
      Set<Map<String, Object>> rawResults =
          this.bizEventsRepository.readBizEventsInTimeSlot(
              formattedLowerBoundDate, formattedUpperBoundDate);
      bizEvents.addAll(rawResults.stream().map(ReceiptEventInfo::new).collect(Collectors.toSet()));
    } catch (DataAccessException e) {
      log.error(
          "An error occurred while searching BizEvents for old payment model (i.e. modelType = 1)"
              + " on time slot [{} - {}].",
          lowerBoundDate,
          upperBoundDate,
          e);
      // TODO throw custom exception
    }

    ndpReceipts.removeAll(bizEvents);
    return ndpReceipts;
  }
}
