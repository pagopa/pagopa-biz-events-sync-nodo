package it.gov.pagopa.bizevents.sync.nodo.scheduler;

import it.gov.pagopa.bizevents.sync.nodo.service.BizEventsSyncNodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class BizEventsSyncNodoScheduler {

    private final BizEventsSyncNodoService bizEventsSyncNodoService;

    @Autowired
    public BizEventsSyncNodoScheduler(BizEventsSyncNodoService bizEventsSyncNodoService){
        this.bizEventsSyncNodoService = bizEventsSyncNodoService;
    }


    @Scheduled(cron = "${cron.job.schedule.expression.biz-sync-nodo}")
    @Async
    @Transactional
    public void checkBizEventsSyncWithNodo() {
        // Count differences between payments from Nodo & elaborated biz-events
        long countDiff = bizEventsSyncNodoService.checkBizEventsDiffNodoToday();

        if(countDiff > 0){
            // Alert
            log.error("[BIZ-EVENTS-SYNC-NODO] {} payments from Nodo not elaborated to Biz-events", countDiff);

            // Retrieve missing payments from Nodo database
            // TODO Retrieve missing payments from nodo
            // TODO Retrieve biz-events
            // TODO payments v1 with payment_token filter
            // TODO payments v2 with payment_token filter

            // Save missing payments on CosmosDB
            // TODO
        }
    }
}
