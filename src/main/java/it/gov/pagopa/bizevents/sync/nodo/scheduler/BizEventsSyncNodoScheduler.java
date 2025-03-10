package it.gov.pagopa.bizevents.sync.nodo.scheduler;

import it.gov.pagopa.bizevents.sync.nodo.model.NodoReceiptInfo;
import it.gov.pagopa.bizevents.sync.nodo.service.BizEventsSyncNodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
        LocalDateTime todayDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);

        LocalDateTime minDate = todayDate.minusDays(2);
        LocalDateTime maxDate = todayDate.minusDays(1);
        // TODO verify in same date formatter can be used
        DateTimeFormatter bizEventDatesFormatter = DateTimeFormatter.ofPattern("yyyy-MM-ddThh:mm:ss");
        String bizEventMinDate = minDate.format(bizEventDatesFormatter);
        String bizEventMaxDate = maxDate.format(bizEventDatesFormatter);

        DateTimeFormatter nodoDatesFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String nodoMinDate = minDate.format(nodoDatesFormatter);
        String nodoMaxDate = maxDate.format(nodoDatesFormatter);

        // Count differences between payments from Nodo & elaborated biz-events
        long countDiff = this.bizEventsSyncNodoService.checkBizEventsDiffAtDate(bizEventMinDate,bizEventMaxDate,nodoMinDate,nodoMaxDate);

        if(countDiff > 0){
            // Alert
            log.error("[BIZ-EVENTS-SYNC-NODO] {} payments from Nodo not elaborated to Biz-events", countDiff);

            // Retrieve missing payments from Nodo database
            List<NodoReceiptInfo> nodoReceiptInfoList = this.bizEventsSyncNodoService.retrieveNotElaboratedNodoReceipts(bizEventMinDate,bizEventMaxDate,nodoMinDate,nodoMaxDate);

            // Save missing payments on CosmosDB // TODO
        }
    }
}
