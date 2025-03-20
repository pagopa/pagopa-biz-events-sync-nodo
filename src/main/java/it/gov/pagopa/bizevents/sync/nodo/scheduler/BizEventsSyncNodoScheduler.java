package it.gov.pagopa.bizevents.sync.nodo.scheduler;

import it.gov.pagopa.bizevents.sync.nodo.service.BizEventSynchronizerService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
  @Async
  @Transactional
  public void synchronizeBizEventsWithNdpReceipts() {

    //
    LocalDateTime todayDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
    LocalDateTime lowerLimitDate = todayDate.minusDays(lowerBoundDateBeforeDays);
    LocalDateTime upperLimitDate = todayDate.minusDays(lowerBoundDateBeforeDays - 1L);

    //
    bizEventSynchronizerService.executeSynchronization(lowerLimitDate, upperLimitDate, false);
  }
}
