package it.gov.pagopa.bizevents.sync.nodo.service;

import it.gov.pagopa.bizevents.sync.nodo.model.NdpReceipt;
import java.time.LocalDateTime;
import java.util.List;

public interface BizEventsSyncNodoService {

  /**
   * Check whether there are any discrepancies between the biz events and the receipts stored by
   * NdP, searching in a past time slot.
   *
   * @param lowerBoundDate the lower bound of the timeslot
   * @param upperBoundDate the upper bound of the timeslot
   * @return true if the number of Biz Events are lower than the number of receipts stored by NdP in
   *     the time slot.
   */
  boolean checkIfMissingBizEventsAtTimeSlot(
      LocalDateTime lowerBoundDate, LocalDateTime upperBoundDate);

  /**
   * @param lowerBoundDate
   * @param upperBoundDate
   * @return
   */
  List<NdpReceipt> retrieveNotElaboratedNodoReceipts(
      LocalDateTime lowerBoundDate, LocalDateTime upperBoundDate);
}
