package it.gov.pagopa.bizevents.sync.nodo.service.impl;

import it.gov.pagopa.bizevents.sync.nodo.repository.BizEventsRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.NodoReceiptNewModelRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.NodoReceiptOldModelRepository;
import it.gov.pagopa.bizevents.sync.nodo.service.BizEventsSyncNodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
public class BizEventsSyncNodoServiceImpl implements BizEventsSyncNodoService {

    private final BizEventsRepository bizEventsRepository;
    private final NodoReceiptNewModelRepository nodoReceiptNewModelRepository;
    private final NodoReceiptOldModelRepository nodoReceiptOldModelRepository;


    @Autowired
    public BizEventsSyncNodoServiceImpl(BizEventsRepository bizEventsRepository, NodoReceiptNewModelRepository nodoReceiptNewModelRepository, NodoReceiptOldModelRepository nodoReceiptOldModelRepository) {
        this.bizEventsRepository = bizEventsRepository;
        this.nodoReceiptNewModelRepository = nodoReceiptNewModelRepository;
        this.nodoReceiptOldModelRepository = nodoReceiptOldModelRepository;
    }

    @Override
    public long checkBizEventsDiffNodoToday() {
        LocalDateTime todayDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);

        // Retrieve all biz events from yesterday
        DateTimeFormatter bizEventDatesFormatter = DateTimeFormatter.ofPattern("yyyy-MM-ddThh:mm:ss");
        String bizEventMinDate = todayDate.minusDays(1).format(bizEventDatesFormatter);
        String bizEventMaxDate = todayDate.format(bizEventDatesFormatter);
        long numberOfBizEvents = this.bizEventsRepository.countBizEventsFromPaymentDateTime(bizEventMinDate, bizEventMaxDate);

        // Retrieve all payments from nodo with receipt day yesterday and inserted time max today
        DateTimeFormatter nodoDatesFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String nodoMinDate = todayDate.minusDays(1).format(nodoDatesFormatter);
        String nodoMaxDateReceipt = todayDate.format(nodoDatesFormatter);
        String nodoMaxDateInsert = todayDate.plusDays(1).format(nodoDatesFormatter);
        long numberOfPaymentsNewModelFromNodo = this.nodoReceiptNewModelRepository.countPositionReceiptFromReceiptDate(nodoMinDate, nodoMaxDateReceipt, nodoMaxDateInsert);
        long numberOfPaymentsOldModelFromNodo = this.nodoReceiptOldModelRepository.countReceiptFromReceiptDate(nodoMinDate, nodoMaxDateReceipt, nodoMaxDateInsert);

        return (numberOfPaymentsNewModelFromNodo + numberOfPaymentsOldModelFromNodo) - numberOfBizEvents;
    }

}
