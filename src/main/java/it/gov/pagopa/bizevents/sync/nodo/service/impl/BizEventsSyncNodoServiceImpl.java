package it.gov.pagopa.bizevents.sync.nodo.service.impl;

import it.gov.pagopa.bizevents.sync.nodo.model.NodoReceiptInfo;
import it.gov.pagopa.bizevents.sync.nodo.repository.BizEventsRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.NodoReceiptNewModelRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.NodoReceiptOldModelRepository;
import it.gov.pagopa.bizevents.sync.nodo.service.BizEventsSyncNodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    public long checkBizEventsDiffAtDate(String bizEventMinDate, String bizEventMaxDate, String nodoMinDate, String nodoMaxDate) {
        // Count all biz events from yesterday
        long numberOfBizEvents = this.bizEventsRepository.countBizEventsFromPaymentDateTime(bizEventMinDate, bizEventMaxDate);

        // Count all payments from nodo with receipt day yesterday and inserted time max today
        long numberOfPaymentsNewModelFromNodo = this.nodoReceiptNewModelRepository.countPositionReceiptFromReceiptDate(nodoMinDate, nodoMaxDate);
        long numberOfPaymentsOldModelFromNodo = this.nodoReceiptOldModelRepository.countReceiptFromReceiptDate(nodoMinDate, nodoMaxDate);

        return (numberOfPaymentsNewModelFromNodo + numberOfPaymentsOldModelFromNodo) - numberOfBizEvents;
    }

    @Override
    public List<NodoReceiptInfo> retrieveNotElaboratedNodoReceipts(String bizEventMinDate, String bizEventMaxDate, String nodoMinDate, String nodoMaxDate) {
        // Retrieve all biz events payment_token from yesterday
        List<String> bizEventPaymentTokenList = this.bizEventsRepository.getBizEventsPaymentTokenFromPaymentDateTime(bizEventMinDate, bizEventMaxDate);

        // Retrieve all payments from nodo with receipt day yesterday and inserted time max today
        List<NodoReceiptInfo> list = new ArrayList<>();
        list.addAll(
                this.nodoReceiptNewModelRepository.getPositionReceiptFromReceiptDateAndNotInPaymentTokenList(
                        nodoMinDate,
                        nodoMaxDate,
                        bizEventPaymentTokenList
                ));
        list.addAll(
                this.nodoReceiptOldModelRepository.getReceiptFromReceiptDateAndNotInPaymentTokenList(
                        nodoMinDate,
                        nodoMaxDate,
                        bizEventPaymentTokenList
                ));

        return list;
    }

}
