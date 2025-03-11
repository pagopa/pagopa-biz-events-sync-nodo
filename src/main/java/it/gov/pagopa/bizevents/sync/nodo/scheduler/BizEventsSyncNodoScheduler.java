package it.gov.pagopa.bizevents.sync.nodo.scheduler;

import it.gov.pagopa.bizevents.sync.nodo.service.BizEventsSyncNodoService;
import it.gov.pagopa.bizevents.sync.nodo.util.CommonUtility;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class BizEventsSyncNodoScheduler {

  private final BizEventsSyncNodoService bizEventsSyncNodoService;

  @Value("${synchronization-process.lower-bound-date.before-days}")
  private Integer lowerBoundDateBeforeDays;

  @Autowired
  public BizEventsSyncNodoScheduler(BizEventsSyncNodoService bizEventsSyncNodoService) {

    this.bizEventsSyncNodoService = bizEventsSyncNodoService;
  }

  @Scheduled(cron = "${synchronization-process.schedule.expression}")
  @Async
  @Transactional
  public void synchronizeBizEventsWithNdpReceipts() {

    LocalDateTime todayDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
    LocalDateTime lowerLimitDate = todayDate.minusDays(lowerBoundDateBeforeDays);
    LocalDateTime upperLimitDate = todayDate.minusDays(lowerBoundDateBeforeDays - 1L);

    List<Pair<LocalDateTime, LocalDateTime>> timeSlots =
        getTimeSlotThatRequireSynchronization(lowerLimitDate, upperLimitDate);

    if (!timeSlots.isEmpty()) {

      for (Pair<LocalDateTime, LocalDateTime> timeSlotBoundary : timeSlots) {

        LocalDateTime lowerDateBound = timeSlotBoundary.getLeft();
        LocalDateTime upperDateBound = timeSlotBoundary.getRight();

        /*
        List<NodoReceiptInfo> nodoReceiptInfoList =
            this.bizEventsSyncNodoService.retrieveNotElaboratedNodoReceipts(
                lowerDateBound, upperDateBound);
         */
        // log.error("[BIZ-EVENTS-SYNC-NODO] Found [{}] payments from Nodo not elaborated to
        // Biz-events", countDiff);
      }

    } else {
      log.info("No time slot to be synchronized. All BizEvents were correctly stored!");
    }

    // Alert

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
          this.bizEventsSyncNodoService.checkIfMissingBizEventsAtTimeSlot(minDate, maxDate);

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
}
