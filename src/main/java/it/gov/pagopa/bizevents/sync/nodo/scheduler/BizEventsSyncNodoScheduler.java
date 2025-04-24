package it.gov.pagopa.bizevents.sync.nodo.scheduler;

import it.gov.pagopa.bizevents.sync.nodo.service.BizEventSynchronizerService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BizEventsSyncNodoScheduler {

  private final BizEventSynchronizerService bizEventSynchronizerService;

  @Value("${synchronization-process.lower-bound-date.before-days}")
  private Integer lowerBoundDateBeforeDays;

  @Autowired
  public BizEventsSyncNodoScheduler(BizEventSynchronizerService bizEventSynchronizerService) {

    this.bizEventSynchronizerService = bizEventSynchronizerService;
  }

  @Scheduled(cron = "${synchronization-process.schedule.expression}")
  public void synchronizeBizEventsWithNdpReceipts() {

    long start = System.currentTimeMillis();
    log.info("Starting scheduled execution of NdP receipts to BizEvents!");

    //
    LocalDateTime todayDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
    LocalDateTime lowerLimitDate = todayDate.minusDays(lowerBoundDateBeforeDays);
    LocalDateTime upperLimitDate = todayDate.minusDays(lowerBoundDateBeforeDays - 1L);

    //
    bizEventSynchronizerService.executeSynchronization(lowerLimitDate, upperLimitDate, -1, false);

    long end = System.currentTimeMillis();
    log.info(
        "Ended scheduled execution of NdP receipts to BizEvents! Elapsed time: [{}] ms.",
        end - start);
  }
}
