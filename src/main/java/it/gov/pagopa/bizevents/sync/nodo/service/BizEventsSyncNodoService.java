package it.gov.pagopa.bizevents.sync.nodo.service;

import it.gov.pagopa.bizevents.sync.nodo.model.NodoReceiptInfo;

import java.util.List;

public interface BizEventsSyncNodoService {

    long checkBizEventsDiffNodoToday(String bizEventMinDate, String bizEventMaxDate, String nodoMinDate, String nodoMaxDateReceipt, String nodoMaxDateInsert);

    List<NodoReceiptInfo>  retrieveNotElaboratedNodoReceipts(String bizEventMinDate, String bizEventMaxDate, String nodoMinDate, String nodoMaxDateReceipt, String nodoMaxDateInsert);
}
