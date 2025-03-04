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

        DateTimeFormatter bizEventDatesFormatter = DateTimeFormatter.ofPattern("yyyy-MM-ddThh:mm:ss");
        String bizEventMinDate = todayDate.minusDays(1).format(bizEventDatesFormatter);
        String bizEventMaxDate = todayDate.format(bizEventDatesFormatter);

        DateTimeFormatter nodoDatesFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String nodoMinDate = todayDate.minusDays(1).format(nodoDatesFormatter);
        String nodoMaxDateReceipt = todayDate.format(nodoDatesFormatter);
        String nodoMaxDateInsert = todayDate.plusDays(1).format(nodoDatesFormatter);

        // Count differences between payments from Nodo & elaborated biz-events
        long countDiff = this.bizEventsSyncNodoService.checkBizEventsDiffNodoToday(bizEventMinDate,bizEventMaxDate,nodoMinDate,nodoMaxDateReceipt,nodoMaxDateInsert);

        if(countDiff > 0){
            // Alert
            log.error("[BIZ-EVENTS-SYNC-NODO] {} payments from Nodo not elaborated to Biz-events", countDiff);

            // Retrieve missing payments from Nodo database
            List<NodoReceiptInfo> nodoReceiptInfoList = this.bizEventsSyncNodoService.retrieveNotElaboratedNodoReceipts(bizEventMinDate,bizEventMaxDate,nodoMinDate,nodoMaxDateReceipt,nodoMaxDateInsert);

            // Save missing payments on CosmosDB // TODO
        }
    }
}
