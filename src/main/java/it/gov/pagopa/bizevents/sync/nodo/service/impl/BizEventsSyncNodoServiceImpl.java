package it.gov.pagopa.bizevents.sync.nodo.service.impl;

import it.gov.pagopa.bizevents.sync.nodo.model.NodoReceiptInfo;
import it.gov.pagopa.bizevents.sync.nodo.repository.BizEventsRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.NodoNewModelReceiptsRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.NodoOldModelReceiptsRepository;
import it.gov.pagopa.bizevents.sync.nodo.service.BizEventsSyncNodoService;
import it.gov.pagopa.bizevents.sync.nodo.util.Constants;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        this.bizEventsRepository.countBizEventsWithPaymentDateTimeInTimeSlot(
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
  public List<NodoReceiptInfo> retrieveNotElaboratedNodoReceipts(
      LocalDateTime lowerBoundDate, LocalDateTime upperBoundDate) {

    // Retrieve all biz events payment_token from yesterday
    List<String> bizEventPaymentTokenList =
        this.bizEventsRepository.getBizEventsPaymentTokenFromPaymentDateTime(
            lowerBoundDate, upperBoundDate);

    // Retrieve all payments from nodo with receipt day yesterday and inserted time max today
    List<NodoReceiptInfo> list = new ArrayList<>();
    /*
    list.addAll(
        this.nodoReceiptNewModelRepository
            .readExcludedPaymentTokensInTimeSlot(
                nodoMinDate, nodoMaxDate, bizEventPaymentTokenList));
    list.addAll(
        this.nodoReceiptOldModelRepository.getReceiptFromReceiptDateAndNotInPaymentTokenList(
            nodoMinDate, nodoMaxDate, bizEventPaymentTokenList));
    */
    return list;
  }
}
